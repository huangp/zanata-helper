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

        if (jobConfig.getJobType().equals(JobConfig.Type.SYNC_TO_REPO)) {
            processSyncToRepo(jobConfig);
        } else if (jobConfig.getJobType()
            .equals(JobConfig.Type.SYNC_TO_SERVER)) {
            processSyncToServer(jobConfig);
        }
    }

    private void processSyncToRepo(JobConfig jobConfig) {

        SourceRepoExecutor srcExecutor = jobConfig.getSourceRepoExecutor();
        TranslationServerExecutor transExecutor =
            jobConfig.getTranslationServerExecutor();

        if (srcExecutor == null || transExecutor == null) {
            log.info("No plugin in job. Skipping." + jobConfig.toString());
            return;
        }

        log.info("Sync to repository starts:" + jobConfig.toString());

        File destDir = getDestDirectory(jobConfig.getId().toString());
        log.info("Cloning repository to " + destDir);
        srcExecutor.cloneRepo(destDir);
        log.info("Pulling files to server from " + destDir);
        transExecutor.pullFromServer(destDir, jobConfig.getSyncType());
        log.info("Commits to repository from " + destDir);
        srcExecutor.pushToRepo(destDir, jobConfig.getSyncType());

        log.info("Sync to repository completed:" + jobConfig.toString());
    }

    private void processSyncToServer(JobConfig jobConfig) {

        SourceRepoExecutor srcExecutor = jobConfig.getSourceRepoExecutor();
        TranslationServerExecutor transExecutor =
            jobConfig.getTranslationServerExecutor();

        if (srcExecutor == null || transExecutor == null) {
            log.info("No plugin in job. Skipping." + jobConfig.toString());
            return;
        }

        log.info("Sync to server starts:" + jobConfig.toString());

        File destDir = getDestDirectory(jobConfig.getId().toString());
        log.info("Cloning repository to " + destDir);
        srcExecutor.cloneRepo(destDir);
        log.info("Pushing files to server from " + destDir);
        transExecutor.pushToServer(destDir, jobConfig.getSyncType());

        log.info("Sync to server completed:" + jobConfig.toString());
    }

    private File getDestDirectory(String name) {
        File dest = new File(basedir, name);
        dest.mkdir();
        return dest;
    }


    //See SyncRepoITCase#canSyncToZanataThenSyncToRepo

//    // 1. clone repo
//    repoSyncService.cloneRepo(githubRepo, baseDir);
//
//    // 2. push to zanata
//    pushOptions.setPushType("both");
//    zanataSyncService.pushToZanata(baseDir.toPath());
//
//    // 3. pull from zanata
//    pullOptions.setPullType("trans");
//    zanataSyncService.pullFromZanata(baseDir.toPath());
//
//    // 4. push back to repo
//    repoSyncService.syncTranslationToRepo(githubRepo, baseDir);
}
