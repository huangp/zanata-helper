package org.zanata.helper.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.zanata.helper.component.AppConfiguration;
import org.zanata.helper.model.SyncWorkConfig;
import org.zanata.helper.model.SystemSettings;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;

import static org.apache.commons.io.Charsets.UTF_8;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@ApplicationScoped
@Slf4j
public class SystemSettingsRepository {
    private static final String SETTING_FILE = "settings.yaml";

    private final static Yaml YAML = new Yaml();

    @Inject
    protected AppConfiguration appConfiguration;

    private ReentrantLock lock = new ReentrantLock();

    private File configDirectory;

    @PostConstruct
    public void postConstruct() {
        configDirectory = appConfiguration.getConfigDirectory();
    }

    public void persist(SystemSettings systemSettings) {
        try {
            lock.tryLock(5, TimeUnit.SECONDS);
            String incomingYaml = toYaml(systemSettings);

            File configFile = new File(configDirectory, SETTING_FILE);
            FileUtils.write(configFile, incomingYaml, UTF_8);
            log.info("System settings saved.");
        } catch (InterruptedException | IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public Optional<SystemSettings> loadFromDisk() {
        File config = new File(configDirectory, SETTING_FILE);
        return Optional.of(fromYaml(config));
    }

    private SystemSettings fromYaml(File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            YAML.setBeanAccess(BeanAccess.FIELD);
            SystemSettings settings = (SystemSettings) YAML.load(inputStream);
            return settings;
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private String toYaml(SystemSettings systemSettings) {
        YAML.setBeanAccess(BeanAccess.FIELD);
        return YAML.dump(systemSettings);
    }

}
