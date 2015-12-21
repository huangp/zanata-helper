package org.zanata.helper.controller;

import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.zanata.helper.api.APIController;
import org.zanata.helper.api.JobController;
import org.zanata.helper.model.JobInfo;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Controller
public class IndexController {

    @Autowired
    private ServletContext context;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getIndexPage(ModelMap model) {
        RestTemplate restTemplate = new RestTemplate();
        JobInfo[] allJobs =
                restTemplate.getForObject(getAllJobsUrl(), JobInfo[].class);
        JobInfo[] runningJobs =
                restTemplate.getForObject(getRunningJobsUrl(), JobInfo[].class);
        model.addAttribute("allJobs", Arrays.asList(allJobs));
        model.addAttribute("runningJobs", Arrays.asList(runningJobs));
        return "index";
    }

    private String getRunningJobsUrl() {
        return APIController.getBaseUrl() + JobController.RUNNING_JOBS_URL;
    }

    private String getAllJobsUrl() {
        return APIController.getBaseUrl() + JobController.ALL_JOBS_URL;
    }
}
