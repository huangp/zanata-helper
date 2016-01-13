package org.zanata.helper.action;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.zanata.helper.api.JobResource;
import org.zanata.helper.api.WorkResource;
import org.zanata.helper.model.JobStatusType;
import org.zanata.helper.model.JobSummary;
import org.zanata.helper.model.JobType;
import org.zanata.helper.model.SyncWorkConfig;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Named("workAction")
@RequestScoped
@Slf4j
public class WorkAction {

    @Inject
    private WorkResource workResourceImpl;

    @Inject
    private JobResource jobResource;

    @Getter
    @Setter
    private String id;

    private SyncWorkConfig syncWorkConfig;

    public SyncWorkConfig getSyncWorkConfig() {
        if(syncWorkConfig == null) {
            Response response = workResourceImpl.getWork(id, "");
            syncWorkConfig = (SyncWorkConfig)response.getEntity();
        }
        return syncWorkConfig;
    }

    public void triggerSyncToRepoJob() {
        jobResource.triggerJob(id, JobType.REPO_SYNC);
    }

    public void triggerSyncToServerJob() {
        jobResource.triggerJob(id, JobType.SERVER_SYNC);
    }

    public boolean isSyncToRepoRunning() {
        return isJobRunning(JobType.REPO_SYNC);
    }

    public boolean isSyncToServerRunning() {
        return isJobRunning(JobType.SERVER_SYNC);
    }

    private boolean isJobRunning(JobType jobType) {
        Response response =
                jobResource.getJob(id, jobType, JobStatusType.RUNNING);
        List<JobSummary> result = (List<JobSummary>) response.getEntity();
        return !result.isEmpty();
    }
}
