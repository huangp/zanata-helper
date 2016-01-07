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
public class SyncWorkConfigBuilder {

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

    public SyncWorkConfigBuilder(JobForm jobForm) {
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

    public SyncWorkConfigBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public SyncWorkConfigBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public SyncWorkConfigBuilder setCron(String cron) {
        this.cron = cron;
        return this;
    }

    public SyncWorkConfigBuilder setSyncType(SyncOption syncOption) {
        this.syncOption = syncOption;
        return this;
    }

    public SyncWorkConfigBuilder setSourceRepoConfig(
            Map<String, String> sourceRepoConfig) {
        this.sourceRepoConfig = sourceRepoConfig;
        return this;
    }

    public SyncWorkConfigBuilder setTransServerConfig(
            Map<String, String> transServerConfig) {
        this.transServerConfig = transServerConfig;
        return this;
    }

    public SyncWorkConfigBuilder setSourceRepoExecutorName(
            String sourceRepoExecutorName) {
        this.sourceRepoExecutorName = sourceRepoExecutorName;
        return this;
    }

    public SyncWorkConfigBuilder setTranslationServerExecutorName(
            String translationServerExecutorName) {
        this.translationServerExecutorName = translationServerExecutorName;
        return this;
    }

    public SyncWorkConfig build() {
        // TODO the two cron and options should be separated
        JobConfig syncToServerConfig =
                new JobConfig(JobConfig.Type.SYNC_TO_SERVER, cron,
                        syncOption);
        JobConfig syncToRepoConfig = new JobConfig(
                JobConfig.Type.SYNC_TO_REPO, cron, syncOption);
        return new SyncWorkConfig(name, description,
                syncToServerConfig, syncToRepoConfig, sourceRepoConfig,
                sourceRepoExecutorName, transServerConfig,
                translationServerExecutorName);
    }
}
