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
import org.zanata.helper.model.JobConfig;
import org.zanata.helper.model.SyncConfig;
import org.zanata.helper.util.YamlUtil;
import com.google.common.base.Throwables;

public class JobConfigRepositoryTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private JobConfigRepository jobConfigRepository;
    private File configDir;

    @Before
    public void setUp() throws Exception {
        File storeDir = temporaryFolder.newFolder();
        configDir = new File(storeDir, "config");
        jobConfigRepository = new JobConfigRepository(configDir);

    }

    private static JobConfig makeJobConfig(long id, String name) {
        return new JobConfig(id, name, "description",
                new SyncConfig(SyncConfig.Type.SYNC_TO_SERVER, "",
                        SyncOption.SOURCE),
                new SyncConfig(SyncConfig.Type.SYNC_TO_REPO, "",
                        SyncOption.TRANSLATIONS),
                new HashMap<>(), "sourceRepoPluginName",
                new HashMap<>(),
                "translationServerExecutorName");
    }

    @Test
    public void testLoad() throws Exception {
        jobConfigRepository.persist(makeJobConfig(1L, "name"));

        Optional<JobConfig> configOpt = jobConfigRepository.load(1L);

        Assertions.assertThat(configOpt.isPresent()).isTrue();

        JobConfig config = configOpt.get();
        Assertions.assertThat(config).isEqualTo(config);
    }

    @Test
    public void canPersistForTheFirstTime() throws Exception {
        JobConfig jobConfig = makeJobConfig(1L, "name");

        jobConfigRepository.persist(jobConfig);

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
        JobConfig jobConfig = makeJobConfig(1L, "name");

        jobConfigRepository.persist(jobConfig);
        jobConfigRepository.persist(jobConfig);
        jobConfigRepository.persist(jobConfig);

        File jobConfigFolder = asSubFile(configDir, "1");
        File yamlFile = asSubFile(jobConfigFolder, "current.yaml");

        Assertions.assertThat(jobConfigFolder.listFiles(File::isFile))
                .containsExactly(yamlFile);
        Assertions.assertThat(yamlFile).exists();
    }

    @Test
    public void canPersistDifferentConfigMultipleTimesAndKeepHistory() {
        // same id but different name
        JobConfig jobConfig1 = makeJobConfig(1L, "name1");
        JobConfig jobConfig2 = makeJobConfig(1L, "name2");
        JobConfig jobConfig3 = makeJobConfig(1L, "name3");

        jobConfigRepository.persist(jobConfig1);
        jobConfigRepository.persist(jobConfig2);
        jobConfigRepository.persist(jobConfig3);

        File jobConfigFolder = asSubFile(configDir, "1");
        File latestFile = asSubFile(jobConfigFolder, "current.yaml");

        File[] files = jobConfigFolder.listFiles(File::isFile);
        Assertions.assertThat(files).contains(latestFile);
        Assertions.assertThat(files).hasSize(3);

        try (InputStream inputStream = new FileInputStream(latestFile)) {
            Assertions.assertThat(
                    YamlUtil.generateJobConfig(inputStream).getName())
                    .isEqualTo("name3");
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Test
    public void testDelete() throws Exception {

    }


    @Test
    public void testGetHistory() throws Exception {

    }
}
