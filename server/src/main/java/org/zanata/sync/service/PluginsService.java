package org.zanata.sync.service;

import org.zanata.sync.common.plugin.RepoExecutor;
import org.zanata.sync.common.plugin.TranslationServerExecutor;
import org.zanata.sync.exception.UnableLoadPluginException;

import java.util.List;
import java.util.Map;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public interface PluginsService {

    List<RepoExecutor> getAvailableSourceRepoPlugins();

    RepoExecutor getNewSourceRepoPlugin(String className);

    RepoExecutor getNewSourceRepoPlugin(String className,
        Map<String, String> fields) throws UnableLoadPluginException;

    void init();
}
