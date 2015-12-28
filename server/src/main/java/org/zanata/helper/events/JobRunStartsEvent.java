package org.zanata.helper.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Date;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
public class JobRunStartsEvent extends ApplicationEvent {
    private Long id;
    private Date startTime;

    public JobRunStartsEvent(Object source, Long id, Date startTime) {
        super(source);
        this.id = id;
        this.startTime = startTime;
    }
}
