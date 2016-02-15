package org.zanata.sync.repository;

import java.io.File;
import java.util.HashMap;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.zanata.sync.common.model.SyncOption;
import org.zanata.sync.model.JobType;
import org.zanata.sync.model.SyncWorkConfig;
import org.zanata.sync.model.JobConfig;

public class SyncWorkConfigFileRepositoryTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private SyncWorkConfigFileRepository syncWorkConfigFileRepository;
    private File configDir;
    private SyncWorkConfigSerializer serializer =
            new SyncWorkConfigSerializerImpl();

    @Before
    public void setUp() throws Exception {
        File storeDir = temporaryFolder.newFolder();
        configDir = new File(storeDir, "config");
        syncWorkConfigFileRepository = new SyncWorkConfigFileRepository(configDir,
                serializer);

    }

    private static SyncWorkConfig makeJobConfig(long id, String name) {
        return new SyncWorkConfig(id, name, "description",
                new JobConfig(JobType.SERVER_SYNC, "",
                        SyncOption.SOURCE),
                new JobConfig(JobType.REPO_SYNC, "",
                        SyncOption.TRANSLATIONS),
                new HashMap<>(), "sourceRepoPluginName",
                new HashMap<>(),
                "translationServerExecutorName", null, true, true);
    }

    @Test
    public void testLoad() throws Exception {
        syncWorkConfigFileRepository.persist(makeJobConfig(1L, "name"));

        Optional<SyncWorkConfig> configOpt = syncWorkConfigFileRepository.load(1L);

        Assertions.assertThat(configOpt.isPresent()).isTrue();

        SyncWorkConfig config = configOpt.get();
        Assertions.assertThat(config).isEqualTo(config);
    }

    @Test
    public void canPersistForTheFirstTime() throws Exception {
        SyncWorkConfig syncWorkConfig = makeJobConfig(1L, "name");

        syncWorkConfigFileRepository.persist(syncWorkConfig);

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

        syncWorkConfigFileRepository.persist(syncWorkConfig);
        syncWorkConfigFileRepository.persist(syncWorkConfig);
        syncWorkConfigFileRepository.persist(syncWorkConfig);

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

        syncWorkConfigFileRepository.persist(syncWorkConfig1);
        syncWorkConfigFileRepository.persist(syncWorkConfig2);
        syncWorkConfigFileRepository.persist(syncWorkConfig3);

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
