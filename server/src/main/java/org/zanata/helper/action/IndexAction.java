package org.zanata.helper.action;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.zanata.helper.api.JobsResource;
import org.zanata.helper.api.JobResource;
import org.zanata.helper.api.WorkResource;
import org.zanata.helper.model.JobSummary;

import lombok.extern.slf4j.Slf4j;
import org.zanata.helper.model.WorkSummary;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Named("indexAction")
@RequestScoped
@Slf4j
public class IndexAction {

    @Inject
    private WorkResource workResource;

    @Inject
    private JobResource jobResource;

    @Inject
    private JobsResource jobsResource;

    public List<WorkSummary> getAllWork() {
        Response response = workResource.getAllWork();
        return (List<WorkSummary>)response.getEntity();
    }

    public List<JobSummary> getRunningJobs() {
        Response response = jobsResource.getRunningJobs();
        return (List<JobSummary>)response.getEntity();
    }

    /**
     * Cancel running job
     * @param id
     * @param type - SyncConfig.Type
     */
    public void cancelRunningJob(String id, String type) {
        jobResource.cancelRunningJob(id, type);
    }
}
