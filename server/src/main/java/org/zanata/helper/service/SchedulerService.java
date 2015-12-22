package org.zanata.helper.service;

import java.util.List;

import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;
import org.zanata.helper.exception.TaskNotFoundException;
import org.zanata.helper.model.JobSummary;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.model.JobConfig;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public interface SchedulerService {
    JobStatus getLastStatus(Long id)
        throws SchedulerException, TaskNotFoundException;

    List<JobSummary> getRunningJob() throws SchedulerException;

    List<JobSummary> getAllJobs() throws SchedulerException;

    void addSyncJob(JobConfig sync) throws SchedulerException;

    void cancelInProgressSyncJob(JobConfig sync) throws UnableToInterruptJobException;
}
