package org.zanata.helper.events;

import lombok.Getter;
import org.zanata.helper.model.JobConfig;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public class ConfigurationChangeEvent {
    @Getter
    private JobConfig jobConfig;

    public ConfigurationChangeEvent(JobConfig jobConfig) {
        this.jobConfig = jobConfig;
    }
}
