package org.zanata.helper.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.enterprise.context.ApplicationScoped;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.zanata.helper.model.SystemSettings;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import static org.apache.commons.io.Charsets.UTF_8;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@ApplicationScoped
@Slf4j
public class AppConfiguration implements Serializable {
    private final static String CONFIG_DIR = "configuration";
    private final static String REPO_DIR = "repository";
    private static final String SETTING_FILE = "settings.yaml";
    private final static Yaml YAML = new Yaml();

    private ReentrantLock lock = new ReentrantLock();

    @Getter
    private SystemSettings systemSettings;

    @Getter
    private String buildVersion;

    @Getter
    private String buildInfo;

    @Getter
    private File configDir;

    @Getter
    private File repoDir;

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

            Optional<SystemSettings> systemSettings = loadSettingsFromYaml();
            if(systemSettings.isPresent()) {
                this.systemSettings = systemSettings.get();
            } else {
                String fields =
                    properties.getProperty("fields.need.encryption", "");
                List<String> fieldsNeedEncryption = ImmutableList.copyOf(
                    Splitter.on(",").omitEmptyStrings().trimResults().split(fields));
                boolean deleteJobDir = Boolean.valueOf(
                    properties.getProperty("delete.job.dir"));
                String storageDir = properties.getProperty("store.directory");
                updateSettings(storageDir, deleteJobDir, fieldsNeedEncryption);
            }
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @VisibleForTesting
    public AppConfiguration(File configDir, File repoDir) {
        this.configDir = configDir;
        this.repoDir = repoDir;
    }

    public void updateSettings(String newStorageDir, boolean deleteJobDir,
        List<String> fieldsNeedEncryption) {
        systemSettings =
            new SystemSettings(newStorageDir, deleteJobDir, fieldsNeedEncryption);
        updateStorageDir();
    }

    /**
     * Save current settings.
     * Use {@link #updateSettings} to update current settings.
     */
    public void saveCurrentSettings() {
        try {
            lock.tryLock(5, TimeUnit.SECONDS);
            String incomingYaml = toYaml(systemSettings);

            File configFile = new File(configDir, SETTING_FILE);
            FileUtils.write(configFile, incomingYaml, UTF_8);
            log.info("System settings saved.");
        } catch (InterruptedException | IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private Optional<SystemSettings> loadSettingsFromYaml() {
        File config = new File(configDir, SETTING_FILE);
        return Optional.ofNullable(fromYaml(config));
    }

    private SystemSettings fromYaml(File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            YAML.setBeanAccess(BeanAccess.FIELD);
            SystemSettings settings = (SystemSettings) YAML.load(inputStream);
            return settings;
        } catch (IOException e) {
            log.warn("No settings file found, " + SETTING_FILE);
        } finally {
            return null;
        }
    }

    private String toYaml(SystemSettings systemSettings) {
        YAML.setBeanAccess(BeanAccess.FIELD);
        return YAML.dump(systemSettings);
    }

    private void updateStorageDir() {
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

    private String buildConfigDirectory() {
        return removeTrailingSlash(systemSettings.getStorageDir()) + File.separatorChar
                + CONFIG_DIR;
    }

    private String buildRepoDirectory() {
        return removeTrailingSlash(systemSettings.getStorageDir()) + File.separatorChar
                + REPO_DIR;
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
}
