package org.zanata.sync.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.zanata.sync.annotation.ConfigurationDir;
import org.zanata.sync.model.SystemSettings;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import static org.apache.commons.io.Charsets.UTF_8;

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
    private final static String CONFIG_DIR = "configuration";
    private final static String REPO_DIR = "repository";
    private static final String SETTING_FILE_NAME = "settings.yaml";

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

    private File settingsFile;

    @PostConstruct
    public void init() {
        Properties infoProps = loadBuildInProps("info.properties");
        buildInfo = infoProps.getProperty("build.info");
        buildVersion = infoProps.getProperty("build.version");

        Optional<SystemSettings> systemSettings = loadSettings();
        if(systemSettings.isPresent()) {
            this.systemSettings = systemSettings.get();
        } else {
            String message = "can not locate " + SETTING_FILE_NAME +
                    ". Please specify its path as " + SYSTEM_SETTINGS_PATH +
                    " or put it under current working directory";
            throw new IllegalStateException(message);
        }

        updateLogbackConfig();
    }

    private void updateLogbackConfig() {
        File logbackConfigurationFile = getLogbackConfigurationFile();
        if (logbackConfigurationFile == null) {
            return;
        }
        // assume SLF4J is bound to logback in the current environment
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            // Call context.reset() to clear any previous configuration, e.g. default
            // configuration. For multi-step configuration, omit calling context.reset().
            context.reset();
            configurator.doConfigure(logbackConfigurationFile);
        } catch (JoranException je) {
            // StatusPrinter will handle this
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
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
    public AppConfiguration(File configDir, File repoDir) {
        this.configDir = configDir;
        this.repoDir = repoDir;
    }

    public void updateSettingsAndSave(boolean deleteJobDir,
            List<String> fieldsNeedEncryption, File logbackConfigFile) {
        systemSettings.updateSettings(deleteJobDir, fieldsNeedEncryption,
                logbackConfigFile);
        updateLogbackConfig();
        saveCurrentSettings();
    }

    /**
     * Save current settings.
     * Use {@link #updateSettingsAndSave} to update current settings.
     */
    private void saveCurrentSettings() {
        try {
            lock.tryLock(5, TimeUnit.SECONDS);
            String incomingYaml = toYaml(systemSettings);

            if (settingsFile.exists()) {
                FileUtils.write(settingsFile, incomingYaml, UTF_8);
                log.info("System settings saved to {}", settingsFile);
            } else {
                File configFile = new File(configDir, SETTING_FILE_NAME);
                FileUtils.write(configFile, incomingYaml, UTF_8);
                log.info("System settings saved to {}", configFile);
            }

        } catch (InterruptedException | IOException e) {
            throw Throwables.propagate(e);
        }
    }

    // TODO: make system property for storage directory mandatory, and remove from admin screen
    private Optional<SystemSettings> loadSettings() {
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
            log.info("=========== loading settings file from: {}", settingsFile);
            return Optional.ofNullable(fromYaml(settingsFile));
        }

        // if none of above works, we fall back to built-in settings
        log.warn(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        log.warn(">>>>>> Using default settings. Please use system property {} for a more permanent settings", SYSTEM_SETTINGS_PATH);
        log.warn(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        Properties buildInProps = loadBuildInProps("config.properties");
        String fields =
                buildInProps.getProperty("fields.need.encryption", "");
        List<String> fieldsNeedEncryption = ImmutableList.copyOf(
                Splitter.on(",").omitEmptyStrings().trimResults().split(fields));
        boolean deleteJobDir = Boolean.valueOf(
                buildInProps.getProperty("delete.job.dir"));
        String storageDir = buildInProps.getProperty("store.directory");

        File logbackConfigFile = new File(buildInProps.getProperty("logback.configurationFile"));
        if (!logbackConfigFile.isAbsolute()) {
            URL url = Thread.currentThread().getContextClassLoader()
                    .getResource(logbackConfigFile.getPath());
            if (url != null) {
                logbackConfigFile = new File(url.getFile());
            } else {
                log.warn("can not locate {}", logbackConfigFile);
                logbackConfigFile = null;
            }
        }

        SystemSettings systemSettings =
                new SystemSettings(storageDir, deleteJobDir, fieldsNeedEncryption, logbackConfigFile);

        return Optional.of(systemSettings);

    }

    private SystemSettings fromYaml(File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            YAML.setBeanAccess(BeanAccess.FIELD);
            SystemSettings settings = (SystemSettings) YAML.load(inputStream);
            return settings;
        } catch (IOException e) {
            log.warn("faile to read settings file {}", file);
            return null;
        }
    }

    private String toYaml(SystemSettings systemSettings) {
        YAML.setBeanAccess(BeanAccess.FIELD);
        return YAML.dump(systemSettings);
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

    public File getLogbackConfigurationFile() {
        return systemSettings.getLogbackConfigFile();
    }

    public String getStorageDir() {
        return systemSettings.getStorageDir();
    }

    public boolean isDeleteJobDir() {
        return systemSettings.isDeleteJobDir();
    }


    @Produces
    @Dependent
    @ConfigurationDir
    protected File getConfigurationDir() {
        return configDir;
    }

}
