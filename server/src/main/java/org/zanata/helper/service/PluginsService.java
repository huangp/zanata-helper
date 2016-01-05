package org.zanata.helper.service;

import org.zanata.helper.common.plugin.RepoExecutor;
import org.zanata.helper.common.plugin.TranslationServerExecutor;
import org.zanata.helper.exception.UnableLoadPluginException;

import java.util.List;
import java.util.Map;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public interface PluginsService {

    List<RepoExecutor> getAvailableSourceRepoPlugins();

    List<TranslationServerExecutor> getAvailableTransServerPlugins();

    RepoExecutor getNewSourceRepoPlugin(String className);

    TranslationServerExecutor getNewTransServerPlugin(String className);

    RepoExecutor getNewSourceRepoPlugin(String className,
        Map<String, String> fields) throws UnableLoadPluginException;

    TranslationServerExecutor getNewTransServerPlugin(
        String className,
        Map<String, String> fields) throws UnableLoadPluginException;

    void init();
}
