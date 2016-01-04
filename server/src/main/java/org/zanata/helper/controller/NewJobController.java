package org.zanata.helper.controller;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.deltaspike.core.api.common.DeltaSpike;
import org.zanata.helper.api.APIController;
import org.zanata.helper.common.model.SyncOption;
import org.zanata.helper.common.model.Field;
import org.zanata.helper.common.plugin.RepoExecutor;
import org.zanata.helper.common.plugin.TranslationServerExecutor;
import org.zanata.helper.component.MessageResource;
import org.zanata.helper.model.JobConfig;
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
@RequestScoped
@Slf4j
public class NewJobController {

    @Inject
    private PluginsService pluginsServiceImpl;

    @Inject
    private MessageResource messageResource;

    @Inject @DeltaSpike
    private HttpServletRequest request;

    private List<RepoExecutor> repoExecutors;
    private List<TranslationServerExecutor> transServerExecutors;
    private List<Field> syncTypes;
    private List<Field> jobTypes;

//    @RequestMapping(value = "/jobs/new", method = RequestMethod.GET)
    public String getNewJobPage(/*@ModelAttribute("jobForm") */JobForm jobForm/*,
        ModelMap model*/) {
//        initModel(model, jobForm);
        return "new_job";
    }

//    @RequestMapping(value = "/jobs/new/settings", params = {
//        "selectedPlugin", "type" }, method = RequestMethod.GET)
    public String getSrcTransSettingsPage(/*ModelMap model,
        @RequestParam(value = "selectedPlugin") String className,
        @RequestParam(value = "type", defaultValue = "repo") String type*/) {

//        if (type.equals("trans")) {
//            TranslationServerExecutor selectedExecutor =
//                pluginsServiceImpl.getNewTransServerPlugin(className);
//            model.addAttribute("selectedTransPlugin", selectedExecutor);
//            return "view/trans_settings";
//        } else {
//            RepoExecutor selectedExecutor =
//                pluginsServiceImpl.getNewSourceRepoPlugin(className);
//            model.addAttribute("selectedSrcPlugin", selectedExecutor);
//            return "view/repo_settings";
//        }
        return "view/repo_settings";
    }

//    @RequestMapping(value = "/jobs/new", method = RequestMethod.POST)
    public String onSubmitNewJob(
            /*@Valid @ModelAttribute("jobForm") */JobForm jobForm /*,
        BindingResult result, ModelMap model, HttpServletRequest request*/) {
        for (Map.Entry<String, String[]> entry : request.getParameterMap()
            .entrySet()) {
            if (entry.getKey().startsWith(JobForm.repoSettingsPrefix)) {
                String newKey =
                    entry.getKey().replaceFirst(JobForm.repoSettingsPrefix, "");
                jobForm.getSourceRepoConfig().put(newKey, entry.getValue()[0]);
            } else if (entry.getKey().startsWith(JobForm.transSettingsPrefix)) {
                String newKey =
                    entry.getKey()
                        .replaceFirst(JobForm.transSettingsPrefix, "");
                jobForm.getTransServerConfig().put(newKey, entry.getValue()[0]);
            }
        }
        jobForm.setName(request.getParameterMap().get("name")[0]);
        jobForm.setDescription(request.getParameterMap().get("description")[0]);

//        Map<String, String> errors = createNewJob(jobForm);
//        if(!errors.isEmpty()) {
//            model.put("errors", errors);
//            initModel(model, jobForm);
//            return "new_job";
//        }
        return "index";
    }

//    private Map<String, String> createNewJob(JobForm form) {
//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
//            protected boolean hasError(HttpStatus statusCode) {
//                return false;
//            }});
//
//        String url = APIController.getBaseUrl() + APIController.API_ROOT +
//            APIController.JOB_ROOT;
//        Map<String, String> errors = new HashMap<>();
//        HttpEntity<JobForm> request = new HttpEntity<JobForm>(form);
//
//        ResponseEntity<Map> response =
//            restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
//
//        return response.getBody();
//    }

//    private void initModel(ModelMap model, JobForm jobForm) {
//        model.addAttribute("jobForm", jobForm);
//        model.addAttribute("repoSettingsPrefix", JobForm.repoSettingsPrefix);
//        model.addAttribute("transSettingsPrefix", JobForm.transSettingsPrefix);
//
//        model.addAttribute("repoPluginOptions", getRepoExecutors());
//        model.addAttribute("selectedSrcPlugin", getRepoExecutors().get(0));
//
//        model.addAttribute("serverPluginOptions", getTransServerExecutors());
//        model.addAttribute("selectedTransPlugin", getTransServerExecutors().get(
//            0));
//
//        model.addAttribute("syncTypes", getSyncTypes());
//        model.addAttribute("jobTypes", getJobTypes());
//    }

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
            syncTypes.add(new Field(SyncOption.SOURCE.name(), messageResource
                .getMessage("jsf.newJob.syncType.sourceOnly.explanation"), "",
                ""));
            syncTypes
                .add(new Field(SyncOption.TRANSLATIONS.name(), messageResource
                    .getMessage(
                        "jsf.newJob.syncType.translationsOnly.explanation"), "",
                    ""));
            syncTypes.add(new Field(SyncOption.BOTH.name(), messageResource
                .getMessage("jsf.newJob.syncType.both.explanation"), "",
                ""));
        }
        return syncTypes;
    }
}
