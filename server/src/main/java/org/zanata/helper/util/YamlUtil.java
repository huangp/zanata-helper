package org.zanata.helper.util;

import java.io.InputStream;
import java.io.Writer;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.zanata.helper.model.JobConfig_test;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public final class YamlUtil {

    private final static Yaml YAML = new Yaml();

    public static JobConfig_test generateJobConfig(String yamlString) {
        YAML.setBeanAccess(BeanAccess.FIELD);
        JobConfig_test config = (JobConfig_test) YAML.load(yamlString);
        return config;
    }

    public static JobConfig_test generateJobConfig(InputStream inputStream) {
        YAML.setBeanAccess(BeanAccess.FIELD);
        JobConfig_test config = (JobConfig_test) YAML.load(inputStream);
        return config;
    }

    public static String generateYaml(JobConfig_test jobConfig) {
        YAML.setBeanAccess(BeanAccess.FIELD);
        return YAML.dump(jobConfig);
    }

    public static void generateAndWriteYaml(JobConfig_test jobConfig,
            Writer output) {
        YAML.setBeanAccess(BeanAccess.FIELD);
        YAML.dump(jobConfig, output);
    }
}
