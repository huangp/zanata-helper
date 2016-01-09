package org.zanata.helper.service;

import java.util.List;

import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;
import org.zanata.helper.exception.JobNotFoundException;
import org.zanata.helper.model.JobType;
import org.zanata.helper.model.SyncWorkConfig;
import org.zanata.helper.model.JobSummary;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.model.WorkSummary;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public interface SchedulerService {
    JobStatus getJobLastStatus(Long id, JobType type)
        throws SchedulerException, JobNotFoundException;

    List<JobSummary> getRunningJobs() throws SchedulerException;

    List<WorkSummary> getAllWork() throws SchedulerException;

    void persistAndScheduleWork(SyncWorkConfig syncWorkConfig) throws SchedulerException;

    void cancelRunningJob(Long id, JobType type)
        throws UnableToInterruptJobException, JobNotFoundException;

    void deleteJob(Long id, JobType type)
        throws SchedulerException, JobNotFoundException;

    void startJob(Long id, JobType type)
        throws JobNotFoundException, SchedulerException;
}
