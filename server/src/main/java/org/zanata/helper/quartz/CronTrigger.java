/**
 *
 */
package org.zanata.helper.quartz;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.UnableToInterruptJobException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.zanata.helper.common.plugin.RepoExecutor;
import org.zanata.helper.common.plugin.TranslationServerExecutor;
import org.zanata.helper.events.JobRunUpdate;
import org.zanata.helper.exception.UnableLoadPluginException;
import org.zanata.helper.model.JobType;
import org.zanata.helper.model.SyncWorkConfig;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.model.JobStatusType;
import org.zanata.helper.component.AppConfiguration;
import org.zanata.helper.service.PluginsService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@ApplicationScoped
@Slf4j
public class CronTrigger {
    private Scheduler scheduler;

    @Inject
    private AppConfiguration appConfiguration;

    @Inject
    private PluginsService pluginsService;

    @Inject
    private JobConfigListener triggerListener;

    public static JobStatusType getType(Trigger.TriggerState state,
        boolean isRunning) {
        if(isRunning) {
            return JobStatusType.RUNNING;
        }
        return JobStatusType.valueOf(state.name());
    }

    @PostConstruct
    public void start() throws SchedulerException {
        scheduler = StdSchedulerFactory.getDefaultScheduler();
        if (scheduler.getListenerManager().getJobListeners().isEmpty()) {
            scheduler.getListenerManager()
                    .addTriggerListener(triggerListener);
        }
        scheduler.start();;
    }

    public Optional<TriggerKey> scheduleMonitorForRepoSync(SyncWorkConfig syncWorkConfig)
            throws SchedulerException {
        return scheduleMonitor(syncWorkConfig, JobType.REPO_SYNC);
    }

    public Optional<TriggerKey> scheduleMonitorForServerSync(SyncWorkConfig syncWorkConfig)
            throws SchedulerException {
        return scheduleMonitor(syncWorkConfig, JobType.SERVER_SYNC);
    }

    private JobDetail buildJobDetail(SyncWorkConfig syncWorkConfig, JobKey key,
            Class jobClass, String cronExp, boolean isEnabled) {
        JobBuilder builder = JobBuilder
                .newJob(jobClass)
                .withIdentity(key)
                .withDescription(syncWorkConfig.toString());

        if(StringUtils.isEmpty(cronExp) || !isEnabled) {
            builder.storeDurably();
        }
        return builder.build();
    }

    private boolean isJobEnabled(SyncWorkConfig syncWorkConfig, JobType jobType) {
        if(jobType.equals(JobType.SERVER_SYNC)) {
            return syncWorkConfig.isSyncToServerEnabled();
        } else if(jobType.equals(JobType.REPO_SYNC)) {
            return syncWorkConfig.isSyncToRepoEnabled();
        }
        return false;
    }

    private <J extends SyncJob> Optional<TriggerKey> scheduleMonitor(
            SyncWorkConfig syncWorkConfig, JobType type)
                    throws SchedulerException {
        JobKey jobKey = type.toJobKey(syncWorkConfig.getId());
        boolean isEnabled = isJobEnabled(syncWorkConfig, type);

        if (scheduler.checkExists(jobKey)) {
            return Optional.empty();
        }
        try {
            String cronExp;
            Class jobClass;
            if (type.equals(JobType.REPO_SYNC)) {
                cronExp = syncWorkConfig.getSyncToRepoConfig().getCron();
                jobClass = RepoSyncJob.class;
            } else if (type.equals(JobType.SERVER_SYNC)) {
                cronExp = syncWorkConfig.getSyncToServerConfig().getCron();
                jobClass = TransServerSyncJob.class;
            } else {
                throw new IllegalStateException(
                    "can not determine what job to run for " + type);
            }

            JobDetail jobDetail =
                buildJobDetail(syncWorkConfig, jobKey, jobClass, cronExp,
                    isEnabled);

            jobDetail.getJobDataMap().put("value", syncWorkConfig);
            jobDetail.getJobDataMap()
                    .put("basedir", type.baseWorkDir(
                            appConfiguration.getRepoDir()));

            jobDetail.getJobDataMap().put("jobType", type);

            jobDetail.getJobDataMap()
                    .put(RepoExecutor.class.getSimpleName(), pluginsService
                            .getNewSourceRepoPlugin(
                                    syncWorkConfig.getSrcRepoPluginName(),
                                    syncWorkConfig.getSrcRepoPluginConfig()));

            jobDetail.getJobDataMap()
                .put(TranslationServerExecutor.class.getSimpleName(),
                    pluginsService
                        .getNewTransServerPlugin(
                            syncWorkConfig
                                .getTransServerPluginName(),
                            syncWorkConfig.getTransServerPluginConfig()));

            if (scheduler.getListenerManager().getJobListeners().isEmpty()) {
                scheduler.getListenerManager()
                        .addTriggerListener(triggerListener);
            }

            if (!StringUtils.isEmpty(cronExp) && isEnabled) {
                Trigger trigger = buildTrigger(cronExp, syncWorkConfig.getId(),
                    type, isEnabled);
                scheduler.scheduleJob(jobDetail, trigger);
                return Optional.of(trigger.getKey());
            }
            scheduler.addJob(jobDetail, false);
            return Optional.empty();
        } catch (UnableLoadPluginException e) {
            log.error("Unable to load plugin", e.getMessage());
        }
        return Optional.empty();
    }

