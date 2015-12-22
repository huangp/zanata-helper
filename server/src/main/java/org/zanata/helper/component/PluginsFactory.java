package org.zanata.helper.component;

import org.zanata.helper.common.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public final class PluginsFactory {
    private final static Map<String, Plugin> pluginMap =
        new HashMap<String, Plugin>();

    /**
     * Initiate all plugins available
     */
    public static void initiate() {

//        pluginMap.put()
    }

    public static Plugin getPlugin(String name) {
        pluginMap.put("test", null);
        return pluginMap.get(name);
    }
}
