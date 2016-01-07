package org.zanata.helper.service;

import java.util.List;

import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;
import org.zanata.helper.exception.JobNotFoundException;
import org.zanata.helper.model.SyncWorkConfig;
import org.zanata.helper.model.JobSummary;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.model.JobConfig;
import org.zanata.helper.model.WorkSummary;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public interface SchedulerService {
    JobStatus getSyncToRepoJobLastStatus(Long id)
        throws SchedulerException, JobNotFoundException;

    List<JobSummary> getRunningJob() throws SchedulerException;

    List<WorkSummary> getAllWork() throws SchedulerException;

    SyncWorkConfig getJob(Long id);

    void persistAndScheduleJob(SyncWorkConfig syncWorkConfig) throws SchedulerException;

    void cancelRunningJob(Long id, JobConfig.Type type)
        throws UnableToInterruptJobException, JobNotFoundException;

    void deleteJob(Long id, JobConfig.Type type)
        throws SchedulerException, JobNotFoundException;

    void startJob(Long id, JobConfig.Type type)
        throws JobNotFoundException, SchedulerException;
}
