package org.zanata.helper.events;

import java.util.Date;

import org.quartz.JobKey;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public interface JobRunUpdate {
    JobKey getJobKey();

    Long getId();

    Date getStartTime();

    Date getCompletedTime();
}
