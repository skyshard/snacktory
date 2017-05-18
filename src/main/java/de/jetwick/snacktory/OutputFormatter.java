package de.jetwick.snacktory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

/**
 * @author goose | jim
 * @author karussell
 *
 * this class will be responsible for taking our top node and stripping out junk
 * we don't want and getting it ready for how we want it presented to the user
 */
public class OutputFormatter {

    // For debugging
    private static final boolean DEBUG_OUTPUT = false;
    public static final int MIN_FIRST_PARAGRAPH_TEXT = 50; // Min size of first paragraph
    public static final int MIN_PARAGRAPH_TEXT = 30;       // Min size of any other paragraphs
    protected boolean clearWhitespace = true;

    private static final List<String> NODES_TO_REPLACE = Arrays.asList("strong", "b", "i");
    private Pattern unlikelyPattern = Pattern.compile("display\\:none|visibility\\:hidden");
    protected final int minFirstParagraphText;
    protected final int minParagraphText;
    protected final List<String> nodesToReplace;
    protected String nodesToKeepCssSelector = "p, ol, em, ul, li, small, blockquote";

    public OutputFormatter() {
        this(MIN_FIRST_PARAGRAPH_TEXT, MIN_PARAGRAPH_TEXT, NODES_TO_REPLACE);
    }

    public OutputFormatter(int minParagraphText) {
        this(minParagraphText, minParagraphText, NODES_TO_REPLACE);
    }

    public OutputFormatter(int minFirstParagraphText, int minParagraphText) {
        this(minFirstParagraphText, minParagraphText, NODES_TO_REPLACE);
    }

    public OutputFormatter(int minFirstParagraphText, int minParagraphText, 
                           List<String> nodesToReplace) {
        this.minFirstParagraphText = minFirstParagraphText;
        this.minParagraphText = minParagraphText;
        this.nodesToReplace = nodesToReplace;
    }

    /**
     * set elements to keep in output text
     */
    public void setNodesToKeepCssSelector(String nodesToKeepCssSelector) {
        this.nodesToKeepCssSelector = nodesToKeepCssSelector;
    }

    /**
     * Disables removal of line-breaks
     **/
    public void setClearWhitespace(boolean flag) {
	this.clearWhitespace = flag;
    }

    /**
     * takes an element and turns the P tags into \n\n
     * may then remove \n\n content if clearWhitespace
     * setting is in use
     */
    public String getFormattedText(Element topNode, boolean removeNegative) {
        setParagraphIndex(topNode, nodesToKeepCssSelector);
        if(removeNegative){
            removeNodesWithNegativeScores(topNode);
        }
        StringBuilder sb = new StringBuilder();
        int countOfP = append(topNode, sb, nodesToKeepCssSelector);
        String str = this.clearWhitespace ? SHelper.innerTrim(sb.toString()) : sb.toString();

	
        int topNodeLength = topNode.text().length();
        if (topNodeLength == 0) {
            topNodeLength = 1;
        }

        boolean lowTextRatio = ((str.length() / (topNodeLength * 1.0)) < 0.25);
        if (str.length() > 100 && countOfP > 0 && !lowTextRatio)
            return str;

        // no subelements
        if (str.isEmpty() || (!topNode.text().isEmpty() 
            && str.length() <= topNode.ownText().length())
            || countOfP == 0 || lowTextRatio){
            str = topNode.text();
        }

        // if jsoup failed to parse the whole html now parse this smaller 
        // snippet again to avoid html tags disturbing our text:
        return Jsoup.parse(str).text();
    }


    /**
     * If there are elements inside our top node that have a negative gravity
     * score remove them
     */
    protected void removeNodesWithNegativeScores(Element topNode) {
        Elements gravityItems = topNode.select("*[gravityScore]");
        for (Element item : gravityItems) {
            int score = getScore(item);
            int paragraphIndex = getParagraphIndex(item);
            if (score < 0 || item.text().length() < getMinParagraph(paragraphIndex)){
                if (item.parent()!=null)
                    item.remove();
            }
        }
    }
    
