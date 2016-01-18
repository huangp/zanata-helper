package org.zanata.helper.service.impl;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import org.apache.deltaspike.core.api.lifecycle.Initialized;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;

import org.zanata.helper.events.JobProgressEvent;
import org.zanata.helper.events.JobRunCompletedEvent;
import org.zanata.helper.events.JobRunUpdate;
import org.zanata.helper.exception.JobNotFoundException;
import org.zanata.helper.exception.WorkNotFoundException;
import org.zanata.helper.model.JobProgress;
import org.zanata.helper.model.JobStatusType;
import org.zanata.helper.model.JobType;
import org.zanata.helper.model.SyncWorkConfig;
import org.zanata.helper.model.JobSummary;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.model.WorkSummary;
import org.zanata.helper.quartz.CronTrigger;
import org.zanata.helper.component.AppConfiguration;
import org.zanata.helper.quartz.RunningJobKey;
import org.zanata.helper.repository.SyncWorkConfigRepository;
import org.zanata.helper.service.PluginsService;
import org.zanata.helper.service.SchedulerService;
import org.zanata.helper.util.WorkUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.util.ArrayList;
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
    private CronTrigger cronTrigger;

    private Map<RunningJobKey, JobProgress> progressMap =
        Collections.synchronizedMap(Maps.newHashMap());

    public void onStartUp(@Observes @Initialized ServletContext servletContext) {
        log.info("=====================================================");
        log.info("=====================================================");
        log.info("================Zanata helper starts=================");
        log.info("== build :            {}-{}",
                appConfiguration.getBuildVersion(),
                appConfiguration.getBuildInfo());
        log.info("== repo directory:    {}",
                appConfiguration.getRepoDir());
        log.info("== config directory:  {}",
                appConfiguration.getConfigDir());
        log.info("== fields to encrypt: {}",
                appConfiguration.getFieldsNeedEncryption());
        log.info("=====================================================");
        log.info("=====================================================");

        pluginsServiceImpl.init();

        log.info("Initialising jobs...");

        List<SyncWorkConfig> syncWorkConfigs = syncWorkConfigRepository.getAllWorks();
        try {
            for (SyncWorkConfig syncWorkConfig : syncWorkConfigs) {
                scheduleWork(syncWorkConfig);
            }
        } catch (SchedulerException e) {
            throw Throwables.propagate(e);
        }

        log.info("Initialised {} jobs.", syncWorkConfigs.size());
    }

    // TODO: fire websocket
    public void onJobProgressUpdate(@Observes JobProgressEvent event) {
        log.info(event.toString());
        JobProgress progress = new JobProgress(event.getCompletePercent(),
                event.getDescription(), event.getJobStatusType());
        progressMap.put(new RunningJobKey(event.getId(), event.getJobType()), progress);
    }

    public void onJobCompleted(@Observes JobRunCompletedEvent event)
        throws JobNotFoundException, SchedulerException {
        progressMap.remove(new RunningJobKey(event.getId(), event.getJobType()));
        Optional<SyncWorkConfig> syncWorkConfigOpt =
                syncWorkConfigRepository.load(event.getId());
        if (syncWorkConfigOpt.isPresent()) {
            SyncWorkConfig syncWorkConfig = syncWorkConfigOpt.get();
            log.debug(
                "Job: " + event.getJobType() + "-" + syncWorkConfig.getName() +
                    " is completed.");
            syncWorkConfig.setJobStatus(getStatus(event.getId(), event),
                event.getJobType(), new JobProgress(100, "completed",
                    JobStatusType.COMPLETE));
            syncWorkConfigRepository.persist(syncWorkConfig);
        }
    }

    @Override
    public JobStatus getJobStatus(Long id, JobType type) throws JobNotFoundException {
        Optional<SyncWorkConfig> syncWorkConfigOpt =
                syncWorkConfigRepository.load(id);
        if (syncWorkConfigOpt.isPresent()) {
            SyncWorkConfig syncWorkConfig = syncWorkConfigOpt.get();
            setJobProgress(syncWorkConfig, type);

            if(type.equals(JobType.REPO_SYNC)) {
                return syncWorkConfig.getSyncToRepoConfig().getStatus();
            } else {
                return syncWorkConfig.getSyncToServerConfig().getStatus();
            }
        }
        throw new JobNotFoundException(id.toString());
    }

    @Override
    public List<JobSummary> getJobs() throws SchedulerException {
        List<JobDetail> runningJobs = cronTrigger.getJobs();
        return runningJobs.stream().map(this::convertToJobSummary)
            .collect(Collectors.toList());
    }

    @Override
    public List<WorkSummary> getAllWorkSummary() throws SchedulerException {
        List<WorkSummary> results = new ArrayList<>();
        for (SyncWorkConfig config : getAllWork()) {
            setJobProgress(config, JobType.REPO_SYNC);
            setJobProgress(config, JobType.SERVER_SYNC);
            results.add(WorkUtil.convertToWorkSummary(config));
        }
        return results;
    }

    @Override
    public void scheduleWork(SyncWorkConfig syncWorkConfig)
        throws SchedulerException {
        cronTrigger.scheduleMonitorForRepoSync(syncWorkConfig);
        cronTrigger.scheduleMonitorForServerSync(syncWorkConfig);
    }

    @Override
    public void rescheduleWork(SyncWorkConfig syncWorkConfig)
        throws SchedulerException {

        cronTrigger.deleteAndReschedule(
            JobType.REPO_SYNC.toTriggerKey(syncWorkConfig.getId()),
            syncWorkConfig.getSyncToRepoConfig().getCron(),
            syncWorkConfig.getId(), JobType.REPO_SYNC,
            syncWorkConfig.isSyncToRepoEnabled());

        cronTrigger.deleteAndReschedule(
            JobType.SERVER_SYNC.toTriggerKey(syncWorkConfig.getId()),
            syncWorkConfig.getSyncToServerConfig().getCron(),
            syncWorkConfig.getId(), JobType.SERVER_SYNC,
            syncWorkConfig.isSyncToServerEnabled());
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
    public void disableJob(Long id, JobType type) throws SchedulerException {
        cronTrigger.disableJob(id, type);
    }

    @Override
    public void enableJob(Long id, JobType type) throws SchedulerException {
        cronTrigger.enableJob(id, type);
    }

    @Override
    public void triggerJob(Long id, JobType type)
        throws JobNotFoundException, SchedulerException {
        Optional<SyncWorkConfig> workConfigOptional =
                syncWorkConfigRepository.load(id);
        if (!workConfigOptional.isPresent()) {
            throw new JobNotFoundException(id.toString());
        }
        cronTrigger.triggerJob(id, type);
    }

    @Override
    public SyncWorkConfig getWork(String id) throws WorkNotFoundException {
        Optional<SyncWorkConfig> syncWorkConfig =
                syncWorkConfigRepository.load(new Long(id));
        if(!syncWorkConfig.isPresent()) {
            throw new WorkNotFoundException(id);
        }
        setJobProgress(syncWorkConfig.get(), JobType.SERVER_SYNC);
        setJobProgress(syncWorkConfig.get(), JobType.REPO_SYNC);
        return syncWorkConfig.get();
    }

    @Override
    public WorkSummary getWorkSummary(String id) throws WorkNotFoundException {
        SyncWorkConfig syncWorkConfig = getWork(id);
        return WorkUtil.convertToWorkSummary(syncWorkConfig);
    }

    @Override
    public List<SyncWorkConfig> getAllWork() throws SchedulerException {
        List<SyncWorkConfig> allWork = syncWorkConfigRepository.getAllWorks();
        for(SyncWorkConfig config: allWork) {
            setJobProgress(config, JobType.SERVER_SYNC);
            setJobProgress(config, JobType.REPO_SYNC);
        }
        return allWork;
    }

    private JobStatus getStatus(Long id, JobRunUpdate event)
        throws SchedulerException, JobNotFoundException {
        Optional<SyncWorkConfig> workConfigOptional =
                syncWorkConfigRepository.load(id);
        if (!workConfigOptional.isPresent()) {
            String stringId = id.toString();
            throw new JobNotFoundException(stringId);
        }

        return cronTrigger.getTriggerStatus(id, event);
    }

    private JobSummary convertToJobSummary(JobDetail jobDetail) {
        if (jobDetail != null) {
            SyncWorkConfig syncWorkConfig =
                    syncWorkConfigRepository
                            .load(new Long(jobDetail.getKey().getGroup())).get();
            JobType type = JobType.valueOf(jobDetail.getKey().getName());
            setJobProgress(syncWorkConfig, type);

            JobStatus status;
            if(type.equals(JobType.REPO_SYNC)) {
                status = syncWorkConfig.getSyncToRepoConfig().getStatus();
            } else {
                status = syncWorkConfig.getSyncToServerConfig().getStatus();
            }

            return new JobSummary(jobDetail.getKey().toString(),
                    syncWorkConfig.getId().toString(), syncWorkConfig.getName(),
                    syncWorkConfig.getDescription(), type, status);
        }
        return new JobSummary();
    }

    private void setJobProgress(SyncWorkConfig syncWorkConfig, JobType jobType) {
        RunningJobKey key = new RunningJobKey(syncWorkConfig.getId(), jobType);
        if(jobType.equals(JobType.REPO_SYNC)) {
            syncWorkConfig.setSyncToRepoJobProgress(progressMap.get(key));
        } else {
            syncWorkConfig.setSyncToServerJobProgress(progressMap.get(key));
        }
    }
}
