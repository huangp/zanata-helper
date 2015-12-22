package org.zanata.helper.plugin.git;

import org.zanata.helper.common.SyncType;
import org.zanata.helper.common.UsernamePasswordCredential;
import org.zanata.helper.common.plugin.SourceRepoExecutor;
import org.zanata.helper.plugin.git.service.impl.GitSyncService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public class Plugin implements SourceRepoExecutor {
    private final String name = "Git plugin";
    private final String description =
        "Git plugin for push and pull to git repository";

    private final Map<String, String> fields = new HashMap<>();

    private final GitSyncService gitSyncService;

    public Plugin(Map<String, String> fields) {
        gitSyncService = new GitSyncService(
            new UsernamePasswordCredential(fields.get("Username"),
                fields.get("ApiKey")));

        fields.put("Username", "");
        fields.put("ApiKey", "");
        fields.put("URL", "");
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
        gitSyncService.cloneRepo(getFields().get("URL"), dir);
    }

    @Override
    public void pushToRepo(File dir, SyncType syncType) {
        gitSyncService.syncTranslationToRepo(getFields().get("URL"), dir);
    }

    @Override
    public Map<String, String> getFields() {
        return fields;
    }
}
