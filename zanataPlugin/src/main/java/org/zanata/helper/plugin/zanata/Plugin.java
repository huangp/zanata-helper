package org.zanata.helper.plugin.zanata;

import org.zanata.client.commands.pull.PullOptionsImpl;
import org.zanata.client.commands.push.PushOptionsImpl;
import org.zanata.helper.common.SyncType;
import org.zanata.helper.common.plugin.Field;
import org.zanata.helper.common.plugin.TranslationServerExecutor;
import org.zanata.helper.plugin.zanata.service.impl.ZanataSyncServiceImpl;

import java.io.File;
import java.util.Map;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public class Plugin extends TranslationServerExecutor {

    private final String name = "Zanata plugin";
    private final String description =
        "Zanata plugin for push and pull files";

    private final Field urlField = new Field("url", "URL");
    private final Field usernameField = new Field("username", "Username");
    private final Field apiKeyField = new Field("apiKey", "API Key");

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
    public void pullFromServer(File dir, SyncType syncType) {
        if (syncType.equals(SyncType.BOTH)) {
            pullOptions.setPullType("both");
        } else if (syncType.equals(SyncType.SOURCE)) {
            pullOptions.setPullType("source");
        } else {
            pullOptions.setPullType("trans");
        }

        zanataSyncService.pullFromZanata(dir.toPath());
    }

    @Override
    public void pushToServer(File dir, SyncType syncType) {
        if (syncType.equals(SyncType.BOTH)) {
            pushOptions.setPushType("both");
        } else if (syncType.equals(SyncType.SOURCE)) {
            pushOptions.setPushType("source");
        } else {
            pushOptions.setPushType("trans");
        }
        zanataSyncService.pushToZanata(dir.toPath());
    }
}
