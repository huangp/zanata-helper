package org.zanata.helper.service.impl;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.zanata.helper.common.model.SyncOption;
import org.zanata.helper.events.ConfigurationChangeEvent;
import org.zanata.helper.events.JobProgressEvent;
import org.zanata.helper.events.JobRunStartsEvent;
import org.zanata.helper.model.JobConfig;
import org.zanata.helper.events.EventPublisher;
import org.zanata.helper.events.JobRunCompletedEvent;
import org.zanata.helper.exception.JobNotFoundException;
import org.zanata.helper.model.JobSummary;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.quartz.CronTrigger;
import org.zanata.helper.component.AppConfiguration;
import org.zanata.helper.service.PluginsService;
import org.zanata.helper.service.SchedulerService;
import org.zanata.helper.util.CronHelper;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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

    @Autowired
    private PluginsService pluginsServiceImpl;

    private Map<Long, JobConfig> jobConfigMap = Maps.newHashMap();

    private Map<Long, TriggerKey> jobConfigKeyMap = Maps.newHashMap();

    private CronTrigger cronTrigger;

    // TODO: database connection, thread count, scheduler, queue, event

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
        cronTrigger = new CronTrigger(eventPublisher, appConfiguration,
            pluginsServiceImpl);
        for (JobConfig jobConfig : jobConfigs) {
            scheduleJob(jobConfig);
        }
        log.info("Initialised {} jobs.", jobConfigMap.size());
    }

    // TODO: read from database
    private List<JobConfig> getJobs() {
        String username = "username";
        String apiKey = "apiKey";

        List<JobConfig> configs = new ArrayList<JobConfig>();

//        for (int i = 0; i < 7; i++) {
//            Long id = new Long(i);
//            String name = "name" + i;
//            String description = "description" + i;
//
//            Map<String, String> srcConfig = new HashMap<>();
//            srcConfig.put("url", "http://github.com/aeng/zanata-helper");
//            srcConfig.put("username", username);
//            srcConfig.put("apiKey", apiKey);
//
//            Map<String, String> transConfig = new HashMap<>();
//            transConfig.put("url", "http://localhost:8080/zanata/project/zanata-helper/" + i);
//            transConfig.put("username", username);
//            transConfig.put("apiKey", apiKey);
//
//            JobConfig job =
//                new JobConfig(id, name, description,
//                    JobConfig.Type.SYNC_TO_SERVER,
//                    SyncType.TRANSLATIONS,
//                    srcConfig, "org.zanata.helper.plugin.git.Plugin",
//                    transConfig,
//                    "org.zanata.helper.plugin.zanata.Plugin",
//                    CronHelper.CronType.THRITY_SECONDS.getExpression());
//            configs.add(job);
//
//        }
        return configs;
    }

    @EventListener
    public void onApplicationEvent(ConfigurationChangeEvent event) {
        if (jobConfigMap.containsKey(event.getSync().getId())) {
            jobConfigMap.put(event.getSync().getId(), event.getSync());
            try {
                cronTrigger.reschedule(
                    jobConfigKeyMap.get(event.getSync().getId()),
                    event.getSync());
            }
            catch (SchedulerException e) {
                log.error("Error rescheduling job:" + e.getMessage());
            }
        }
    }

    // TODO: update job details
    @EventListener
    public void onJobProgressUpdate(JobProgressEvent event) {
        JobConfig jobConfig = jobConfigMap.get(event.getId());
        if (jobConfig != null) {
            log.info(jobConfig.getName() + ":" + event.getDescription());
        }
    }

    // TODO: fire websocket event
    @EventListener
    public void onJobStarts(JobRunStartsEvent event) {
        JobConfig jobConfig = jobConfigMap.get(event.getId());
        if (jobConfig != null) {
            log.debug(
                "Job : " + jobConfig.getName() + " starting.");
        }
    }

    // TODO: update database record, create history
    @EventListener
    public void onJobCompleted(JobRunCompletedEvent event)
        throws JobNotFoundException, SchedulerException {
        JobConfig jobConfig = jobConfigMap.get(event.getId());

        if (jobConfig != null) {
            log.debug("Job : " + jobConfig.getName() + " is completed.");
            jobConfig.setLastJobStatus(getStatus(event.getId(), event));
        }
    }

    @Override
    public JobStatus getLastStatus(Long id) throws JobNotFoundException {
        JobConfig jobConfig = jobConfigMap.get(id);
        if (jobConfig != null) {
            return jobConfig.getLastJobStatus();
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
    public List<JobSummary> getAllJobs() throws SchedulerException {
        Collection<JobConfig> syncList = jobConfigMap.values();
        return syncList.stream().map(this::convertToJobSummary)
            .collect(Collectors.toList());
    }

    @Override
    public JobConfig getJob(Long id) {
        return jobConfigMap.get(id);
    }

    @Override
    public void persistAndScheduleJob(JobConfig jobConfig)
        throws SchedulerException {
        //TODO: persist jobConfig in db
        scheduleJob(jobConfig);
    }

    @Override
    public void cancelRunningJob(Long id)
        throws UnableToInterruptJobException, JobNotFoundException {
        JobConfig jobConfig = jobConfigMap.get(id);
        if(jobConfig == null) {
            throw new JobNotFoundException(id.toString());
        }
        cronTrigger.cancelRunningJob(jobConfig);
    }

    @Override
    public void deleteJob(Long id)
        throws SchedulerException, JobNotFoundException {
        JobConfig jobConfig = jobConfigMap.get(id);
        if(jobConfig == null) {
            throw new JobNotFoundException(id.toString());
        }
        cronTrigger.deleteJob(jobConfig);
    }

    private void scheduleJob(JobConfig jobConfig) throws SchedulerException {
        TriggerKey key = cronTrigger.scheduleMonitor(jobConfig);
        if (key != null) {
            jobConfigMap.put(jobConfig.getId(), jobConfig);
            jobConfigKeyMap.put(jobConfig.getId(), key);
        }
    }

    private JobStatus getStatus(Long id, JobRunCompletedEvent event)
        throws SchedulerException, JobNotFoundException {
        if (id == null) {
            throw new JobNotFoundException("");
        }
        TriggerKey triggerKey = jobConfigKeyMap.get(id);
        if (triggerKey == null) {
            throw new JobNotFoundException(id.toString());
        }
        return cronTrigger.getTriggerStatus(triggerKey, event);
    }

    private JobSummary convertToJobSummary(JobDetail jobDetail) {
        if (jobDetail != null) {
            JobConfig jobConfig =
                jobConfigMap.get(new Long(jobDetail.getKey().getName()));
            return convertToJobSummary(jobConfig);
        }
        return new JobSummary();
    }

    private JobSummary convertToJobSummary(JobConfig jobConfig) {
        if (jobConfig != null) {
            return new JobSummary(jobConfig.getId(), jobConfig.getName(),
                jobConfig.getDescription(), jobConfig.getLastJobStatus());
        }
        return new JobSummary();
    }
}
