package org.zanata.helper.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.zanata.helper.model.Sync;
import org.zanata.helper.model.SyncToZanata;

@Slf4j
public class SyncJob implements Job {
    public void execute(JobExecutionContext context)
        throws JobExecutionException {
        Sync sync =
            (Sync) context.getJobDetail().getJobDataMap().get("value");

        if(sync.getType().equals(Sync.Type.SYNC_TO_REPO)) {

        } else if(sync.getType().equals(Sync.Type.SYNC_TO_ZANATA)) {
            processSyncToZanata((SyncToZanata) sync);
        }
    }

    private void processSyncToZanata(SyncToZanata syncToZanata) {
        System.out.println("Start sync to Zanata:" + syncToZanata.toString());
    }

    private void processSyncToRepo() {
        System.out.println("Start sync to Repo");
    }
}
