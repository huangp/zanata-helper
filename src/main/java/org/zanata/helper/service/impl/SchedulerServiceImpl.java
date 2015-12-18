package org.zanata.helper.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zanata.helper.events.ConfigurationChangeEvent;
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
public class SchedulerServiceImpl implements SchedulerService {
    private static final Logger logger =
        LoggerFactory.getLogger(SchedulerServiceImpl.class);

    @Autowired
    private AppConfiguration appConfiguration;

    private Map<Long, Sync> syncMap = Maps.newHashMap();

    private Map<Long, TriggerKey> syncKeyMap = Maps.newHashMap();

    private CronTrigger cronTrigger;

    //TODO: database connection, thread count, scheduler, queue, event


    @PostConstruct
    public void onApplicationEvent() throws SchedulerException {
        logger.info("=====================================================");
        logger.info("=====================================================");
        logger.info("================Zanata Helper starts=================");
        logger.info(appConfiguration.getBuildVersion() + ":" +
            appConfiguration.getBuildInfo());
        logger.info("=====================================================");
        logger.info("=====================================================");
        logger.info("Initialising jobs...");

        List<Sync> jobs = getJobs();
        cronTrigger = new CronTrigger();
        for (Sync sync : jobs) {
            addSyncJob(sync);
        }
        logger.info("Initialised {0} jobs.", syncMap.size());
    }

    //TODO: read from database
    private List<Sync> getJobs() {
        SyncToZanata job =
            new SyncToZanata(1L, "http://github.com/aeng/zanata-helper",
                "http://localhost:8080/zanata/project/zanata-helper/1",
                CronHelper.CronType.FIVE_MINUTES.getExpression(), null);
        return Lists.newArrayList(job);
    }

    @Override
    public void onApplicationEvent(ConfigurationChangeEvent event) {
        if (syncMap.containsKey(event.getSync().getId())) {
            syncMap.put(event.getSync().getId(), event.getSync());
            try {
                cronTrigger.reschedule(syncKeyMap.get(event.getSync().getId()),
                    event.getSync());
            }
            catch (SchedulerException e) {
                logger.error("Error rescheduling job:" + e.getMessage());
            }
        }
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
