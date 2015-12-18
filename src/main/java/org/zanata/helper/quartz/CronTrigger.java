/**
 *
 */
package org.zanata.helper.quartz;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.UnableToInterruptJobException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;
import org.zanata.helper.model.Sync;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public class CronTrigger {
    private final Scheduler scheduler =
        StdSchedulerFactory.getDefaultScheduler();

    public CronTrigger() throws SchedulerException {
        scheduler.start();
    }

    public TriggerKey scheduleMonitor(Sync sync) throws SchedulerException {
        if (sync != null) {
            JobKey jobKey = new JobKey(sync.getId().toString());

            if (!scheduler.checkExists(jobKey)) {
                JobDetail jobDetail =
                    JobBuilder.newJob(SyncJob.class).withIdentity(jobKey)
                        .build();

                jobDetail.getJobDataMap().put("value", sync);

                Trigger trigger = buildTrigger(sync);
                scheduler.getListenerManager()
                    .addJobListener(new SyncJobListener(), KeyMatcher
                        .keyEquals(jobKey));
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

    public void cancelInProgressJob(Sync sync) throws UnableToInterruptJobException {
        JobKey jobKey = new JobKey(sync.getId().toString());
        scheduler.interrupt(jobKey);
    }

    public void reschedule(TriggerKey key, Sync sync)
        throws SchedulerException {
        scheduler.rescheduleJob(key, buildTrigger(sync));
    }

    private Trigger buildTrigger(Sync sync) {
        Trigger trigger = TriggerBuilder
            .newTrigger()
            .withIdentity("Trigger:" + sync.getSourceRepositoryUrl())
            .withSchedule(
                CronScheduleBuilder.cronSchedule(sync.getCron()))
            .build();
        return trigger;
    }
}