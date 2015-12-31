package org.zanata.helper.service;

import java.util.List;

import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;
import org.zanata.helper.exception.JobNotFoundException;
import org.zanata.helper.model.JobSummary;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.model.JobConfig;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public interface SchedulerService {
    JobStatus getLastStatus(Long id)
        throws SchedulerException, JobNotFoundException;

    List<JobSummary> getRunningJob() throws SchedulerException;

    List<JobSummary> getAllJobs() throws SchedulerException;

    JobConfig getJob(Long id);

    void persistAndScheduleJob(JobConfig sync) throws SchedulerException;

    void cancelRunningJob(Long id)
        throws UnableToInterruptJobException, JobNotFoundException;

    void deleteJob(Long id) throws SchedulerException, JobNotFoundException;
}
