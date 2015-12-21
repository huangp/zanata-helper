package org.zanata.helper.controller;

import java.util.Arrays;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.zanata.helper.model.JobInfo;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Controller
public class IndexController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getIndexPage(ModelMap model) {
        RestTemplate restTemplate = new RestTemplate();
        String allJobsUrl = "http://localhost:8080/api/jobs";
        String runningJobsUrl = "http://localhost:8080/api/jobs/running";
        JobInfo[] allJobs = restTemplate.getForObject(allJobsUrl, JobInfo[].class);
        JobInfo[] runningJobs = restTemplate.getForObject(runningJobsUrl, JobInfo[].class);
        model.addAttribute("allJobs", Arrays.asList(allJobs));
        model.addAttribute("runningJobs", Arrays.asList(runningJobs));
        return "index";
    }
}
