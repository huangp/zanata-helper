package org.zanata.helper.common.plugin;

import org.zanata.helper.common.SyncType;

import java.io.File;
import java.io.Serializable;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public interface SourceRepoExecutor extends Plugin, Serializable {

    /**
     * Clone from source repository
     *
     * @param basedir - directory to clone to
     */
    void cloneRepo(File dir);

    /**
     * Push changes to source repository
     *
     * @param basedir - directory to push from
     * @param syncType - source only, translations only, or both
     * @return push successful
     */
    void pushToRepo(File dir, SyncType syncType);
}
