package org.zanata.sync.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.zanata.sync.dao.SystemSettingsDAO;
import org.zanata.sync.model.SystemSettings;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import lombok.Getter;

/**
 * User can pass in {@link AppConfiguration#SYSTEM_SETTINGS_PATH} as system
 * property to locate settings.yaml file. Alternatively user can just place the
 * file in current working directory. If none of the above resolves to a file,
 * it will try to load built-in config.properties and use values there as
 * default. User can update it in admin page.
 *
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@ApplicationScoped
@Slf4j
@NoArgsConstructor
public class AppConfiguration implements Serializable {

    public static final String SYSTEM_SETTINGS_PATH = "systemSettings";
    public static final String DB_FILE_PATH = "db";
    public static final String DB_FILE_NAME = "zanata-sync-db";

    private static final String REPO_DIR = "repository";
    private static final String SETTING_FILE_NAME = "settings.yaml";
    private static final Yaml YAML = new Yaml();

    @Inject
    private SystemSettingsDAO systemSettingsDAO;

    @Getter
    private SystemSettings systemSettings;

    @Getter
    private String buildVersion;

    @Getter
    private String buildInfo;

    @Getter
    private File repoDir;

    private File settingsFile;

    @PostConstruct
    public void init() {
        Properties infoProps = loadBuildInProps("info.properties");
        buildInfo = infoProps.getProperty("build.info");
        buildVersion = infoProps.getProperty("build.version");
        loadSystemSettings();
    }

    private static Properties loadBuildInProps(String propFileName) {
        ClassLoader contextClassLoader =
                Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = contextClassLoader
                        .getResourceAsStream(propFileName)) {
            Properties buildInProperties = new Properties();
            buildInProperties.load(inputStream);
            return buildInProperties;
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @VisibleForTesting
    public AppConfiguration(File repoDir) {
        this.repoDir = repoDir;
    }

    public void updateSettingsAndSave(boolean deleteJobDir,
            List<String> fieldsNeedEncryption) {
        systemSettings.updateSettings(deleteJobDir, fieldsNeedEncryption);
        systemSettingsDAO.persist(systemSettings);
    }

    private void loadSystemSettings() {
        Optional<SystemSettings> optional = loadSettingsFromSystemPath();
        if(optional.isPresent()) {
            this.systemSettings = optional.get();
        } else {
            log.info("load settings from database");
            this.systemSettings = systemSettingsDAO.getSystemSettings();
        }

        this.repoDir = Paths.get(buildRepoDirectory()).toFile();
        checkDirectory(REPO_DIR, repoDir);
    }

    private Optional<SystemSettings> loadSettingsFromSystemPath() {
        // first we try system property
        String settingsPath = System.getProperty(SYSTEM_SETTINGS_PATH);
        if (settingsPath == null) {
            // second we try loading it from current working directory
            settingsFile = Paths.get(".").toAbsolutePath().normalize()
                    .resolve(SETTING_FILE_NAME).toFile();
        } else {
            settingsFile = new File(settingsPath, SETTING_FILE_NAME);
        }
        if (settingsFile.exists()) {
            log.info("load settings file from: {}", settingsFile);
            return fromYaml(settingsFile);
        }
        return Optional.absent();
    }

    private Optional<SystemSettings> fromYaml(File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            YAML.setBeanAccess(BeanAccess.FIELD);
            SystemSettings settings = (SystemSettings) YAML.load(inputStream);
            return Optional.of(settings);
        } catch (IOException e) {
            log.warn("failed to read settings file {}", file);
            return Optional.absent();
        }
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

    private String buildRepoDirectory() {
        return removeTrailingSlash(systemSettings.getStorageDir())
                + File.separatorChar + REPO_DIR;
    }

    private static String removeTrailingSlash(String string) {
        return StringUtils.chomp(string, "" + File.separatorChar);
    }

    public List<String> getFieldsNeedEncryption() {
        return systemSettings.getFieldsNeedEncryption();
    }

    public String getStorageDir() {
        return systemSettings.getStorageDir();
    }

    public boolean isDeleteJobDir() {
        return systemSettings.isDeleteJobDir();
    }

    public String getDBFilePath() {
        return FilenameUtils.concat(DB_FILE_PATH, DB_FILE_NAME);
    }
}
