package org.zanata.helper.plugin.zanata;

import org.zanata.client.commands.pull.PullOptionsImpl;
import org.zanata.client.commands.push.PushOptionsImpl;
import org.zanata.helper.common.SyncType;
import org.zanata.helper.common.plugin.TranslationServerExecutor;
import org.zanata.helper.plugin.zanata.service.impl.ZanataSyncServiceImpl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public class Plugin implements TranslationServerExecutor {

    private final String name = "Zanata plugin";
    private final String description =
        "Zanata plugin for push and pull files";


    private final Map<String, String> fields = new HashMap<>();

    private final ZanataSyncServiceImpl zanataSyncService;

    private PushOptionsImpl pushOptions;
    private PullOptionsImpl pullOptions;


    public Plugin() {
        pushOptions = new PushOptionsImpl();
        pullOptions = new PullOptionsImpl();
        zanataSyncService =
            new ZanataSyncServiceImpl(pullOptions, pushOptions,
                fields.get("Username"), fields.get("ApiKey"));

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
    public Map<String, String> getFields() {
        return fields;
    }

    @Override
    public void pullFromServer(File dir, SyncType syncType) {
        if(syncType.equals(SyncType.BOTH)) {
            pullOptions.setPullType("both");
        } else if(syncType.equals(SyncType.SOURCE)) {
            pullOptions.setPullType("source");
        } else {
            pullOptions.setPullType("trans");
        }

        zanataSyncService.pullFromZanata(dir.toPath());
    }

    @Override
    public void pushToServer(File dir, SyncType syncType) {
        if(syncType.equals(SyncType.BOTH)) {
            pushOptions.setPushType("both");
        } else if(syncType.equals(SyncType.SOURCE)) {
            pushOptions.setPushType("source");
        } else {
            pushOptions.setPushType("trans");
        }
        zanataSyncService.pushToZanata(dir.toPath());
    }
}
