package org.zanata.helper.plugin.zanata;

import org.zanata.client.commands.pull.PullOptionsImpl;
import org.zanata.client.commands.push.PushOptionsImpl;
import org.zanata.helper.common.annotation.TranslationServerPlugin;
import org.zanata.helper.common.model.SyncOption;
import org.zanata.helper.common.model.Field;
import org.zanata.helper.common.annotation.RepoPlugin;
import org.zanata.helper.common.plugin.TranslationServerExecutor;
import org.zanata.helper.common.validator.StringValidator;
import org.zanata.helper.common.validator.UrlValidator;
import org.zanata.helper.plugin.zanata.exception.ZanataSyncException;
import org.zanata.helper.plugin.zanata.service.impl.ZanataSyncServiceImpl;

import java.io.File;
import java.util.Map;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
@TranslationServerPlugin
public class Plugin extends TranslationServerExecutor {

    private final String name = "Zanata plugin";
    private final String description = Messages.getString("plugin.description");
    private final ZanataSyncServiceImpl zanataSyncService;

    private PushOptionsImpl pushOptions;
    private PullOptionsImpl pullOptions;

    public Plugin(Map<String, String> fields) {
        super(fields);
        pushOptions = new PushOptionsImpl();
        pullOptions = new PullOptionsImpl();
        zanataSyncService =
            new ZanataSyncServiceImpl(pullOptions, pushOptions,
                this.fields.get("username").getValue(),
                this.fields.get("apiKey").getValue());
    }

    @Override
    public void initFields() {
        Field urlField = new Field("url", Messages.getString("field.url.label"),
            "https://translate.zanata.org/zanata/iteration/view/zanata-server/master",
            null, new UrlValidator());
        Field usernameField =
            new Field("username", Messages.getString("field.username.label"),
                "", Messages.getString("field.username.tooltip"),
                new StringValidator(null, null, true));
        Field apiKeyField =
            new Field("apiKey", Messages.getString("field.apiKey.label"), "",
                Messages.getString("field.apiKey.tooltip"));

        fields.put(urlField.getKey(), urlField);
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
    public Map<String, Field> getFields() {
        return fields;
    }

    @Override
    public void pullFromServer(File dir, SyncOption syncOption) throws
        ZanataSyncException {
        if (syncOption.equals(SyncOption.BOTH)) {
            pullOptions.setPullType("both");
        } else if (syncOption.equals(SyncOption.SOURCE)) {
            pullOptions.setPullType("source");
        } else {
            pullOptions.setPullType("trans");
        }

        zanataSyncService.pullFromZanata(dir.toPath());
    }

    @Override
    public void pushToServer(File dir, SyncOption syncOption)
        throws ZanataSyncException {
        if (syncOption.equals(SyncOption.BOTH)) {
            pushOptions.setPushType("both");
        } else if (syncOption.equals(SyncOption.SOURCE)) {
            pushOptions.setPushType("source");
        } else {
            pushOptions.setPushType("trans");
        }
        zanataSyncService.pushToZanata(dir.toPath());
    }
}
