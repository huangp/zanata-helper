package org.zanata.helper.quartz;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.zanata.helper.events.JobRunCompletedEvent;
import org.zanata.helper.events.JobRunStartsEvent;
import org.zanata.helper.model.SyncWorkConfig;

@Slf4j
@Dependent
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
        SyncWorkConfig syncWorkConfig = getJobConfigJob(context);
        jobRunStartsEvent.fire(
            new JobRunStartsEvent(syncWorkConfig.getId(),
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

        SyncWorkConfig syncWorkConfig = getJobConfigJob(context);
        jobRunCompletedEvent.fire(
            new JobRunCompletedEvent(syncWorkConfig.getId(),
                trigger.getKey(),
                context.getJobRunTime(),
                context.getFireTime()));
    }

    private SyncWorkConfig getJobConfigJob(JobExecutionContext context) {
        return (SyncWorkConfig) context.getJobDetail().getJobDataMap().get("value");
    }
}
