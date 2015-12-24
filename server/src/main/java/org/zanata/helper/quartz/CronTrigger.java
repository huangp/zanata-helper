/**
 *
 */
package org.zanata.helper.quartz;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import org.zanata.helper.events.EventPublisher;
import org.zanata.helper.events.JobRunCompletedEvent;
import org.zanata.helper.exception.UnableLoadPluginException;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.model.JobStatusType;
import org.zanata.helper.model.JobConfig;
import org.zanata.helper.component.AppConfiguration;
import org.zanata.helper.service.PluginsService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Slf4j
public class CronTrigger {
    private final Scheduler scheduler =
        StdSchedulerFactory.getDefaultScheduler();

    private final EventPublisher eventPublisher;

    private final AppConfiguration appConfiguration;

    private final PluginsService pluginsService;

    public CronTrigger(EventPublisher eventPublisher,
        AppConfiguration appConfiguration, PluginsService pluginsService)
        throws SchedulerException {
        this.eventPublisher = eventPublisher;
        this.appConfiguration = appConfiguration;
        this.pluginsService = pluginsService;
        scheduler.start();
    }

    public TriggerKey scheduleMonitor(JobConfig jobConfig)
        throws SchedulerException {
        if (jobConfig != null) {
            JobKey jobKey = new JobKey(jobConfig.getId().toString());

            if (!scheduler.checkExists(jobKey)) {
                try {
                    JobDetail jobDetail =
                        JobBuilder
                            .newJob(org.zanata.helper.quartz.SyncJob.class)
                            .withIdentity(jobConfig.getId().toString())
                            .withDescription(jobConfig.toString())
                            .build();

                    jobDetail.getJobDataMap().put("value", jobConfig);
                    jobDetail.getJobDataMap()
                        .put("basedir", appConfiguration.getStorageDirectory());

                    jobDetail.getJobDataMap()
                        .put("sourceRepoExecutor", pluginsService
                            .getNewSourceRepoPlugin(
                                jobConfig.getSourceRepoExecutorName(),
                                jobConfig.getSourceRepoConfig()));

                    jobDetail.getJobDataMap()
                        .put("translationServerExecutor", pluginsService
                            .getNewTransServerPlugin(
                                jobConfig.getTranslationServerExecutorName(),
                                jobConfig.getTransServerConfig()));

                    Trigger trigger = buildTrigger(jobConfig);

                    if (scheduler.getListenerManager().getJobListeners()
                        .isEmpty()) {
                        scheduler.getListenerManager()
                            .addJobListener(
                                new JobConfigListener(eventPublisher));
                    }
                    scheduler.scheduleJob(jobDetail, trigger);
                    return trigger.getKey();
                } catch (UnableLoadPluginException e) {
                    log.error("Unable to load plugin", e.getMessage());
                }
            }
        }
        return null;
    }

    public void pauseJob(JobKey jobKey) throws SchedulerException {
        scheduler.pauseJob(jobKey);
    }

    public void resumeJob(JobKey jobKey) throws SchedulerException {
        scheduler.resumeJob(jobKey);
    }

    public void pauseAll() throws SchedulerException {
        scheduler.pauseAll();
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

    public void cancelInProgressJob(JobConfig sync)
        throws UnableToInterruptJobException {
        JobKey jobKey = new JobKey(sync.getId().toString());
        scheduler.interrupt(jobKey);
    }

    public void reschedule(TriggerKey key, JobConfig sync)
        throws SchedulerException {
        scheduler.rescheduleJob(key, buildTrigger(sync));
    }

    private Trigger buildTrigger(JobConfig sync) {
        TriggerBuilder builder = TriggerBuilder
            .newTrigger()
            .withIdentity("Trigger:" + sync.getId());

        if (!StringUtils.isEmpty(sync.getCron())) {
            builder.withSchedule(
                CronScheduleBuilder.cronSchedule(sync.getCron()));
        }
        return builder.build();
    }
}