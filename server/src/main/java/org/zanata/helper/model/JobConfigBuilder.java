package org.zanata.helper.model;

import org.zanata.helper.action.JobForm;
import org.zanata.helper.common.model.SyncOption;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@NoArgsConstructor
public class JobConfigBuilder {

    private String name;
    private String description;
    private String cron;
    private SyncOption syncOption;
    private String sourceRepoExecutorName;
    private Map<String, String> sourceRepoConfig =
            new HashMap<String, String>();
    private String translationServerExecutorName;
    private Map<String, String> transServerConfig =
            new HashMap<String, String>();

    public JobConfigBuilder(JobForm jobForm) {
        this.name = jobForm.getName();
        this.description = jobForm.getDescription();
//        this.cron = jobForm.getCron();
//        this.syncOption = jobForm.getSyncOption();
//        this.sourceRepoExecutorName = jobForm.getSourceRepoExecutorName();
//        this.translationServerExecutorName =
//                jobForm.getTranslationServerExecutorName();
//        this.sourceRepoConfig = jobForm.getSourceRepoConfig();
        this.transServerConfig = jobForm.getTransServerConfig();
    }

    public JobConfigBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public JobConfigBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public JobConfigBuilder setCron(String cron) {
        this.cron = cron;
        return this;
    }

    public JobConfigBuilder setSyncType(SyncOption syncOption) {
        this.syncOption = syncOption;
        return this;
    }

    public JobConfigBuilder setSourceRepoConfig(
            Map<String, String> sourceRepoConfig) {
        this.sourceRepoConfig = sourceRepoConfig;
        return this;
    }

    public JobConfigBuilder setTransServerConfig(
            Map<String, String> transServerConfig) {
        this.transServerConfig = transServerConfig;
        return this;
    }

    public JobConfigBuilder setSourceRepoExecutorName(
            String sourceRepoExecutorName) {
        this.sourceRepoExecutorName = sourceRepoExecutorName;
        return this;
    }

    public JobConfigBuilder setTranslationServerExecutorName(
            String translationServerExecutorName) {
        this.translationServerExecutorName = translationServerExecutorName;
        return this;
    }

    public JobConfig build() {
        // TODO the two cron and options should be separated
        SyncConfig syncToServerConfig =
                new SyncConfig(SyncConfig.Type.SYNC_TO_SERVER, cron,
                        syncOption);
        SyncConfig syncToRepoConfig = new SyncConfig(
                SyncConfig.Type.SYNC_TO_REPO, cron, syncOption);
        return new JobConfig(name, description,
                syncToServerConfig, syncToRepoConfig, sourceRepoConfig,
                sourceRepoExecutorName, transServerConfig,
                translationServerExecutorName);
    }
}
