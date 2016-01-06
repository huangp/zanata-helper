package org.zanata.helper.quartz;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.zanata.helper.events.JobRunCompletedEvent;
import org.zanata.helper.events.JobRunStartsEvent;
import org.zanata.helper.model.JobConfig;

@Slf4j
public class JobConfigListener implements TriggerListener {
    public static final String LISTENER_NAME = "JobConfigListener";

    private final BeanManager beanManager = CDI.current().getBeanManager();

    public String getName() {
        return LISTENER_NAME;
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        JobConfig jobConfig = getJobConfigJob(context);
        fireEvent(new JobRunStartsEvent(jobConfig.getId(),
            context.getFireTime()));
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger,
        JobExecutionContext context) {
        log.debug("jobExecutionVetoed: " + getJobConfigJob(context).getName());
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context,
        Trigger.CompletedExecutionInstruction triggerInstructionCode) {

        JobConfig jobConfig = getJobConfigJob(context);
        fireEvent(new JobRunCompletedEvent(jobConfig.getId(),
            context.getJobRunTime(), context.getFireTime()));
    }

    private JobConfig getJobConfigJob(JobExecutionContext context) {
        return (JobConfig) context.getJobDetail().getJobDataMap().get("value");
    }

    private void fireEvent(Object event) {
        beanManager.fireEvent(event);
    }
}
