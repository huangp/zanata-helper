//package org.zanata.sync.sync.service.impl;
//
//import java.io.File;
//import java.net.MalformedURLException;
//
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.TemporaryFolder;
//import org.zanata.client.commands.pull.PullOptionsImpl;
//import org.zanata.client.commands.push.PushOptionsImpl;
//
///**
// * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
// */
//public class SyncRepoITCase {
//    @Rule
//    public TemporaryFolder temporaryFolder = new TemporaryFolder();
//
//    private GitSyncService repoSyncService;
//    private PushOptionsImpl pushOptions;
//    private PullOptionsImpl pullOptions;
//    private ZanataSyncServiceImpl zanataSyncService;
//
//    @Before
//    public void setUp() throws MalformedURLException {
//        String username = JunitAssumptions.assumeGitUsernameExists();
//        String password = JunitAssumptions.assumeGitPasswordExists();
//        JunitAssumptions.assumeZanataUrlExists();
//
//        repoSyncService = new GitSyncService(
//                new UsernamePasswordCredential(username, password));
//
//
//        pushOptions = new PushOptionsImpl();
//        pullOptions = new PullOptionsImpl();
//
//        zanataSyncService =
//                new ZanataSyncServiceImpl(pullOptions, pushOptions, "admin",
//                        "a5965da87d26a94c3531abc2b5602b93");
//
//    }
//
//    @Test
//    public void canSyncToZanataThenSyncToRepo() throws Exception {
//        String githubRepo = JunitAssumptions.assumeGithubRepoUrlExists();
//        File baseDir = temporaryFolder.newFolder();
//
//        // 1. clone repo
//        repoSyncService.cloneRepo(githubRepo, baseDir);
//
//        // 2. push to zanata
//        pushOptions.setPushType("both");
//        zanataSyncService.pushToZanata(baseDir.toPath());
//
//        // 3. pull from zanata
//        pullOptions.setPullType("trans");
//        zanataSyncService.pullFromZanata(baseDir.toPath());
//
//        // 4. push back to repo
//        repoSyncService.syncTranslationToRepo(githubRepo, baseDir);
//    }
//}
