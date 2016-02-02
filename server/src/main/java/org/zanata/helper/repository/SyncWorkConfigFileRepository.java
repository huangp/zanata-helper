/*
 * Copyright 2015, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.zanata.helper.repository;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.helper.annotation.ConfigurationDir;
import org.zanata.helper.model.SyncWorkConfig;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import static org.apache.commons.io.Charsets.UTF_8;

/**
 * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@ApplicationScoped
public class SyncWorkConfigFileRepository implements Repository<SyncWorkConfig, Long> {
    private static final Logger log =
            LoggerFactory.getLogger(SyncWorkConfigFileRepository.class);

    private static final String historyFileFormat = "archive-%s.yaml";

    private static final String LATEST_CONFIG_FILE = "current.yaml";

    private final Cache<Long, Optional<SyncWorkConfig>> cache = CacheBuilder
            .newBuilder()
            .build();

    @Inject
    @ConfigurationDir
    private File configDirectory;

    private ReentrantLock lock = new ReentrantLock();

    @Inject
    private SyncWorkConfigSerializer serializer;

    private AtomicLong latestId = new AtomicLong(0);

    @VisibleForTesting
    protected SyncWorkConfigFileRepository(File configDirectory,
            SyncWorkConfigSerializer serializer) {
        this.configDirectory = configDirectory;
        this.serializer = serializer;
    }

    public SyncWorkConfigFileRepository() {
    }

    @PostConstruct
    public void postConstruct() {
        latestId = new AtomicLong(largestStoredWorkId());
    }

    private long nextID() {
        return latestId.incrementAndGet();
    }

    @Override
    public Optional<SyncWorkConfig> load(Long id) {
        try {
            return cache.get(id, () -> loadFromDisk(id));
        } catch (ExecutionException e) {
            log.error("error loading config: {}", id, e);
            return Optional.empty();
        }
    }

    private Optional<SyncWorkConfig> loadFromDisk(long id) {
        File latestworkConfig = latestWorkConfig(id);
        return loadConfigFile(latestworkConfig);
    }

    private Optional<SyncWorkConfig> loadConfigFile(File configFile) {
        if (configFile.exists()) {
            return Optional.of(serializer.fromYaml(configFile));
        }
        return Optional.empty();
    }

    @Override
    public void persist(SyncWorkConfig syncWorkConfig) {
        try {
            lock.tryLock(5, TimeUnit.SECONDS);

            Long id = syncWorkConfig.getId();
            if (id == null) {
                syncWorkConfig.setId(nextID());
            }
            File workConfigFolder = workConfigFolder(syncWorkConfig.getId());
            File latestConfigFile = latestWorkConfig(syncWorkConfig.getId());


            boolean made = workConfigFolder.mkdirs();
            if (!made && latestConfigFile.exists()) {
                SyncWorkConfig current =
                        serializer.fromYaml(latestConfigFile);
                if (current.equalsExceptCreatedDate(syncWorkConfig)) {
                    log.info("SyncWorkConfig has not changed");
                    return;
                }
                // back up current work config
                FileUtils.moveFile(latestConfigFile,
                        new File(workConfigFolder,
                                String.format(historyFileFormat,
                                        new Date().getTime())));
            }
            // write new work config
            syncWorkConfig.onPersist();
            String incomingYaml = serializer.toYaml(syncWorkConfig);
            FileUtils.write(latestConfigFile, incomingYaml, UTF_8);
            cache.invalidate(syncWorkConfig.getId());
            log.info("SyncWorkConfig saved." + syncWorkConfig.getName());
        } catch (InterruptedException | IOException e) {
            throw Throwables.propagate(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean delete(Long id) {
        File workConfigFolder = workConfigFolder(id);
        try {
            FileUtils.deleteDirectory(workConfigFolder);
            cache.invalidate(id);
            log.info("SyncWorkConfig deleted." + id);
            return true;
        } catch (IOException e) {
            log.error("failed to delete the job config folder: {}",
                    workConfigFolder, e);
            return false;
        }
    }

    private File workConfigFolder(long id) {
        return new File(configDirectory, id + "");
    }

    private File latestWorkConfig(long id) {
        return new File(workConfigFolder(id), LATEST_CONFIG_FILE);
    }

    private static File latestWorkConfig(File jobConfigFolder) {
        return new File(jobConfigFolder, LATEST_CONFIG_FILE);
    }


    @Override
    public List<SyncWorkConfig> getHistory(Long id) {
        throw new UnsupportedOperationException("implement me");
    }

    /**
     * Our job id is incremental so the largest number in the config directory
     * will be the largest work id.
     *
     * @return largest work id or 0 if there is no work yet
     */
    private long largestStoredWorkId() {
        File[] jobConfigFolders = configDirectory.listFiles(File::isDirectory);
        Optional<String> largestJob =
                Arrays.stream(jobConfigFolders)
                        .map(File::getName)
                        .sorted(Collections.reverseOrder())
                        .findFirst();
        if (largestJob.isPresent()) {
            return Long.parseLong(largestJob.get());
        }
        return 0;
    }

    @Override
    public List<SyncWorkConfig> getAll() {
        List<SyncWorkConfig> allWorkConfig =
                Arrays.stream(configDirectory.listFiles(File::isDirectory))
                        .map(SyncWorkConfigFileRepository::latestWorkConfig)
                        .filter(file -> file.exists() && file.canRead())
                        .map(serializer::fromYaml)
                        .collect(Collectors.toList());
        allWorkConfig.forEach(
                config -> cache.put(config.getId(), Optional.of(config)));
        return allWorkConfig;
    }
}
