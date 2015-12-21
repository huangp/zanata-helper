package org.zanata.helper.quartz;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.zanata.helper.events.EventPublisher;
import org.zanata.helper.events.JobRunCompletedEvent;
import org.zanata.helper.model.Sync;
import org.zanata.helper.model.SyncToZanata;
import org.zanata.helper.service.impl.ContextBeanProvider;

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
        fireCompletedEvent(sync);
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

    private void fireCompletedEvent(Sync sync) {
        EventPublisher eventPublisher =
                ContextBeanProvider.getBean(EventPublisher.class);

        if (eventPublisher != null) {
            eventPublisher
                    .fireEvent(new JobRunCompletedEvent(this, sync.getSha(),
                            new Date()));
        }
    }
}
