package org.zanata.helper.quartz;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.zanata.helper.events.JobProgressEvent;
import org.zanata.helper.model.JobConfig;
import org.zanata.helper.common.plugin.RepoExecutor;
import org.zanata.helper.common.plugin.TranslationServerExecutor;

import java.io.File;
import java.io.IOException;
import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

@Slf4j
public class SyncJob implements InterruptableJob {

    private File basedir;

    private final int syncToRepoTotalSteps = 5;
    private final int syncToServerTotalSteps = 4;

    private JobConfig jobConfig;

    private final BeanManager beanManager = CDI.current().getBeanManager();

    @Override
    public void execute(JobExecutionContext context)
        throws JobExecutionException {

        try {
            jobConfig =
                    (JobConfig) context.getJobDetail().getJobDataMap()
                            .get("value");
            basedir =
                    (File) context.getJobDetail().getJobDataMap()
                            .get("basedir");

            RepoExecutor srcExecutor =
                    (RepoExecutor) context.getJobDetail().getJobDataMap()
                            .get(RepoExecutor.class.getSimpleName());

            TranslationServerExecutor transServerExecutor =
                    (TranslationServerExecutor) context.getJobDetail()
                            .getJobDataMap()
                            .get(TranslationServerExecutor.class
                                    .getSimpleName());

            if (jobConfig.getJobType().equals(JobConfig.Type.SYNC_TO_REPO)) {
                processSyncToRepo(srcExecutor, transServerExecutor);
            } else if (jobConfig.getJobType()
                    .equals(JobConfig.Type.SYNC_TO_SERVER)) {
                processSyncToServer(srcExecutor, transServerExecutor);
            }
        } catch (JobExecutionException e) {
            log.error("Error running sync job.", e);
        } finally {
            cleanupDirectory(new File(basedir, jobConfig.getId().toString()));
        }
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        Thread.currentThread().interrupt();
        updateProgress(jobConfig.getId(), 0, 0, "interrupted");
    }

    private void processSyncToRepo(RepoExecutor srcExecutor,
        TranslationServerExecutor transServerExecutor)
        throws JobExecutionException {

        if (srcExecutor == null || transServerExecutor == null) {
            log.info("No plugin in job. Skipping. {}", jobConfig.toString());
            return;
        }

        try {
            updateProgress(jobConfig.getId(), 1, syncToRepoTotalSteps,
                "Sync to repository starts");
            File destDir = getDestDirectory(jobConfig.getId().toString());
            updateProgress(jobConfig.getId(),
                2, syncToRepoTotalSteps, "Cloning repository to " + destDir);
            srcExecutor.cloneRepo(destDir);
            updateProgress(jobConfig.getId(),
                3, syncToRepoTotalSteps,
                "Pulling files to server from " + destDir);
            transServerExecutor
                .pullFromServer(destDir, jobConfig.getSyncOption());
            updateProgress(jobConfig.getId(),
                4, syncToRepoTotalSteps,
                "Commits to repository from " + destDir);
            srcExecutor.pushToRepo(destDir, jobConfig.getSyncOption());
            updateProgress(jobConfig.getId(), 5, syncToRepoTotalSteps,
                "Sync to repository completed");
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }

    private void processSyncToServer(RepoExecutor repoExecutor,
        TranslationServerExecutor transServerExecutor)
        throws JobExecutionException {

        if (repoExecutor == null || transServerExecutor == null) {
            log.info("No plugin in job. Skipping." + jobConfig.toString());
            return;
        }
        try {
            updateProgress(jobConfig.getId(), 1, syncToServerTotalSteps,
                "Sync to server starts");
            File destDir = getDestDirectory(jobConfig.getId().toString());
            updateProgress(jobConfig.getId(),
                2, syncToServerTotalSteps, "Cloning repository to " + destDir);
            repoExecutor.cloneRepo(destDir);
            updateProgress(jobConfig.getId(),
                3, syncToServerTotalSteps,
                "Pushing files to server from " + destDir);
            transServerExecutor.pushToServer(destDir, jobConfig.getSyncOption());
            updateProgress(jobConfig.getId(), 4, syncToServerTotalSteps,
                "Sync to server completed");
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }

    private void updateProgress(Long id, int currentStep, int totalSteps,
        String description) {
        fireEvent(
            new JobProgressEvent(id, currentStep, totalSteps,
                description));
    }

    private File getDestDirectory(String name) {
        File dest = new File(basedir, name);
        cleanupDirectory(dest);
        dest.mkdir();
        return dest;
    }

    private void cleanupDirectory(File destDir) {
        try {
            if (destDir.exists()) {
                FileUtils.deleteDirectory(destDir);
            }
        } catch (IOException e) {
            log.error("Unable to remove directory {}. {}", destDir,
                e.getStackTrace());
        }
    }

    private void fireEvent(Object event) {
        beanManager.fireEvent(event);
    }
}
