package org.zanata.helper.controller;

import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.zanata.helper.api.JobsAPIController;
import org.zanata.helper.model.JobSummary;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Named("indexController")
@ViewScoped
@Slf4j
public class IndexController {

    @Inject
    private JobsAPIController jobsAPIController;

    public List<JobSummary> getAllJobs() {
        Response response = jobsAPIController.getAllJobs();
        JobSummary[] allJobs = (JobSummary[])response.getEntity();
        return Arrays.asList(allJobs);
    }

    private List<JobSummary> getRunningJobs() {
        Response response = jobsAPIController.getRunningJobs();
        JobSummary[] runningJobs = (JobSummary[])response.getEntity();
        return Arrays.asList(runningJobs);
    }
}
