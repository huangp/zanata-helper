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
import org.springframework.web.bind.annotation.RequestParam;
import org.zanata.helper.common.plugin.SourceRepoExecutor;
import org.zanata.helper.common.plugin.TranslationServerExecutor;
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
    public String getNewJobPage(@ModelAttribute("jobForm") JobForm jobForm,
        ModelMap model) {

        List<SourceRepoExecutor> sourceRepoExecutors =
            pluginsServiceImpl.getAvailableSourceRepoPlugins();

        model.addAttribute("repoPluginOptions", sourceRepoExecutors);
        model.addAttribute("selectedSrcPlugin", sourceRepoExecutors.get(0));


        List<TranslationServerExecutor> transServerExecutors =
            pluginsServiceImpl.getAvailableTransServerPlugins();

        model.addAttribute("serverPluginOptions", transServerExecutors);
        model.addAttribute("selectedTransPlugin", transServerExecutors.get(0));

        return "new_job";
    }

    @RequestMapping(value = "/jobs/new/settings", params = {
        "selectedPlugin", "type" }, method = RequestMethod.POST)
    public String getSrcTransSettingsPage(
        @ModelAttribute("jobForm") JobForm jobForm, ModelMap model,
        @RequestParam(value = "selectedPlugin") String className,
        @RequestParam(value = "type", defaultValue = "repo") String type) {

        if (type.equals("trans")) {
            TranslationServerExecutor selectedExecutor = null;
            for (TranslationServerExecutor executor : pluginsServiceImpl
                .getAvailableTransServerPlugins()) {
                if (executor.getClass().getName().equals(className)) {
                    selectedExecutor = executor;
                    break;
                }
            }
            model.addAttribute("selectedTransPlugin", selectedExecutor);
            model.addAttribute("transServerConfig",
                jobForm.getTransServerConfig());
            return "view/trans_settings";
        } else {
            SourceRepoExecutor selectedExecutor = null;
            for (SourceRepoExecutor executor : pluginsServiceImpl
                .getAvailableSourceRepoPlugins()) {
                if (executor.getClass().getName().equals(className)) {
                    selectedExecutor = executor;
                    break;
                }
            }
            model.addAttribute("selectedSrcPlugin", selectedExecutor);
            model.addAttribute("sourceRepoConfig",
                jobForm.getSourceRepoConfig());
            return "view/repo_settings";
        }
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
