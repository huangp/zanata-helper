package org.zanata.helper.events;

import lombok.Getter;

import java.util.Date;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
public class JobRunStartsEvent {
    private Long id;
    private Date startTime;

    public JobRunStartsEvent(Long id, Date startTime) {
        this.id = id;
        this.startTime = startTime;
    }
}
