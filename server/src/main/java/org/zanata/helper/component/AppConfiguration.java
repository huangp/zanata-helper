package org.zanata.helper.component;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 *
 */
@Component
public class AppConfiguration {
    @Getter
    @Value("${build.version}")
    private String buildVersion;

    @Getter
    @Value("${build.info}")
    private String buildInfo;

    @Getter
    @Setter
    @Value("${store.directory}")
    private String storageDirectory;
}
