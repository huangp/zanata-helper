package org.zanata.helper.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Date;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public final class DateUtil {
//    private final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final static String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private final static SimpleDateFormat FORMATTER = new SimpleDateFormat(
        DATE_TIME_FORMAT);

    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return FORMATTER.format(date);
    }

    public static Date addMilliseconds(Date date, long milliseconds) {
        LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        LocalDateTime completedTime = ldt.plus(milliseconds, ChronoField.MILLI_OF_DAY.getBaseUnit());
        return Date.from(completedTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
