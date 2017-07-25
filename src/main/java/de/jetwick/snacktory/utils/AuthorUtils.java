package de.jetwick.snacktory.utils;

import de.jetwick.snacktory.SHelper;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
            "\\|"
    };
    public static final String SPECIAL_SYMBOLS_PATTERN = "(" + StringUtils.join(SPECIAL_SYMBOLS, "|") + ")";
    public static final Pattern[] IGNORE_AUTHOR_PARTS = new Pattern[]{
            // Deliberately keeping patterns separate to make is more readable and maintainable

            // Extract author-name from facebook profile urls
            Pattern.compile("((http(s)?://)?(www\\.)?facebook.com/)"),

//            // Remove the Prefixes
            Pattern.compile("(?<![\\w])(a|an|and|are|as|at|be|but|by|for|if|in|into|is|it|no|not|of|on|or|such|that|the|their|then|there|these|they|this|to|was|will|with|about the|from|Door|Über|by|name|author|posted|twitter|handle|news|locally researched|report(ing|ed)?( by)?|edit(ing|ed)( by)?)(?![\\w])", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS),
            // Remove month names if any
            Pattern.compile("\\s+" + DateUtils.MMM_PATTERN + "\\s+"),
            // Remove the Suffixes
            //Pattern.compile("((\\|| - |, ).*)"),
            // Remove any sequence of numbers
            Pattern.compile("(\\d+)"),
            // Remove any arbitrary special symbol "whitespace followed by special symbol followed by whitespace"
            Pattern.compile("(?<![\\w])" + SPECIAL_SYMBOLS_PATTERN + "(?![\\w])", Pattern.UNICODE_CHARACTER_CLASS),
            // Remove any starting special symbols
            Pattern.compile("^[\\s]*" + SPECIAL_SYMBOLS_PATTERN),
            // Remove any ending special symbols
            Pattern.compile(SPECIAL_SYMBOLS_PATTERN + "[\\s]*$"),
    };
    public static final Pattern IGNORE_WORDS = createRegexPattern(
            //"Facebook|Pinterest|Twitter|Linkedin"
            "(?<![\\w])(a|an|and|are|as|at|be|but|by|for|if|in|into|is|it|no|not|of|on|or|such|that|their|then|there|these|they|this|to|was|will|with|publish(ed)?|report(ing|ed)?|read)(?![\\w])"
    );
    final static Pattern ITEMPROP = createRegexPattern("author|creator");
    final static Pattern ITEMPROP_POSITIVE = createRegexPattern("person|name");
    private static final Logger logger = LoggerFactory.getLogger(AuthorUtils.class);
    private static final int MAX_AUTHOR_NAME_LENGTH = 255;
    private static final Pattern HIGHLY_POSITIVE = createRegexPattern(
            "autor|author|author[\\-_]*name|article[\\-_]*author[\\-_]*name|author[\\-_]*card|story[\\-_]*author|" +
                    "author[\\-_]*link|date[\\-_]*author|author[\\-_]*date|byline|byline[\\-_]*name|byLine[\\-_]Tag|" +
                    "contrib[\\-_]*byline|vcard"
    );
    private static final Pattern POSITIVE = createRegexPattern(
            "address|time[\\-_]*date|post[\\-_]*date|source|news[\\-_]*post[\\-_]*source|meta[\\-_]*author|" +
                    "author[\\-_]*meta|writer|submitted|creator|reporter[\\-_]*name|profile-data|posted|contact"
    );
