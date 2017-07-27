package de.jetwick.snacktory.utils;

import de.jetwick.snacktory.models.*;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AUTH;
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

    private static final Pattern SET_TO_REMOVE = createRegexPattern(
            "without[\\-_]?author|post-list|(twitter|fb|facebook|pinterest|linkedin)[\\-_]?share|entry-user|widget widget_tabs|article[\\-_]*comment(s)?|comment(s)?[\\-_]*article|reply|listen|related[\\-_]*(story|article|content)|(story|article|content)[\\-_]*related|meettheauthor|navigation|sidenav|join|discuss|thread|tooltip|no[\\-_]*print|hidden|related[\\-_]*article|popular|feedback|slideshow|additional|dont[\\-_]miss|comment[\\-_]*author"
    );

    private static final Pattern META_NAME = createRegexPattern(
            "name|author|creator"
    );
    private static final Pattern FACEBOOK_PROFILE_URL_PATTERN = createRegexPattern("((http(s)?://)?(www\\.)?facebook.com/)");
    private static final Pattern STOPWORDS_PATTERN = createRegexPattern("(?<![\\w])(true|false|a|an|are|as|at|be|but|by|for|if|in|into|is|it|no|not|of|on|or|such|that|their|then|there|these|they|this|to|was|will|with|about the|from|Door|Über|by|name|author|posted|twitter|handle|locally researched|report(ing|ed)?|edit(ing|ed)|publish(ed)?|read)(?![\\w])|autor|redakteur|facebook|linkedin|pinterest");
    private static final Pattern MONTHS_PATTERN = createRegexPattern("(?<![\\w])(January|February|March|April|May|June|July|August|September|October|November|December|Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)(?![\\w])");
    private static final Pattern WEEKDAYS_PATTERN = createRegexPattern("(?<![\\w])(Sunday|Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sun|Mon|Tue|Wed|Thu|Fri|Sat)(?![\\w])");
    private static final Pattern INVALID_CHARS_IN_NAMED_ENTITY = createRegexPattern("[^\\w\\.\\-\\' ]+");

    private static final String SPECIAL_SYMBOLS_TO_REMOVE = "\\+|^@|:|\\(|\\)|\\/|\\.\\.\\.|…|\"";
    private static final String ADDITIONAL_SYMBOLS_TO_REMOVE = ",|\\||-|'";     // Can be present as part of name
    private static final String ALL_SYMBOLS = String.format("(%s|%s)", SPECIAL_SYMBOLS_TO_REMOVE, ADDITIONAL_SYMBOLS_TO_REMOVE);

    private static final int MAX_NO_OF_PERSON_NAMES_TO_USE = 2;
    private static final int MAX_NO_OF_ORGANIZATION_NAMES_TO_USE = 1;

    // Private constructor since this is an utility class
    private AuthorUtils() {
    }

    /**
     * Clean up the excessive words, symbols, etc from the authorName
     *
     * @param authorName {@link String}
     * @return {@link String}
     */
    public static String cleanup(String authorName) {
        return preExtractionCleanup(authorName);
    }

    /**
     * This method helps to remove junk text from the author name
     * - Remove date patterns
     * - Remove unwanted symbols
     * - Remove unwanted urls
     *
     * @param authorName {@link String}
     * @return {@link String}
     */
    public static String preExtractionCleanup(String authorName) {
        CharSequence cleanAuthorName = removeFacebookProfileUrl(authorName);
        cleanAuthorName = removeStopWords(cleanAuthorName);
        cleanAuthorName = removeDateTime(cleanAuthorName);
        cleanAuthorName = cleanAuthorName.toString().replaceAll("\\d+", StringUtils.EMPTY); // Remove digits
        cleanAuthorName = removeSpecialSymbols(cleanAuthorName);

        return cleanAuthorName.toString();
    }

    /**
     * Clean up text by applying sequence of rules
     * - Remove unwanted symbols
     * - Remove extra whitespaces
     *
     * @param text {@link String}
     * @return {@link CharSequence}
     */
    public static CharSequence removeSpecialSymbols(CharSequence text) {
        return text.toString()
                .replaceAll(SPECIAL_SYMBOLS_TO_REMOVE, " ")                           // Remove Special Symbols
                .replaceAll(ALL_SYMBOLS + "+\\s*" + ALL_SYMBOLS + "+", " | ")   // Squeeze consecutive Symbols
                .replaceAll("^\\W+|\\W+$", "")                                  // Remove starting and trailing non-word characters
                .replaceAll("\\s+", " ")                                        // Squeeze consecutive Symbols
                .trim();
    }

    /**
     * Remove date / time / days pattern from the text
     *
     * @param text {@link String}
     * @return {@link CharSequence}
     */
    public static CharSequence removeDateTime(CharSequence text) {

        StringBuffer cleanText = new StringBuffer(text);

        // Remove Date patterns if any
        for (Pattern pattern : DateUtils.DATE_PATTERNS) {
            cleanText = new StringBuffer(pattern.matcher(cleanText).replaceAll(StringUtils.EMPTY));
        }

        // Remove Month Name Patterns
        cleanText = new StringBuffer(MONTHS_PATTERN.matcher(cleanText).replaceAll(StringUtils.EMPTY));

        // Remove Weekdays Names
        cleanText = new StringBuffer(WEEKDAYS_PATTERN.matcher(cleanText).replaceAll(StringUtils.EMPTY));

        return cleanText.toString();
    }

    /**
     * Remove stop words from the text
     *
     * @param text {@link String}
     * @return {@link CharSequence}
     */
    public static CharSequence removeStopWords(CharSequence text) {
        return STOPWORDS_PATTERN.matcher(text).replaceAll(StringUtils.EMPTY);
    }

    /**
     * Clean up facebook url and get profile name
     *
     * @param text {@link String}
     * @return {@link CharSequence}
     */
    public static CharSequence removeFacebookProfileUrl(String text) {
        return FACEBOOK_PROFILE_URL_PATTERN.matcher(text).replaceAll(StringUtils.EMPTY);
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
                        childElement.attr("weight", Integer.toString(highlyPositiveCases(childElement) + positiveCases(element) + negativeCases(childElement) + 300));
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
                ((element.attr("href").contains("/author") || element.attr("href").contains("/profile")))) {
            weight += 30;
        }

        return weight;
    }

    public static Integer highlyPositiveCases(Element element) {
        Integer weight = 0;
//        if (element.tagName().equals("meta") && HIGHLY_POSITIVE.matcher(element.attr("name")).find()) {
//            weight += 200;
//        }

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
                    SET_TO_REMOVE.matcher(element.id()).find()) {
                logger.trace("Removing element because of : " + element.toString());
                element.remove();
            } else if ((!element.tagName().equals("meta")) && StringUtils.isBlank(element.text())) {
                element.remove();
            }
        }
    }

    public static String extractAuthor(Document document, String domain) {


        String siteSpecificRule = Configuration.getInstance().getBestElementForAuthor().get(domain);
        if (siteSpecificRule != null) {


            Element probableElement = document.select(siteSpecificRule).first();

            String authorName = null;
            if (probableElement != null) {
                authorName = probableElement.text();
            }


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

        // Remove the elements having the same body
        Map<String, Element> uniqueElementsByBody = new HashMap<>();


        for (Element element : document.select("*")) {

            element.attr("weight", Integer.toString(calWeight(element)));

            if (getWeight(element) <= 0) {
                continue;
            }
            uniqueElementsByBody.put(element.toString(), element);
        }

        // Array by descending order of weight
        TreeSet<Element> sortedResultByWeight = new TreeSet<>(byWeight);
        sortedResultByWeight.addAll(uniqueElementsByBody.values());


        int sameWeightedElements = 0;

        if (sortedResultByWeight.size() > 0) {
            int weightTopElement = getWeight(sortedResultByWeight.first());
            String classTopElement = sortedResultByWeight.first().className();
            int iteration = 0;
            for (Element element : sortedResultByWeight) {

                if (iteration == 0) {
                    iteration++;
                    continue;

                }
                iteration++;
                if (getWeight(element) == weightTopElement && classTopElement.equals(element.className())) {
                    sameWeightedElements++;
                } else {
                    break;
                }
            }
            if (sameWeightedElements > 2) {

                List<Element> temp = new LinkedList<>();

                for (Element element : sortedResultByWeight) {
                    if (weightTopElement != getWeight(element)) {
                        temp.add(element);
                    }
                }

                sortedResultByWeight.clear();
                sortedResultByWeight.addAll(temp);
            }
        }


        // Clean up
        int iterations = 0;
        String authorName;
        for (Element element : sortedResultByWeight) {
            authorName = extractText(element);
            //authorName = IGNORE_WORDS.matcher(authorName).replaceAll("");
            if (sanityCheck(authorName)) {
                return authorName.replaceAll("\\s+", " ").trim();
            }

            if (iterations == 2) {
                break;
            }
        }

        Elements elements = document.select("meta[property=author], meta[property=creator], meta[name=creator], meta[name=author], meta[name=dcterms.creator]");
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
                + positiveCases(element)
                + negativeCases(element);

    }

    private static Integer negativeCases(Element element) {

        int weight = 0;
        if (SET_TO_REMOVE.matcher(element.className()).find() ||
                SET_TO_REMOVE.matcher(element.id()).find()) {
            weight -= 200;

        } else if ((!element.tagName().equals("meta")) && StringUtils.isBlank(element.text())) {
            weight -= 100;
        }
        return weight;
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
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);
    }

    private static boolean sanityCheck(String authorName) {
        String cleanedUpAuthorName = preExtractionCleanup(authorName);
        if (cleanedUpAuthorName.length() < 3 || cleanedUpAuthorName.length() > 150) {
            return false;
        }
        return true;
    }

    /**
     * Extract named entities from the given text
     *
     * @param text {@link String}
     * @return {@link List< NamedEntity >}
     */
    public static List<NamedEntity> extractNamedEntities(String text) {
        text = preExtractionCleanup(text);
        logger.info("Pre Entity Extractio Clean Up: " + text);
        logger.info("Extracting named entities from text " + text);

//        text = IGNORE_WORDS.matcher(text).replaceAll("");
//        text = Pattern.compile(SPECIAL_SYMBOLS_PATTERN).matcher(text).replaceAll(" $1 ");

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
    public static AuthorInfo cleanUpUsingNER(String text) {
        AuthorInfo authorInfo = new AuthorInfo();

        if (StringUtils.isBlank(text)) {
            logger.info("Found empty text. No further processing required.");
            return authorInfo;
        }

        if (!Configuration.getInstance().isUseNamedEntityForAuthorExtraction()) {
            logger.info("Use of NER for author extraction is disabled.");
            authorInfo.setNames(new String[]{cleanup(text)});
            logger.info("Cleaned up author name: " + authorInfo.getNamesAsString());
            return authorInfo;
        }

        logger.info("Looking if text has whitelisted named entities.");
        Map<String, String> identifiedNamedEntities = Configuration.getInstance().getNerExclusion().entrySet().stream()
                .filter(e -> StringUtils.containsIgnoreCase(text, e.getKey()))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        if (identifiedNamedEntities.size() > 0) {
            authorInfo.setNames(identifiedNamedEntities.keySet());
            authorInfo.setEntityType(EntityType.forValue(identifiedNamedEntities.values().iterator().next()));
            logger.info("Found whitelisted named entities. Cleaned up author name: " + authorInfo.getNamesAsString());
            return authorInfo;
        }

        List<NamedEntity> namedEntities = extractNamedEntities(text);
        if (namedEntities == null || namedEntities.size() == 0) {
            authorInfo.setNames(new String[]{cleanup(text)});
            logger.info("Cleaned up author name: " + authorInfo.getNamesAsString());
            return authorInfo;
        }

        TreeMap<Integer, String> sortedByPosition = new TreeMap<>();

        // Lookup for Person Names
        getTopNEntities(namedEntities, EntityType.PERSON,  MAX_NO_OF_PERSON_NAMES_TO_USE).stream()
                .filter(e -> StringUtils.containsIgnoreCase(text, e.getRepresentative()))
                .map(e -> cleanUpPersonName(e.getRepresentative()))
                .forEach(name -> sortedByPosition.put(StringUtils.indexOfIgnoreCase(text, name), name));

        if (sortedByPosition.size() > 0) {
            authorInfo.setNames(sortedByPosition.values());
            authorInfo.setEntityType(EntityType.PERSON);
            logger.info("Cleaned up author name: " + authorInfo.getNamesAsString());
            return authorInfo;
        }

        getTopNEntities(namedEntities, EntityType.ORGANIZATION, MAX_NO_OF_ORGANIZATION_NAMES_TO_USE).stream()
                .filter(e -> StringUtils.containsIgnoreCase(text, e.getRepresentative()))
                .map(e -> cleanUpOrganizationName(e.getRepresentative()))
                .forEach(name -> sortedByPosition.put(StringUtils.indexOfIgnoreCase(text, name), name));

        if (sortedByPosition.size() > 0) {
            authorInfo.setNames(sortedByPosition.values());
            authorInfo.setEntityType(EntityType.ORGANIZATION);
            logger.info("Cleaned up author name: " + authorInfo.getNamesAsString());
            return authorInfo;
        }

        logger.info("Unable to clean up text : " + text);
        authorInfo.setNames(new String[]{cleanup(text)});
        return authorInfo;
    }

    /**
     * Clean up Person Name with pre-defined rules
     * - Convert case (Title case) appropriately
     * - Remove unwanted text
     *
     * @param personName {@link String}
     * @return {@link String} - Cleaned up name
     */
    public static String cleanUpPersonName(String personName) {
        // Remove unwanted junk chars
        personName = INVALID_CHARS_IN_NAMED_ENTITY.matcher(personName).replaceAll(" ");

        // Generally article contains name in well formed case (Title case), so we don't have to do much here
        // So convert names to title case only if the whole name is in small case or uppercase
        return personName.toUpperCase().equals(personName) || personName.toLowerCase().matches(personName) ?
                WordUtils.capitalizeFully(personName, new char[]{' ', '-', '\''}) :
                personName;
    }

    /**
     * Clean up Organization Name with pre-defined rules
     * - Convert case (Title case) appropriately
     *
     * @param organizationName {@link String}
     * @return {@link String} - Cleaned up name
     */
    public static String cleanUpOrganizationName(String organizationName) {

        // If organization name is single word and in all caps -> Leave as is e.g. CNN, BBC
        // Else convert it to title case
        if (organizationName.split("\\s+").length == 1
                && organizationName.toUpperCase().equals(organizationName)) {
            return organizationName;
        }
        return WordUtils.capitalizeFully(organizationName, new char[]{' ', '-'});
    }

    /**
     * Return top `n` entities of type `type` from `entities` based on salience score
     *
     * @param entities {@link List<NamedEntity>}
     * @param type     {@link EntityType}
     * @param n        {@link int}
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