    public JobStatus getTriggerStatus(Long id,
            JobRunUpdate event) throws SchedulerException {
        JobKey key = event.getJobType().toJobKey(id);

        if (scheduler.checkExists(key)) {
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(key);

            if (!triggers.isEmpty()) {
                Trigger trigger = triggers.get(0);
                Date endTime = event.getCompletedTime();
                Trigger.TriggerState state =
                        scheduler.getTriggerState(trigger.getKey());

                return new JobStatus(
                        getType(state,
                                isJobRunning(trigger.getKey())),
                        trigger.getPreviousFireTime(), endTime,
                        trigger.getNextFireTime());
            }
        }
        return JobStatus.EMPTY;
    }

    public boolean isJobRunning(TriggerKey key) throws SchedulerException {
        List<JobExecutionContext> currentJobs =
            scheduler.getCurrentlyExecutingJobs();

        for (JobExecutionContext jobCtx : currentJobs) {
            if (jobCtx.getTrigger().getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public List<JobDetail> getJobs() throws SchedulerException {
        List<JobDetail> jobs = new ArrayList<>();
        for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.anyJobGroup())) {
            JobType jobType = JobType.valueOf(jobKey.getName());
            Long workId = new Long(jobKey.getGroup());
            JobDetail jobDetail =
                    scheduler.getJobDetail(jobType.toJobKey(workId));
            jobs.add(jobDetail);
        }
        return jobs;
    }

    public void cancelRunningJob(Long id, JobType type)
        throws UnableToInterruptJobException {
        JobKey jobKey = type.toJobKey(id);
        scheduler.interrupt(jobKey);
    }

    public void deleteJob(Long id, JobType type) throws SchedulerException {
        JobKey jobKey = type.toJobKey(id);
        scheduler.deleteJob(jobKey);
    }

    public void disableJob(Long id, JobType type) throws SchedulerException {
        JobKey jobKey = type.toJobKey(id);
        scheduler.pauseJob(jobKey);
    }

    public void enableJob(Long id, JobType type) throws SchedulerException {
        JobKey jobKey = type.toJobKey(id);
        scheduler.resumeJob(jobKey);
    }

    public void deleteAndReschedule(SyncWorkConfig syncWorkConfig, JobType type)
            throws SchedulerException {
        deleteJob(syncWorkConfig.getId(), type);
        scheduleMonitor(syncWorkConfig, type);
    }

    public void triggerJob(Long id, JobType type) throws SchedulerException {
        JobKey key = type.toJobKey(id);
        scheduler.triggerJob(key);
    }

    private <J extends SyncJob> Trigger buildTrigger(String cronExp,
        Long id, JobType type, boolean isEnabled) {
        TriggerBuilder builder = TriggerBuilder
            .newTrigger()
            .withIdentity(type.toTriggerKey(id));
        if (!StringUtils.isEmpty(cronExp) && isEnabled) {
            builder.withSchedule(
                CronScheduleBuilder.cronSchedule(cronExp));
        }
        return builder.build();
    }

}
