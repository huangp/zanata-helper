package org.zanata.helper.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.zanata.helper.exception.JobNotFoundException;
import org.zanata.helper.exception.WorkNotFoundException;
import org.zanata.helper.model.JobType;
import org.zanata.helper.model.SyncWorkConfig;
import org.zanata.helper.model.WorkSummary;
import org.zanata.helper.repository.SyncWorkConfigRepository;
import org.zanata.helper.service.SchedulerService;
import org.zanata.helper.service.WorkService;
import org.zanata.helper.util.WorkUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
@ApplicationScoped
@Slf4j
public class WorkServiceImpl implements WorkService {

    @Inject
    private SchedulerService schedulerServiceImpl;

    @Inject
    private SyncWorkConfigRepository syncWorkConfigRepository;

    @Override
    public void deleteWork(Long id) throws WorkNotFoundException {
        checkWorkExist(id);
        try {
            schedulerServiceImpl.deleteJob(id, JobType.REPO_SYNC);
            schedulerServiceImpl.deleteJob(id, JobType.SERVER_SYNC);
            syncWorkConfigRepository.delete(id);
        }
        catch (SchedulerException e) {
            log.warn("Error when delete job in work", e);
        }
        catch (JobNotFoundException e) {
            log.debug("No job found for work", e);
        }
    }

    @Override
    public WorkSummary disableJob(JobType jobType, Long id)
        throws WorkNotFoundException {
        checkWorkExist(id);
        try {
            schedulerServiceImpl.disableJob(id, JobType.REPO_SYNC);
            schedulerServiceImpl.disableJob(id, JobType.SERVER_SYNC);

            SyncWorkConfig config = syncWorkConfigRepository.load(id).get();
            config.enableJob(jobType, false);
            syncWorkConfigRepository.persist(config);
        } catch (SchedulerException e) {
            log.debug("No job found for work", e);
        }
        return getWorkSummary(id);
    }

    @Override
    public WorkSummary enableJob(JobType jobType, Long id) throws WorkNotFoundException {
        checkWorkExist(id);
        try {
            schedulerServiceImpl.enableJob(id, JobType.REPO_SYNC);
            schedulerServiceImpl.enableJob(id, JobType.SERVER_SYNC);

            SyncWorkConfig config = syncWorkConfigRepository.load(id).get();
            config.enableJob(jobType, true);
            syncWorkConfigRepository.persist(config);
        } catch (SchedulerException e) {
            log.debug("No job found for work", e);
        }
        return getWorkSummary(id);
    }

    @Override
    public void updateOrPersist(SyncWorkConfig syncWorkConfig) {
        syncWorkConfigRepository.persist(syncWorkConfig);
    }

    private void checkWorkExist(Long id) throws WorkNotFoundException {
        if(!syncWorkConfigRepository.load(id).isPresent()) {
            throw new WorkNotFoundException(id.toString());
        };
    }

    private WorkSummary getWorkSummary(Long id) {
        SyncWorkConfig config = syncWorkConfigRepository.load(id).get();
        return WorkUtil.convertToWorkSummary(config);
    }
}
