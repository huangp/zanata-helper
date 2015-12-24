package org.zanata.helper.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.zanata.helper.model.JobConfig;
import org.zanata.helper.common.plugin.SourceRepoExecutor;
import org.zanata.helper.common.plugin.TranslationServerExecutor;

import java.io.File;

@Slf4j
public class SyncJob implements Job {

    private String basedir;

    public void execute(JobExecutionContext context)
        throws JobExecutionException {
        JobConfig jobConfig =
            (JobConfig) context.getJobDetail().getJobDataMap().get("value");
        basedir =
            (String) context.getJobDetail().getJobDataMap().get("basedir");

        SourceRepoExecutor srcExecutor =
            (SourceRepoExecutor) context.getJobDetail().getJobDataMap()
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
        SourceRepoExecutor srcExecutor,
        TranslationServerExecutor transServerExecutor) {

        if (srcExecutor == null || transServerExecutor == null) {
            log.info("No plugin in job. Skipping." + jobConfig.toString());
            return;
        }

        log.info("Sync to repository starts:" + jobConfig.toString());

        File destDir = getDestDirectory(jobConfig.getId().toString());
        log.info("Cloning repository to " + destDir);
        srcExecutor.cloneRepo(destDir);
        log.info("Pulling files to server from " + destDir);
        transServerExecutor.pullFromServer(destDir, jobConfig.getSyncType());
        log.info("Commits to repository from " + destDir);
        srcExecutor.pushToRepo(destDir, jobConfig.getSyncType());

        log.info("Sync to repository completed:" + jobConfig.toString());
    }

    private void processSyncToServer(JobConfig jobConfig,
        SourceRepoExecutor srcExecutor,
        TranslationServerExecutor transServerExecutor) {

        if (srcExecutor == null || transServerExecutor == null) {
            log.info("No plugin in job. Skipping." + jobConfig.toString());
            return;
        }

        log.info("Sync to server starts:" + jobConfig.toString());

        File destDir = getDestDirectory(jobConfig.getId().toString());
        log.info("Cloning repository to " + destDir);
        srcExecutor.cloneRepo(destDir);
        log.info("Pushing files to server from " + destDir);
        transServerExecutor.pushToServer(destDir, jobConfig.getSyncType());

        log.info("Sync to server completed:" + jobConfig.toString());
    }

    private File getDestDirectory(String name) {
        File dest = new File(basedir, name);
        dest.mkdir();
        return dest;
    }
}
