package org.zanata.helper.service;

import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;
import org.springframework.context.ApplicationListener;
import org.zanata.helper.events.ConfigurationChangeEvent;
import org.zanata.helper.model.Sync;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public interface SchedulerService extends
    ApplicationListener<ConfigurationChangeEvent> {

    void addSyncJob(Sync sync) throws SchedulerException;

    void cancelInProgressSyncJob(Sync sync) throws UnableToInterruptJobException;
}
