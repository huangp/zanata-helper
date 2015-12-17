package org.zanata.sync.service.impl;

import java.io.File;
import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.hamcrest.CoreMatchers;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GitCloneSyncServiceTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private GitCloneSyncService syncService;
    private File dest;

    @Before
    public void setUp() throws Exception {
        String username = System.getProperty("github.user");
        String password = System.getProperty("github.password");
        Assume.assumeThat(
                "github username is provided as system property: github.user",
                username,
                CoreMatchers.notNullValue());
        Assume.assumeThat(
                "github password is provided as system property: github.password",
                password,
                CoreMatchers.notNullValue());
        syncService =
                new GitCloneSyncService(new UsernamePasswordCredential(username,
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
