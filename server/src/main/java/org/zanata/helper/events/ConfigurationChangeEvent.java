package org.zanata.helper.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.zanata.helper.model.JobConfig;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public class ConfigurationChangeEvent extends ApplicationEvent {
    @Getter
    private JobConfig sync;

    public ConfigurationChangeEvent(Object source, JobConfig sync) {
        super(source);
        this.sync = sync;
    }
}
