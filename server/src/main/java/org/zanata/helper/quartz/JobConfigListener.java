package org.zanata.helper.quartz;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.zanata.helper.events.EventPublisher;
import org.zanata.helper.events.JobRunCompletedEvent;
import org.zanata.helper.events.JobRunStartsEvent;
import org.zanata.helper.model.JobConfig;

@Slf4j
public class JobConfigListener implements TriggerListener {
    public static final String LISTENER_NAME = "JobConfigListener";

    private final EventPublisher eventPublisher;

    public JobConfigListener(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public String getName() {
        return LISTENER_NAME;
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        JobConfig jobConfig = getJobConfigJob(context);
        eventPublisher.fireEvent(
            new JobRunStartsEvent(this, jobConfig.getId(),
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

        JobConfig jobConfig = getJobConfigJob(context);
        eventPublisher.fireEvent(
            new JobRunCompletedEvent(this, jobConfig.getId(),
                context.getJobRunTime(),
                context.getFireTime()));
    }

    private JobConfig getJobConfigJob(JobExecutionContext context) {
        return (JobConfig) context.getJobDetail().getJobDataMap().get("value");
    }
}