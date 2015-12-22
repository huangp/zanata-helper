package org.zanata.helper.common.plugin;

import org.zanata.helper.common.SyncType;

import java.util.Map;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public interface Plugin {

    /**
     * @return name of this plugin
     */
    String getName();

    /**
     * @return description of this plugin
     */
    String getDescription();

    /**
     * Return values for plugin
     */
    Map<String, String> getFields();
}
