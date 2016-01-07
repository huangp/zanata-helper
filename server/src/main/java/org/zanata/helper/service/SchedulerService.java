package org.zanata.helper.service;

import java.util.List;

import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;
import org.zanata.helper.exception.JobNotFoundException;
import org.zanata.helper.model.JobConfig_test;
import org.zanata.helper.model.JobSummary;
import org.zanata.helper.model.JobStatus;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public interface SchedulerService {
    JobStatus getSyncToRepoJobLastStatus(Long id)
        throws SchedulerException, JobNotFoundException;

    List<JobSummary> getRunningJob() throws SchedulerException;

    List<JobSummary> getAllJobs() throws SchedulerException;

    JobConfig_test getJob(Long id);

    void persistAndScheduleJob(JobConfig_test jobConfig) throws SchedulerException;

    void cancelRunningJob(Long id)
        throws UnableToInterruptJobException, JobNotFoundException;

    void deleteJob(Long id) throws SchedulerException, JobNotFoundException;
}
