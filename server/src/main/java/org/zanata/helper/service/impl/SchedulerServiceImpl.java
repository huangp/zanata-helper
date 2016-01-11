package org.zanata.helper.service.impl;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import org.apache.deltaspike.core.api.lifecycle.Initialized;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.UnableToInterruptJobException;

import org.zanata.helper.events.ConfigurationChangeEvent;
import org.zanata.helper.events.JobProgressEvent;
import org.zanata.helper.events.JobRunStartsEvent;
import org.zanata.helper.events.JobRunCompletedEvent;
import org.zanata.helper.exception.JobNotFoundException;
import org.zanata.helper.model.JobType;
import org.zanata.helper.model.SyncWorkConfig;
import org.zanata.helper.model.JobSummary;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.model.WorkSummary;
import org.zanata.helper.quartz.CronTrigger;
import org.zanata.helper.component.AppConfiguration;
import org.zanata.helper.quartz.JobConfigListener;
import org.zanata.helper.repository.SyncWorkConfigRepository;
import org.zanata.helper.service.PluginsService;
import org.zanata.helper.service.SchedulerService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@ApplicationScoped
@Slf4j
public class SchedulerServiceImpl implements SchedulerService {
    @Inject
    private AppConfiguration appConfiguration;

    @Inject
    private PluginsService pluginsServiceImpl;

    @Inject
    private SyncWorkConfigRepository syncWorkConfigRepository;

    @Inject
    private JobConfigListener triggerListener;

    //TODO: move this to dao or workRepository
    private Map<Long, JobKeys> jobConfigKeyMap =
            Collections.synchronizedMap(Maps.newHashMap());

    private CronTrigger cronTrigger;

    // TODO: database connection, thread count, scheduler, queue, event
    public void onStartUp(@Observes @Initialized ServletContext servletContext) {
        log.info("=====================================================");
        log.info("=====================================================");
        log.info("================Zanata helper starts=================");
        log.info("== build :            {}-{}",
                appConfiguration.getBuildVersion(),
                appConfiguration.getBuildInfo());
        log.info("== repo directory:    {}",
                appConfiguration.getRepoDirectory());
        log.info("== config directory:  {}",
                appConfiguration.getConfigDirectory());
        log.info("=====================================================");
        log.info("=====================================================");

        pluginsServiceImpl.init();

        log.info("Initialising jobs...");

        List<SyncWorkConfig> syncWorkConfigs = getJobs();
        try {
            cronTrigger = new CronTrigger(appConfiguration,
                pluginsServiceImpl, triggerListener);
            for (SyncWorkConfig syncWorkConfig : syncWorkConfigs) {
                scheduleJob(syncWorkConfig);
            }
        } catch (SchedulerException e) {
            throw Throwables.propagate(e);
        }

        log.info("Initialised {} jobs.", syncWorkConfigs.size());
    }

    //TODO: need to validate all config
    private List<SyncWorkConfig> getJobs() {
        return syncWorkConfigRepository.getAllWorks();
    }

    public void onApplicationEvent(@Observes ConfigurationChangeEvent event) {
        Long id = event.getSyncWorkConfig().getId();
        try {
            cronTrigger.reschedule(
                jobConfigKeyMap.get(id).repoSyncJobKey,
                event.getSyncWorkConfig().getSyncToRepoConfig().getCron(),
                event.getSyncWorkConfig().getId(), JobType.REPO_SYNC);

            cronTrigger.reschedule(
                jobConfigKeyMap.get(id).serverSyncJobKey,
                event.getSyncWorkConfig().getSyncToServerConfig().getCron(),
                event.getSyncWorkConfig().getId(), JobType.SERVER_SYNC);
        } catch (SchedulerException e) {
            log.error("Error rescheduling job:" + e.getMessage());
        }
    }

    // TODO: update job details
    public void onJobProgressUpdate(@Observes JobProgressEvent event) {
        Optional<SyncWorkConfig> syncWorkConfigOpt =
                syncWorkConfigRepository.load(event.getId());
        if (syncWorkConfigOpt.isPresent()) {
            SyncWorkConfig syncWorkConfig = syncWorkConfigOpt.get();
            log.info(syncWorkConfig.getName() + ":" + event.getDescription());
        }
    }

    // TODO: fire websocket event
    public void onJobStarts(@Observes JobRunStartsEvent event) {
        Optional<SyncWorkConfig> syncWorkConfigOpt =
                syncWorkConfigRepository.load(event.getId());
        if (syncWorkConfigOpt.isPresent()) {
            SyncWorkConfig syncWorkConfig = syncWorkConfigOpt.get();
            log.debug("Job : " + syncWorkConfig.getName() + " starting.");
        }
    }

    // TODO: update database record, create history
    public void onJobCompleted(@Observes JobRunCompletedEvent event)
        throws JobNotFoundException, SchedulerException {
        Optional<SyncWorkConfig> syncWorkConfigOpt =
                syncWorkConfigRepository.load(event.getId());
        if (syncWorkConfigOpt.isPresent()) {
            SyncWorkConfig syncWorkConfig = syncWorkConfigOpt.get();
            log.debug("Job : " + syncWorkConfig.getName() + " is completed.");
            syncWorkConfig.setLastJobStatus(getStatus(event.getId(), event),
                JobType.valueOf(event.getTriggerKey().getName()));
        }
    }

    @Override
    public JobStatus getJobLastStatus(Long id, JobType type) throws JobNotFoundException {
        Optional<SyncWorkConfig> syncWorkConfigOpt =
                syncWorkConfigRepository.load(id);
        if (syncWorkConfigOpt.isPresent()) {
            SyncWorkConfig syncWorkConfig = syncWorkConfigOpt.get();
            if(type.equals(JobType.REPO_SYNC)) {
                return syncWorkConfig.getSyncToRepoConfig().getLastJobStatus();
            }
            return syncWorkConfig.getSyncToServerConfig().getLastJobStatus();
        }
        throw new JobNotFoundException(id.toString());
    }

