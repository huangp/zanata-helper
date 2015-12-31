package org.zanata.helper.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.zanata.helper.exception.JobNotFoundException;
import org.zanata.helper.model.JobConfig;
import org.zanata.helper.service.SchedulerService;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
@Controller
@Slf4j
public class JobController {
    @Autowired
    private SchedulerService schedulerServiceImpl;

    @RequestMapping(value = "/job", params = {"id"}, method = RequestMethod.GET)
    public String getNewJobPage(ModelMap model,
        @RequestParam(value = "id", defaultValue = "") String id)
        throws JobNotFoundException {
        if(StringUtils.isEmpty(id)) {
            throw new JobNotFoundException(id);
        }
        JobConfig jobConfig = schedulerServiceImpl.getJob(new Long(id));
        if(jobConfig == null) {
            throw new JobNotFoundException(id);
        }
        model.addAttribute("job", jobConfig);
        return "job";
    }
}
