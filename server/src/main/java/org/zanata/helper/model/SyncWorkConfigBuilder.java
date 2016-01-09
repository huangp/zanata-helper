package org.zanata.helper.model;

import org.zanata.helper.action.SyncWorkForm;

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

    private String srcRepoPluginName;
    private Map<String, String> srcRepoPluginConfig =
            new HashMap<String, String>();

    private String transServerPluginName;
    private Map<String, String> transServerPluginConfig =
            new HashMap<String, String>();

    private JobConfig syncToServerConfig;
    private JobConfig syncToRepoConfig;

    public SyncWorkConfigBuilder(SyncWorkForm syncWorkForm) {
        this.name = syncWorkForm.getName();
        this.description = syncWorkForm.getDescription();

        this.syncToServerConfig = new JobConfig(JobType.SERVER_SYNC,
            syncWorkForm.getSyncToServerCron(),
            syncWorkForm.getSyncToServerOption());
        
        this.syncToRepoConfig = new JobConfig(JobType.REPO_SYNC,
            syncWorkForm.getSyncToRepoCron(),
            syncWorkForm.getSyncToRepoOption());

        this.srcRepoPluginName = syncWorkForm.getSrcRepoPluginName();
        this.transServerPluginName =
            syncWorkForm.getTransServerPluginName();
        this.srcRepoPluginConfig = syncWorkForm.getSrcRepoConfig();
        this.transServerPluginConfig = syncWorkForm.getTransServerConfig();
    }

    public SyncWorkConfigBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public SyncWorkConfigBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public SyncWorkConfigBuilder setSrcRepoPluginConfig(
        Map<String, String> srcRepoPluginConfig) {
        this.srcRepoPluginConfig = srcRepoPluginConfig;
        return this;
    }

    public SyncWorkConfigBuilder setTransServerPluginConfig(
            Map<String, String> transServerPluginConfig) {
        this.transServerPluginConfig = transServerPluginConfig;
        return this;
    }

    public SyncWorkConfigBuilder setSrcRepoPluginName(
        String srcRepoPluginName) {
        this.srcRepoPluginName = srcRepoPluginName;
        return this;
    }

    public SyncWorkConfigBuilder setTransServerPluginName(
        String transServerPluginName) {
        this.transServerPluginName = transServerPluginName;
        return this;
    }

    public SyncWorkConfig build() {
        return new SyncWorkConfig(name, description,
            syncToServerConfig, syncToRepoConfig, srcRepoPluginConfig,
            srcRepoPluginName, transServerPluginConfig,
            transServerPluginName);
    }
}
