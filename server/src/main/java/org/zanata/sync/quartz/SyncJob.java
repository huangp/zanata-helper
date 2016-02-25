package org.zanata.sync.quartz;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.deltaspike.core.api.provider.BeanManagerProvider;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.zanata.sync.events.JobProgressEvent;
import org.zanata.sync.common.plugin.RepoExecutor;
import org.zanata.sync.common.plugin.TranslationServerExecutor;
import org.zanata.sync.events.JobRunCompletedEvent;
import org.zanata.sync.model.JobStatusType;
import org.zanata.sync.model.JobType;
import org.zanata.sync.model.SyncWorkConfig;

import java.io.File;
import java.io.IOException;

@Slf4j
public abstract class SyncJob implements InterruptableJob {

    protected File basedir;
    protected SyncWorkConfig syncWorkConfig;
    protected boolean cleanDir = true;
    protected boolean interrupted = false;
    protected JobType jobType;

    @Override
    public final void execute(JobExecutionContext context)
        throws JobExecutionException {
        boolean hasError = false;
        try {
            syncWorkConfig =
                    (SyncWorkConfig) context.getJobDetail().getJobDataMap()
                            .get("value");
            basedir =
                    (File) context.getJobDetail().getJobDataMap()
                            .get("basedir");

            cleanDir = (Boolean) context.getJobDetail().getJobDataMap().get("cleanDir");

            jobType = (JobType) context.getJobDetail().getJobDataMap().get("jobType");

            RepoExecutor srcExecutor =
                    (RepoExecutor) context.getJobDetail().getJobDataMap()
                            .get(RepoExecutor.class.getSimpleName());

            TranslationServerExecutor transServerExecutor =
                    (TranslationServerExecutor) context.getJobDetail()
                            .getJobDataMap()
                            .get(TranslationServerExecutor.class
                                    .getSimpleName());

            doSync(srcExecutor, transServerExecutor);
        } catch (JobExecutionException e) {
            log.error("Error running sync job.", e);
            hasError = true;

        } finally {
            if(cleanDir) {
                cleanupDirectory(
                    new File(basedir, syncWorkConfig.getId().toString()));
            }
            if(!interrupted) {
                JobRunCompletedEvent event;
                if (hasError) {
                    event = new JobRunCompletedEvent(syncWorkConfig.getId(),
                        context.getJobRunTime(),
                        context.getFireTime(),
                        jobType, JobStatusType.ERROR);
                } else {
                    event = new JobRunCompletedEvent(syncWorkConfig.getId(),
                        context.getJobRunTime(),
                        context.getFireTime(),
                        jobType, JobStatusType.COMPLETE);
                }
                BeanManagerProvider.getInstance().getBeanManager().fireEvent(event);
            }
        }
    }

    protected abstract JobType getJobType();

    protected abstract void doSync(RepoExecutor repoExecutor,
            TranslationServerExecutor serverExecutor)
            throws JobExecutionException;

    @Override
    public final void interrupt() throws UnableToInterruptJobException {
        interrupted = true;
        Thread.currentThread().interrupt();
        updateProgress(syncWorkConfig.getId(), 0, "job interrupted",
            JobStatusType.INTERRUPTED);
    }

    protected final void updateProgress(Long id, double completePercent,
        String description, JobStatusType jobStatusType) {
        JobProgressEvent event =
            new JobProgressEvent(id, getJobType(), completePercent,
                description, jobStatusType);
        BeanManagerProvider.getInstance().getBeanManager().fireEvent(event);
    }

    protected final File getDestDirectory(String id) {
        File dest = new File(basedir, id);
        cleanupDirectory(dest);
        dest.mkdirs();
        return dest;
    }

    private void cleanupDirectory(File destDir) {
        try {
            FileUtils.deleteDirectory(destDir);
        } catch (IOException e) {
            log.warn("Unable to remove directory {}", destDir);
            log.debug("Unable to remove directory", e);
        }
    }
}
