package org.zanata.helper.events;

import lombok.Getter;
import org.zanata.helper.model.SyncWorkConfig;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public class ConfigurationChangeEvent {
    @Getter
    private SyncWorkConfig syncWorkConfig;

    public ConfigurationChangeEvent(SyncWorkConfig syncWorkConfig) {
        this.syncWorkConfig = syncWorkConfig;
    }
}
