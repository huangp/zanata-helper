package org.zanata.sync.service.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;

import org.hamcrest.CoreMatchers;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.zanata.client.commands.push.PushOptionsImpl;
import org.zanata.common.ProjectType;

import static org.junit.Assert.*;

public class PushServiceImplITTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private PushServiceImpl pushService;
    private PushOptionsImpl pushOptions;
    private GitCloneSyncService syncService;
    private File dest;

    @Before
    public void setUp() throws Exception {
        assumeEnvironmentIsUp();
        pushOptions = new PushOptionsImpl();
        pushOptions.setBatchMode(true);
        pushOptions.setUsername("admin");
        pushOptions.setKey("a5965da87d26a94c3531abc2b5602b93");

        pushService = new PushServiceImpl(pushOptions);

        dest = temporaryFolder.newFolder();
    }

    private void assumeEnvironmentIsUp() throws Exception {
        String zanataUrl = System.getProperty("zanata.url");
        Assume.assumeThat(
                "zanata test server is running and url is given as system property: zanata.url",
                zanataUrl,
                CoreMatchers.notNullValue());
        pushOptions.setUrl(URI.create(zanataUrl).toURL());

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

    }

    @Test
    public void canSyncRepoAndPushIfProjectVersionAlreadyExists()
            throws Exception {
        syncService.cloneRepo("https://github.com/zanata/zanata-client.git",
                dest);

        pushOptions.setProj("zanata-client");
        pushOptions.setProjectVersion("master");
        pushOptions.setProjectType(ProjectType.Properties.name().toLowerCase());
        File srcDir = new File(dest,
                "zanata-client-commands/src/main/resources");
        pushOptions.setSrcDir(srcDir);
        pushOptions.setTransDir(srcDir);
        pushOptions.setPushType("both");

        pushService.push();
    }
}
