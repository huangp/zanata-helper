/*
 * Copyright 2015, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.zanata.helper.quartz;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.zanata.helper.events.JobRunCompletedEvent;
import org.zanata.helper.events.JobRunStartsEvent;
import org.zanata.helper.model.JobType;
import org.zanata.helper.model.SyncWorkConfig;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class JobConfigListener implements TriggerListener {
    public static final String LISTENER_NAME = "JobConfigListener";

    @Inject
    private Event<JobRunCompletedEvent> jobRunCompletedEvent;

    @Inject
    private Event<JobRunStartsEvent> jobRunStartsEvent;

    private Map<RunningJobKey, AtomicInteger> runningJobs = Maps.newConcurrentMap();

    public String getName() {
        return LISTENER_NAME;
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        SyncWorkConfig syncWorkConfig = getJobConfigJob(context);
        JobType jobType = getJobTypeFromContext(context);
        RunningJobKey key = new RunningJobKey(syncWorkConfig.getId(), jobType);

        runningJobs.putIfAbsent(key, new AtomicInteger(0));
        runningJobs.get(key).incrementAndGet();

        jobRunStartsEvent.fire(
            new JobRunStartsEvent(syncWorkConfig.getId(),
                context.getFireTime(), jobType));
    }

    private static JobType getJobTypeFromContext(JobExecutionContext context) {
        return (JobType) context.getJobDetail().getJobDataMap().get("jobType");
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger,
        JobExecutionContext context) {
        Long id = getJobConfigJob(context).getId();
        JobType jobType = getJobTypeFromContext(context);
        RunningJobKey key = new RunningJobKey(id, jobType);

        if (runningJobs.get(key).get() > 1) {
            log.warn(
                    "job {} execution vetoed: {}. A previous scheduled job is still running",
                    getJobConfigJob(context).getName(), key);
            return true;
        }
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {

    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context,
        Trigger.CompletedExecutionInstruction triggerInstructionCode) {

        SyncWorkConfig syncWorkConfig = getJobConfigJob(context);
        runningJobs.remove(new RunningJobKey(syncWorkConfig.getId(), getJobTypeFromContext(context)));
        jobRunCompletedEvent.fire(
            new JobRunCompletedEvent(syncWorkConfig.getId(),
                trigger.getKey(),
                context.getJobRunTime(),
                context.getFireTime(),
                    getJobTypeFromContext(context)));
    }

    private static SyncWorkConfig getJobConfigJob(JobExecutionContext context) {
        return (SyncWorkConfig) context.getJobDetail().getJobDataMap().get("value");
    }

    private static class RunningJobKey {
        private final Long workId;
        private final JobType jobType;

        private RunningJobKey(Long workId, JobType jobType) {
            this.workId = workId;
            this.jobType = jobType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RunningJobKey that = (RunningJobKey) o;
            return Objects.equals(workId, that.workId) &&
                    jobType == that.jobType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(workId, jobType);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("workId", workId)
                    .add("jobType", jobType)
                    .toString();
        }
    }
}
