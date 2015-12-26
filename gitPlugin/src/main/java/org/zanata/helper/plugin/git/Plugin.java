package org.zanata.helper.plugin.git;

import org.zanata.helper.common.SyncType;
import org.zanata.helper.common.UsernamePasswordCredential;
import org.zanata.helper.common.plugin.Field;
import org.zanata.helper.common.plugin.SourceRepoExecutor;
import org.zanata.helper.plugin.git.service.impl.GitSyncService;

import java.io.File;
import java.util.Map;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 *
 * TODO: internationalise this class
 */
public class Plugin extends SourceRepoExecutor {
    private final String name = "Git plugin";
    private final String description =
        "Git plugin for push and pull to git repository";

    private final GitSyncService gitSyncService;

    public Plugin(Map<String, String> fields) {
        super(fields);

        gitSyncService = new GitSyncService(
            new UsernamePasswordCredential(
                this.fields.get("username").getValue(),
                this.fields.get("apiKey").getValue()));
    }

    @Override
    public void initFields() {
        Field urlField = new Field("url", "URL", "https://github.com/zanata/zanata-server.git", null);
        Field branchField = new Field("branch", "Branch", "master", "Default to master branch");
        Field usernameField = new Field("username", "Username", "", "Username for repository");
        Field apiKeyField = new Field("apiKey", "API Key", "", "API key for repository");

        fields.put(urlField.getKey(), urlField);
        fields.put(branchField.getKey(), branchField);
        fields.put(usernameField.getKey(), usernameField);
        fields.put(apiKeyField.getKey(), apiKeyField);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void cloneRepo(File dir) {
        gitSyncService.cloneRepo(getFields().get("url").getValue(), dir);
    }

    @Override
    public void pushToRepo(File dir, SyncType syncType) {
        gitSyncService
            .syncTranslationToRepo(getFields().get("url").getValue(), dir);
    }


}
