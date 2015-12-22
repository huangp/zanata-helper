package org.zanata.helper.service.impl;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.zanata.helper.events.ConfigurationChangeEvent;
import org.zanata.helper.model.SyncToZanata;
import org.zanata.helper.events.EventPublisher;
import org.zanata.helper.events.JobRunCompletedEvent;
import org.zanata.helper.exception.TaskNotFoundException;
import org.zanata.helper.model.JobSummary;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.model.JobConfig;
import org.zanata.helper.quartz.CronTrigger;
import org.zanata.helper.service.SchedulerService;
import org.zanata.helper.util.CronHelper;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Service
@Slf4j
public class SchedulerServiceImpl implements SchedulerService {
    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired
    private EventPublisher eventPublisher;

    private Map<Long, JobConfig> jobConfigMap = Maps.newHashMap();

    private Map<Long, TriggerKey> jobConfigKeyMap = Maps.newHashMap();

    private CronTrigger cronTrigger;

    //TODO: database connection, thread count, scheduler, queue, event


    @PostConstruct
    public void onApplicationEvent() throws SchedulerException {
        log.info("=====================================================");
        log.info("=====================================================");
        log.info("================Zanata helper starts=================");
        log.info(appConfiguration.getBuildVersion() + ":" +
            appConfiguration.getBuildInfo());
        log.info("=====================================================");
        log.info("=====================================================");
        log.info("Initialising jobs...");

        List<JobConfig> jobConfigs = getJobs();
        cronTrigger = new CronTrigger(eventPublisher);
        for (JobConfig sync : jobConfigs) {
            addSyncJob(sync);
        }
        log.info("Initialised {} jobs.", jobConfigMap.size());
    }

    //TODO: read from database
    private List<JobConfig> getJobs() {
        SyncToZanata job =
            new SyncToZanata(1L, "name1", "description1", "http://github.com/aeng/zanata-helper",
                "http://localhost:8080/zanata/project/zanata-helper/1",
                CronHelper.CronType.THRITY_SECONDS.getExpression());

        SyncToZanata job2 =
            new SyncToZanata(2L, "name2", "description2", "http://github.com/aeng/zanata-helper",
                "http://localhost:8080/zanata/project/zanata-helper/1",
                CronHelper.CronType.THRITY_SECONDS.getExpression());

        SyncToZanata job3 =
            new SyncToZanata(3L, "name3", "description3", "http://github.com/aeng/zanata-helper",
                "http://localhost:8080/zanata/project/zanata-helper/1",
                CronHelper.CronType.THRITY_SECONDS.getExpression());

        SyncToZanata job4 =
            new SyncToZanata(4L, "name4", "description4", "http://github.com/aeng/zanata-helper",
                "http://localhost:8080/zanata/project/zanata-helper/1",
                CronHelper.CronType.THRITY_SECONDS.getExpression());

        SyncToZanata job5 =
            new SyncToZanata(5L, "name5", "description5", "http://github.com/aeng/zanata-helper",
                "http://localhost:8080/zanata/project/zanata-helper/1",
                CronHelper.CronType.THRITY_SECONDS.getExpression());

        SyncToZanata job6 =
            new SyncToZanata(6L, "name6", "description6", "http://github.com/aeng/zanata-helper",
                "http://localhost:8080/zanata/project/zanata-helper/1",
                CronHelper.CronType.THRITY_SECONDS.getExpression());
        return Lists.<JobConfig>newArrayList(job, job2, job3, job4, job5, job6);
    }

    @EventListener
    public void onApplicationEvent(ConfigurationChangeEvent event) {
        if (jobConfigMap.containsKey(event.getSync().getId())) {
            jobConfigMap.put(event.getSync().getId(), event.getSync());
            try {
                cronTrigger.reschedule(jobConfigKeyMap.get(event.getSync().getId()),
                    event.getSync());
            }
            catch (SchedulerException e) {
                log.error("Error rescheduling job:" + e.getMessage());
            }
        }
    }

    //TODO: update database record
    @EventListener
    public void onJobCompleted(JobRunCompletedEvent event)
        throws TaskNotFoundException, SchedulerException {
        JobConfig jobConfig = jobConfigMap.get(event.getId());

        if (jobConfig != null) {
            jobConfig.setLastJobStatus(getStatus(event.getId(), event));
        }
    }

    @Override
    public JobStatus getLastStatus(Long id) throws TaskNotFoundException {
        JobConfig jobConfig = jobConfigMap.get(id);
        if(jobConfig != null) {
            return jobConfig.getLastJobStatus();
        }
        throw new TaskNotFoundException(id.toString());
    }

    @Override
    public List<JobSummary> getRunningJob() throws SchedulerException {
        List<JobDetail> runningJobs = cronTrigger.getRunningJobs();
        return runningJobs.stream().map(this::convertToJobSummary)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobSummary> getAllJobs() throws SchedulerException {
        Collection<JobConfig> syncList = jobConfigMap.values();
        return syncList.stream().map(this::convertToJobSummary)
            .collect(Collectors.toList());
    }

    private JobStatus getStatus(Long id, JobRunCompletedEvent event)
        throws SchedulerException, TaskNotFoundException {
        if(id == null) {
            throw new TaskNotFoundException("");
        }
        TriggerKey triggerKey = jobConfigKeyMap.get(id);
        if (triggerKey == null) {
            throw new TaskNotFoundException(id.toString());
        }
        return cronTrigger.getTriggerStatus(triggerKey, event);
    }

    private JobSummary convertToJobSummary(JobDetail jobDetail) {
        if (jobDetail != null) {
            JobConfig jobConfig = jobConfigMap.get(jobDetail.getKey().getName());
            return convertToJobSummary(jobConfig);
        }
        return new JobSummary();
    }

    private JobSummary convertToJobSummary(JobConfig jobConfig) {
        if(jobConfig != null) {
            return new JobSummary(jobConfig.getId(), jobConfig.getName(),
                    jobConfig.getDescription(), jobConfig.getLastJobStatus());
        }
        return new JobSummary();
    }

    @Override
    public void addSyncJob(JobConfig jobConfig) throws SchedulerException {
        TriggerKey key = cronTrigger.scheduleMonitor(jobConfig);
        if (key != null) {
            jobConfigMap.put(jobConfig.getId(), jobConfig);
            jobConfigKeyMap.put(jobConfig.getId(), key);
        }
    }

    @Override
    public void cancelInProgressSyncJob(JobConfig jobConfig)
        throws UnableToInterruptJobException {
        cronTrigger.cancelInProgressJob(jobConfig);
    }
}
