package org.zanata.helper.component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.enterprise.context.Dependent;

import org.apache.commons.lang.StringUtils;
import com.google.common.base.Throwables;
import lombok.Getter;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Dependent
public class AppConfiguration {
    private final static String CONFIG_DIR = "configuration";
    private final static String repoDirectory = "repository";

    public AppConfiguration() {
        ClassLoader contextClassLoader =
                Thread.currentThread().getContextClassLoader();
        try (InputStream config = contextClassLoader
                .getResourceAsStream("config.properties");
                InputStream info = contextClassLoader
                        .getResourceAsStream("info.properties")) {
            Properties properties = new Properties();
            properties.load(info);
            buildInfo = properties.getProperty("build.info");
            buildVersion = properties.getProperty("build.version");
            properties.load(config);
            storageDirectory = properties.getProperty("store.directory");

        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Getter
    private String buildVersion;

    @Getter
    private String buildInfo;

    //TODO: make this configurable
    /**
     * Must have read write access
     * i.e /tmp/zanataHelperRoot
     */
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
