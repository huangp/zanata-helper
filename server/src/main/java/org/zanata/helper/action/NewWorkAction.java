package org.zanata.helper.action;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.zanata.helper.api.impl.WorkResourceImpl;
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
@Named("newWorkAction")
public class NewWorkAction {

    @Inject
    private PluginsService pluginsServiceImpl;

    @Inject
    private WorkResourceImpl workResource;

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

    public String submitNewWork() {
        Response response = workResource.createWork(form);
        errors = (Map<String, String>) response.getEntity();
        if (!errors.isEmpty()) {
            return "/job/new.xhtml";
        } else {
            return "/home.xhtml";
        }
    }

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
                    msg.get("jsf.newWork.syncType.sourceOnly.explanation"), "",
                    ""));
            syncOptions
                    .add(new Field(SyncOption.TRANSLATIONS.name(), msg
                            .get("jsf.newWork.syncType.translationsOnly.explanation"),
                            "", ""));
            syncOptions.add(new Field(SyncOption.BOTH.name(), msg
                    .get("jsf.newWork.syncType.both.explanation"), "",""));
        }
        return syncOptions;
    }
}