    @Override
    public List<JobSummary> getRunningJobs() throws SchedulerException {
        List<JobDetail> runningJobs = cronTrigger.getRunningJobs();
        return runningJobs.stream().map(this::convertToJobSummary)
            .collect(Collectors.toList());
    }

    @Override
    public List<WorkSummary> getAllWork() throws SchedulerException {
        Collection<SyncWorkConfig> syncList =
                syncWorkConfigRepository.getAllWorks();
        return syncList.stream().map(this::convertToWorkSummary)
            .collect(Collectors.toList());
    }

    @Override
    public void persistAndScheduleWork(SyncWorkConfig syncWorkConfig)
        throws SchedulerException {
        syncWorkConfigRepository.persist(syncWorkConfig);
        scheduleJob(syncWorkConfig);
    }

    @Override
    public void cancelRunningJob(Long id, JobType type)
        throws UnableToInterruptJobException, JobNotFoundException {
        Optional<SyncWorkConfig> workConfigOptional =
                syncWorkConfigRepository.load(id);
        if (!workConfigOptional.isPresent()) {
            throw new JobNotFoundException(id.toString());
        }
        cronTrigger.cancelRunningJob(id, type);
    }

    @Override
    public void deleteJob(Long id, JobType type)
        throws SchedulerException, JobNotFoundException {
        Optional<SyncWorkConfig> workConfigOptional =
                syncWorkConfigRepository.load(id);
        if (!workConfigOptional.isPresent()) {
            throw new JobNotFoundException(id.toString());
        }
        cronTrigger.deleteJob(id, type);
    }

    @Override
    public void startJob(Long id, JobType type)
        throws JobNotFoundException, SchedulerException {
        Optional<SyncWorkConfig> workConfigOptional =
                syncWorkConfigRepository.load(id);
        if (!workConfigOptional.isPresent()) {
            throw new JobNotFoundException(id.toString());
        }
        cronTrigger.triggerJob(id, type);
    }

    private void scheduleJob(SyncWorkConfig syncWorkConfig) throws SchedulerException {
        Optional<TriggerKey> keyForRepoJob =
                cronTrigger.scheduleMonitorForRepoSync(syncWorkConfig);
        Optional<TriggerKey> keyForServerJob =
                cronTrigger.scheduleMonitorForServerSync(syncWorkConfig);
        jobConfigKeyMap.put(syncWorkConfig.getId(),
                new JobKeys(keyForRepoJob.orElse(null),
                        keyForServerJob.orElse(null)));
    }

    private JobStatus getStatus(Long id, JobRunCompletedEvent event)
        throws SchedulerException, JobNotFoundException {
        Optional<SyncWorkConfig> workConfigOptional =
                syncWorkConfigRepository.load(id);
        if (!workConfigOptional.isPresent()) {
            String stringId = id.toString();
            throw new JobNotFoundException(stringId);
        }

        JobKeys jobKeys = jobConfigKeyMap.get(id);

        Optional<TriggerKey> triggerKeyOpt = jobKeys.matchedKey(event.getTriggerKey());
        if (triggerKeyOpt.isPresent()) {
            return cronTrigger.getTriggerStatus(triggerKeyOpt.get(), event);
        }
        return cronTrigger.getTriggerStatus(id, event);
    }

    private JobSummary convertToJobSummary(JobDetail jobDetail) {
        if (jobDetail != null) {
            SyncWorkConfig syncWorkConfig =
                    syncWorkConfigRepository
                            .load(new Long(jobDetail.getKey().getName())).get();
            JobType type = JobType.valueOf(jobDetail.getKey().getName());
            JobStatus status;
            if(type.equals(JobType.REPO_SYNC)) {
                status = syncWorkConfig.getSyncToRepoConfig().getLastJobStatus();
            } else {
                status = syncWorkConfig.getSyncToServerConfig().getLastJobStatus();
            }
            return new JobSummary(jobDetail.getKey().toString(),
                    syncWorkConfig.getName(),
                    syncWorkConfig.getDescription(), type, status);
        }
        return new JobSummary();
    }

    private WorkSummary convertToWorkSummary(SyncWorkConfig syncWorkConfig) {
        if (syncWorkConfig != null) {
            return new WorkSummary(syncWorkConfig.getId(), syncWorkConfig.getName(),
                    syncWorkConfig.getDescription(),
                    new JobSummary("", syncWorkConfig.getName(),
                            syncWorkConfig.getDescription(),
                            JobType.REPO_SYNC,
                            syncWorkConfig.getSyncToRepoConfig().getLastJobStatus()),
                    new JobSummary("", syncWorkConfig.getName(),
                            syncWorkConfig.getDescription(),
                            JobType.SERVER_SYNC,
                            syncWorkConfig.getSyncToServerConfig()
                                    .getLastJobStatus()));
        }
        return new WorkSummary();
    }

    public static class JobKeys {
        private final TriggerKey repoSyncJobKey;
        private final TriggerKey serverSyncJobKey;

        public JobKeys(TriggerKey repoSyncJobKey, TriggerKey serverSyncJobKey) {
            this.repoSyncJobKey = repoSyncJobKey;
            this.serverSyncJobKey = serverSyncJobKey;
        }

        Optional<TriggerKey> matchedKey(TriggerKey key) {
            if (key.equals(repoSyncJobKey)) {
                return Optional.of(repoSyncJobKey);
            } else if (key.equals(serverSyncJobKey)) {
                return Optional.of(serverSyncJobKey);
            }
            return Optional.empty();
        }
    }
}
