package org.zanata.helper.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.zanata.helper.api.APIController;
import org.zanata.helper.api.JobController;
import org.zanata.helper.model.JobInfo;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Controller
public class IndexController {

    private final RestTemplate restTemplate = new RestTemplate();;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getIndexPage(ModelMap model) {
        model.addAttribute("allJobs", getAllJobs());
        model.addAttribute("runningJobs", getRunningJobs());
        return "index";
    }

    @RequestMapping(value = "/runningJobs", method = RequestMethod.GET)
    public String getRunningJobs(ModelMap model) {
        model.addAttribute("runningJobs", getRunningJobs());
        return "view/running_jobs";
    }

    private List<JobInfo> getRunningJobs() {
        JobInfo[] runningJobs =
                restTemplate.getForObject(getRunningJobsUrl(), JobInfo[].class);
        return Arrays.asList(runningJobs);
    }

    private List<JobInfo> getAllJobs() {
        JobInfo[] allJobs =
                restTemplate.getForObject(getAllJobsUrl(), JobInfo[].class);
        return Arrays.asList(allJobs);
    }

    private String getRunningJobsUrl() {
        return APIController.getBaseUrl() + JobController.RUNNING_JOBS_URL;
    }

    private String getAllJobsUrl() {
        return APIController.getBaseUrl() + JobController.ALL_JOBS_URL;
    }
}
