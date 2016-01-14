package org.zanata.helper.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.zanata.helper.common.model.SyncOption;
import org.zanata.helper.model.JobType;
import org.zanata.helper.model.SyncWorkConfig;
import org.zanata.helper.model.JobConfig;

public class SyncWorkConfigRepositoryTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private SyncWorkConfigRepository syncWorkConfigRepository;
    private File configDir;

    @Before
    public void setUp() throws Exception {
        File storeDir = temporaryFolder.newFolder();
        configDir = new File(storeDir, "config");
        syncWorkConfigRepository = new SyncWorkConfigRepository(configDir);

    }

    private static SyncWorkConfig makeJobConfig(long id, String name) {
        return new SyncWorkConfig(id, name, "description",
                new JobConfig(JobType.SERVER_SYNC, "",
                        SyncOption.SOURCE),
                new JobConfig(JobType.REPO_SYNC, "",
                        SyncOption.TRANSLATIONS),
                new HashMap<>(), "sourceRepoPluginName",
                new HashMap<>(),
                "translationServerExecutorName", null);
    }

    @Test
    public void testLoad() throws Exception {
        syncWorkConfigRepository.persist(makeJobConfig(1L, "name"));

        Optional<SyncWorkConfig> configOpt = syncWorkConfigRepository.load(1L);

        Assertions.assertThat(configOpt.isPresent()).isTrue();

        SyncWorkConfig config = configOpt.get();
        Assertions.assertThat(config).isEqualTo(config);
    }

    @Test
    public void canPersistForTheFirstTime() throws Exception {
        SyncWorkConfig syncWorkConfig = makeJobConfig(1L, "name");

        syncWorkConfigRepository.persist(syncWorkConfig);

        File jobConfigFolder = asSubFile(configDir, "1");
        Assertions.assertThat(configDir.listFiles(File::isDirectory))
                .containsExactly(jobConfigFolder);

        File yamlFile = asSubFile(jobConfigFolder, "current.yaml");
        Assertions.assertThat(jobConfigFolder.listFiles(File::isFile))
                .containsExactly(yamlFile);
        Assertions.assertThat(yamlFile).exists();
    }

    private static File asSubFile(File current, String subfolder) {
        return new File(current, subfolder);
    }

    @Test
    public void canPersistSameFileMultipleTimes() {
        SyncWorkConfig syncWorkConfig = makeJobConfig(1L, "name");

        syncWorkConfigRepository.persist(syncWorkConfig);
        syncWorkConfigRepository.persist(syncWorkConfig);
        syncWorkConfigRepository.persist(syncWorkConfig);

        File jobConfigFolder = asSubFile(configDir, "1");
        File yamlFile = asSubFile(jobConfigFolder, "current.yaml");

        Assertions.assertThat(jobConfigFolder.listFiles(File::isFile))
                .containsExactly(yamlFile);
        Assertions.assertThat(yamlFile).exists();
    }

    @Test
    public void canPersistDifferentConfigMultipleTimesAndKeepHistory() {
        // same id but different name
        SyncWorkConfig syncWorkConfig1 = makeJobConfig(1L, "name1");
        SyncWorkConfig syncWorkConfig2 = makeJobConfig(1L, "name2");
        SyncWorkConfig syncWorkConfig3 = makeJobConfig(1L, "name3");

        syncWorkConfigRepository.persist(syncWorkConfig1);
        syncWorkConfigRepository.persist(syncWorkConfig2);
        syncWorkConfigRepository.persist(syncWorkConfig3);

        File jobConfigFolder = asSubFile(configDir, "1");
        File latestFile = asSubFile(jobConfigFolder, "current.yaml");

        File[] files = jobConfigFolder.listFiles(File::isFile);
        Assertions.assertThat(files).contains(latestFile);
        Assertions.assertThat(files).hasSize(3);

        Assertions.assertThat(
                new SyncWorkConfigSerializerImpl().fromYaml(latestFile)
                        .getName()).isEqualTo("name3");

    }

    @Test
    public void testDelete() throws Exception {

    }


    @Test
    public void testGetHistory() throws Exception {

    }
}
