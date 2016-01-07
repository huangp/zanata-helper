package org.zanata.helper.events;

import lombok.Getter;
import org.zanata.helper.model.JobConfig_test;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public class ConfigurationChangeEvent {
    @Getter
    private JobConfig_test jobConfig;

    public ConfigurationChangeEvent(JobConfig_test jobConfig) {
        this.jobConfig = jobConfig;
    }
}
