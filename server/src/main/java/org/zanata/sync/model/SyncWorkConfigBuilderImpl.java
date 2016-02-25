package org.zanata.sync.model;

import org.zanata.sync.common.model.SyncOption;
import org.zanata.sync.controller.SyncWorkForm;
import org.zanata.sync.util.CronHelper;

import javax.enterprise.context.RequestScoped;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@RequestScoped
public class SyncWorkConfigBuilderImpl implements SyncWorkConfigBuilder {

    @Override
    public SyncWorkConfig buildObject(SyncWorkForm syncWorkForm) {
        JobConfig syncToServerConfig = null;
        if (syncWorkForm.getSyncToServerCron() != null
                && syncWorkForm.getSyncToServerOption() != null) {
            syncToServerConfig = new JobConfig(JobType.SERVER_SYNC,
                    syncWorkForm.getSyncToServerCron().getExpression(),
                    syncWorkForm.getSyncToServerOption());
        }

        JobConfig syncToRepoConfig = null;
        if( syncWorkForm.getSyncToRepoCron() != null) {
            syncToRepoConfig = new JobConfig(JobType.REPO_SYNC,
                syncWorkForm.getSyncToRepoCron().getExpression(),
                // repo sync should only sync translations
                SyncOption.TRANSLATIONS);
        }

        return new SyncWorkConfig(syncWorkForm.getId(),
            syncWorkForm.getName(),
            syncWorkForm.getDescription(),
            syncToServerConfig,
            syncToRepoConfig,
            syncWorkForm.getSrcRepoPluginConfig(),
            syncWorkForm.getSrcRepoPluginName(),
            syncWorkForm.getTransServerPluginConfig(),
            syncWorkForm.getTransServerPluginName(),
            syncWorkForm.getEncryptionKey(),
            syncWorkForm.isSyncToServerEnabled(),
            syncWorkForm.isSyncToRepoEnabled());
    }

    @Override
    public SyncWorkForm buildForm(SyncWorkConfig syncWorkConfig) {

        SyncWorkForm form = new SyncWorkForm();
        form.setId(syncWorkConfig.getId());
        form.setName(syncWorkConfig.getName());
        form.setDescription(syncWorkConfig.getDescription());
        form.setEncryptionKey(syncWorkConfig.getEncryptionKey());
        form.setSrcRepoPluginName(syncWorkConfig.getSrcRepoPluginName());
        form.setTransServerPluginName(syncWorkConfig.getTransServerPluginName());

        form.setSrcRepoPluginConfig(syncWorkConfig.getSrcRepoPluginConfig());
        form.setTransServerPluginConfig(
            syncWorkConfig.getTransServerPluginConfig());

        if(syncWorkConfig.getSyncToServerConfig() != null) {
            form.setSyncToServerOption(
                syncWorkConfig.getSyncToServerConfig().getOption());

            form.setSyncToServerCron(
                CronHelper.getTypeFromExpression(
                    syncWorkConfig.getSyncToServerConfig().getCron()));
        }

        if(syncWorkConfig.getSyncToRepoConfig() != null) {
            form.setSyncToRepoCron(CronHelper.getTypeFromExpression(
                syncWorkConfig.getSyncToRepoConfig().getCron()));
        }

        form.setSyncToRepoEnabled(syncWorkConfig.isSyncToRepoEnabled());
        form.setSyncToServerEnabled(syncWorkConfig.isSyncToServerEnabled());
        return form;
    }
}
