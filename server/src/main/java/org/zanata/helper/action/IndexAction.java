package org.zanata.helper.action;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.zanata.helper.api.JobsResource;
import org.zanata.helper.api.JobResource;
import org.zanata.helper.model.JobSummary;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Named("indexAction")
@RequestScoped
@Slf4j
public class IndexAction {

    @Inject
    private JobsResource jobsResource;

    @Inject
    private JobResource jobResource;

    public List<JobSummary> getAllJobs() {
        Response response = jobsResource.getAllJobs();
        List<JobSummary> allJobs = (List<JobSummary>)response.getEntity();
        return allJobs;
    }

    public List<JobSummary> getRunningJobs() {
        Response response = jobsResource.getRunningJobs();
        List<JobSummary> runningJobs = (List<JobSummary>)response.getEntity();
        return runningJobs;
    }

    public void cancelRunningJob(String id) {
        jobResource.cancelRunningJob(id);
    }
}
