package org.zanata.sync.events;

import java.util.Date;

import org.zanata.sync.model.JobType;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public interface JobRunUpdate {
    Long getId();

    JobType getJobType();

    Date getStartTime();

    Date getCompletedTime();
}
