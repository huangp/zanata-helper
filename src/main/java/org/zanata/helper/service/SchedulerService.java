package org.zanata.helper.service;

import java.util.List;

import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;
import org.zanata.helper.exception.TaskNotFoundException;
import org.zanata.helper.model.JobInfo;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.model.Sync;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public interface SchedulerService {
    JobStatus getStatus(String sha)
        throws SchedulerException, TaskNotFoundException;

    List<JobInfo> getRunningJob() throws SchedulerException;

    List<JobInfo> getAllJobs() throws SchedulerException;

    void addSyncJob(Sync sync) throws SchedulerException;

    void cancelInProgressSyncJob(Sync sync) throws UnableToInterruptJobException;
}