    protected int append(Element node, StringBuilder sb, String tagName) {
        int countOfP = 0; // Number of P elements in the article
        int paragraphWithTextIndex = 0;
        boolean hasParagraph = false;

        // is select more costly then getElementsByTag?
        MAIN:
        for (Element e : node.select(tagName)) {

            if(DEBUG_OUTPUT)
                System.out.println("1) Tag:" + e.tagName() + "| text:" + e.text() + "|");

            // If the list is at the beginning of the content we assume it is not part of it.
            if (!hasParagraph){
                if (e.tagName().equals("ul") ||
                    e.tagName().equals("li")){
                    continue;
                } else {
                    hasParagraph = true;
                }
            }

            Element tmpEl = e;
            // check all elements until 'node'
            while (tmpEl != null && !tmpEl.equals(node)) {
                if (unlikely(tmpEl))
                    continue MAIN;
                tmpEl = tmpEl.parent();
            }

            String text = node2Text(e);

            if(DEBUG_OUTPUT)
                System.out.println("2) Tag:" + e.tagName() + "| text:" + text + "|");

            if (e.tagName()!="em"){
                if (text.isEmpty() || text.length() < getMinParagraph(paragraphWithTextIndex) 
                    || text.length() > SHelper.countLetters(text) * 2){
                    continue;
                }
            }


            if (e.tagName().equals("p")){
                countOfP++;
            }

            /* Don't prepend an space to a dot */
            if (paragraphWithTextIndex > 0 && text.length() > 1 )
                sb.append("\n\n");
            sb.append(text);
            
            paragraphWithTextIndex+=1;

            if(DEBUG_OUTPUT)
                System.out.println("SB:" + sb + "|");
        }

        return countOfP;
    }
    
    protected void setParagraphIndex(Element node, String tagName) {
        int paragraphIndex = 0;
        for (Element e : node.select(tagName)) {
            e.attr("paragraphIndex", Integer.toString(paragraphIndex++));
        }
    }

    protected int getMinParagraph(int paragraphIndex){
        if(paragraphIndex < 1){
            return minFirstParagraphText;
        } else {
            return minParagraphText;
        }
    }

    protected int getParagraphIndex(Element el){
        try {
            return Integer.parseInt(el.attr("paragraphIndex"));
        } catch(NumberFormatException ex) {
            return -1;
        }
    }

    protected int getScore(Element el) {
        try {
            return Integer.parseInt(el.attr("gravityScore"));
        } catch (Exception ex) {
            return 0;
        }
    }

    boolean unlikely(Node e) {
        if (e.attr("class") != null && e.attr("class").toLowerCase().contains("caption"))
            return true;

        String style = e.attr("style");
        String clazz = e.attr("class");
        if (unlikelyPattern.matcher(style).find() || unlikelyPattern.matcher(clazz).find())
            return true;
        return false;
    }

    void appendTextSkipHidden(Element e, StringBuilder accum, int indent) {
        for (Node child : e.childNodes()) {
            if (unlikely(child)){
                continue;
            }
            if (child instanceof TextNode) {
                TextNode textNode = (TextNode) child;
                String txt = textNode.text();
                accum.append(txt);
            } else if (child instanceof Element) {
                Element element = (Element) child;
                if (accum.length() > 0 && element.isBlock() 
                    && !lastCharIsWhitespace(accum))
                    accum.append(" ");
                else if (element.tagName().equals("br"))
                    accum.append(" ");
                appendTextSkipHidden(element, accum, indent + 1);
                if (element.tagName().equals("cite")) // Used in cnn.com
                    accum.append(" ");
            }
        }
    }

    void printWithIndent(String text, int indent){
        if (indent <= 0) {
            System.out.println(text);
        } else {
            System.out.println(String.format("%1$" + (indent*2) +"s%2$s", " ", text));
        }
    }

    boolean lastCharIsWhitespace(StringBuilder accum) {
        if (accum.length() == 0)
            return false;
        return Character.isWhitespace(accum.charAt(accum.length() - 1));
    }

    protected String node2TextOld(Element el) {
        return el.text();
    }

    protected String node2Text(Element el) {
        StringBuilder sb = new StringBuilder(200);
        appendTextSkipHidden(el, sb, 0);
        return sb.toString();
    }

    public OutputFormatter setUnlikelyPattern(String unlikelyPattern) {
        this.unlikelyPattern = Pattern.compile(unlikelyPattern);
        return this;
    }

    public OutputFormatter appendUnlikelyPattern(String str) {
        return setUnlikelyPattern(unlikelyPattern.toString() + "|" + str);
    }
}
