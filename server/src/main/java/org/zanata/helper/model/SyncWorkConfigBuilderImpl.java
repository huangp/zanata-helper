package org.zanata.helper.model;

import org.zanata.helper.controller.SyncWorkForm;
import org.zanata.helper.util.CronHelper;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@RequestScoped
public class SyncWorkConfigBuilderImpl implements SyncWorkConfigBuilder {
    @Inject
    private SyncWorkIDGenerator idGenerator;

    @Override
    public SyncWorkConfig buildObject(SyncWorkForm syncWorkForm) {
        JobConfig syncToServerConfig = new JobConfig(JobType.SERVER_SYNC,
                syncWorkForm.getSyncToServerCron().getExpression(),
                syncWorkForm.getSyncToServerOption());

        JobConfig syncToRepoConfig = new JobConfig(JobType.REPO_SYNC,
                syncWorkForm.getSyncToRepoCron().getExpression(),
                syncWorkForm.getSyncToRepoOption());

        Long id = syncWorkForm.getId() == null ? idGenerator.nextID() :
            syncWorkForm.getId();

        return new SyncWorkConfig(id,
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

        form.setSyncToRepoOption(
            syncWorkConfig.getSyncToRepoConfig().getOption());
        form.setSyncToServerOption(
            syncWorkConfig.getSyncToServerConfig().getOption());

        form.setSyncToRepoCron(CronHelper.getTypeFromExpression(
                syncWorkConfig.getSyncToRepoConfig().getCron()));
        form.setSyncToServerCron(
                CronHelper.getTypeFromExpression(
                        syncWorkConfig.getSyncToServerConfig().getCron()));
        form.setSyncToRepoEnabled(syncWorkConfig.isSyncToRepoEnabled());
        form.setSyncToServerEnabled(syncWorkConfig.isSyncToServerEnabled());
        return form;
    }
}
