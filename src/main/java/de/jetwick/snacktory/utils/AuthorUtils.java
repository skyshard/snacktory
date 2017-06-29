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

    public static final Pattern[] IGNORE_AUTHOR_PARTS = new Pattern[]{
            // Deliberately keeping patterns separate to make is more readable and maintainable

            // Remove the Prefixes
            Pattern.compile("(?<![a-zA-Z])(from|Door|Über|by|name|author|posted|twitter|handle|news|locally researched|report(ing|ed)?( by)?|edit(ing|ed)( by)?)(?![a-zA-Z])", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS),
            // Remove month names if any
            Pattern.compile("\\s+" + DateUtils.MMM_PATTERN + "\\s+"),
            // Remove the Suffixes
            Pattern.compile("((\\|| - |, ).*)"),
            // Remove any sequence of numbers
            Pattern.compile("(\\d+)"),
            // Remove any arbitrary special symbols
            Pattern.compile("(" + StringUtils.join(SPECIAL_SYMBOLS, "|") + ")", Pattern.CASE_INSENSITIVE),
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
