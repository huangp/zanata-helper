package org.zanata.helper.quartz;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.zanata.helper.events.JobRunCompletedEvent;
import org.zanata.helper.events.JobRunStartsEvent;
import org.zanata.helper.model.JobConfig_test;

@Slf4j
public class JobConfigListener implements TriggerListener {
    public static final String LISTENER_NAME = "JobConfigListener";

    @Inject
    private Event<JobRunCompletedEvent> jobRunCompletedEvent;

    @Inject
    private Event<JobRunStartsEvent> jobRunStartsEvent;


    public String getName() {
        return LISTENER_NAME;
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        JobConfig_test jobConfig = getJobConfigJob(context);
        jobRunStartsEvent.fire(
            new JobRunStartsEvent(jobConfig.getId(),
                context.getFireTime()));
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger,
        JobExecutionContext context) {
        log.debug("jobExecutionVetoed: " + getJobConfigJob(context).getName());
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {

    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context,
        Trigger.CompletedExecutionInstruction triggerInstructionCode) {

        JobConfig_test jobConfig = getJobConfigJob(context);
        jobRunCompletedEvent.fire(
            new JobRunCompletedEvent(jobConfig.getId(), trigger.getKey(),
                context.getJobRunTime(),
                context.getFireTime()));
    }

    private JobConfig_test getJobConfigJob(JobExecutionContext context) {
        return (JobConfig_test) context.getJobDetail().getJobDataMap().get("value");
    }
}
