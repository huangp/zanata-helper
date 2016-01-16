package org.zanata.helper.service;

import org.zanata.helper.exception.WorkNotFoundException;
import org.zanata.helper.model.JobType;
import org.zanata.helper.model.SyncWorkConfig;
import org.zanata.helper.model.WorkSummary;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public interface WorkService {
    void deleteWork(Long id) throws WorkNotFoundException;

    WorkSummary disableJob(JobType jobType, Long id)
        throws WorkNotFoundException;

    WorkSummary enableJob(JobType jobType, Long id)
        throws WorkNotFoundException;

    void updateOrPersist(SyncWorkConfig syncWorkConfig);
}
