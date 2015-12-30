package org.zanata.helper.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.zanata.helper.component.ContextBeanProvider;
import org.zanata.helper.events.EventPublisher;
import org.zanata.helper.events.JobProgressEvent;
import org.zanata.helper.model.JobConfig;
import org.zanata.helper.common.plugin.RepoExecutor;
import org.zanata.helper.common.plugin.TranslationServerExecutor;

import java.io.File;

@Slf4j
public class SyncJob implements Job {

    private String basedir;

    private final EventPublisher eventPublisher =
        ContextBeanProvider.getBean(EventPublisher.class);

    private final int syncToRepoTotalSteps = 5;
    private final int syncToServerTotalSteps = 4;

    public void execute(JobExecutionContext context)
        throws JobExecutionException {

        JobConfig jobConfig =
            (JobConfig) context.getJobDetail().getJobDataMap().get("value");
        basedir =
            (String) context.getJobDetail().getJobDataMap().get("basedir");

        RepoExecutor srcExecutor =
            (RepoExecutor) context.getJobDetail().getJobDataMap()
                .get("sourceRepoExecutor");

        TranslationServerExecutor transServerExecutor =
            (TranslationServerExecutor) context.getJobDetail().getJobDataMap()
                .get("translationServerExecutor");

        if (jobConfig.getJobType().equals(JobConfig.Type.SYNC_TO_REPO)) {
            processSyncToRepo(jobConfig, srcExecutor, transServerExecutor);
        } else if (jobConfig.getJobType()
            .equals(JobConfig.Type.SYNC_TO_SERVER)) {
            processSyncToServer(jobConfig, srcExecutor, transServerExecutor);
        }
    }

    private void processSyncToRepo(JobConfig jobConfig,
        RepoExecutor srcExecutor,
        TranslationServerExecutor transServerExecutor)
        throws JobExecutionException {

        if (srcExecutor == null || transServerExecutor == null) {
            log.info("No plugin in job. Skipping." + jobConfig.toString());
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
                .pullFromServer(destDir, jobConfig.getSyncType());
            updateProgress(jobConfig.getId(),
                4, syncToRepoTotalSteps,
                "Commits to repository from " + destDir);
            srcExecutor.pushToRepo(destDir, jobConfig.getSyncType());
            updateProgress(jobConfig.getId(), 5, syncToRepoTotalSteps,
                "Sync to repository completed");
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }

    private void updateProgress(Long id, int currentStep, int totalSteps,
        String description) {
        eventPublisher.fireEvent(
            new JobProgressEvent(this, id, currentStep, totalSteps,
                description));
    }

    private void processSyncToServer(JobConfig jobConfig,
        RepoExecutor repoExecutor,
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
            transServerExecutor.pushToServer(destDir, jobConfig.getSyncType());
            updateProgress(jobConfig.getId(), 4, syncToServerTotalSteps,
                "Sync to server completed");
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }

    private File getDestDirectory(String name) {
        File dest = new File(basedir, name);
        dest.mkdir();
        return dest;
    }
}
