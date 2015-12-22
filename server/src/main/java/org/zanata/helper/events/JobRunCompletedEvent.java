package org.zanata.helper.events;

import java.util.Date;

import org.springframework.context.ApplicationEvent;
import org.zanata.helper.util.DateUtil;

import lombok.Getter;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
public class JobRunCompletedEvent extends ApplicationEvent {
    private Long id;
    private Date startTime;
    private long runDuration;

    public JobRunCompletedEvent(Object source, Long id, long runDuration,
            Date startTime) {
        super(source);
        this.id = id;
        this.runDuration = runDuration;
        this.startTime = startTime;
    }

    public Date getCompletedTime() {
        return DateUtil.addMilliseconds(startTime, runDuration);
    }
}
