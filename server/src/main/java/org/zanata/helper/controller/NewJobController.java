package org.zanata.helper.controller;

import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.zanata.helper.common.plugin.SourceRepoExecutor;
import org.zanata.helper.model.JobConfig;
import org.zanata.helper.model.JobConfigBuilder;
import org.zanata.helper.service.PluginsService;
import org.zanata.helper.service.SchedulerService;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Controller
@Slf4j
public class NewJobController {

    @Autowired
    private SchedulerService schedulerServiceImpl;

    @Autowired
    private PluginsService pluginsServiceImpl;

    @RequestMapping(value = "/jobs/new", method = RequestMethod.GET)
    public String getNewJobPage(ModelMap model) {
        model.addAttribute("jobForm", new JobForm());

        model.addAttribute("repoPluginOptions",
            pluginsServiceImpl.getAvailableSourceRepoPlugins());
        model.addAttribute("serverPluginOptions",
            pluginsServiceImpl.getAvailableTransServerPlugins());
        return "new_job";
    }

    @RequestMapping(value = "/jobs/new", method = RequestMethod.POST)
    public String onSubmitNewJob(
            @Valid @ModelAttribute("jobForm") JobForm jobForm,
            BindingResult result, ModelMap model) {

        if (result.hasErrors()) {
            return "new_job";
        }

        JobConfig jobConfig = new JobConfigBuilder(jobForm).build();
        try {
            schedulerServiceImpl.persistAndScheduleJob(jobConfig);
        }
        catch (SchedulerException e) {
            log.error("Error trying to schedule job", e.getMessage());
        }
        return "/";
    }
}
