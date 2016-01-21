package org.zanata.helper.controller;

import lombok.Getter;
import org.zanata.helper.common.model.Field;
import org.zanata.helper.common.model.FieldType;
import org.zanata.helper.common.model.SyncOption;
import org.zanata.helper.common.plugin.RepoExecutor;
import org.zanata.helper.common.plugin.TranslationServerExecutor;
import org.zanata.helper.i18n.Messages;
import org.zanata.helper.service.PluginsService;
import org.zanata.helper.util.CronHelper;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

import com.google.common.collect.Lists;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public abstract class HasFormController implements Serializable {
    @Getter
    protected Map<String, String> errors = new HashMap<>();

    @Getter
    protected RepoExecutor selectedSrcPlugin;

    @Getter
    protected TranslationServerExecutor selectedServerPlugin;

    protected SyncWorkForm form;

    protected List<RepoExecutor> repoExecutors;

    protected List<TranslationServerExecutor> transServerExecutors;

    protected List<Field> syncOptions;

    abstract protected Messages getMessage();

    abstract protected PluginsService getPluginService();

    abstract public String onSubmit() throws IOException;

    abstract public SyncWorkForm getForm();

    @PostConstruct
    public void init() {
        if(!getRepoExecutors().isEmpty()) {
            selectedSrcPlugin = getRepoExecutors().get(0);
        }
        if(!getTransServerExecutors().isEmpty()) {
            selectedServerPlugin = getTransServerExecutors().get(0);
        }
    }

    public List<RepoExecutor> getRepoExecutors() {
        if (repoExecutors == null) {
            repoExecutors =
                getPluginService().getAvailableSourceRepoPlugins();
        }
        return repoExecutors;
    }

    public List<TranslationServerExecutor> getTransServerExecutors() {
        if (transServerExecutors == null) {
            transServerExecutors =
                getPluginService().getAvailableTransServerPlugins();
        }
        return transServerExecutors;
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

    public List<Field> getSyncOptions() {
        if (syncOptions == null) {
            syncOptions = new ArrayList<>();
            syncOptions.add(new Field(SyncOption.SOURCE.name(),
                    getMessage()
                            .get("jsf.work.syncType.sourceOnly.explanation"),
                    "", "", FieldType.TEXT));
            syncOptions
                    .add(new Field(SyncOption.TRANSLATIONS.name(), getMessage()
                            .get("jsf.work.syncType.translationsOnly.explanation"),
                            "", "", FieldType.TEXT));
            syncOptions.add(new Field(SyncOption.BOTH.name(), getMessage()
                    .get("jsf.work.syncType.both.explanation"), "", "",
                    FieldType.TEXT));
        }
        return syncOptions;
    }

    public boolean hasError(String fieldName) {
        return errors.containsKey(fieldName);
    }

    public String getErrorMessage(String fieldName) {
        return errors.get(fieldName);
    }

    public List<CronHelper.CronType> getSupportedIntervals() {
        return Lists.newArrayList(CronHelper.CronType.values());
    }
}
