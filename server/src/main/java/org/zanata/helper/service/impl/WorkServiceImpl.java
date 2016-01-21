package org.zanata.helper.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.zanata.helper.exception.JobNotFoundException;
import org.zanata.helper.exception.WorkNotFoundException;
import org.zanata.helper.model.JobType;
import org.zanata.helper.model.SyncWorkConfig;
import org.zanata.helper.model.WorkSummary;
import org.zanata.helper.repository.Repository;
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
    private Repository<SyncWorkConfig, Long> syncWorkConfigRepository;

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
    public void updateOrPersist(SyncWorkConfig syncWorkConfig) {
        syncWorkConfigRepository.persist(syncWorkConfig);
    }

    private void checkWorkExist(Long id) throws WorkNotFoundException {
        if(!syncWorkConfigRepository.load(id).isPresent()) {
            throw new WorkNotFoundException(id.toString());
        };
    }
}
