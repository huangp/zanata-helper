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
public class JobConfig implements Serializable {

    private JobType type;
    /**
     * see http://en.wikipedia.org/wiki/Cron#CRON_expression
     */
    private String cron;
    private SyncOption option;
    private JobStatus status = JobStatus.EMPTY;

    public JobConfig(JobType type, String cron, SyncOption option) {
        this.type = type;
        this.cron = cron;
        this.option = option;
    }

    public void updateStatus(JobStatusType statusType, Date lastStartTime,
            Date lastEndTime, Date nextStartTime, JobProgress progress) {
        status = new JobStatus(statusType, lastStartTime, lastEndTime,
                nextStartTime, progress);
    }
}
