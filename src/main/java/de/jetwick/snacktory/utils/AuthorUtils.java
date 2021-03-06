package de.jetwick.snacktory.utils;

import de.jetwick.snacktory.SHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * @author Abhishek Mulay
 */
final public class AuthorUtils {

    public static final String[] SPECIAL_SYMBOLS = new String[]{
            "\\.",
            "\\+",
            "-",
            "@",
            ":",
            "\\(",
            "\\)",
            "/",
            "\\.\\.\\.",    // Ellipsis
            "…",            // Ellipsis
    };

    public static final String SPECIAL_SYMBOLS_PATTERN = "(" + StringUtils.join(SPECIAL_SYMBOLS, "|") + ")";

    public static final Pattern[] IGNORE_AUTHOR_PARTS = new Pattern[]{
            // Deliberately keeping patterns separate to make is more readable and maintainable

            // Extract author-name from facebook profile urls
            Pattern.compile("((http(s)?://)?(www\\.)?facebook.com/)"),

            // Remove the Prefixes
            Pattern.compile("(?<![\\w])(from|Door|Über|by|name|author|posted|twitter|handle|news|locally researched|report(ing|ed)?( by)?|edit(ing|ed)( by)?)(?![\\w])", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS),
            // Remove month names if any
            Pattern.compile("\\s+" + DateUtils.MMM_PATTERN + "\\s+"),
            // Remove the Suffixes
            Pattern.compile("((\\|| - |, ).*)"),
            // Remove any sequence of numbers
            Pattern.compile("(\\d+)"),
            // Remove any arbitrary special "whitespace followed by specil symbol followed by whitespace"
            Pattern.compile("(?<![\\w])" + SPECIAL_SYMBOLS_PATTERN + "(?![\\w])", Pattern.UNICODE_CHARACTER_CLASS),
            // Remove any starting special symbols
            Pattern.compile("^[\\s]*" + SPECIAL_SYMBOLS_PATTERN),
            // Remove any ending special symbols
            Pattern.compile(SPECIAL_SYMBOLS_PATTERN + "[\\s]*$"),
    };

    private static final int MAX_AUTHOR_NAME_LENGTH = 255;

    private AuthorUtils() {
    }

    /**
     * Clean up the excessive words, symbols, etc from the authorName
     *
     * @param authorName {@link String}
     * @return {@link String}
     */
    public static String cleanup(String authorName) {

        StringBuffer cleanAuthorName = new StringBuffer(authorName);

        // Remove date patterns if any
        for (Pattern pattern : DateUtils.DATE_PATTERNS) {
            cleanAuthorName = new StringBuffer(pattern.matcher(cleanAuthorName.toString()).replaceAll(""));
        }

        // Remove common prefixes, suffixes, symbols, etc
        for (Pattern pattern : IGNORE_AUTHOR_PARTS) {
            cleanAuthorName = new StringBuffer(pattern.matcher(cleanAuthorName.toString()).replaceAll(" "));
        }

        // Limit the max size
        if (cleanAuthorName.length() > MAX_AUTHOR_NAME_LENGTH) {
            cleanAuthorName = new StringBuffer(SHelper.utf8truncate(cleanAuthorName.toString(), MAX_AUTHOR_NAME_LENGTH));
        }

        return SHelper.innerTrim(cleanAuthorName.toString());
    }
}
