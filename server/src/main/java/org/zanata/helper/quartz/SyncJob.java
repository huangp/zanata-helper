package org.zanata.helper.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.zanata.helper.model.JobConfig;

@Slf4j
public class SyncJob implements Job {
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        JobConfig jobConfig =
                (JobConfig) context.getJobDetail().getJobDataMap().get("value");
        if (jobConfig.getJobType().equals(JobConfig.Type.SYNC_TO_REPO)) {
            processSyncToRepo(jobConfig);
        } else if (jobConfig.getJobType().equals(JobConfig.Type.SYNC_TO_ZANATA)) {
            processSyncToZanata(jobConfig);
        }
    }

    private void processSyncToRepo(JobConfig jobConfig) {
        log.info("Start sync to Repo:" + jobConfig.toString());
        //See SyncRepoITCase#canSyncToZanataThenSyncToRepo

    }

    private void processSyncToZanata(JobConfig jobConfig) {
        log.info("Start sync to Zanata:" + jobConfig.toString());
        //See SyncRepoITCase#canSyncToZanataThenSyncToRepo
    }
}
