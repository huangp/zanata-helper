package org.zanata.helper.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import org.zanata.helper.model.JobInfo;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.model.Sync;
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

    private Map<String, Sync> syncMap = Maps.newHashMap();

    private Map<String, TriggerKey> syncKeyMap = Maps.newHashMap();

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

        List<Sync> jobs = getJobs();
        cronTrigger = new CronTrigger(eventPublisher);
        for (Sync sync : jobs) {
            addSyncJob(sync);
        }
        log.info("Initialised {} jobs.", syncMap.size());
    }

    //TODO: read from database
    private List<Sync> getJobs() {
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
        return Lists.<Sync>newArrayList(job, job2, job3, job4, job5, job6);
    }

    @EventListener
    public void onApplicationEvent(ConfigurationChangeEvent event) {
        if (syncMap.containsKey(event.getSync().getSha())) {
            syncMap.put(event.getSync().getSha(), event.getSync());
            try {
                cronTrigger.reschedule(syncKeyMap.get(event.getSync().getSha()),
                    event.getSync());
            }
            catch (SchedulerException e) {
                log.error("Error rescheduling job:" + e.getMessage());
            }
        }
    }

    //TODO: update database record
    @EventListener
    public void onJobCompleted(JobRunCompletedEvent event) {
        if (syncMap.containsKey(event.getSha())) {
            syncMap.get(event.getSha()).setLastCompletedTime(event.getCompletedTime());
        }
    }

    @Override
    public JobStatus getStatus(String sha)
        throws SchedulerException, TaskNotFoundException {
        if(StringUtils.isEmpty(sha)) {
            throw new TaskNotFoundException(sha);
        }
        TriggerKey triggerKey = syncKeyMap.get(sha);
        if (triggerKey == null) {
            throw new TaskNotFoundException(sha);
        }
        return cronTrigger.getTriggerStatus(triggerKey);
    }

    @Override
    public List<JobInfo> getRunningJob() throws SchedulerException {
        List<JobDetail> runningJobs = cronTrigger.getRunningJobs();
        return runningJobs.stream().map(this::convertToJobInfo)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobInfo> getAllJobs() throws SchedulerException {
        Collection<Sync> syncList = syncMap.values();
        return syncList.stream().map(this::convertToJobInfo)
            .collect(Collectors.toList());
    }

    private JobInfo convertToJobInfo(JobDetail jobDetail) {
        if (jobDetail != null) {
            Sync sync = syncMap.get(jobDetail.getKey().getName());
            return convertToJobInfo(sync);
        }
        return new JobInfo();
    }

    private JobInfo convertToJobInfo(Sync sync) {
        if(sync != null) {
            return new JobInfo(sync.getSha(), sync.getName(),
                    sync.getDescription(), sync.getLastCompletedTime());
        }
        return new JobInfo();
    }

    @Override
    public void addSyncJob(Sync sync) throws SchedulerException {
        TriggerKey key = cronTrigger.scheduleMonitor(sync);
        if (key != null) {
            syncMap.put(sync.getSha(), sync);
            syncKeyMap.put(sync.getSha(), key);
        }
    }

    @Override
    public void cancelInProgressSyncJob(Sync sync)
        throws UnableToInterruptJobException {
        cronTrigger.cancelInProgressJob(sync);
    }
}
