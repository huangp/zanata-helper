package org.zanata.helper.events;

import lombok.Getter;

import java.util.Date;

import org.zanata.helper.model.JobType;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
public class JobRunStartsEvent {
    private Long id;
    private Date startTime;
    private JobType jobType;

    public JobRunStartsEvent(Long id, Date startTime, JobType jobType) {
        this.id = id;
        this.startTime = startTime;
        this.jobType = jobType;
    }
}
