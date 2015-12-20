package org.zanata.helper.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.zanata.helper.events.ConfigurationChangeEvent;
import org.zanata.helper.exception.TaskNotFoundException;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.model.Sync;
import org.zanata.helper.model.SyncToZanata;
import org.zanata.helper.quartz.CronTrigger;
import org.zanata.helper.service.SchedulerService;
import org.zanata.helper.util.CronHelper;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Service
@Slf4j
public class SchedulerServiceImpl implements SchedulerService {
    @Autowired
    private AppConfiguration appConfiguration;

    private Map<Long, Sync> syncMap = Maps.newHashMap();

    private Map<Long, TriggerKey> syncKeyMap = Maps.newHashMap();

    private CronTrigger cronTrigger;

    //TODO: database connection, thread count, scheduler, queue, event


    @PostConstruct
    public void onApplicationEvent() throws SchedulerException {
        log.info("=====================================================");
        log.info("=====================================================");
        log.info("================Zanata Helper starts=================");
        log.info(appConfiguration.getBuildVersion() + ":" +
            appConfiguration.getBuildInfo());
        log.info("=====================================================");
        log.info("=====================================================");
        log.info("Initialising jobs...");

        List<Sync> jobs = getJobs();
        cronTrigger = new CronTrigger();
        for (Sync sync : jobs) {
            addSyncJob(sync);
        }
        log.info("Initialised {} jobs.", syncMap.size());
    }

    //TODO: read from database
    private List<Sync> getJobs() {
        SyncToZanata job =
            new SyncToZanata(1L, "http://github.com/aeng/zanata-helper",
                "http://localhost:8080/zanata/project/zanata-helper/1",
                CronHelper.CronType.THRITY_SECONDS.getExpression(), null);

        SyncToZanata job2 =
            new SyncToZanata(2L, "http://github.com/aeng/zanata-helper",
                "http://localhost:8080/zanata/project/zanata-helper/1",
                CronHelper.CronType.THRITY_SECONDS.getExpression(), null);

        SyncToZanata job3 =
            new SyncToZanata(3L, "http://github.com/aeng/zanata-helper",
                "http://localhost:8080/zanata/project/zanata-helper/1",
                CronHelper.CronType.THRITY_SECONDS.getExpression(), null);

        SyncToZanata job4 =
            new SyncToZanata(4L, "http://github.com/aeng/zanata-helper",
                "http://localhost:8080/zanata/project/zanata-helper/1",
                CronHelper.CronType.THRITY_SECONDS.getExpression(), null);

        SyncToZanata job5 =
            new SyncToZanata(5L, "http://github.com/aeng/zanata-helper",
                "http://localhost:8080/zanata/project/zanata-helper/1",
                CronHelper.CronType.THRITY_SECONDS.getExpression(), null);

        SyncToZanata job6 =
            new SyncToZanata(6L, "http://github.com/aeng/zanata-helper",
                "http://localhost:8080/zanata/project/zanata-helper/1",
                CronHelper.CronType.THRITY_SECONDS.getExpression(), null);
        return Lists.newArrayList(job, job2, job3, job4, job5, job6);
    }

    @EventListener
    public void onApplicationEvent(ConfigurationChangeEvent event) {
        if (syncMap.containsKey(event.getSync().getId())) {
            syncMap.put(event.getSync().getId(), event.getSync());
            try {
                cronTrigger.reschedule(syncKeyMap.get(event.getSync().getId()),
                    event.getSync());
            }
            catch (SchedulerException e) {
                log.error("Error rescheduling job:" + e.getMessage());
            }
        }
    }

    @Override
    public JobStatus getStatus(String key)
        throws SchedulerException, TaskNotFoundException {
        if(StringUtils.isEmpty(key)) {
            throw new TaskNotFoundException(key);
        }
        TriggerKey triggerKey = syncKeyMap.get(new Long(key));
        if (triggerKey == null) {
            throw new TaskNotFoundException(key);
        }
        return cronTrigger.getTriggerStatus(triggerKey);
    }

    @Override
    public void addSyncJob(Sync sync) throws SchedulerException {
        TriggerKey key = cronTrigger.scheduleMonitor(sync);
        if (key != null) {
            syncMap.put(sync.getId(), sync);
            syncKeyMap.put(sync.getId(), key);
        }
    }

    @Override
    public void cancelInProgressSyncJob(Sync sync)
        throws UnableToInterruptJobException {
        cronTrigger.cancelInProgressJob(sync);
    }
}
