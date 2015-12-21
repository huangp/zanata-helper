package org.zanata.helper.quartz;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.zanata.helper.events.EventPublisher;
import org.zanata.helper.events.JobRunCompletedEvent;
import org.zanata.helper.model.Sync;

@Slf4j
public class SyncJobListener implements JobListener {
    public static final String LISTENER_NAME = "SyncJobListener";

    private final EventPublisher eventPublisher;

    public SyncJobListener(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public String getName() {
        return LISTENER_NAME;
    }

    // Run this if job is about to be executed.
    public void jobToBeExecuted(JobExecutionContext context) {
        log.debug("Job : " + getSyncJob(context).getName() + " starting.");
    }

    public void jobExecutionVetoed(JobExecutionContext context) {
        log.debug("jobExecutionVetoed: " + getSyncJob(context).getName());
    }

    public void jobWasExecuted(JobExecutionContext context,
        JobExecutionException jobException) {
        Sync sync = getSyncJob(context);
        log.debug("Job : " + sync.getName() + " is completed.");

        eventPublisher
                .fireEvent(new JobRunCompletedEvent(this, sync.getSha(),
                        context.getJobRunTime(),
                        context.getFireTime()));

        if (jobException != null &&
                !StringUtils.isEmpty(jobException.getMessage())) {
            log.error("Exception thrown by: " + getSyncJob(context).getName()
                    + " Exception: " + jobException.getMessage());
        }
    }

    private Sync getSyncJob(JobExecutionContext context) {
        return (Sync) context.getJobDetail().getJobDataMap().get("value");
    }
}