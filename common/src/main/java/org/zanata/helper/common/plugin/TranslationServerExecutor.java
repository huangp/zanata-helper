package org.zanata.helper.common.plugin;

import lombok.Getter;
import org.zanata.helper.common.SyncType;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public abstract class TranslationServerExecutor implements Plugin, Serializable {

    @Getter
    public final Map<String, Field> fields = new HashMap<String, Field>();

    public TranslationServerExecutor(Map<String, String> fields) {
        initFields();
        if (fields != null) {
            fields.entrySet().stream()
                .filter(entry -> this.fields.containsKey(entry.getKey()))
                .forEach(entry -> {
                    this.fields.get(entry.getKey()).setValue(entry.getValue());
                });
        }
    }
    /**
     * Pull from server into given directory
     *
     * @param dir - directory to pull to
     * @param syncType - source only, translations only, or both
     */
    public abstract void pullFromServer(File dir, SyncType syncType);

    /**
     * Push files to server from given directory
     *
     * @param dir - directory to push from
     * @param syncType - source only, translations only, or both
     */
    public abstract void pushToServer(File dir, SyncType syncType);
}
