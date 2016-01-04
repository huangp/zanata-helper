package org.zanata.helper.controller;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.RequestScoped;

import org.zanata.helper.api.APIController;
import org.zanata.helper.api.JobsAPIController;
import org.zanata.helper.model.JobSummary;
import org.zanata.helper.util.DateUtil;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@RequestScoped
public class IndexController {

    private final DateUtil dateUtil = new DateUtil();

//    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getIndexPage() {
//        model.addAttribute("dateUtil", dateUtil);
//        model.addAttribute("allJobs", getAllJobs());
//        model.addAttribute("runningJobs", getRunningJobs());
        return "index";
    }

//    @RequestMapping(value = "/runningJobs", method = RequestMethod.GET)
    public String getRunningJobs() {
//        model.addAttribute("runningJobs", _getRunningJobs());
        return "view/running_jobs";
    }

    private List<JobSummary> _getRunningJobs() {
        JobSummary[] runningJobs = new JobSummary[] {};
//                restTemplate.getForObject(getRunningJobsUrl(), JobSummary[].class);
        return Arrays.asList(runningJobs);
    }

    private List<JobSummary> getAllJobs() {
        JobSummary[] allJobs = new JobSummary[] {};
//                restTemplate.getForObject(getAllJobsUrl(), JobSummary[].class);
        return Arrays.asList(allJobs);
    }

    private String getRunningJobsUrl() {
        return APIController.API_ROOT
                + APIController.JOBS_ROOT + JobsAPIController.RUNNING_JOBS_URL;
    }

    private String getAllJobsUrl() {
        return APIController.API_ROOT
                + APIController.JOBS_ROOT;
    }
}
