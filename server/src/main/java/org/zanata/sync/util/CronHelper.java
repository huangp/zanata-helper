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
        THIRTY_SECONDS("30 seconds", "0/30 * * * * ?"), //this is for testing purposes
        ONE_HOUR("1 hour", "0 * * * * ?"),
        TWO_HOUR("2 hour", "0 */2 * * * ?"),
        SIX_HOUR("6 hour", "0 */6 * * * ?"),
        TWELVE_HOUR("12 hour", "0 */12 * * * ?"),
        ONE_DAY("24 hour", "0 0 * * * ?");

        private final String display;
        private final String expression;

        CronType(String display, String expression) {
            this.display = display;
            this.expression = expression;
        }
    }
}
