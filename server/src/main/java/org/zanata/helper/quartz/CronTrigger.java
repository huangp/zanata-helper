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
import org.zanata.helper.common.plugin.RepoExecutor;
import org.zanata.helper.common.plugin.TranslationServerExecutor;
import org.zanata.helper.events.JobRunCompletedEvent;
import org.zanata.helper.exception.UnableLoadPluginException;
import org.zanata.helper.model.JobType;
import org.zanata.helper.model.SyncWorkConfig;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.model.JobStatusType;
import org.zanata.helper.component.AppConfiguration;
import org.zanata.helper.service.PluginsService;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Slf4j
public class CronTrigger {
    public static final String REPO_SYNC_KEY_SUFFIX = "-repoSync";
    public static final String SERVER_SYNC_KEY_SUFFIX = "-serverSync";
    private final Scheduler scheduler =
        StdSchedulerFactory.getDefaultScheduler();

    private final AppConfiguration appConfiguration;

    private final PluginsService pluginsService;
    private final JobConfigListener triggerListener;

    public CronTrigger(AppConfiguration appConfiguration,
            PluginsService pluginsService, JobConfigListener triggerListener)
        throws SchedulerException {
        this.appConfiguration = appConfiguration;
        this.pluginsService = pluginsService;
        this.triggerListener = triggerListener;
        scheduler.start();
    }

    public Optional<TriggerKey> scheduleMonitorForRepoSync(SyncWorkConfig syncWorkConfig)
            throws SchedulerException {
        return scheduleMonitor(syncWorkConfig, RepoSyncJob.class,
            JobType.REPO_SYNC);
    }

    public Optional<TriggerKey> scheduleMonitorForServerSync(SyncWorkConfig syncWorkConfig)
            throws SchedulerException {
        return scheduleMonitor(syncWorkConfig, TransServerSyncJob.class,
            JobType.SERVER_SYNC);
    }

    private  <J extends SyncJob> Optional<TriggerKey> scheduleMonitor(
            SyncWorkConfig syncWorkConfig, Class<J> jobClass, JobType type)
            throws SchedulerException {
        JobKey jobKey = getJobKey(syncWorkConfig, getKeySuffix(type));

        if (scheduler.checkExists(jobKey)) {
            return Optional.empty();
        }
        try {
            JobDetail jobDetail =
                    JobBuilder
                            .newJob(jobClass)
                            .withIdentity(jobKey.getName())
                            .withDescription(syncWorkConfig.toString())
                            .build();

            jobDetail.getJobDataMap().put("value", syncWorkConfig);
            jobDetail.getJobDataMap().put("type", getKeySuffix(type));
            jobDetail.getJobDataMap()
                    .put("basedir", appConfiguration.getRepoDirectory());

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
                                            syncWorkConfig.getTransServerConfig()));

            String cronExp;
            if (jobClass.equals(RepoSyncJob.class)) {
                cronExp = syncWorkConfig.getSyncToRepoConfig().getCron();
            } else if (jobClass.equals(TransServerSyncJob.class)) {
                cronExp = syncWorkConfig.getSyncToServerConfig().getCron();
            } else {
                throw new IllegalStateException(
                        "can not determine what job to run for " + jobClass);
            }

            if (scheduler.getListenerManager().getJobListeners().isEmpty()) {
                scheduler.getListenerManager()
                        .addTriggerListener(triggerListener);
            }

            if(!StringUtils.isEmpty(cronExp)) {
                Trigger trigger = buildTrigger(cronExp, jobKey.getName());
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

    public JobStatus getTriggerStatus(TriggerKey key,
        JobRunCompletedEvent event) throws SchedulerException {
        if (scheduler.checkExists(key)) {
            Trigger.TriggerState state = scheduler.getTriggerState(key);
            Trigger trigger = scheduler.getTrigger(key);
            Date endTime =
                event != null ? event.getCompletedTime() : null;

            return new JobStatus(
                JobStatusType.getType(state, isJobRunning(key)),
                trigger.getPreviousFireTime(), endTime,
                trigger.getNextFireTime());
        }
        return JobStatus.EMPTY;
    }

    public JobStatus getTriggerStatus(SyncWorkConfig syncWorkConfig,
            JobRunCompletedEvent event) throws SchedulerException {
        JobKey key = getJobKey(syncWorkConfig, getKeySuffix(event.getType()));

        if (scheduler.checkExists(key)) {
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(key);

            if (!triggers.isEmpty()) {
                Trigger trigger = triggers.get(0);
                Date endTime = event != null ? event.getCompletedTime() : null;

                Trigger.TriggerState state =
                        scheduler.getTriggerState(trigger.getKey());

                return new JobStatus(
                        JobStatusType.getType(state,
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

    public List<JobDetail> getRunningJobs() throws SchedulerException {
        return scheduler.getCurrentlyExecutingJobs().stream()
            .map(JobExecutionContext::getJobDetail)
            .collect(Collectors.toList());
    }

    public void cancelRunningJob(SyncWorkConfig syncWorkConfig, JobType type)
        throws UnableToInterruptJobException {
        JobKey jobKey = getJobKey(syncWorkConfig, getKeySuffix(type));
        scheduler.interrupt(jobKey);
    }

    public void deleteJob(SyncWorkConfig syncWorkConfig, JobType type)
            throws SchedulerException {
        JobKey jobKey = getJobKey(syncWorkConfig, getKeySuffix(type));
        scheduler.deleteJob(jobKey);
    }

    public void reschedule(TriggerKey key, String cron, String triggerKey)
        throws SchedulerException {
        scheduler.rescheduleJob(key, buildTrigger(cron, triggerKey));
    }

    public void triggerJob(SyncWorkConfig syncWorkConfig, JobType type)
            throws SchedulerException {
        JobKey key = getJobKey(syncWorkConfig, getKeySuffix(type));
        scheduler.triggerJob(key);
    }

    private <J extends SyncJob> Trigger buildTrigger(String cronExp,
            String triggerKey) {
        TriggerBuilder builder = TriggerBuilder
                .newTrigger()
                .withIdentity(triggerKey);
        if (!StringUtils.isEmpty(cronExp)) {
            builder.withSchedule(
                    CronScheduleBuilder.cronSchedule(cronExp));
        }
        return builder.build();
    }

    private String getKeySuffix(JobType type) {
        if(type.equals(JobType.REPO_SYNC)) {
            return REPO_SYNC_KEY_SUFFIX;
        }
        return SERVER_SYNC_KEY_SUFFIX;
    }

    private JobKey getJobKey(SyncWorkConfig syncWorkConfig, String suffix) {
        return new JobKey(syncWorkConfig.getId().toString() + suffix);
    }
}
