package org.zanata.helper.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.zanata.helper.component.AppConfiguration;
import org.zanata.helper.model.SyncWorkConfig;
import org.zanata.helper.model.SystemSettings;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;

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

    private File configDirectory;

    @PostConstruct
    public void postConstruct() {
        configDirectory = appConfiguration.getConfigDirectory();
    }

    private Optional<SystemSettings> loadFromDisk() {
        File config = new File(configDirectory, SETTING_FILE);
        return Optional.of(fromYaml(config));
    }

    public void persist(SystemSettings systemSettings) {
        String yaml = toYaml(systemSettings);
    }

    public SystemSettings fromYaml(File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            YAML.setBeanAccess(BeanAccess.FIELD);
            SystemSettings settings = (SystemSettings) YAML.load(inputStream);
            return settings;
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public String toYaml(SystemSettings systemSettings) {
        YAML.setBeanAccess(BeanAccess.FIELD);
        return YAML.dump(systemSettings);
    }

}
