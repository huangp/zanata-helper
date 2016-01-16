package org.zanata.helper.model;

import org.quartz.Trigger;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public enum JobStatusType {
    RUNNING, NONE, NORMAL, PAUSED, COMPLETE, ERROR, BLOCKED, INTERRUPTED;

    public static JobStatusType getType(Trigger.TriggerState state,
        boolean isRunning) {
        if(isRunning) {
            return RUNNING;
        }
        return valueOf(state.name());
    }
}
