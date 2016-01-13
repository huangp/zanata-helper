package org.zanata.helper.action;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.zanata.helper.api.JobResource;
import org.zanata.helper.api.WorkResource;
import org.zanata.helper.model.JobStatusType;
import org.zanata.helper.model.JobSummary;

import lombok.extern.slf4j.Slf4j;

import org.zanata.helper.model.JobType;
import org.zanata.helper.model.WorkSummary;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Named("indexAction")
@RequestScoped
@Slf4j
public class IndexAction {

    @Inject
    private WorkResource workResourceImpl;

    @Inject
    private JobResource jobResource;

    public List<WorkSummary> getAllWork() {
        Response response = workResourceImpl.getWork("", "summary");
        return (List<WorkSummary>)response.getEntity();
    }

    public List<JobSummary> getRunningJobs() {
        Response response =
                jobResource.getJob(null, null, JobStatusType.RUNNING);
        return (List<JobSummary>) response.getEntity();
    }

    /**
     * Cancel running job
     * @param id
     * @param type - SyncConfig.Type
     */
    public void cancelRunningJob(String id, String type) {
        jobResource.cancelRunningJob(id, JobType.valueOf(type));
    }
}
