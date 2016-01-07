package org.zanata.helper.quartz;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.deltaspike.core.api.provider.BeanManagerProvider;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.zanata.helper.events.JobProgressEvent;
import org.zanata.helper.common.plugin.RepoExecutor;
import org.zanata.helper.common.plugin.TranslationServerExecutor;
import org.zanata.helper.model.SyncWorkConfig;

import java.io.File;
import java.io.IOException;

@Slf4j
public abstract class SyncJob implements InterruptableJob {

    protected String basedir;
    protected SyncWorkConfig syncWorkConfig;

    @Override
    public final void execute(JobExecutionContext context)
        throws JobExecutionException {

        try {
            syncWorkConfig =
                    (SyncWorkConfig) context.getJobDetail().getJobDataMap()
                            .get("value");
            basedir =
                    (String) context.getJobDetail().getJobDataMap()
                            .get("basedir");

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
        } finally {
            cleanupDirectory(new File(basedir, syncWorkConfig.getId().toString()));
        }
    }

    protected abstract void doSync(RepoExecutor repoExecutor,
            TranslationServerExecutor serverExecutor)
            throws JobExecutionException;

    @Override
    public final void interrupt() throws UnableToInterruptJobException {
        Thread.currentThread().interrupt();
        updateProgress(syncWorkConfig.getId(), 0, 0, "interrupted");
    }

    protected final void updateProgress(Long id, int currentStep, int totalSteps,
            String description) {
        BeanManagerProvider.getInstance().getBeanManager()
                .fireEvent(
                        new JobProgressEvent(id, currentStep, totalSteps,
                                description)
                );
    }

    protected final File getDestDirectory(String name) {
        File dest = new File(basedir, name);
        cleanupDirectory(dest);
        dest.mkdirs();
        return dest;
    }

    private void cleanupDirectory(File destDir) {
        try {
            if (destDir.exists()) {
                FileUtils.deleteDirectory(destDir);
            }
        } catch (IOException e) {
            log.error("Unable to remove directory {}", destDir, e);
        }
    }
}
