package de.jetwick.snacktory.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * @author Abhishek Mulay
 */
final public class DateUtils {

    public final static String MMM_PATTERN = "(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec|January|February|March|April|May|June|July|August|September|October|November|December)";

    public final static Pattern[] DATE_PATTERNS = new Pattern[] {

            // Covers below patterns (date and time delimiter can .-/:)
            // "yyyy/MM/dd"
            // "yyyy/MM/dd HH:mm"
            // "yyyy/MM/dd HH:mm:ss"
            Pattern.compile("\\d{4}[\\-./]?\\d{2}[\\-./]?\\d{2}\\s*(\\d{2}[\\-.:]?\\d{2}([\\-.:]?\\d{2})?)?"),

            // Covers below patterns (date and time delimiter can .-/:)
            // "dd MMM yyyy"
            // "dd MMM yyyy HH:mm"
            // "dd MMM yyyy HH:mm:ss"
            // "dd MMMM yyyy"
            // "dd MMMM yyyy HH:mm"
            // "dd MMMM yyyy HH:mm:ss"
            Pattern.compile("\\d{2} " + MMM_PATTERN + "\\s\\d{4}\\s*(\\d{2}[\\-.:]?\\d{2}([\\-.:]?\\d{2})?)?", Pattern.CASE_INSENSITIVE),

            // Covers below patterns (date and time delimiter can .-/:)
            // "MMM dd, yyyy"
            // "MMM dd, yyyy HH:mm"
            // "MMM dd, yyyy HH:mm:ss"
            // "MMMM dd, yyyy"
            // "MMMM dd, yyyy HH:mm"
            // "MMMM dd, yyyy HH:mm:ss"
            Pattern.compile( MMM_PATTERN + "\\s\\d{2},\\s\\d{4}\\s*(\\d{2}[\\-.:]?\\d{2}([\\-.:]?\\d{2})?)?", Pattern.CASE_INSENSITIVE),

            // Covers below patterns (date and time delimiter can .-/:)
            // This is ambiguous to MM-dd-yyyy pattern. Not sure how we can differentiate between two.
            // "dd-MM-yyyy"
            // "dd-MM-yyyy HH:mm"
            // "dd-MM-yyyy HH:mm:ss"
            Pattern.compile("\\d{2}[\\-./]?\\d{2}[\\-./]?\\d{4}\\s*(\\d{2}[\\-.:]?\\d{2}([\\-.:]?\\d{2})?)?")
    };

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
