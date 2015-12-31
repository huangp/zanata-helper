package org.zanata.helper.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.zanata.helper.api.APIController;
import org.zanata.helper.api.JobsAPIController;
import org.zanata.helper.model.JobSummary;
import org.zanata.helper.util.DateUtil;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Controller
public class IndexController {

    private final RestTemplate restTemplate = new RestTemplate();

    private final DateUtil dateUtil = new DateUtil();

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getIndexPage(ModelMap model) {
        model.addAttribute("dateUtil", dateUtil);
        model.addAttribute("allJobs", getAllJobs());
        model.addAttribute("runningJobs", getRunningJobs());
        return "index";
    }

    @RequestMapping(value = "/runningJobs", method = RequestMethod.GET)
    public String getRunningJobs(ModelMap model) {
        model.addAttribute("runningJobs", getRunningJobs());
        return "view/running_jobs";
    }

    private List<JobSummary> getRunningJobs() {
        JobSummary[] runningJobs =
                restTemplate.getForObject(getRunningJobsUrl(), JobSummary[].class);
        return Arrays.asList(runningJobs);
    }

    private List<JobSummary> getAllJobs() {
        JobSummary[] allJobs =
                restTemplate.getForObject(getAllJobsUrl(), JobSummary[].class);
        return Arrays.asList(allJobs);
    }

    private String getRunningJobsUrl() {
        return APIController.getBaseUrl() + APIController.API_ROOT
                + APIController.JOBS_ROOT + JobsAPIController.RUNNING_JOBS_URL;
    }

    private String getAllJobsUrl() {
        return APIController.getBaseUrl() + APIController.API_ROOT
                + APIController.JOBS_ROOT;
    }
}
