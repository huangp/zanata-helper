package org.zanata.helper.component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import javax.enterprise.context.Dependent;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.zanata.helper.model.SystemSettings;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Dependent
@Slf4j
public class AppConfiguration implements Serializable {
    private final static String CONFIG_DIR = "configuration";
    private final static String REPO_DIR = "repository";

//    @Inject
//    private SystemSettingsRepository systemSettingsRepository;

    @Getter
    private SystemSettings systemSettings;

    private File configDir;
    private File repoDir;

    @Getter
    private String buildVersion;

    @Getter
    private String buildInfo;

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
            List<String> fieldsNeedEncryption = ImmutableList.copyOf(
                Splitter.on(",").omitEmptyStrings().trimResults().split(fields));
            boolean deleteJobDir = Boolean.valueOf(
                properties.getProperty("delete.job.dir"));
            String storageDir = properties.getProperty("store.directory");

            systemSettings = new SystemSettings(storageDir, deleteJobDir,
                fieldsNeedEncryption);
            updateStorageDir();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * Load default settings from config.properties file
     */
    public void init() {
//        Optional<SystemSettings> settings =
//            systemSettingsRepository.loadFromDisk();
//
//        if (settings.isPresent()) {
//            log.info("Loading settings from storage");
//            this.systemSettings = settings.get();
//            updateStorageDir();
//        }
    }

    @VisibleForTesting
    public AppConfiguration(File configDir, File repoDir) {
        this.configDir = configDir;
        this.repoDir = repoDir;
    }

    private void updateStorageDir() {
        configDir = Paths.get(buildConfigDirectory()).toFile();
        checkDirectory(CONFIG_DIR, configDir);

        repoDir = Paths.get(buildRepoDirectory()).toFile();
        checkDirectory(REPO_DIR, repoDir);
    }

    public void updateSettings(String newDir, boolean deleteJobDir,
        List<String> fieldsNeedEncryption) {
        systemSettings =
            new SystemSettings(newDir, deleteJobDir, fieldsNeedEncryption);
        updateStorageDir();
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

    private String buildConfigDirectory() {
        return removeTrailingSlash(systemSettings.getStorageDir()) + File.separatorChar
                + CONFIG_DIR;
    }

    private String buildRepoDirectory() {
        return removeTrailingSlash(systemSettings.getStorageDir()) + File.separatorChar
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
        return systemSettings.getFieldsNeedEncryption();
    }

    public String getStorageDirectory() {
        return systemSettings.getStorageDir();
    }

    public boolean isDeleteJobDir() {
        return systemSettings.isDeleteJobDir();
    }
}
