package de.jetwick.snacktory.utils;

import de.jetwick.snacktory.SHelper;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
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

    private static final Pattern HIGHLY_POSITIVE = createRegexPattern(
            ".*(author|author[\\-_]?name|article[\\-_]?author[\\-_]?name|author[\\-_]?card|story[\\-_]?author|" +
                    "author[\\-_]?link|date[\\-_]?author|author[\\-_]?date|byline|byline[\\-_]?name|byLine[\\-_]Tag|" +
                    "contrib[\\-_]?byline|writer|submitted|creator).*"
    );
    private static final Pattern POSITIVE = createRegexPattern(
            ".*(address|time[\\-_]date|post[\\-_]?date|source|news[\\-_]?post[\\-_]?source|meta[\\-_]?author|author[\\-_]?meta).*"
    );
    private static final Pattern META_NAME = createRegexPattern(
            ".*(name|author|creator).*"
    );


    private static Integer HIGHLY_POSITIVE_CLASS_WEIGHT = 300;
    private static Integer HIGHLY_POSITIVE_ID_WEIGHT = 200;
    private static Integer POSITIVE_WEIGHT = 200;

    public static String extractAuthor(Document document) {
        String authorName = "";
        Set<String> HIGHLY_POSITIVE_TAGS = new HashSet<String>() {{
            add("meta");
        }};
        PriorityQueue<QueueElement> priorityQueue = new PriorityQueue<>();

        for (Element element : document.select("*")) {
            Integer weight = 0;

            if (HIGHLY_POSITIVE_TAGS.contains(element.tagName())) {
                if (META_NAME.matcher(element.attr("name")).matches()) {
                    weight += 350;
                }
                if (META_NAME.matcher(element.attr("name")).matches()) {
                    weight += 200;
                }
            }

            if (element.hasAttr("itemprop")) {

                if (element.attr("itemprop").toLowerCase().contains("author")
                        || element.attr("itemprop").toLowerCase().contains("name")) {
                    weight += 350;
                }
            }

            // Highly Positive
            if (HIGHLY_POSITIVE.matcher(element.className()).matches()) {
                weight += 200;
            }

            if (HIGHLY_POSITIVE.matcher(element.id()).matches()) {
                weight += 90;
            }

            // Positive
            if (POSITIVE.matcher(element.className()).matches()) {
                weight += 35;
            }

            if (POSITIVE.matcher(element.id()).matches()) {
                weight += 45;
            }

            // Special Cases
            if (element.tagName().equals("a") && element.attr("href").contains("author")) {
                weight += 30;
            }

            if (weight > 0) {
                element.attr("weight", weight.toString());
                priorityQueue.add(new QueueElement(element));
            }
        }

        if (!priorityQueue.isEmpty()) {

            int iterations = 0;
            while (StringUtils.isBlank(authorName) && !priorityQueue.isEmpty() && iterations < 3) {
                Element probableAuthorNameElement = priorityQueue.remove().getElement();
                int weight = Integer.parseInt(probableAuthorNameElement.attr("weight"));
                authorName = extractText(probableAuthorNameElement);
            }
        }

        return authorName;
    }

    private static String extractText(Element element) {

        String textNodes = "p, span, em, h1, h2, h3, h4, a, li";
        Set<String> textNodesSet = new HashSet<>();

        if (element.tagName() == "meta") {
            return element.attr("content");
        }

        String text = "";
        if (textNodesSet.contains(element.tagName())) {
            text = element.text();
        }

        if(StringUtils.isBlank(text)) {

            PriorityQueue<QueueElement> childElements = new PriorityQueue<>();
            element.select(textNodes)
                    .stream()
                    .filter(childElement -> childElement.hasAttr("weight"))
                    .forEach(childElement -> childElements.add(new QueueElement(childElement)));

            text = "";
            while (StringUtils.isBlank(text) && !childElements.isEmpty()) {
                text = SHelper.innerTrim(childElements.remove().getElement().text());
            }
        }
        return text;
    }

    private static Pattern createRegexPattern(String regex) {
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    public static void main(String[] args) {


    }

    static private class QueueElement implements Comparable<QueueElement> {

        Element element;

        public QueueElement(Element element) {
            this.element = element;
        }

        public Element getElement() {
            return element;
        }

        public QueueElement setElement(Element element) {
            this.element = element;
            return this;
        }

        @Override
        public int compareTo(QueueElement other) {
            Integer thisWeight = Integer.parseInt(this.element.attr("weight"));
            Integer otherWeight = Integer.parseInt(other.element.attr("weight"));
            if (thisWeight > otherWeight) {
                return -1;
            }
            return 1;
        }

        @Override
        public String toString() {
            return element.text();
        }
    }
}
