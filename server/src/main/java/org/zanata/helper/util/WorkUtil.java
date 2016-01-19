package org.zanata.helper.util;

import org.zanata.helper.model.JobStatus;
import org.zanata.helper.model.JobSummary;
import org.zanata.helper.model.JobType;
import org.zanata.helper.model.SyncWorkConfig;
import org.zanata.helper.model.WorkSummary;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public class WorkUtil {

    public static final WorkSummary toWorkSummary(
        SyncWorkConfig syncWorkConfig, JobStatus syncToRepoJobStatus, JobStatus syncToServerJobStatus) {
        if(syncWorkConfig == null) {
            return new WorkSummary();
        }
        JobSummary syncToRepoJob =
            new JobSummary("", syncWorkConfig.getName(),
                syncWorkConfig.getId().toString(),
                syncWorkConfig.getDescription(),
                JobType.REPO_SYNC,
                syncToRepoJobStatus);

        JobSummary syncToServerJob =
            new JobSummary("", syncWorkConfig.getName(),
                syncWorkConfig.getId().toString(),
                syncWorkConfig.getDescription(),
                JobType.SERVER_SYNC,
                syncToServerJobStatus);

        return new WorkSummary(syncWorkConfig.getId(),
            syncWorkConfig.getName(),
            syncWorkConfig.getDescription(),
            syncToRepoJob,
            syncToServerJob);
    }
}
