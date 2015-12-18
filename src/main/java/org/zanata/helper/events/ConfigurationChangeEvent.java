package org.zanata.helper.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.zanata.helper.model.Sync;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public class ConfigurationChangeEvent extends ApplicationEvent {
    @Getter
    private Sync sync;

    public ConfigurationChangeEvent(Object source, Sync sync) {
        super(source);
        this.sync = sync;
    }
}
