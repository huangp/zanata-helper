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
import org.zanata.helper.common.plugin.RepoExecutor;
import org.zanata.helper.common.plugin.TranslationServerExecutor;
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
                            .newJob(SyncJob.class)
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
                        .put(TranslationServerExecutor.class.getSimpleName(), pluginsService
                            .getNewTransServerPlugin(
                                jobConfig.getTranslationServerExecutorName(),
                                jobConfig.getTransServerConfig()));

                    Trigger trigger = buildTrigger(jobConfig);

                    if (scheduler.getListenerManager().getJobListeners()
                        .isEmpty()) {
                        scheduler.getListenerManager()
                            .addTriggerListener(
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

    public void cancelRunningJob(JobConfig jobConfig)
        throws UnableToInterruptJobException {
        JobKey jobKey = new JobKey(jobConfig.getId().toString());
        scheduler.interrupt(jobKey);
    }

    public void deleteJob(JobConfig jobConfig) throws SchedulerException {
        JobKey jobKey = new JobKey(jobConfig.getId().toString());
        scheduler.deleteJob(jobKey);
    }

    public void reschedule(TriggerKey key, JobConfig jobConfig)
        throws SchedulerException {
        scheduler.rescheduleJob(key, buildTrigger(jobConfig));
    }

    private Trigger buildTrigger(JobConfig jobConfig) {
        TriggerBuilder builder = TriggerBuilder
            .newTrigger()
            .withIdentity(jobConfig.getId().toString());

        if (!StringUtils.isEmpty(jobConfig.getCron())) {
            builder.withSchedule(
                CronScheduleBuilder.cronSchedule(jobConfig.getCron()));
        }
        return builder.build();
    }
}