package org.zanata.helper.component;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 *
 */
@Component
public class AppConfiguration {
    private final static String CONFIG_DIR = "configuration";
    private final static String repoDirectory = "repository";

    @Getter
    @Value("${build.version}")
    private String buildVersion;

    @Getter
    @Value("${build.info}")
    private String buildInfo;

    //TODO: make this configurable
    /**
     * Must have read write access
     * i.e /tmp/zanataHelperRoot
     */
    @Setter
    @Value("${store.directory}")
    private String storageDirectory;

    public String getConfigDirectory() {
        return removeTrailingSlash(storageDirectory) + File.separatorChar
                + CONFIG_DIR;
    }

    public String getRepoDirectory() {
        return removeTrailingSlash(storageDirectory) + File.separatorChar
                + repoDirectory;
    }

   private static String removeTrailingSlash(String string) {
      return StringUtils.chomp(string, "" + File.separatorChar);
   }
}
