package org.zanata.helper.sync.service.impl;

import java.io.File;
import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GitSyncServiceTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private GitSyncService syncService;
    private File dest;

    @Before
    public void setUp() throws Exception {
        String username = JunitAssumptions.assumeGitUsernameExists();
        String password = JunitAssumptions.assumeGitPasswordExists();
        syncService =
                new GitSyncService(new UsernamePasswordCredential(username,
                        password));
        dest = temporaryFolder.newFolder();
    }

    @Test
    public void canCloneGitRepo() throws IOException {
        Assertions.assertThat(dest.listFiles()).isNullOrEmpty();

        syncService.cloneRepo("https://github.com/zanata/zanata-api.git",
                dest);

        Assertions.assertThat(dest.listFiles()).isNotEmpty();
    }
}
