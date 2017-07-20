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

            // Remove the Prefixes
            Pattern.compile("(?<![\\w])(about the|from|Door|Über|by|name|author|posted|twitter|handle|news|locally researched|report(ing|ed)?( by)?|edit(ing|ed)( by)?)(?![\\w])", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS),
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
            "Facebook|Pinterest|Twitter|Linkedin"
    );
    final static Pattern ITEMPROP = createRegexPattern("person|name|author|creator");
    private static final Logger logger = LoggerFactory.getLogger(AuthorUtils.class);
    private static final int MAX_AUTHOR_NAME_LENGTH = 255;
    private static final Pattern HIGHLY_POSITIVE = createRegexPattern(
            "autor|author|author[\\-_]*name|article[\\-_]*author[\\-_]*name|author[\\-_]*card|story[\\-_]*author|" +
                    "author[\\-_]*link|date[\\-_]*author|author[\\-_]*date|byline|byline[\\-_]*name|byLine[\\-_]Tag|" +
                    "contrib[\\-_]*byline|vcard|profile"
    );
    private static final Pattern POSITIVE = createRegexPattern(
            "address|time[\\-_]date|post[\\-_]*date|source|news[\\-_]*post[\\-_]*source|meta[\\-_]*author|" +
                    "author[\\-_]*meta|writer|submitted|creator|reporter[\\-_]*name|profile-data|posted|contact"
    );
    private static final Pattern SET_TO_REMOVE = createRegexPattern(
            "tooltip|no_print|related[\\-_]*post(s)?|sidenav|navigation|feedback[\\-_]*prompt|related[\\-_]*combined[\\-_]*coverage|visually[\\-_]*hidden|page-footer|" +
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

    private static Integer getWeight(Element element) {
        return (element != null && element.hasAttr("weight")) ?
                Integer.parseInt(element.attr("weight")) :
                0;
    }

    private static Integer getMaxOccurance(Element element) {
        return (element != null && element.hasAttr("max_occurance")) ?
                Integer.parseInt(element.attr("max_occurance")) :
                0;
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
                (element.attr("href").contains("/author") || element.attr("href").contains("/profile") || element.attr("href").contains("/report"))) {
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

    private static void cleanUpDocument(Document document) {
        for (Element element : document.select("*")) {
            if (SET_TO_REMOVE.matcher(element.className()).find()) {
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

        //Comparator<Element> byWeight = (Element e1, Element e2) -> getWeight(e1).compareTo(getWeight(e2));
        Comparator<Element> byOccurance = (Element e1, Element e2) -> getMaxOccurance(e1).compareTo(getMaxOccurance(e2));

        Comparator byWeight = new Comparator<Element>() {
            @Override
            public int compare(Element e1, Element e2) {

                if (getWeight(e1) < getWeight(e2)) {
                    return 1;
                }

                if (getWeight(e1) > getWeight(e2)) {
                    return -1;
                }

                // @Todo: As of now don't know how to chose better element if weight is equal
                return 0;
            }
        };

        document = document.clone();
        cleanUpDocument(document);

        TreeSet<Element> sortedResultByWeight = new TreeSet<>(byWeight);

        for (Element element : document.select("*")) {

            if (element.tagName().equals("meta")) {
                continue;
            }

            if (StringUtils.isBlank(element.text())) {
                continue;
            }

            element.attr("weight", Integer.toString(calWeight(element)));

            if (getWeight(element) <= 0) {
                continue;
            }
            sortedResultByWeight.add(element);
        }

        int iterations = 0;
        String authorName;
        for (Element element : sortedResultByWeight) {
            authorName = extractText(element);
            authorName = IGNORE_WORDS.matcher(authorName).replaceAll("");
            if (sanityCheck(authorName)) {
                return authorName.replaceAll("\\s+", " ").trim();
            }

            if (iterations == 3) {
                break;
            }
        }

        Elements elements = document.select("meta[property*=author], meta[property*=creator]");
        if (elements != null && elements.size() > 0) {
            for (Element element : elements) {
                authorName = extractText(element);
                if (sanityCheck(authorName)) {
                    return authorName;
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

//        // Item Prop
//        Set<String> textSet = element.select("[itemprop]").stream()
//                .filter(childElement -> !childElement.equals(element))
//                .filter(childElement -> ITEMPROP.matcher(childElement.attr("itemprop")).find())
//                .map(childElement -> childElement.text())
//                .collect(Collectors.toSet());
//
//
//        if (textSet.size() > 0) {
//            return StringUtils.join(textSet, ", ");
//        }

        StringBuffer textBuffer = new StringBuffer();
        nodeToText(element, textBuffer);
        return StringUtils.strip(textBuffer.toString().trim(), "|").trim();
    }

    private static Pattern createRegexPattern(String regex) {
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    private static boolean sanityCheck(String authorName) {
        return StringUtils.isNotBlank(authorName) && authorName.split(" ").length > 1;
    }


    /**
     * Extract named entities from the given text
     *
     * @param text {@link String}
     * @return {@link List<NamedEntity>}
     */
    public static List<NamedEntity> extractNamedEntities(String text) {
        logger.info("Extracting named entities from text " + text);
        EntitiesResponse entitiesResponse = AirPRExtractorApiUtils.getEntities(text);

        if (entitiesResponse == null || entitiesResponse.getEntities().size() == 0) {
            logger.info("Unable to extract named entities " + text + " in first attempt");
            logger.info("Retrying named entity extraction with modified text " + text);

            // Retry extracting entities
            // This time add empty space around the special symbols in the text sometimes it helps
            String modifiedText = Pattern.compile(AuthorUtils.SPECIAL_SYMBOLS_PATTERN).matcher(text).replaceAll(" $1 ");

            logger.info("Retrying named entity extraction with modified text " + modifiedText);
            entitiesResponse = AirPRExtractorApiUtils.getEntities(modifiedText);
        }
        if (entitiesResponse == null || entitiesResponse.getEntities().size() == 0) {
            logger.info("Unable to extract named entities for text " + text);
            return Collections.EMPTY_LIST;
        }
        return entitiesResponse.getEntities();
    }

    public static String cleanUpUsingNER(String text) {
        if (StringUtils.isBlank(text)) {
            return StringUtils.EMPTY;
        }

        if (!Configuration.getInstance().isUseNamedEntityForAuthorExtraction()) {
            return cleanup(text);
        }

        List<String> identifiedNamedEntities = Configuration.getInstance().getNerExclusion().parallelStream()
                .filter(ne -> text.toLowerCase().contains(ne.toLowerCase()))
                .collect(Collectors.toList());

        if (identifiedNamedEntities.size() > 0) {
            return StringUtils.join(identifiedNamedEntities, ", ");
        }

        List<NamedEntity> namedEntities = extractNamedEntities(text);
        if (namedEntities == null || namedEntities.size() == 0) {
            return cleanup(text);
        }

        // Lookup for Person Names
        TreeMap<Integer, String> sortedByPosition = new TreeMap<>();
        for (NamedEntity entity : getTopNEntities(namedEntities, EntityType.PERSON, 2)) {
            if (text.contains(entity.getRepresentative())) {
                sortedByPosition.put(text.indexOf(entity.getRepresentative()), entity.getRepresentative());
            }
        }

        if (sortedByPosition.size() > 0) {
            return StringUtils.join(cleanUpPersonNames(new LinkedList<String>(sortedByPosition.values())),
                    ", ");
        }

        // Lookup for Organization Names if no Person Name found
        for (NamedEntity entity : getTopNEntities(namedEntities, EntityType.ORGANIZATION, 1)) {
            if (text.contains(entity.getRepresentative())) {
                sortedByPosition.put(text.indexOf(entity.getRepresentative()), entity.getRepresentative());
            }
        }

        if (sortedByPosition.size() > 0) {
            return StringUtils.join(cleanUpOrganizationNames(new LinkedList<String>(sortedByPosition.values())),
                    ", ");
        }

        return StringUtils.EMPTY;
    }

    public static List<String> cleanUpPersonNames(List<String> personNames) {

        // Generally article contains name in well formed case (Title case), so we don't have to do much here
        // So convert names to title case only if the whole name is in small case or uppercase

        final Pattern INVALID_CHARS = Pattern.compile("[^\\w\\.\\-\\' ]+", Pattern.UNICODE_CHARACTER_CLASS);

        return personNames.stream()
                .limit(2)
                .map(name -> INVALID_CHARS.matcher(name).replaceAll(" "))   // Remove unwanted junk chars
                .map(name -> name.toUpperCase().equals(name) || name.toLowerCase().matches(name) ?
                        WordUtils.capitalizeFully(name, new char[]{' ', '-', '\''}) :
                        name)
                .collect(Collectors.toList());
    }

    public static List<String> cleanUpOrganizationNames(List<String> organizationNames) {

        // If organization name is single word and in all caps -> Leave as is e.g. CNN, BBC
        // Else convert it to title case
        return organizationNames.stream()
                .limit(1)
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

    public static void main(String[] args) {

        //System.out.println(CharMatcher.JAVA_UPPER_CASE.matchesAllOf("ABHISHEKMULAY"));
        System.out.println(cleanUpUsingNER("***" + "Abhishek Mulay, Mark Luther"));
        System.out.println(cleanUpUsingNER("*** " + "BBC and Google Inc"));
    }
}