//    private static final Pattern SET_TO_REMOVE = createRegexPattern(
//            "navigation|widget|sidebar|comment[\\-_]*holder|meettheauthor|join|discuss|thread|tooltip|no_print|related[\\-_]*post(s)?|sidenav|navigation|feedback[\\-_]*prompt|related[\\-_]*combined[\\-_]*coverage|visually[\\-_]*hidden|page-footer|" +
//                    "ad[\\-_]*topjobs|slideshow[\\-_]*overlay[\\-_]*data|next[\\-_]*post[\\-_]*thumbnails|video[\\-_]*desc|related[\\-_]*links|widget popular" +
//                    "|^widget marketplace$|^widget ad panel$|slideshowOverlay|^share-twitter$|^share-facebook$|dont_miss_container|" +
//                    "^share-google-plus-1$|^inline-list tags$|^tag_title$|article_meta comments|^related-news$|^recomended$|" +
//                    "^news_preview$|related--galleries|image-copyright--copyright|^credits$|^photocredit$|^morefromcategory$|" +
//                    "^pag-photo-credit$|gallery-viewport-credit|^image-credit$|story-secondary$|carousel-body|slider_container|" +
//                    "widget_stories|post-thumbs|^custom-share-links|socialTools|trendingStories|jcarousel-container|module-video-slider|" +
//                    "jcarousel-skin-tango|^most-read-content$|^commentBox$|^faqModal$|^widget-area|login-panel|^copyright$|relatedSidebar|" +
//                    "shareFooterCntr|most-read-container|email-signup|outbrain|^wnStoryBodyGraphic|articleadditionalcontent|most-popular|" +
//                    "shatner-box|form-errors|theme-summary|story-supplement|global-magazine-recent|nocontent|hidden-print|externallinks"
//    );

    private static final Pattern SET_TO_REMOVE = createRegexPattern(
            "meettheauthor|navigation|sidenav|join|discuss|thread|tooltip|no[\\-_]*print|hidden|related[\\-_]*article|popular|feedback|slideshow|thumbnail|share|additional|dont[\\-_]miss"
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
        cleanAuthorName = new StringBuffer(Pattern.compile("(\\s|^)+" + DateUtils.MMM_PATTERN + "(\\s|$)+").matcher(cleanAuthorName).replaceAll(" "));

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

    private static Integer getWeight(Element element) {
        return (element != null && element.hasAttr("weight")) ?
                Integer.parseInt(element.attr("weight")) :
                0;
    }

    private static Integer getChildrenWeight(Element element) {
        return element.select("*").stream()
                .map(child -> getWeight(child))
                .collect(Collectors.summingInt(Integer::intValue));
    }

    public static Integer specialCases(Element element) {
        Integer weight = getWeight(element);

        if (element.hasAttr("itemprop") && weight == 0) {
            if (ITEMPROP.matcher(element.attr("itemprop")).find()) {
                weight = 250;

                for (Element childElement : element.select("*")) {
                    if (childElement.hasAttr("itemprop") && ITEMPROP.matcher(childElement.attr("itemprop")).find()) {
                        childElement.attr("weight", "300");
                        weight += 200;
                    }
                }
            }
        } else {
            long count = element.childNodes().stream()
                    .filter(childNode -> childNode.hasAttr("itemprop") && ITEMPROP.matcher(childNode.attr("itemprop")).find())
                    .collect(Collectors.counting());

            if (count > 1) {
                weight = ((int) count) * 200 + 450;
            }
        }


        if (element.tagName().equals("a") &&
                ((element.attr("href").contains("/author") || element.attr("href").contains("/profile") || element.attr("href").contains("/report")))) {
            weight += 30;
        }

        return weight;
    }

    public static Integer highlyPositiveCases(Element element) {
        Integer weight = 0;

        // Highly Positive
        if (HIGHLY_POSITIVE.matcher(element.className()).matches()) {
            weight += 220;
        } else if (HIGHLY_POSITIVE.matcher(element.className()).find()) {
            weight += 180;
        }

        if (HIGHLY_POSITIVE.matcher(element.id()).matches()) {
            weight += 150;
        } else if (HIGHLY_POSITIVE.matcher(element.id()).find()) {
            weight += 120;
        }

        return weight;
    }

    public static Integer positiveCases(Element element) {
        Integer weight = 0;

        if (POSITIVE.matcher(element.className()).matches()) {
            weight += 100;
        } else if (POSITIVE.matcher(element.className()).find()) {
            weight += 80;
        }

        if (POSITIVE.matcher(element.id()).matches()) {
            weight += 60;
        } else if (POSITIVE.matcher(element.id()).find()) {
            weight += 40;
        }

        return weight;
    }

    private static void cleanUpDocument(Document document) {
        for (Element element : document.select("*")) {
            if (SET_TO_REMOVE.matcher(element.className()).find() ||
                    SET_TO_REMOVE.matcher(element.id()).find() ||
                    StringUtils.isBlank(element.text())) {
                logger.trace("Removing element: " + element.toString());
                element.remove();
            }
        }
    }

    public static String extractAuthor(Document document, String domain) {


        String siteSpecificRule = Configuration.getInstance().getBestElementForAuthor().get(domain);
        if (siteSpecificRule != null) {
            String authorName = document.select(siteSpecificRule).text();

            if (StringUtils.isBlank(authorName)) {
                authorName = document.select(siteSpecificRule).attr("content");
            }

            if (StringUtils.isNotBlank(authorName)) {
                System.out.println(authorName);
                return authorName;
            }
        }

        Comparator byWeight = new Comparator<Element>() {
            @Override
            public int compare(Element e1, Element e2) {

                if (getWeight(e1) < getWeight(e2)) {
                    return 1;
                }

                if (getWeight(e1) > getWeight(e2)) {
                    return -1;
                }

                if (getChildrenWeight(e1) < getChildrenWeight(e2)) {
                    return 1;
                }
                return -1;
            }
        };

        document = document.clone();
        cleanUpDocument(document);

        TreeSet<Element> sortedResultByWeight = new TreeSet<>(byWeight);

        // Remove the elements having the same body
        Map<String, Element> uniqueElementsByBody = new HashMap<>();


        for (Element element : document.select("*")) {

            element.attr("weight", Integer.toString(calWeight(element)));

            if (getWeight(element) <= 0) {
                continue;
            }
            uniqueElementsByBody.put(element.toString(), element);
            // weightedElements.add(element);
        }

        // Array by descending order of weight
        sortedResultByWeight.addAll(uniqueElementsByBody.values());





        // Clean up
        int iterations = 0;
        String authorName;
        for (Element element : sortedResultByWeight) {
            authorName = extractText(element);
            //authorName = IGNORE_WORDS.matcher(authorName).replaceAll("");
            if (sanityCheck(authorName)) {
                return authorName.replaceAll("\\s+", " ").trim();
            }

            if (iterations == 3) {
                break;
            }
        }

        Elements elements = document.select("meta[property=author], meta[property=creator], meta[name=creator], meta[name=author]");
        if (elements != null && elements.size() > 0) {
            for (Element element : elements) {
                authorName = extractText(element);
                if (sanityCheck(authorName)) {
                    return authorName;
                }
            }
        }

        elements = document.select("[rel]");
        if (elements != null && elements.size() > 0) {
            for (Element element : elements) {
                if (HIGHLY_POSITIVE.matcher(element.attr("rel")).find() ||
                        POSITIVE.matcher(element.attr("rel")).find()) {
                    authorName = extractText(element);
                    if (sanityCheck(authorName)) {
                        return authorName;
                    }
                }
            }
        }
        return StringUtils.EMPTY;
    }

    private static Integer calWeight(Element element) {
        return specialCases(element)
                + highlyPositiveCases(element)
                + positiveCases(element);
    }

    private static void nodeToText(Node node, StringBuffer text) {

        String nodesToExtract = "div, p, span, em, h1, h2, h3, h4, a, li, td, b, strong";
        Set<String> nodesToExtractSet = new HashSet<>();
        nodesToExtractSet.addAll(Arrays.asList(nodesToExtract.split(", ")));

        for (Node childNode : node.childNodes()) {
            if (childNode instanceof TextNode && nodesToExtract.contains(childNode.parent().nodeName())) {
                if (text.length() != 0 && !text.toString().endsWith(" | ")) {
                    text.append(" | ");
                }
                if (StringUtils.isNotBlank(((TextNode) childNode).text())) {
                    text.append(((TextNode) childNode).text().trim());
                }
            } else {
                nodeToText(childNode, text);
            }
        }
    }

    private static String extractText(Element element) {

        if (element.tagName() == "meta") {
            return element.attr("content");
        }

        StringBuffer textBuffer = new StringBuffer();
        nodeToText(element, textBuffer);
        return StringUtils.strip(textBuffer.toString().trim(), "|").trim();
    }

    private static Pattern createRegexPattern(String regex) {
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    private static boolean sanityCheck(String authorName) {
        return StringUtils.isNotBlank(authorName);
    }

    /**
     * Extract named entities from the given text
     *
     * @param text {@link String}
     * @return {@link List<NamedEntity>}
     */
    public static List<NamedEntity> extractNamedEntities(String text) {
        logger.info("Extracting named entities from text " + text);

        text = IGNORE_WORDS.matcher(text).replaceAll("");
        text = Pattern.compile(SPECIAL_SYMBOLS_PATTERN).matcher(text).replaceAll(" $1 ");

        EntitiesResponse entitiesResponse = AirPRExtractorApiUtils.getEntities(text);

//        if (entitiesResponse == null || entitiesResponse.getEntities().size() == 0) {
//            logger.info("Unable to extract named entities " + text + " in first attempt");
//
//            // Retry extracting entities
//            // This time add empty space around the special symbols in the text sometimes it helps
//            String modifiedText = Pattern.compile(AuthorUtils.SPECIAL_SYMBOLS_PATTERN).matcher(text).replaceAll(" $1 ");
//            if (!modifiedText.equals(text)) {
//                logger.info("Retrying named entity extraction with modified text " + modifiedText);
//                entitiesResponse = AirPRExtractorApiUtils.getEntities(modifiedText);
//            }
//        }
        if (entitiesResponse == null || entitiesResponse.getEntities().size() == 0) {
            logger.info("Unable to extract named entities for text " + text);
            return Collections.EMPTY_LIST;
        }
        return entitiesResponse.getEntities();
    }

    /**
     * Clean up text using Named Entity Recognition
     * - Try to match and extract Person Names
     * - Try to match Organization Names
     * - Clean up based on regex to get rid of unwanted symbols, suffixes, etc.
     *
     * @param text {@link String}
     * @return {@link String}
     */
    public static String cleanUpUsingNER(String text) {
        String authorName;
        if (StringUtils.isBlank(text)) {
            logger.info("Found empty text. No further processing required.");
            return StringUtils.EMPTY;
        }

        if (!Configuration.getInstance().isUseNamedEntityForAuthorExtraction()) {
            logger.info("Use of NER for author extraction is disabled.");
            authorName = cleanup(text);
            logger.info("Cleaned up author name: " + authorName);
            return authorName;
        }

        logger.info("Looking if text has whitelisted named entities.");
        List<String> identifiedNamedEntities = Configuration.getInstance().getNerExclusion().parallelStream()
                .filter(ne -> text.toLowerCase().contains(ne.toLowerCase()))
                .collect(Collectors.toList());

        if (identifiedNamedEntities.size() > 0) {
            authorName = StringUtils.join(identifiedNamedEntities, ", ");
            logger.info("Found whitelisted named entities. Cleaned up author name: " + authorName);
            return authorName;
        }

        List<NamedEntity> namedEntities = extractNamedEntities(text);
        if (namedEntities == null || namedEntities.size() == 0) {
            authorName = cleanup(text);
            logger.info("Cleaned up author name: " + authorName);
            return authorName;
        }

        TreeMap<Integer, String> sortedByPosition = new TreeMap<>();

        // Lookup for Person Names
        for (NamedEntity entity : getTopNEntities(namedEntities, EntityType.PERSON, 2)) {
            if (text.contains(entity.getRepresentative())) {
                sortedByPosition.put(text.indexOf(entity.getRepresentative()), entity.getRepresentative());
            }
        }

        if (sortedByPosition.size() > 0) {
            authorName = StringUtils.join(cleanUpPersonNames(new LinkedList<String>(sortedByPosition.values())),
                    ", ");
            logger.info("Cleaned up author name: " + authorName);
            return authorName;
        }

        // Lookup for Organization Names if no Person Name is found
        for (NamedEntity entity : getTopNEntities(namedEntities, EntityType.ORGANIZATION, 1)) {
            if (text.contains(entity.getRepresentative())) {
                sortedByPosition.put(text.indexOf(entity.getRepresentative()), entity.getRepresentative());
            }
        }

        if (sortedByPosition.size() > 0) {
            authorName = StringUtils.join(cleanUpOrganizationNames(new LinkedList<String>(sortedByPosition.values())),
                    ", ");
            logger.info("Cleaned up author name: " + authorName);
            return authorName;
        }

        logger.info("Unable to clean up text : " + text);
        return text;
    }

    /**
     * Clean up Person Names with some pre-defined rules
     * - Convert case (Title case) appropriately
     * - Remove unwanted text
     *
     * @param personNames {@link List<String>}
     * @return {@link List<String>} - Cleaned up name
     */

    public static List<String> cleanUpPersonNames(List<String> personNames) {

        // Generally article contains name in well formed case (Title case), so we don't have to do much here
        // So convert names to title case only if the whole name is in small case or uppercase
        final Pattern INVALID_CHARS = Pattern.compile("[^\\w\\.\\-\\' ]+", Pattern.UNICODE_CHARACTER_CLASS);

        return personNames.stream()
                .map(name -> INVALID_CHARS.matcher(name).replaceAll(" "))   // Remove unwanted junk chars
                .map(name -> name.toUpperCase().equals(name) || name.toLowerCase().matches(name) ?
                        WordUtils.capitalizeFully(name, new char[]{' ', '-', '\''}) :
                        name)
                .collect(Collectors.toList());
    }

    /**
     * Clean up Person Names with some pre-defined rules
     * - Convert case (Title case) appropriately
     *
     * @param organizationNames {@link List<String>}
     * @return {@link List<String>} - Cleaned up name
     */
    public static List<String> cleanUpOrganizationNames(List<String> organizationNames) {

        // If organization name is single word and in all caps -> Leave as is e.g. CNN, BBC
        // Else convert it to title case
        return organizationNames.stream()
                .map(name -> {
                            if (name.split("\\s+").length == 1
                                    && name.toUpperCase().equals(name)) {
                                return name;
                            }
                            return WordUtils.capitalizeFully(name, new char[]{' ', '-'});
                        }
                )
                .collect(Collectors.toList());
    }

    /**
     * Return top `n` entities of type `type` from `entities`
     * @param entities {@link List<NamedEntity>}
     * @param type {@link EntityType}
     * @param n {@link int}
     * @return {@link List<EntityType>}
     */
    public static List<NamedEntity> getTopNEntities(List<NamedEntity> entities, EntityType type, int n) {
        return entities.stream()
                .filter(entity -> type.equals(entity.getType()))
                .sorted(Comparator.comparing(NamedEntity::getSalienceScore).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }
}
