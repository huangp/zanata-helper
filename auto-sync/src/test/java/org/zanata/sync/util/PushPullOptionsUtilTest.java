package org.zanata.sync.util;

import java.io.File;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.junit.Assert.*;

public class PushPullOptionsUtilTest {

    @Test
    public void testFindProjectConfig() throws Exception {
        Optional<File> projectConfig =
                PushPullOptionsUtil.findProjectConfig(new File("."));

        Assertions.assertThat(projectConfig.isPresent()).isFalse();
    }
}
