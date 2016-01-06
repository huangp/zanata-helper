package org.zanata.helper.component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;
import javax.enterprise.context.Dependent;

import org.apache.commons.lang.StringUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import lombok.Getter;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Dependent
public class AppConfiguration {
    private final static String CONFIG_DIR = "configuration";
    private final static String repoDirectory = "repository";
    private final File configDir;
    private final File repoDir;

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

            configDir = Paths.get(buildConfigDirectory()).toFile();
            checkDirectory("configuration", configDir);

            repoDir = Paths.get(buildRepoDirectory()).toFile();
            checkDirectory("repo", repoDir);

        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @VisibleForTesting
    public AppConfiguration(File configDir, File repoDir) {
        this.configDir = configDir;
        this.repoDir = repoDir;
    }

    private static void checkDirectory(String nameOfDirectory, File directory) {
        if (!directory.exists()) {
            directory.mkdir();
        }
        Preconditions.checkState(directory.isDirectory(),
                "%s directory %s must be a directory",
                nameOfDirectory, directory);
        Preconditions.checkState(directory.canRead(),
                "%s directory %s must be readable", nameOfDirectory,
                directory);
        Preconditions.checkState(directory.canWrite(),
                "%s directory %s must be writable", nameOfDirectory,
                directory);
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

    private String buildConfigDirectory() {
        return removeTrailingSlash(storageDirectory) + File.separatorChar
                + CONFIG_DIR;
    }

    private String buildRepoDirectory() {
        return removeTrailingSlash(storageDirectory) + File.separatorChar
                + repoDirectory;
    }

    public File getConfigDirectory() {
        return configDir;
    }

    public File getRepoDirectory() {
        return repoDir;
    }

    private static String removeTrailingSlash(String string) {
        return StringUtils.chomp(string, "" + File.separatorChar);
    }
}
