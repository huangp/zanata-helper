package org.zanata.helper.repository;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.zanata.helper.annotation.ConfigurationDir;
import org.zanata.helper.component.AppConfiguration;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.model.JobStatusList;
import org.zanata.helper.model.JobType;
import org.zanata.helper.model.SyncWorkConfig;
import org.zanata.helper.util.HashUtil;
import org.zanata.helper.util.JsonUtil;

import com.google.common.base.Throwables;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.apache.commons.io.Charsets.UTF_8;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@ApplicationScoped
@Slf4j
@NoArgsConstructor
public class JobStatusRepository {

    @Inject
    private SyncWorkConfigSerializer serializer;


    private final static String SEPARATOR = ":";

    @Inject
    @ConfigurationDir
    private File configDirectory;

    private ReentrantLock lock = new ReentrantLock();

    public JobStatusList getJobStatusList(SyncWorkConfig config, JobType type) {
        Optional<JobStatusList> list = loadFromDisk(config, type);
        if(list.isPresent()) {
            return list.get();
        }
        return null;
    }

    public void saveJobStatus(SyncWorkConfig config, JobType type, JobStatus jobStatus) {
        String hash = generateJobStatusHash(config, type);

        try {
            lock.tryLock(5, TimeUnit.SECONDS);

            File workConfigFolder = workConfigFolder(config.getId());
            File jobStatusesFile = statusFile(config.getId(), hash);

            JobStatusList statusList;

            boolean made = workConfigFolder.mkdirs();
            if (!made && jobStatusesFile.exists()) {
                String existingJson =
                    FileUtils.readFileToString(jobStatusesFile, UTF_8);
                statusList = JsonUtil.fromJson(
                        existingJson, JobStatusList.class);
            } else {
                statusList = new JobStatusList();
            }
            statusList.add(0, jobStatus);

            FileUtils.write(jobStatusesFile, JsonUtil.toJson(statusList), UTF_8);
            log.info("JobStatus saved." + config.getName() + ":" + type);
        } catch (InterruptedException | IOException e) {
            throw Throwables.propagate(e);
        } finally {
            lock.unlock();
        }
    }

    private String generateJobStatusHash(SyncWorkConfig config, JobType type) {
        String json = serializer.toYaml(config);
        return HashUtil.generateHash(
                json + SEPARATOR + type.toString());
    }

    private File workConfigFolder(long id) {
        return new File(configDirectory, id + "");
    }

    private File statusFile(long id, String hash) {
        return new File(workConfigFolder(id), hash + ".json");
    }

    private Optional<JobStatusList> loadFromDisk(SyncWorkConfig config, JobType type) {
        String hash = generateJobStatusHash(config, type);
        File jobStatusFile = statusFile(config.getId(), hash);
        return loadJobStatus(jobStatusFile);
    }

    private Optional<JobStatusList> loadJobStatus(File jobStatusFile) {
        if (jobStatusFile.exists()) {
            try {
                String existingJson =
                    FileUtils.readFileToString(jobStatusFile, UTF_8);
                return Optional.of(
                    JsonUtil.fromJson(existingJson, JobStatusList.class));
            } catch (IOException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

}
