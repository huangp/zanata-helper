package org.zanata.helper.component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import javax.enterprise.context.Dependent;

import org.apache.commons.lang.StringUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Dependent
public class AppConfiguration implements Serializable {
    private final static String CONFIG_DIR = "configuration";
    private final static String REPO_DIR = "repository";

    private File configDir;
    private File repoDir;


    private List<String> fieldsNeedEncryption;

    @Getter
    @Setter
    private boolean deleteJobDir;

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

            String fields =
                    properties.getProperty("fields.need.encryption", "");
            fieldsNeedEncryption = ImmutableList.copyOf(Splitter.on(",").omitEmptyStrings().trimResults()
                   .split(fields));

            deleteJobDir = Boolean.valueOf(properties.getProperty("delete.job.dir"));

            updateStorageDir(properties.getProperty("store.directory"));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @VisibleForTesting
    public AppConfiguration(File configDir, File repoDir) {
        this.configDir = configDir;
        this.repoDir = repoDir;
    }

    public void updateStorageDir(String newDir) {
        this.storageDirectory = newDir;

        configDir = Paths.get(buildConfigDirectory()).toFile();
        checkDirectory(CONFIG_DIR, configDir);

        repoDir = Paths.get(buildRepoDirectory()).toFile();
        checkDirectory(REPO_DIR, repoDir);
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

    /**
     * Must have read write access
     * i.e /tmp/zanataHelperRoot
     */
    @Getter
    private String storageDirectory;

    private String buildConfigDirectory() {
        return removeTrailingSlash(storageDirectory) + File.separatorChar
                + CONFIG_DIR;
    }

    private String buildRepoDirectory() {
        return removeTrailingSlash(storageDirectory) + File.separatorChar
                + REPO_DIR;
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

    public List<String> getFieldsNeedEncryption() {
        return fieldsNeedEncryption;
    }
}
