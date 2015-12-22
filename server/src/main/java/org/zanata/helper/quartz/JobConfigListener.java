package org.zanata.helper.quartz;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.zanata.helper.events.EventPublisher;
import org.zanata.helper.events.JobRunCompletedEvent;
import org.zanata.helper.model.JobConfig;

@Slf4j
public class JobConfigListener implements JobListener {
    public static final String LISTENER_NAME = "JobConfigListener";

    private final EventPublisher eventPublisher;

    public JobConfigListener(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public String getName() {
        return LISTENER_NAME;
    }

    // Run this if job is about to be executed.
    public void jobToBeExecuted(JobExecutionContext context) {
        log.debug("Job : " + getJobConfigJob(context).getName() + " starting.");
    }

    public void jobExecutionVetoed(JobExecutionContext context) {
        log.debug("jobExecutionVetoed: " + getJobConfigJob(context).getName());
    }

    public void jobWasExecuted(JobExecutionContext context,
        JobExecutionException jobException) {
        JobConfig jobConfig = getJobConfigJob(context);
        log.debug("Job : " + jobConfig.getName() + " is completed.");

        eventPublisher.fireEvent(
            new JobRunCompletedEvent(this, jobConfig.getId(),
                    context.getJobRunTime(),
                    context.getFireTime()));

        if (jobException != null &&
                !StringUtils.isEmpty(jobException.getMessage())) {
            log.error("Exception thrown by: " + getJobConfigJob(context).getName()
                    + " Exception: " + jobException.getMessage());
        }
    }

    private JobConfig getJobConfigJob(JobExecutionContext context) {
        return (JobConfig) context.getJobDetail().getJobDataMap().get("value");
    }
}