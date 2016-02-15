package org.zanata.sync.events;

import java.util.Date;

import org.zanata.sync.model.JobType;
import org.zanata.sync.util.DateUtil;

import lombok.Getter;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
public class JobRunCompletedEvent implements JobRunUpdate {
    private Long id;
    private Date startTime;
    private JobType jobType;
    private long runDuration;

    public JobRunCompletedEvent(Long id,
            long runDuration, Date startTime, JobType jobType) {
        this.id = id;
        this.runDuration = runDuration;
        this.startTime = startTime;
        this.jobType = jobType;
    }

    public Date getCompletedTime() {
        return DateUtil.addMilliseconds(startTime, runDuration);
    }
}
