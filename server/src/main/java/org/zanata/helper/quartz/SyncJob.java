package org.zanata.helper.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.zanata.helper.model.SyncToZanata;
import org.zanata.helper.model.Sync;

@Slf4j
public class SyncJob implements Job {
    public void execute(JobExecutionContext context)
        throws JobExecutionException {
        Sync sync =
            (Sync) context.getJobDetail().getJobDataMap().get("value");
        if(sync.getType().equals(Sync.Type.SYNC_TO_REPO)) {
            processSyncToRepo();
        } else if(sync.getType().equals(Sync.Type.SYNC_TO_ZANATA)) {
            processSyncToZanata((SyncToZanata) sync);
        }
    }

    private void processSyncToZanata(SyncToZanata syncToZanata) {
        log.info("Start sync to Zanata:" + syncToZanata.toString());
//        "git clone github"
//        "zanata-cli push"
    }

    private void processSyncToRepo() {
        log.info("Start sync to Repo");
        //        "git clone github"
//        "zanata-cli pull ......."
//            "git push github"
    }
}
