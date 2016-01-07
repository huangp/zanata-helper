package org.zanata.helper.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.zanata.helper.model.SyncWorkConfig;
import com.google.common.base.Throwables;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public final class YamlUtil {

    private final static Yaml YAML = new Yaml();

    public static SyncWorkConfig generateJobConfig(String yamlString) {
        YAML.setBeanAccess(BeanAccess.FIELD);
        SyncWorkConfig config = (SyncWorkConfig) YAML.load(yamlString);
        return config;
    }

    public static SyncWorkConfig generateJobConfig(InputStream inputStream) {
        YAML.setBeanAccess(BeanAccess.FIELD);
        SyncWorkConfig config = (SyncWorkConfig) YAML.load(inputStream);
        return config;
    }

    public static String generateYaml(SyncWorkConfig syncWorkConfig) {
        YAML.setBeanAccess(BeanAccess.FIELD);
        return YAML.dump(syncWorkConfig);
    }

    public static void generateAndWriteYaml(SyncWorkConfig syncWorkConfig,
            Writer output) {
        YAML.setBeanAccess(BeanAccess.FIELD);
        YAML.dump(syncWorkConfig, output);
    }

    public static SyncWorkConfig generateJobConfig(File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            return generateJobConfig(inputStream);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}
