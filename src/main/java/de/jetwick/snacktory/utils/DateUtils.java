package de.jetwick.snacktory.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Abhishek Mulay
 */
final public class DateUtils {

    private DateUtils() {
    }

    /**
     * Parse the date string against the list of input `patterns`
     *
     * @param dateString {@link String}: Date string to parse
     * @param timezone   {@link String}: Default timezone to be used if tz is not present
     *                   in dateString (Don't use the host machine timezone)
     * @param patterns   {@link String[]}:
     * @return {@link Date}
     */
    public static Date parseDate(String dateString, String timezone, String[] patterns) {
        Date date;

        ParsePosition pos = new ParsePosition(0);

        SimpleDateFormat dateFormat = new SimpleDateFormat();
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        dateFormat.setLenient(false);

        for (String pattern : patterns) {

            dateFormat.applyPattern(pattern);
            date = dateFormat.parse(dateString, pos);

            if (date != null && pos.getIndex() == dateString.length()) {
                return date;
            }

            // Reset parsing postion
            pos.setIndex(0);
        }
        return null;
    }
}
