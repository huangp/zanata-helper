package org.zanata.helper.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.zanata.helper.model.JobConfig;
import com.google.common.base.Throwables;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public final class YamlUtil {

    private final static Yaml YAML = new Yaml();

    public static JobConfig generateJobConfig(String yamlString) {
        YAML.setBeanAccess(BeanAccess.FIELD);
        JobConfig config = (JobConfig) YAML.load(yamlString);
        return config;
    }

    public static JobConfig generateJobConfig(InputStream inputStream) {
        YAML.setBeanAccess(BeanAccess.FIELD);
        JobConfig config = (JobConfig) YAML.load(inputStream);
        return config;
    }

    public static String generateYaml(JobConfig jobConfig) {
        YAML.setBeanAccess(BeanAccess.FIELD);
        return YAML.dump(jobConfig);
    }

    public static void generateAndWriteYaml(JobConfig jobConfig,
            Writer output) {
        YAML.setBeanAccess(BeanAccess.FIELD);
        YAML.dump(jobConfig, output);
    }

    public static JobConfig generateJobConfig(File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            return generateJobConfig(inputStream);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}
