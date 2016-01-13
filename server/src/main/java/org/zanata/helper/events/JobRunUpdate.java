package org.zanata.helper.events;

import java.util.Date;

import org.quartz.JobKey;
import org.zanata.helper.model.JobType;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public interface JobRunUpdate {
    Long getId();

    JobType getJobType();

    Date getStartTime();

    Date getCompletedTime();
}
