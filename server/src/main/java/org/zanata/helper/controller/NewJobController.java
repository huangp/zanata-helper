package org.zanata.helper.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.zanata.helper.common.SyncType;
import org.zanata.helper.common.plugin.Field;
import org.zanata.helper.common.plugin.Plugin;
import org.zanata.helper.common.plugin.RepoExecutor;
import org.zanata.helper.common.plugin.TranslationServerExecutor;
import org.zanata.helper.common.plugin.Validator;
import org.zanata.helper.component.MessageResource;
import org.zanata.helper.model.JobConfig;
import org.zanata.helper.model.JobConfigBuilder;
import org.zanata.helper.service.PluginsService;
import org.zanata.helper.service.SchedulerService;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private MessageResource messageResource;

    private final String repoSettingsPrefix = "repoSettings-";
    private final String transSettingsPrefix = "transSettings-";

    private List<RepoExecutor> repoExecutors;
    private List<TranslationServerExecutor> transServerExecutors;
    private List<Field> syncTypes;
    private List<Field> jobTypes;

    @RequestMapping(value = "/jobs/new", method = RequestMethod.GET)
    public String getNewJobPage(@ModelAttribute("jobForm") JobForm jobForm,
        ModelMap model) {
        initModel(model);
        return "new_job";
    }

    @RequestMapping(value = "/jobs/new/settings", params = {
        "selectedPlugin", "type" }, method = RequestMethod.GET)
    public String getSrcTransSettingsPage(ModelMap model,
        @RequestParam(value = "selectedPlugin") String className,
        @RequestParam(value = "type", defaultValue = "repo") String type) {

        if (type.equals("trans")) {
            TranslationServerExecutor selectedExecutor =
                getTransServerExecutor(className);
            model.addAttribute("selectedTransPlugin", selectedExecutor);
            return "view/trans_settings";
        } else {
            RepoExecutor selectedExecutor =
                getSourceRepoExecutor(className);
            model.addAttribute("selectedSrcPlugin", selectedExecutor);
            return "view/repo_settings";
        }
    }

    @RequestMapping(value = "/jobs/new", method = RequestMethod.POST)
    public String onSubmitNewJob(
            @Valid @ModelAttribute("jobForm") JobForm jobForm,
        BindingResult result, ModelMap model, HttpServletRequest request) {

        for (Map.Entry<String, String[]> entry : request.getParameterMap()
            .entrySet()) {
            if (entry.getKey().startsWith(repoSettingsPrefix)) {
                String newKey = entry.getKey().replaceFirst(repoSettingsPrefix,
                    "");
                jobForm.getSourceRepoConfig().put(newKey, entry.getValue()[0]);
            } else if (entry.getKey().startsWith(transSettingsPrefix)) {
                String newKey = entry.getKey().replaceFirst(transSettingsPrefix,
                    "");
                jobForm.getTransServerConfig().put(newKey, entry.getValue()[0]);
            }
        }
        if (result.hasErrors()) {
            initModel(model);
            return "new_job";
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.putAll(validateRepoFields(jobForm.getSourceRepoConfig(),
                jobForm.getSourceRepoExecutorName()));
            errors.putAll(validateTransFields(jobForm.getTransServerConfig(),
                jobForm.getTranslationServerExecutorName()));
            if(!errors.isEmpty()) {
                model.put("errors", errors);
                initModel(model);
                return "new_job";
            }
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

    private Map<String, String> validateRepoFields(
        Map<String, String> config, String executorClass) {
        RepoExecutor executor = getSourceRepoExecutor(executorClass);
        if(executor == null) {
            return new HashMap<>();
        }
        return validateFields(config, executor, repoSettingsPrefix);
    }

    private Map<String, String> validateTransFields(
        Map<String, String> config, String executorClass) {
        TranslationServerExecutor executor = getTransServerExecutor(
            executorClass);
        if(executor == null) {
            return new HashMap<>();
        }
        return validateFields(config, executor, transSettingsPrefix);
    }

    private Map<String, String> validateFields(Map<String, String> config,
        Plugin executor, String prefix) {
        Map<String, String> errors = new HashMap<>();

        for (Map.Entry<String, String> entry : config.entrySet()) {
            Field field = executor.getFields().get(entry.getKey());
            if (field != null && field.getValidator() != null) {
                Validator validator = field.getValidator();
                String message = validator.validate(entry.getValue());
                if (!StringUtils.isEmpty(message)) {
                    errors.put(prefix + entry.getKey(), message);
                }
            }
        }
        return errors;
    }

    private RepoExecutor getSourceRepoExecutor(String className) {
        for (RepoExecutor executor : getRepoExecutors()) {
            if (executor.getClass().getName().equals(className)) {
                return executor;
            }
        }
        return null;
    }

    private TranslationServerExecutor getTransServerExecutor(
        String className) {
        for (TranslationServerExecutor executor : getTransServerExecutors()) {
            if (executor.getClass().getName().equals(className)) {
                return executor;
            }
        }
        return null;
    }

    private void initModel(ModelMap model) {
        model.addAttribute("repoSettingsPrefix", repoSettingsPrefix);
        model.addAttribute("transSettingsPrefix", transSettingsPrefix);

        model.addAttribute("repoPluginOptions", getRepoExecutors());
        model.addAttribute("selectedSrcPlugin", getRepoExecutors().get(0));

        model.addAttribute("serverPluginOptions", getTransServerExecutors());
        model.addAttribute("selectedTransPlugin", getTransServerExecutors().get(
            0));

        model.addAttribute("syncTypes", getSyncTypes());
        model.addAttribute("jobTypes", getJobTypes());
    }

    private List<TranslationServerExecutor> getTransServerExecutors() {
        if (transServerExecutors == null) {
            transServerExecutors =
                pluginsServiceImpl.getAvailableTransServerPlugins();
        }
        return transServerExecutors;
    }

    private List<RepoExecutor> getRepoExecutors() {
        if (repoExecutors == null) {
            repoExecutors =
                pluginsServiceImpl.getAvailableSourceRepoPlugins();
        }
        return repoExecutors;
    }

    private List<Field> getJobTypes() {
        if (jobTypes == null) {
            jobTypes = new ArrayList<>();
            jobTypes.add(
                new Field(JobConfig.Type.SYNC_TO_REPO.name(), messageResource
                    .getMessage("jsf.newJob.jobType.SyncToRepo.explanation"),
                    "", ""));
            jobTypes
                .add(new Field(JobConfig.Type.SYNC_TO_SERVER.name(),
                    messageResource
                        .getMessage(
                            "jsf.newJob.jobType.SyncToZanata.explanation"), "",
                    ""));
        }
        return jobTypes;
    }

    private List<Field> getSyncTypes() {
        if (syncTypes == null) {
            syncTypes = new ArrayList<>();
            syncTypes.add(new Field(SyncType.SOURCE.name(), messageResource
                .getMessage("jsf.newJob.syncType.sourceOnly.explanation"), "",
                ""));
            syncTypes
                .add(new Field(SyncType.TRANSLATIONS.name(), messageResource
                    .getMessage(
                        "jsf.newJob.syncType.translationsOnly.explanation"), "",
                    ""));
            syncTypes.add(new Field(SyncType.BOTH.name(), messageResource
                .getMessage("jsf.newJob.syncType.both.explanation"), "",
                ""));
        }
        return syncTypes;
    }
}
