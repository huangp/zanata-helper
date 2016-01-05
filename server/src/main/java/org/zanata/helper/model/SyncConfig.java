package org.zanata.helper.model;

import java.io.Serializable;
import java.util.Date;

import org.zanata.helper.common.model.SyncOption;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
@ToString
@NoArgsConstructor
public class SyncConfig implements Serializable {

    private Type type;
    /**
     * see http://en.wikipedia.org/wiki/Cron#CRON_expression
     */
    private String cron;
    private SyncOption option;
    private JobStatus lastJobStatus;

    public SyncConfig(Type type, String cron, SyncOption option) {
        this.type = type;
        this.cron = cron;
        this.option = option;
    }

    public static enum Type {
        SYNC_TO_SERVER,
        SYNC_TO_REPO;
    }
    
    public void updateStatus(JobStatusType status, Date lastStartTime,
            Date lastEndTime,
            Date nextStartTime) {
        lastJobStatus = new JobStatus(status, lastStartTime, lastEndTime,
                nextStartTime);
    }
}
