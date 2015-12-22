/**
 *
 */
package org.zanata.helper.quartz;

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
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.model.JobStatusType;
import org.zanata.helper.model.JobConfig;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Optional;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public class CronTrigger {
    private final Scheduler scheduler =
        StdSchedulerFactory.getDefaultScheduler();

    private final EventPublisher eventPublisher;

    public CronTrigger(EventPublisher eventPublisher)
            throws SchedulerException {
        this.eventPublisher = eventPublisher;
        scheduler.start();
    }

    public TriggerKey scheduleMonitor(JobConfig sync) throws SchedulerException {
        if (sync != null) {
            JobKey jobKey = new JobKey(sync.getId().toString());

            if (!scheduler.checkExists(jobKey)) {
                JobDetail jobDetail =
                    JobBuilder.newJob(org.zanata.helper.quartz.SyncJob.class)
                        .withIdentity(sync.getId().toString())
                        .withDescription(sync.toString())
                        .build();

                jobDetail.getJobDataMap().put("value", sync);

                Trigger trigger = buildTrigger(sync);
                
                if (scheduler.getListenerManager().getJobListeners()
                        .isEmpty()) {
                    scheduler.getListenerManager()
                            .addJobListener(
                                    new JobConfigListener(eventPublisher));
                }

                scheduler.scheduleJob(jobDetail, trigger);
                return trigger.getKey();
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
        if(scheduler.checkExists(key)) {
            Trigger.TriggerState state = scheduler.getTriggerState(key);
            Trigger trigger = scheduler.getTrigger(key);
            Date endTime =
                    event != null ? event.getCompletedTime() : null;

            return new JobStatus(JobStatusType.getType(state, isJobRunning(key)),
                trigger.getPreviousFireTime(), endTime, trigger.getNextFireTime());
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

    public void cancelInProgressJob(JobConfig sync) throws UnableToInterruptJobException {
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

        if(StringUtils.isEmpty(sync.getCron())) {
            builder.startNow();
        } else {
            builder.withSchedule(
                CronScheduleBuilder.cronSchedule(sync.getCron()));
        }
        return builder.build();
    }
}