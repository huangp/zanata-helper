package org.zanata.helper.service.impl;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import org.apache.deltaspike.cdise.api.ContextControl;
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
import org.zanata.helper.model.JobConfig;
import org.zanata.helper.model.JobSummary;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.model.SyncConfig;
import org.zanata.helper.model.WorkSummary;
import org.zanata.helper.quartz.CronTrigger;
import org.zanata.helper.component.AppConfiguration;
import org.zanata.helper.quartz.JobConfigListener;
import org.zanata.helper.repository.JobConfigRepository;
import org.zanata.helper.service.PluginsService;
import org.zanata.helper.service.SchedulerService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
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
    private JobConfigRepository jobConfigRepository;

    @Inject
    private JobConfigListener triggerListener;

    private Map<Long, JobConfig> jobConfigMap =
            Collections.synchronizedMap(Maps.newHashMap());

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

        List<JobConfig> jobConfigs = getJobs();
        try {
            cronTrigger = new CronTrigger(appConfiguration,
                pluginsServiceImpl, triggerListener);
            for (JobConfig jobConfig : jobConfigs) {
                scheduleJob(jobConfig);
            }
        } catch (SchedulerException e) {
            throw Throwables.propagate(e);
        }

        log.info("Initialised {} jobs.", jobConfigMap.size());
    }

    private List<JobConfig> getJobs() {
        return jobConfigRepository.getAllJobs();
    }

    public void onApplicationEvent(@Observes  ConfigurationChangeEvent event) {
        if (jobConfigMap.containsKey(event.getJobConfig().getId())) {
            jobConfigMap.put(event.getJobConfig().getId(), event.getJobConfig());
            try {
                cronTrigger.reschedule(
                        jobConfigKeyMap.get(event.getJobConfig()
                                .getId()).repoSyncJobKey,
                        event.getJobConfig().getSyncToRepoConfig().getCron(),
                        event.getJobConfig().getId() +
                                CronTrigger.REPO_SYNC_KEY_SUFFIX);
                cronTrigger.reschedule(
                        jobConfigKeyMap.get(event.getJobConfig()
                                .getId()).serverSyncJobKey,
                        event.getJobConfig().getSyncToServerConfig().getCron(),
                        event.getJobConfig().getId() +
                                CronTrigger.SERVER_SYNC_KEY_SUFFIX);
            } catch (SchedulerException e) {
                log.error("Error rescheduling job:" + e.getMessage());
            }
        }
    }

    // TODO: update job details
    public void onJobProgressUpdate(@Observes JobProgressEvent event) {
        JobConfig jobConfig = jobConfigMap.get(event.getId());
        if (jobConfig != null) {
            log.info(jobConfig.getName() + ":" + event.getDescription());
        }
    }

    // TODO: fire websocket event
    public void onJobStarts(@Observes JobRunStartsEvent event) {
        JobConfig jobConfig = jobConfigMap.get(event.getId());
        if (jobConfig != null) {
            log.debug(
                "Job : " + jobConfig.getName() + " starting.");
        }
    }

    // TODO: update database record, create history
    public void onJobCompleted(@Observes JobRunCompletedEvent event)
        throws JobNotFoundException, SchedulerException {
        JobConfig jobConfig = jobConfigMap.get(event.getId());

        if (jobConfig != null) {
            log.debug("Job : " + jobConfig.getName() + " is completed.");
            jobConfig.setLastJobStatus(getStatus(event.getId(), event));
        }
    }

    @Override
    public JobStatus getSyncToRepoJobLastStatus(Long id) throws JobNotFoundException {
        JobConfig jobConfig = jobConfigMap.get(id);
        if (jobConfig != null) {
            return jobConfig.getSyncToRepoConfig().getLastJobStatus();
        }
        throw new JobNotFoundException(id.toString());
    }

    @Override
    public List<JobSummary> getRunningJob() throws SchedulerException {
        List<JobDetail> runningJobs = cronTrigger.getRunningJobs();
        return runningJobs.stream().map(this::convertToJobSummary)
            .collect(Collectors.toList());
    }

    @Override
    public List<WorkSummary> getAllWork() throws SchedulerException {
        Collection<JobConfig> syncList = jobConfigMap.values();
        return syncList.stream().map(this::convertToWorkSummary)
            .collect(Collectors.toList());
    }

    @Override
    public JobConfig getJob(Long id) {
        return jobConfigMap.get(id);
    }

    @Override
    public void persistAndScheduleJob(JobConfig jobConfig)
        throws SchedulerException {
        jobConfigRepository.persist(jobConfig);
        scheduleJob(jobConfig);
    }

    @Override
    public void cancelRunningJob(Long id, SyncConfig.Type type)
        throws UnableToInterruptJobException, JobNotFoundException {
        JobConfig jobConfig = jobConfigMap.get(id);
        if(jobConfig == null) {
            throw new JobNotFoundException(id.toString());
        }
        cronTrigger.cancelRunningJob(jobConfig, type);
    }

    @Override
    public void deleteJob(Long id, SyncConfig.Type type)
        throws SchedulerException, JobNotFoundException {
        JobConfig jobConfig = jobConfigMap.get(id);
        if(jobConfig == null) {
            throw new JobNotFoundException(id.toString());
        }
        cronTrigger.deleteJob(jobConfig, type);
    }

    @Override
    public void startJob(Long id, SyncConfig.Type type)
        throws JobNotFoundException, SchedulerException {
        JobConfig jobConfig = jobConfigMap.get(id);
        if(jobConfig == null) {
            throw new JobNotFoundException(id.toString());
        }
        cronTrigger.triggerJob(jobConfig, type);
    }

    private void scheduleJob(JobConfig jobConfig) throws SchedulerException {
        Optional<TriggerKey> keyForRepoJob =
                cronTrigger.scheduleMonitorForRepoSync(jobConfig);
        Optional<TriggerKey> keyForServerJob =
                cronTrigger.scheduleMonitorForServerSync(jobConfig);
        jobConfigMap.put(jobConfig.getId(), jobConfig);
        jobConfigKeyMap.put(jobConfig.getId(),
                new JobKeys(keyForRepoJob.orElse(null),
                        keyForServerJob.orElse(null)));
    }

    private JobStatus getStatus(Long id, JobRunCompletedEvent event)
        throws SchedulerException, JobNotFoundException {
        if (id == null || !jobConfigMap.containsKey(id)) {
            String stringId = id == null ? "" : id.toString();
            throw new JobNotFoundException(stringId);
        }

        JobKeys jobKeys = jobConfigKeyMap.get(id);

        Optional<TriggerKey> triggerKeyOpt = jobKeys.matchedKey(event.getTriggerKey());
        if (triggerKeyOpt.isPresent()) {
            return cronTrigger.getTriggerStatus(triggerKeyOpt.get(), event);
        }
        return cronTrigger.getTriggerStatus(jobConfigMap.get(id), event);
    }

    private JobSummary convertToJobSummary(JobDetail jobDetail) {
        if (jobDetail != null) {
            JobConfig jobConfig =
                jobConfigMap.get(new Long(jobDetail.getKey().getName()));

            SyncConfig.Type type = SyncConfig.Type.valueOf(
                (String) jobDetail.getJobDataMap().get("type"));
            JobStatus status;
            if(type.equals(SyncConfig.Type.SYNC_TO_REPO)) {
                status = jobConfig.getSyncToRepoConfig().getLastJobStatus();
            } else {
                status = jobConfig.getSyncToServerConfig().getLastJobStatus();
            }
            return new JobSummary(jobDetail.getKey().toString(),
                    jobConfig.getName(),
                    jobConfig.getDescription(), type, status);
        }
        return new JobSummary();
    }

    private WorkSummary convertToWorkSummary(JobConfig jobConfig) {
        if (jobConfig != null) {
            return new WorkSummary(jobConfig.getId(), jobConfig.getName(),
                    jobConfig.getDescription(),
                    new JobSummary("", jobConfig.getName(),
                            jobConfig.getDescription(),
                            SyncConfig.Type.SYNC_TO_REPO,
                            jobConfig.getSyncToRepoConfig().getLastJobStatus()),
                    new JobSummary("", jobConfig.getName(),
                            jobConfig.getDescription(),
                            SyncConfig.Type.SYNC_TO_SERVER,
                            jobConfig.getSyncToServerConfig()
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
