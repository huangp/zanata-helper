package org.zanata.helper.events;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Date;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
public class JobRunCompletedEvent extends ApplicationEvent {
    private String sha;
    private Date startTime;
    private long runDuration;

    public JobRunCompletedEvent(Object source, String sha, long runDuration,
            Date startTime) {
        super(source);
        this.sha = sha;
        this.runDuration = runDuration;
        this.startTime = startTime;
    }

    public Date getCompletedTime() {
        LocalDateTime ldt = LocalDateTime.ofInstant(startTime.toInstant(), ZoneId.systemDefault());
        LocalDateTime completedTime = ldt.plus(runDuration, ChronoField.MILLI_OF_DAY.getBaseUnit());
        return Date.from(completedTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
