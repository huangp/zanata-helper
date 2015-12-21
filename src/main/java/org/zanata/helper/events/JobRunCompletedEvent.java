package org.zanata.helper.events;

import java.util.Date;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
public class JobRunCompletedEvent extends ApplicationEvent {
    private String sha;
    private Date completeDate;

    public JobRunCompletedEvent(Object source, String sha, Date completeDate) {
        super(source);
        this.sha = sha;
        this.completeDate = completeDate;
    }
}
