package de.jetwick.snacktory.utils;

import de.jetwick.snacktory.SHelper;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.util.*;
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
            Pattern.compile("(?<![\\w])(about the|from|Door|Über|by|name|author|posted|twitter|handle|news|locally researched|report(ing|ed)?( by)?|edit(ing|ed)( by)?)(?![\\w])", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS),
            // Remove month names if any
            Pattern.compile("\\s+" + DateUtils.MMM_PATTERN + "\\s+"),
            // Remove the Suffixes
            Pattern.compile("((\\|| - |, ).*)"),
            // Remove any sequence of numbers
            Pattern.compile("(\\d+)"),
            // Remove any arbitrary special symbol "whitespace followed by special symbol followed by whitespace"
            Pattern.compile("(?<![\\w])" + SPECIAL_SYMBOLS_PATTERN + "(?![\\w])", Pattern.UNICODE_CHARACTER_CLASS),
            // Remove any starting special symbols
            Pattern.compile("^[\\s]*" + SPECIAL_SYMBOLS_PATTERN),
            // Remove any ending special symbols
            Pattern.compile(SPECIAL_SYMBOLS_PATTERN + "[\\s]*$"),
    };

    private static final int MAX_AUTHOR_NAME_LENGTH = 255;
    private static final Pattern HIGHLY_POSITIVE = createRegexPattern(
            "autor|author|author[\\-_]*name|article[\\-_]*author[\\-_]*name|author[\\-_]*card|story[\\-_]*author|" +
                    "author[\\-_]*link|date[\\-_]*author|author[\\-_]*date|byline|byline[\\-_]*name|byLine[\\-_]Tag|" +
                    "contrib[\\-_]*byline"
    );
    private static final Pattern POSITIVE = createRegexPattern(
            "address|time[\\-_]date|post[\\-_]*date|source|news[\\-_]*post[\\-_]*source|meta[\\-_]*author|author[\\-_]*meta|writer|submitted|creator|about[\\-_]*reporter"
    );
    private static final Pattern SET_TO_REMOVE = createRegexPattern(
            "related[\\-_]*post(s)?|sidenav|navigation|feedback[\\-_]*prompt|related[\\-_]*combined[\\-_]*coverage|visually[\\-_]*hidden|page-footer|" +
                    "ad[\\-_]*topjobs|slideshow[\\-_]*overlay[\\-_]*data|next[\\-_]*post[\\-_]*thumbnails|video[\\-_]*desc|related[\\-_]*links|widget popular" +
                    "|^widget marketplace$|^widget ad panel$|slideshowOverlay|^share-twitter$|^share-facebook$|dont_miss_container|" +
                    "^share-google-plus-1$|^inline-list tags$|^tag_title$|article_meta comments|^related-news$|^recomended$|" +
                    "^news_preview$|related--galleries|image-copyright--copyright|^credits$|^photocredit$|^morefromcategory$|^pag-photo-credit$|gallery-viewport-credit|^image-credit$|story-secondary$|carousel-body|slider_container|widget_stories|post-thumbs|^custom-share-links|socialTools|trendingStories|jcarousel-container|module-video-slider|jcarousel-skin-tango|^most-read-content$|^commentBox$|^faqModal$|^widget-area|login-panel|^copyright$|relatedSidebar|shareFooterCntr|most-read-container|email-signup|outbrain|^wnStoryBodyGraphic|articleadditionalcontent|most-popular|shatner-box|form-errors|theme-summary|story-supplement|global-magazine-recent|nocontent|hidden-print|externallinks"
    );
    private static final Pattern META_NAME = createRegexPattern(
            "name|author|creator"
    );
    private static Integer HIGHLY_POSITIVE_CLASS_WEIGHT = 300;
    private static Integer HIGHLY_POSITIVE_ID_WEIGHT = 200;
    private static Integer POSITIVE_WEIGHT = 200;
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
        cleanAuthorName =  new StringBuffer(Pattern.compile("(\\s|^)+" + DateUtils.MMM_PATTERN + "(\\s|$)+").matcher(cleanAuthorName).replaceAll(" "));

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

    public static boolean shouldSkip(Element element) {

        if (SET_TO_REMOVE.matcher(element.className()).matches()) {
            return true;
        }

        for (Element parent : element.parents()) {
            if (SET_TO_REMOVE.matcher(parent.className()).matches()) {
                return true;
            }
        }
        return false;
    }

    public static Integer specialCases(Element element) {
        Integer weight = 0;

        final Pattern ITEMPROP = createRegexPattern("person|name|author|creator");

        if (element.hasAttr("itemprop")) {
            if (ITEMPROP.matcher(element.attr("itemprop")).find()) {
                weight += 450;
            }
        }

        if (element.tagName().equals("a") && element.attr("href").contains("/author")) {
            weight += 30;
        }

        return weight;
    }

    public static Integer highlyPositiveCases(Element element) {
        Integer weight = 0;

        // Highly Positive
        if (HIGHLY_POSITIVE.matcher(element.className()).find()) {
            weight += 200;
        }

        if (HIGHLY_POSITIVE.matcher(element.id()).find()) {
            weight += 100;
        }

        return weight;
    }

    public static Integer positiveCases(Element element) {
        Integer weight = 0;

        // Positive
        if (POSITIVE.matcher(element.id()).find()) {
            weight += 80;
        }

        if (POSITIVE.matcher(element.className()).find()) {
            weight += 40;
        }

        return weight;
    }

    public static String extractAuthor(Document document) {

        document = document.clone();

        String authorName = "";
        Set<String> HIGHLY_POSITIVE_TAGS = new HashSet<String>() {{
            add("meta");
        }};
        PriorityQueue<QueueElement> priorityQueue = new PriorityQueue<>();
        PriorityQueue<QueueElement> priorityQueueMeta = new PriorityQueue<>();

        for (Element element : document.select("*")) {
            if (SET_TO_REMOVE.matcher(element.className()).find()) {
                element.remove();
            }
        }

        // Extract from Metadata


        for (Element element : document.select("*")) {

            if (element.tagName().equals("meta")) {
                continue;
            }

//            if (HIGHLY_POSITIVE_TAGS.contains(element.tagName())) {
//                if (META_NAME.matcher(element.attr("name")).find()) {
//                    element.attr("weight", "350");
//                    priorityQueueMeta.add(new QueueElement(element));
//                    continue;
//                }
//
//                if (META_NAME.matcher(element.attr("property")).find()) {
//                    element.attr("weight", "350");
//                    priorityQueueMeta.add(new QueueElement(element));
//                    continue;
//                }
//            }

            Integer weight = specialCases(element);

            weight += highlyPositiveCases(element);

            weight += positiveCases(element);

            Integer parentWeight = 0;
            if (element.parent() != null && element.parent().hasAttr("weight")) {
                parentWeight = Integer.parseInt(element.parent().attr("weight"));
            }
            weight += parentWeight;

            if (weight > 0) {
                element.attr("weight", weight.toString());
                priorityQueue.add(new QueueElement(element));
            }
        }


        if (!priorityQueue.isEmpty()) {

            Set<QueueElement> treeSet = new TreeSet<QueueElement>();
            treeSet.addAll(priorityQueue);


            int iterations = 0;
            while (StringUtils.isBlank(authorName) && !priorityQueue.isEmpty() && iterations < 3) {
                Element probableAuthorNameElement = priorityQueue.remove().getElement();
                authorName = extractText(probableAuthorNameElement);

                if (StringUtils.isNotEmpty(authorName)) {

                    if (StringUtils.isNotEmpty(authorName)) {
                        if (authorName.split(" ").length > 1) {
                            return authorName.replaceAll("\\s+", " ").trim();
                        }
                    }
                }
                authorName = "";
                iterations++;

//                while (iterations < 3 &&
//                        !priorityQueue.isEmpty() &&
//                        probableAuthorNameElement.attr("weight").equals(priorityQueue.peek().getElement().attr("weight"))) {
//
//                    probableAuthorNameElement = priorityQueue.remove().getElement();
//                    String additionalAuthorName = extractText(probableAuthorNameElement);
//
//                    if (! authorName.contains(additionalAuthorName)) {
//                        authorName = authorName + " " + additionalAuthorName;
//                    }
//
//                    iterations ++;
//                }

            }

        }


        String text = "";
        Elements elements = document.select("meta[property*=author], meta[property*=creator]");
        if (elements != null && elements.size() > 0) {

            for (Element element : elements) {
                if (element.hasAttr("content")) {
                    text = element.attr("content");

                    if (StringUtils.isNotEmpty(text)) {
                        if (text.split(" ").length > 1) {
                            return text;
                        }
                    }
                }
            }
        }


        return authorName;


    }

    private static void nodeToText(Node node, StringBuffer text) {

        String nodesToExtract = "div, p, span, em, h1, h2, h3, h4, a, li, td, b, strong";
        Set<String> nodesToExtractSet = new HashSet<>();
        nodesToExtractSet.addAll(Arrays.asList(nodesToExtract.split(", ")));

        for (Node childNode : node.childNodes()) {

            if (childNode instanceof TextNode) {
                if (text.length() != 0) {
                    text.append(" ");
                }
                text.append(((TextNode) childNode).text());
            } else {
                if (nodesToExtractSet.contains(childNode.nodeName())) {
                    nodeToText(childNode, text);
                }
            }
        }
    }

    private static String extractText(Element element) {

        String textNodes = "p, span, em, h1, h2, h3, h4, a, li";
        Set<String> textNodesSet = new HashSet<>();

        if (element.tagName() == "meta") {
            return element.attr("content");
        }


        StringBuffer textBuffer = new StringBuffer();
        nodeToText(element, textBuffer);


//        String text = "";
//        if (textNodesSet.contains(element.tagName())) {
//            text = element.text();
//        }
//
//
//
//        if(StringUtils.isBlank(text)) {
//
//            PriorityQueue<QueueElement> childElements = new PriorityQueue<>();
//            element.select(textNodes)
//                    .stream()
//                    .filter(childElement -> childElement.hasAttr("weight"))
//                    .forEach(childElement -> childElements.add(new QueueElement(childElement)));
//
//            text = "";
//            while (StringUtils.isBlank(text) && !childElements.isEmpty()) {
//                text = SHelper.innerTrim(childElements.remove().getElement().text());
//            }
//
//            if (StringUtils.isBlank(text)) {
//                text = element.ownText();
//            }
//        }
        return textBuffer.toString();
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
