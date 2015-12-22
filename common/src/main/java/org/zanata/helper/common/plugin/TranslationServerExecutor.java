package org.zanata.helper.common.plugin;

import org.zanata.helper.common.SyncType;

import java.io.File;
import java.io.Serializable;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public interface TranslationServerExecutor extends Plugin, Serializable {

    /**
     * Pull from server into given directory
     *
     * @param dir - directory to pull to
     * @param syncType - source only, translations only, or both
     */
    void pullFromServer(File dir, SyncType syncType);

    /**
     * Push files to server from given directory
     *
     * @param dir - directory to push from
     * @param syncType - source only, translations only, or both
     */
    void pushToServer(File dir, SyncType syncType);
}
