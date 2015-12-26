package org.zanata.helper.common.plugin;

import lombok.Getter;
import org.zanata.helper.common.SyncType;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public abstract class SourceRepoExecutor implements Plugin, Serializable {

    @Getter
    public final HashMap<String, Field> fields =
        new LinkedHashMap<String, Field>();

    public SourceRepoExecutor(Map<String, String> fields) {
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
     * Clone from source repository
     *
     * @param dir - directory to clone to
     */
    public abstract void cloneRepo(File dir);

    /**
     * Push changes to source repository
     *
     * @param dir - directory to push from
     * @param syncType - source only, translations only, or both
     * @return push successful
     */
    public abstract void pushToRepo(File dir, SyncType syncType);
}
