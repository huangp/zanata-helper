package org.zanata.helper.service;

import java.util.List;

import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;
import org.zanata.helper.exception.JobNotFoundException;
import org.zanata.helper.model.JobConfig;
import org.zanata.helper.model.JobSummary;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.model.SyncConfig;
import org.zanata.helper.model.WorkSummary;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public interface SchedulerService {
    JobStatus getSyncToRepoJobLastStatus(Long id)
        throws SchedulerException, JobNotFoundException;

    List<JobSummary> getRunningJob() throws SchedulerException;

    List<WorkSummary> getAllWork() throws SchedulerException;

    JobConfig getJob(Long id);

    void persistAndScheduleJob(JobConfig jobConfig) throws SchedulerException;

    void cancelRunningJob(Long id, SyncConfig.Type type)
        throws UnableToInterruptJobException, JobNotFoundException;

    void deleteJob(Long id, SyncConfig.Type type)
        throws SchedulerException, JobNotFoundException;

    void startJob(Long id, SyncConfig.Type type)
        throws JobNotFoundException, SchedulerException;
}
