package org.zanata.helper.action;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.zanata.helper.common.model.SyncOption;
import org.zanata.helper.common.model.Field;
import org.zanata.helper.common.plugin.RepoExecutor;
import org.zanata.helper.common.plugin.TranslationServerExecutor;
import org.zanata.helper.i18n.Messages;
import org.zanata.helper.service.PluginsService;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@RequestScoped
@Slf4j
@Named("newJobAction")
public class NewJobAction {

    @Inject
    private PluginsService pluginsServiceImpl;

    @Inject
    private Messages msg;

    private List<RepoExecutor> repoExecutors;
    private List<TranslationServerExecutor> transServerExecutors;
    private List<Field> syncOptions;

    @Getter
    private SyncWorkForm form = new SyncWorkForm();

    @Getter
    private Map<String, String> errors = new HashMap<>();

    @Getter
    private RepoExecutor selectedSrcPlugin;

    @Getter
    private TranslationServerExecutor selectedServerPlugin;

    @PostConstruct
    public void init() {
        if(!getRepoExecutors().isEmpty()) {
            selectedSrcPlugin = getRepoExecutors().get(0);
        }
        if(!getTransServerExecutors().isEmpty()) {
            selectedServerPlugin = getTransServerExecutors().get(0);
        }
    }

    public boolean hasError(String fieldName) {
        return errors.containsKey(fieldName);
    }

    public String getErrorMessage(String fieldName) {
        return errors.get(fieldName);
    }

    public List<Field> getSelectedSrcPluginFields() {
        if(selectedSrcPlugin != null) {
            return new ArrayList(selectedSrcPlugin.getFields().values());
        }
        return Collections.emptyList();
    }

    public List<Field> getSelectedServerPluginFields() {
        if(selectedServerPlugin != null) {
            return new ArrayList(selectedServerPlugin.getFields().values());
        }
        return Collections.emptyList();
    }

    public String onSubmitNewJob() {
        System.out.println("submit");
//        for (Map.Entry<String, String[]> entry : request.getParameterMap()
//            .entrySet()) {
//            if (entry.getKey().startsWith(JobForm.repoSettingsPrefix)) {
//                String newKey =
//                    entry.getKey().replaceFirst(JobForm.repoSettingsPrefix, "");
//                jobForm.getSourceRepoConfig().put(newKey, entry.getValue()[0]);
//            } else if (entry.getKey().startsWith(JobForm.transSettingsPrefix)) {
//                String newKey =
//                    entry.getKey()
//                        .replaceFirst(JobForm.transSettingsPrefix, "");
//                jobForm.getTransServerConfig().put(newKey, entry.getValue()[0]);
//            }
//        }
//        jobForm.setName(request.getParameterMap().get("name")[0]);
//        jobForm.setDescription(request.getParameterMap().get("description")[0]);

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
//        model.addAttribute("syncOptions", getSyncTypes());
//        model.addAttribute("jobTypes", getJobTypes());
//    }

    public List<TranslationServerExecutor> getTransServerExecutors() {
        if (transServerExecutors == null) {
            transServerExecutors =
                pluginsServiceImpl.getAvailableTransServerPlugins();
        }
        return transServerExecutors;
    }

    public List<RepoExecutor> getRepoExecutors() {
        if (repoExecutors == null) {
            repoExecutors =
                pluginsServiceImpl.getAvailableSourceRepoPlugins();
        }
        return repoExecutors;
    }

    public List<Field> getSyncOptions() {
        if (syncOptions == null) {
            syncOptions = new ArrayList<>();
            syncOptions.add(new Field(SyncOption.SOURCE.name(),
                    msg.get("jsf.newJob.syncType.sourceOnly.explanation"), "",
                    ""));
            syncOptions
                    .add(new Field(SyncOption.TRANSLATIONS.name(), msg
                            .get("jsf.newJob.syncType.translationsOnly.explanation"),
                            "", ""));
            syncOptions.add(new Field(SyncOption.BOTH.name(), msg
                    .get("jsf.newJob.syncType.both.explanation"), "",""));
        }
        return syncOptions;
    }
}
