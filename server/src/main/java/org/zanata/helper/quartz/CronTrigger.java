/**
 *
 */
package org.zanata.helper.quartz;

import lombok.extern.slf4j.Slf4j;
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
import org.zanata.helper.model.JobConfig_test;
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

    public Optional<TriggerKey> scheduleMonitorForRepoSync(JobConfig_test jobConfig)
            throws SchedulerException {
        return scheduleMonitor(jobConfig, RepoSyncJob.class,
                REPO_SYNC_KEY_SUFFIX);
    }

    public Optional<TriggerKey> scheduleMonitorForServerSync(JobConfig_test jobConfig)
            throws SchedulerException {
        return scheduleMonitor(jobConfig, TransServerSyncJob.class,
                SERVER_SYNC_KEY_SUFFIX);
    }

    private  <J extends SyncJob> Optional<TriggerKey> scheduleMonitor(
            JobConfig_test jobConfig, Class<J> jobClass, String jobKeySuffix)
            throws SchedulerException {
        JobKey jobKey = new JobKey(jobConfig.getId().toString() + jobKeySuffix);

        if (scheduler.checkExists(jobKey)) {
            return Optional.empty();
        }
        try {
            JobDetail jobDetail =
                    JobBuilder
                            .newJob(jobClass)
                            .withIdentity(jobKey.getName())
                            .withDescription(jobConfig.toString())
                            .build();

            jobDetail.getJobDataMap().put("value", jobConfig);
            jobDetail.getJobDataMap()
                    .put("basedir", appConfiguration.getRepoDirectory());

            jobDetail.getJobDataMap()
                    .put(RepoExecutor.class.getSimpleName(), pluginsService
                            .getNewSourceRepoPlugin(
                                    jobConfig.getSourceRepoExecutorName(),
                                    jobConfig.getSourceRepoConfig()));

            jobDetail.getJobDataMap()
                    .put(TranslationServerExecutor.class.getSimpleName(),
                            pluginsService
                                    .getNewTransServerPlugin(
                                            jobConfig
                                                    .getTranslationServerExecutorName(),
                                            jobConfig.getTransServerConfig()));

            String cronExp;
            if (jobClass.equals(RepoSyncJob.class)) {
                cronExp = jobConfig.getSyncToRepoConfig().getCron();
            } else if (jobClass.equals(TransServerSyncJob.class)) {
                cronExp = jobConfig.getSyncToServerConfig().getCron();
            } else {
                throw new IllegalStateException(
                        "can not determine what job to run for " + jobClass);
            }
            Trigger trigger = buildTrigger(cronExp, jobKey.getName());

            if (scheduler.getListenerManager().getJobListeners()
                    .isEmpty()) {
                scheduler.getListenerManager()
                        .addTriggerListener(
                                triggerListener);
            }
            scheduler.scheduleJob(jobDetail, trigger);
            return Optional.of(trigger.getKey());
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
        return null;
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

    public void cancelRunningJob(JobConfig_test jobConfig)
        throws UnableToInterruptJobException {
        JobKey jobKey = new JobKey(jobConfig.getId().toString());
        scheduler.interrupt(jobKey);
    }

    public void deleteJob(JobConfig_test jobConfig) throws SchedulerException {
        JobKey jobKey = new JobKey(jobConfig.getId().toString());
        scheduler.deleteJob(jobKey);
    }

    public void reschedule(TriggerKey key, String cron, String triggerKey)
        throws SchedulerException {
        scheduler.rescheduleJob(key, buildTrigger(cron, triggerKey));
    }

    private <J extends SyncJob> Trigger buildTrigger(String cronExp,
            String triggerKey) {
        TriggerBuilder builder = TriggerBuilder
                .newTrigger()
                .withIdentity(triggerKey);

        builder.withSchedule(CronScheduleBuilder.cronSchedule(cronExp));
        return builder.build();
    }


}
