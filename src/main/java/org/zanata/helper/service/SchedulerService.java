package org.zanata.helper.service;

import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;
import org.zanata.helper.exception.TaskNotFoundException;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.model.Sync;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public interface SchedulerService {
    JobStatus getStatus(String key)
        throws SchedulerException, TaskNotFoundException;

    void addSyncJob(Sync sync) throws SchedulerException;

    void cancelInProgressSyncJob(Sync sync) throws UnableToInterruptJobException;
}
