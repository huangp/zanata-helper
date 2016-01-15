package org.zanata.helper.model;

import org.zanata.helper.action.SyncWorkForm;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
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
    public SyncWorkConfig build(SyncWorkForm syncWorkForm) {
        JobConfig syncToServerConfig = new JobConfig(JobType.SERVER_SYNC,
                syncWorkForm.getSyncToServerCron(),
                syncWorkForm.getSyncToServerOption());

        JobConfig syncToRepoConfig = new JobConfig(JobType.REPO_SYNC,
                syncWorkForm.getSyncToRepoCron(),
                syncWorkForm.getSyncToRepoOption());

        return new SyncWorkConfig(idGenerator.nextID(),
                syncWorkForm.getName(),
                syncWorkForm.getDescription(),
                syncToServerConfig,
                syncToRepoConfig,
                syncWorkForm.getSrcRepoConfig(),
                syncWorkForm.getSrcRepoPluginName(),
                syncWorkForm.getTransServerConfig(),
                syncWorkForm.getTransServerPluginName(),
                syncWorkForm.getEncryptionKey(), false);
    }
}
