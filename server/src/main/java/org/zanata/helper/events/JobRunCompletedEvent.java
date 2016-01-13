package org.zanata.helper.events;

import java.util.Date;

import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.zanata.helper.util.DateUtil;

import lombok.Getter;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
public class JobRunCompletedEvent implements JobRunUpdate {
    private Long id;
    private Date startTime;
    private JobKey jobKey;
    private long runDuration;

    public JobRunCompletedEvent(Long id, JobKey key,
            long runDuration, Date startTime) {
        this.id = id;
        this.jobKey = key;
        this.runDuration = runDuration;
        this.startTime = startTime;
    }

    public Date getCompletedTime() {
        return DateUtil.addMilliseconds(startTime, runDuration);
    }
}
