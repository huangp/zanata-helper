package org.zanata.helper.plugin.git;

import org.zanata.helper.common.model.SyncType;
import org.zanata.helper.common.model.UsernamePasswordCredential;
import org.zanata.helper.common.exception.RepoSyncException;
import org.zanata.helper.common.model.Field;
import org.zanata.helper.common.plugin.RepoExecutor;
import org.zanata.helper.common.validator.StringValidator;
import org.zanata.helper.plugin.git.service.impl.GitSyncService;
import org.zanata.helper.common.validator.UrlValidator;

import java.io.File;
import java.util.Map;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 *
 * TODO: internationalise this class
 */
public class Plugin extends RepoExecutor {
    private final String name = "Git plugin";
    private final String description = Messages.getString("plugin.description");
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
        Field urlField = new Field("url", Messages.getString("field.url.label"),
            "https://github.com/zanata/zanata-server.git", null,
            new UrlValidator());
        Field branchField =
            new Field("branch", Messages.getString("field.branch.label"),
                "master", Messages.getString("field.branch.tooltip"));
        Field usernameField =
            new Field("username", Messages.getString("field.username.label"),
                "", Messages.getString("field.username.tooltip"),
                new StringValidator(null, null, true));
        Field apiKeyField =
            new Field("apiKey", Messages.getString("field.apiKey.label"), "",
                Messages.getString("field.apiKey.tooltip"));

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
    public void cloneRepo(File dir) throws RepoSyncException {
        gitSyncService.cloneRepo(getFields().get("url").getValue(), dir);
    }

    @Override
    public void pushToRepo(File dir, SyncType syncType)
        throws RepoSyncException {
        gitSyncService
            .syncTranslationToRepo(getFields().get("url").getValue(), dir);
    }


}
