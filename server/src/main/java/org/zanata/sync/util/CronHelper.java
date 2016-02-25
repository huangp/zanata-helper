package org.zanata.sync.util;

import lombok.Getter;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public final class CronHelper {
    public static CronType getTypeFromExpression(String expression) {
        for (CronType cronType : CronType.values()) {
            if (cronType.getExpression().equals(expression)) {
                return cronType;
            }
        }
        throw new IllegalArgumentException(expression);
    }

    public static CronType getTypeFromDisplay(String display) {
        for (CronType cronType : CronType.values()) {
            if (cronType.getDisplay().equals(display)) {
                return cronType;
            }
        }
        throw new IllegalArgumentException(display);
    }

    @Getter
    public enum CronType {
        ONE_HOUR("Hourly", "0 0 0/1 * * ?"),
        TWO_HOUR("2 hourly", "0 0 0/2 * * ?"),
        SIX_HOUR("6 hourly (6:00am,12:00am,6pm,12pm)", "0 0 0/6 * * ?"),
        TWELVE_HOUR("12 hour (12:00am/pm)", "0 0 0,12 * * ?"),
        ONE_DAY("24 hour(12:00am)", "0 0 0 * * ?"),
        //this is for testing purposes
        THIRTY_SECONDS("30 seconds", "0/30 * * * * ?");

        private final String display;
        private final String expression;

        CronType(String display, String expression) {
            this.display = display;
            this.expression = expression;
        }
    }
}
