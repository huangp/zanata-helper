package org.zanata.helper.util;

import java.util.HashMap;

import org.junit.Test;
import org.zanata.helper.common.model.SyncOption;
import org.zanata.helper.model.JobConfig_test;
import org.zanata.helper.model.SyncConfig;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public class YamlUtilTest {

    @Test
    public void testRoundTrip() {
        JobConfig_test jobConfig = new JobConfig_test(1L, "name", "description",
                new SyncConfig(SyncConfig.Type.SYNC_TO_SERVER, "",
                        SyncOption.SOURCE),
                new SyncConfig(SyncConfig.Type.SYNC_TO_REPO, "",
                        SyncOption.TRANSLATIONS),
                new HashMap<>(), "sourceRepoPluginName",
                new HashMap<>(),
                "translationServerExecutorName");

        String yamlString = YamlUtil.generateYaml(jobConfig);
        JobConfig_test config = YamlUtil.generateJobConfig(yamlString);
        assertThat(yamlString).isEqualTo(YamlUtil.generateYaml(config));
    }
}
