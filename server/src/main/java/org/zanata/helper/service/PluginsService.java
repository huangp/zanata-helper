package org.zanata.helper.service;

import org.zanata.helper.common.plugin.SourceRepoExecutor;
import org.zanata.helper.common.plugin.TranslationServerExecutor;
import org.zanata.helper.exception.UnableLoadPluginException;

import java.util.Map;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public interface PluginsService {

    SourceRepoExecutor getNewSourceRepoPlugin(String className,
        Map<String, String> fields) throws UnableLoadPluginException;

    TranslationServerExecutor getNewTransServerPlugin(
        String className,
        Map<String, String> fields) throws UnableLoadPluginException;
}
