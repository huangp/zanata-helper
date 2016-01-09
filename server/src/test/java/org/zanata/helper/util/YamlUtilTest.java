package org.zanata.helper.util;

import java.util.HashMap;

import org.junit.Test;
import org.zanata.helper.common.model.SyncOption;
import org.zanata.helper.model.JobType;
import org.zanata.helper.model.SyncWorkConfig;
import org.zanata.helper.model.JobConfig;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public class YamlUtilTest {

    @Test
    public void testRoundTrip() {
        SyncWorkConfig
                syncWorkConfig = new SyncWorkConfig(1L, "name", "description",
                new JobConfig(JobType.SERVER_SYNC, "",
                        SyncOption.SOURCE),
                new JobConfig(JobType.REPO_SYNC, "",
                        SyncOption.TRANSLATIONS),
                new HashMap<>(), "sourceRepoPluginName",
                new HashMap<>(),
                "translationServerExecutorName");

        String yamlString = YamlUtil.generateYaml(syncWorkConfig);
        SyncWorkConfig config = YamlUtil.generateJobConfig(yamlString);
        assertThat(yamlString).isEqualTo(YamlUtil.generateYaml(config));
    }
}
