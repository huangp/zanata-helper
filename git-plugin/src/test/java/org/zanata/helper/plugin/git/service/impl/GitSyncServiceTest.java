package org.zanata.helper.plugin.git.service.impl;

import java.io.File;
import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.zanata.helper.common.model.EncryptedField;
import org.zanata.helper.common.model.UsernamePasswordCredential;

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
                new GitSyncService(new UsernamePasswordCredential(
                        makeFieldWithValue(username),
                        makeFieldWithValue(password)));
        dest = temporaryFolder.newFolder();
    }

    private static EncryptedField makeFieldWithValue(String value) {
        EncryptedField encryptedField =
                new EncryptedField(null, null, null, null);
        encryptedField.setValue(value);
        return encryptedField;
    }

    @Test
    public void canCloneGitRepo() throws IOException {
        Assertions.assertThat(dest.listFiles()).isNullOrEmpty();

        syncService.cloneRepo("https://github.com/zanata/zanata-api.git", null,
                dest);

        Assertions.assertThat(dest.listFiles()).isNotEmpty();
    }
}
