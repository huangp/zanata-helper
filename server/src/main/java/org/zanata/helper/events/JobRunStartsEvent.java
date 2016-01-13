package org.zanata.helper.events;

import lombok.Getter;

import java.util.Date;

import org.quartz.JobKey;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
public class JobRunStartsEvent implements JobRunUpdate {
    private Long id;
    private Date startTime;
    private JobKey jobKey;

    public JobRunStartsEvent(Long id, Date startTime, JobKey key) {
        this.id = id;
        this.startTime = startTime;
        this.jobKey = key;
    }

    @Override
    public Date getCompletedTime() {
        return null;
    }
}
