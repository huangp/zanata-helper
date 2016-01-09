package org.zanata.helper.events;

import java.util.Date;

import org.quartz.TriggerKey;
import org.zanata.helper.util.DateUtil;

import lombok.Getter;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
public class JobRunCompletedEvent {
    private Long id;
    private Date startTime;
    private TriggerKey triggerKey;
    private long runDuration;

    public JobRunCompletedEvent(Long id, TriggerKey key,
            long runDuration, Date startTime) {
        this.id = id;
        triggerKey = key;
        this.runDuration = runDuration;
        this.startTime = startTime;
    }

    public Date getCompletedTime() {
        return DateUtil.addMilliseconds(startTime, runDuration);
    }
}
