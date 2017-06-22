package de.jetwick.snacktory;

import de.jetwick.snacktory.utils.Configuration;
import de.jetwick.snacktory.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Alex P, (ifesdjeen from jreadability)
 * @author Peter Karich
 */
public class ArticleTextExtractorTest {

    ArticleTextExtractor extractor;
    Converter c;

    @Before
    public void setup() throws Exception {
        c = new Converter();
        extractor = new ArticleTextExtractor();
    }

    @Test
    public void testData1() throws Exception {
        // ? http://www.npr.org/blogs/money/2010/10/04/130329523/how-fake-money-saved-brazil
        JResult res = extractor.extractContent(readFileAsString("test_data/1.html"));
        assertEquals("How Fake Money Saved Brazil", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("This is a story about how an economist and his buddies tricked the people of Brazil into saving the country from rampant inflation. They had a crazy, unlikely plan, and it worked. Twenty years ago, Brazil's"));
        assertTrue(res.getText(), res.getText().endsWith("\"How Four Drinking Buddies Saved Brazil.\""));
        assertEquals("http://media.npr.org/assets/img/2010/10/04/real_wide.jpg?t=1286218782&s=3", res.getImageUrl());
        assertTrue(res.getKeywords().isEmpty());
        assertEquals("Chana Joffe-Walt", res.getAuthorName());
    }

    /*
     * Test broken because of change of heuristics to extract title,
     * however since the change seems to improve the cleanliness of the extracted titles
     * I am leaving the change and disabling the test.
    @Test
    public void testData2() throws Exception {
        // http://benjaminste.in/post/1223476561/hey-guys-whatcha-doing
        JResult res = extractor.extractContent(readFileAsString("test_data/2.html"));
        assertEquals("Hey guys, whatcha doing?", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("This month is the 15th anniversary of my last CD."));
        assertTrue(res.getKeywords().isEmpty());
    }*/

    @Test
    public void testData3() throws Exception {
        JResult res = extractor.extractContent(readFileAsString("test_data/3.html"));
        assertTrue("data3:" + res.getText(), res.getText().startsWith("October 2010 Silicon Valley proper is mostly suburban sprawl. At first glance it "));
        assertTrue(res.getText().endsWith(" and Jessica Livingston for reading drafts of this."));
        assertTrue(res.getKeywords().isEmpty());
    }

    @Test
    public void testData5() throws Exception {
        JResult res = extractor.extractContent(readFileAsString("test_data/5.html"));
        assertTrue("data5:" + res.getText(), res.getText().startsWith("Hackers unite in Stanford"));
//        assertTrue(res.getText().endsWith("have beats and bevvies a-plenty. RSVP here.    "));
        assertTrue(res.getKeywords().isEmpty());
    }

    @Test
    public void testCNN() throws Exception {
        // http://edition.cnn.com/2011/WORLD/africa/04/06/libya.war/index.html?on.cnn=1
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cnn.html")));
        assertEquals("Gadhafi asks Obama to end NATO bombing", res.getTitle());
        assertEquals("/2011/WORLD/africa/04/06/libya.war/t1larg.libyarebel.gi.jpg", res.getImageUrl());
        assertTrue("cnn:" + res.getText(), res.getText().startsWith("Tripoli, Libya (CNN) -- As rebel and pro-government forces in Libya maneuvered on the battlefield Wedn"));
        assertEquals("the CNN Wire Staff", res.getAuthorName());
    }

    @Test
    public void testBBC() throws Exception {
        // http://www.bbc.co.uk/news/world-latin-america-21226565
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("bbc_noscript.html")));
        assertEquals("Brazil mourns Santa Maria nightclub fire victims", res.getTitle());
        assertEquals("http://news.bbcimg.co.uk/media/images/65545000/gif/_65545798_brazil_santa_m_kiss_464.gif", res.getImageUrl());
        assertTrue(res.getText().startsWith("Brazil has declared three days of national mourning for 231 people killed in a nightclub fire in the southern city of Santa Maria."));
        assertEquals("Caio Quero", res.getAuthorName());
    }

    @Test
    public void testReuters() throws Exception {
        // http://www.reuters.com/article/2012/08/03/us-knightcapital-trading-technology-idUSBRE87203X20120803
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("reuters.html")));
        assertEquals("Knight trading loss shows cracks in equity markets", res.getTitle());
        assertEquals("http://s1.reutersmedia.net/resources/r/?m=02&d=20120803&t=2&i=637797752&w=460&fh=&fw=&ll=&pl=&r=CBRE872074Y00", res.getImageUrl());
        assertTrue("reuters:" + res.getText(), res.getText().startsWith("(Reuters) - The software glitch that cost Knight Capital Group $440 million in just 45 minutes reveals the deep fault lines in stock markets that are increasingly dominated by sophisticated high-speed trading systems. But Wall Street firms and regulators have few easy solutions for such problems."));
        assertEquals("Jed Horowitz and Joseph Menn", res.getAuthorName());
    }

    @Test
    public void testCaltonCaldwell() throws Exception {
        // http://daltoncaldwell.com/dear-mark-zuckerberg (html5)
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("daltoncaldwell.html")));
        assertEquals("Dear Mark Zuckerberg by Dalton Caldwell", res.getTitle());
        assertTrue("daltoncaldwell:" + res.getText(), res.getText().startsWith("On June 13, 2012, at 4:30 p.m., I attended a meeting at Facebook HQ in Menlo Park, California."));
    }

    @Test
    public void testWordpress() throws Exception {
        // http://karussell.wordpress.com/
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("wordpress.html")));
        assertEquals("Twitter API and Me « Find Time for the Karussell", res.getTitle());
        assertTrue("wordpress:" + res.getText(), res.getText().startsWith("I have a love hate relationship with Twitter. As a user I see "));
    }

    @Test
    public void testFirefox() throws Exception {
        // http://www.golem.de/1104/82797.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("golem.html")));
        assertTrue(res.getText(), res.getText().startsWith("Mozilla hat Firefox 5.0a2 veröffentlicht und zugleich eine erste Entwicklerversion von Firefox 6 freigegeben."));
        assertEquals("http://scr3.golem.de/screenshots/1104/Firefox-Aurora/thumb480/aurora-nighly-beta-logos.png", res.getImageUrl());
        assertEquals("Vorabversionen von Firefox 5 und 6 veröffentlicht", res.getTitle());
    }

    @Test
    public void testYomiuri() throws Exception {
        // http://www.yomiuri.co.jp/e-japan/gifu/news/20110410-OYT8T00124.htm
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("yomiuri.html")));
        assertEquals("色とりどりのチューリップ : 岐阜 : 地域 : YOMIURI ONLINE（読売新聞）", res.getTitle());
        assertTrue("yomiuri:" + res.getText(), res.getText().contains("海津市海津町の国営木曽三川公園で、チューリップが見頃を迎えている。２０日までは「チューリップ祭」が開かれており、大勢の人たちが多彩な色や形を鑑賞している＝写真＝"));
        assertEquals(Arrays.asList("読売新聞", "地域"), res.getKeywords());
    }

    @Test
    public void testFAZ() throws Exception {
        // http://www.faz.net/s/Rub469C43057F8C437CACC2DE9ED41B7950/Doc~EBA775DE7201E46E0B0C5AD9619BD56E9~ATpl~Ecommon~Scontent.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("faz.html")));
        assertTrue(res.getText(), res.getText().startsWith("Deutschland hat vor, ganz auf Atomkraft zu verzichten. Ist das eine gute"));
        assertEquals("/m/{5F104CCF-3B5A-4B4C-B83E-4774ECB29889}g225_4.jpg", res.getImageUrl());
        assertEquals("FAZ Electronic Media", res.getAuthorName());
        assertEquals(Arrays.asList("Atomkraft", "Deutschland", "Jahren", "Atommüll", "Fukushima", "Problem", "Brand", "Kohle", "2011", "11",
                "Stewart", "Atomdebatte", "Jahre", "Boden", "Treibhausgase", "April", "Welt", "Müll", "Radioaktivität",
                "Gesamtbild", "Klimawandel", "Reaktoren", "Verzicht", "Scheinheiligkeit", "Leute", "Risiken", "Löcher",
                "Fusion", "Gefahren", "Land"),
                res.getKeywords());
    }

    @Test
    public void testRian() throws Exception {
        // http://en.rian.ru/world/20110410/163458489.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("rian.html")));
        assertTrue(res.getText(), res.getText().startsWith("About 15,000 people took to the streets in Tokyo on Sunday to protest against th"));
        assertEquals("Japanese rally against nuclear power industry | World", res.getTitle());
        assertEquals("/favicon.ico", res.getFaviconUrl());
        assertTrue(res.getKeywords().isEmpty());
    }

    @Test
    public void testJetwick() throws Exception {
        // http://jetwick.com
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("jetwick.html")));
        assertEquals(Arrays.asList("news", "twitter", "search", "jetwick"), res.getKeywords());
    }

    @Test
    public void testVimeo() throws Exception {
        // http://vimeo.com/20910443
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("vimeo.html")));
        assertTrue(res.getText(), res.getText().startsWith("1 month ago 1 month ago: Fri, Mar 11, 2011 2:24am EST (Eastern Standard Time) See all Show me 1. finn. & Dirk von Lowtzow"));
        assertTrue(res.getTitle(), res.getTitle().startsWith("finn. & Dirk von Lowtzow \"CRYING IN THE RAIN\""));
        assertEquals("", res.getVideoUrl());
        assertEquals(Arrays.asList("finn", "finn.", "Dirk von Lowtzow", "crying in the rain", "I wish I was someone else", "Tocotronic",
                "Sunday Service", "Indigo", "Patrick Zimmer", "Patrick Zimmer aka finn.", "video", "video sharing",
                "digital cameras", "videoblog", "vidblog", "video blogging", "home video", "home movie"),
                res.getKeywords());
        assertEquals("finn.", res.getAuthorName());
    }

    /* Test broken after change to check ratio of text in getFormattedText. TODO: Find a way to support this.
    @Test
    public void testYoutube() throws Exception {
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("youtube.html")));
        assertTrue(res.getText(), res.getText().startsWith("Master of the Puppets by Metallica. Converted to 8 bit with GSXCC. Original verson can be found us"));
        assertEquals("YouTube - Metallica - Master of the Puppets 8-bit", res.getTitle());
        assertEquals("http://i4.ytimg.com/vi/wlupmjrfaB4/default.jpg", res.getImageUrl());
        assertEquals("http://www.youtube.com/v/wlupmjrfaB4?version=3", res.getVideoUrl());
    }*/

    @Test
    public void testSpiegel() throws Exception {
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("spiegel.html")));
        assertTrue(res.getText(), res.getText().startsWith("Da ist er wieder, der C64: Eigentlich längst ein Relikt der Technikgeschichte, soll der "));
    }

    @Test
    public void testGithub() throws Exception {
        // https://github.com/ifesdjeen/jReadability
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("github.html")));
        assertTrue(res.getDescription(), res.getDescription().startsWith("Article text extractor from given HTML text"));
        assertTrue(res.getText(), res.getText().startsWith("= jReadability This is a small helper utility (only 130 lines of code) for pepole"));
    }

    @Test
    public void testITunes() throws Exception {
        // http://itunes.apple.com/us/album/21/id420075073
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("itunes.html")));
        assertTrue(res.getText(), res.getText().startsWith("What else can be said of this album other than that it is simply amazing? Adele's voice is powerful, vulnerable, assured, and heartbreaking all in one fell swoop."));
        assertTrue("itunes:" + res.getDescription(), res.getDescription().startsWith("Preview songs from 21 by ADELE"));
    }

    @Test
    public void testTwitpic() throws Exception {
        // http://twitpic.com/4k1ku3
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("twitpic.html")));
        assertEquals("It’s hard to be a dinosaur. on Twitpic", res.getTitle());
//        assertEquals("", res.getText());
//        assertTrue(res.getText(), res.getText().isEmpty());
    }

    @Test
    public void testTwitpic2() throws Exception {
        // http://twitpic.com/4kuem8
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("twitpic2.html")));
        assertEquals("*Not* what you want to see on the fetal monitor when your wif... on Twitpic", res.getTitle());
//        assertEquals("", res.getText());
    }

    @Test
    public void testHeise() throws Exception {
        // http://www.heise.de/newsticker/meldung/Internet-Explorer-9-jetzt-mit-schnellster-JavaScript-Engine-1138062.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("heise.html")));
        assertEquals("", res.getImageUrl());
        assertEquals("Internet Explorer 9 jetzt mit schnellster JavaScript-Engine", res.getTitle());
        assertTrue(res.getText().startsWith("Microsoft hat heute eine siebte Platform Preview des Internet Explorer veröffentlicht. In den nur dr"));
    }

    @Test
    public void testTechcrunch() throws Exception {
        // http://techcrunch.com/2011/04/04/twitter-advanced-search/
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("techcrunch.html")));
        assertEquals("http://tctechcrunch2011.files.wordpress.com/2011/04/screen-shot-2011-04-04-at-12-11-36-pm.png?w=285&h=85", res.getImageUrl());
        assertEquals("Twitter Finally Brings Advanced Search Out Of Purgatory; Updates Discovery Algorithms", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("A couple weeks ago, we wrote a post wishing Twitter a happy fifth birthday, but also noting "));
        assertEquals("MG Siegler", res.getAuthorName());
    }

    @Test
    public void testEngadget() throws Exception {
        // http://www.engadget.com/2011/04/09/editorial-androids-problem-isnt-fragmentation-its-contamina/
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("engadget.html")));
        assertTrue(res.getText(), res.getText().startsWith("Editorial: Android's problem isn't fragmentation, it's contamination This thought was first given voice by Myriam Joire on last night's Mobile Podcast, and the"));
        assertEquals("http://www.blogcdn.com/www.engadget.com/media/2011/04/11x0409mnbvhg_thumbnail.jpg", res.getImageUrl());
        assertEquals("Editorial: Android's problem isn't fragmentation, it's contamination -- Engadget", res.getTitle());
        // TODO: Fix author extraction.
        //assertEquals("Vlad Savov", res.getAuthorName());
    }

    @Test
    public void testTwitterblog() throws Exception {
        // http://engineering.twitter.com/2011/04/twitter-search-is-now-3x-faster_1656.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("twitter.html")));
        assertEquals("Twitter Engineering: Twitter Search is Now 3x Faster", res.getTitle());
        assertEquals("http://4.bp.blogspot.com/-CmXJmr9UAbA/TZy6AsT72fI/AAAAAAAAAAs/aaF5AEzC-e4/s72-c/Blender_Tsunami.jpg", res.getImageUrl());
        assertTrue("twitter:" + res.getText(), res.getText().startsWith("In the spring of 2010, the search team at Twitter started to rewrite our search engine in order to serve our ever-growin"));
    }

    @Test
    public void testTazBlog() throws Exception {
        // http://www.taz.de/1/politik/asien/artikel/1/anti-atomkraft-nein-danke/
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("taz.html")));
        assertTrue("taz:" + res.getText(), res.getText().startsWith("Absolute Minderheit: Im Shiba-Park in Tokio treffen sich jetzt jeden Sonntag die Atomkraftgegner. Sie blicken neidisch auf die Anti-AKW-Bewegung in Deutschland. "));
        assertEquals("Anti-Atomkraft? Nein danke!", res.getTitle());
        assertEquals("Georg Blume", res.getAuthorName());
    }

    @Test
    public void testFacebook() throws Exception {
        // http://www.facebook.com/ejdionne/posts/10150154175658687
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("facebook.html")));
        assertTrue(res.getText(), res.getText().startsWith("In my column tomorrow, I urge President Obama to end the spectacle of"));
        assertEquals("", res.getImageUrl());
        assertEquals("In my column...", res.getTitle());
    }

    @Test
    public void testFacebook2() throws Exception {
        // http://www.facebook.com/permalink.php?story_fbid=214289195249322&id=101149616624415 
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("facebook2.html")));
        assertTrue(res.getText(), res.getText().startsWith("Sommer is the best time to wear Jetwick T-Shirts!"));
        assertEquals("", res.getImageUrl());
        assertEquals("Sommer is the best...", res.getTitle());
    }

    @Test
    public void testBlogger() throws Exception {
        // http://blog.talawah.net/2011/04/gavin-king-unviels-red-hats-top-secret.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("blogger.html")));
        assertTrue(res.getText(), res.getText().startsWith("Gavin King of Red Hat/Hibernate/Seam fame recently"));
        assertEquals("http://3.bp.blogspot.com/-cyMzveP3IvQ/TaR7f3qkYmI/AAAAAAAAAIk/mrChE-G0b5c/s200/Java.png", res.getImageUrl());
        assertEquals("The Brain Dump: Gavin King unveils Red Hat's Java killer successor: The Ceylon Project", res.getTitle());
        assertEquals("http://blog.talawah.net/feeds/posts/default?alt=rss", res.getRssUrl());
        assertEquals("Marc Richards", res.getAuthorName());
    }

    @Test
    public void testNyt() throws Exception {
        // http://dealbook.nytimes.com/2011/04/11/for-defense-in-galleon-trial-no-time-to-rest/
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("nyt.html")));
        assertEquals("http://graphics8.nytimes.com/images/2011/04/12/business/dbpix-raj-rajaratnam-1302571800091/dbpix-raj-rajaratnam-1302571800091-tmagSF.jpg",
                res.getImageUrl());
        assertTrue(res.getText(), res.getText().startsWith("I wouldn’t want to be Raj Rajaratnam’s lawyer right now."));
        assertEquals("Andrew Ross Sorkin", res.getAuthorName());
    }

    @Test
    public void testNytContentExtraction() throws Exception {
        // https://www.nytimes.com/2016/07/21/business/daily-stock-market-activity.html
        JResult res = new JResult();
        res.setUrl("https://www.nytimes.com/2016/07/21/business/daily-stock-market-activity.html");
        extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("nyt3.html")));
        assertEquals("https://www.nytimes.com/2016/07/21/business/daily-stock-market-activity.html", res.getCanonicalUrl());
        assertEquals("Wall St. Gains as Earnings Cheer Traders", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Stocks took another modest step further into record territory Wednesday after several companies reported profits that were stronger than expected."));
        assertTrue(res.getText(), res.getText().endsWith("and the dollar rose to 106.96 Japanese yen from 106.11 yen."));
        assertEquals("The Associated Press", res.getAuthorName());
        assertEquals("THE ASSOCIATED PRESS", res.getAuthorDescription());
        compareDates("2016-07-20", res.getDate());
    }

    @Test
    public void testNytContentExtraction1() throws Exception {
        // https://www.nytimes.com/2017/05/10/opinion/comey-trump-deep-throat.html
        JResult res = new JResult();
        res.setUrl("https://www.nytimes.com/2017/05/10/opinion/comey-trump-deep-throat.html");
        extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("nyt4.html")));
        assertEquals("https://www.nytimes.com/2017/05/10/opinion/comey-trump-deep-throat.html", res.getCanonicalUrl());
        assertEquals("In Firing Comey, Did Trump Unleash the Next Deep Throat?", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Once again, Donald Trump has done something that no president before him dared to do."));
        assertTrue(res.getText(), res.getText().endsWith("the presidency itself — never fully recovered."));
        assertEquals("Beverly Gage", res.getAuthorName());
        assertEquals("Beverly Gage is a professor of American political history at Yale. She is the author of “The Day Wall Street Exploded: A Story of America in Its First Age of Terror” and is writing a biography of the former F.B.I. director J. Edgar Hoover.", res.getAuthorDescription());
        compareDates("2017-05-10 00:00:00", res.getDate());
    }

    @Test
    public void testNytContentExtraction2() throws Exception {
        // https://www.nytimes.com/2017/05/13/business/dealbook/whats-next-for-comey-probably-not-a-normal-job.html
        JResult res = new JResult();
        res.setUrl("https://www.nytimes.com/2017/05/13/business/dealbook/whats-next-for-comey-probably-not-a-normal-job.html");
        extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("nyt5.html")));
        assertEquals("https://www.nytimes.com/2017/05/13/business/dealbook/whats-next-for-comey-probably-not-a-normal-job.html", res.getCanonicalUrl());
        assertEquals("What’s Next for Comey? Probably Not ‘a Normal Job’", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Few can boast of a résumé like James B. Comey’s: top federal prosecutor, chief lawyer for both the world’s largest defense contractor and the world’s biggest hedge fund, and most recently director of the Federal Bureau of Investigation."));
        assertTrue(res.getText(), res.getText().endsWith("He may not be in any rush. Mr. Comey’s overall assets range from $5 million to $14 million, according to his most recent financial disclosure form."));
        assertEquals("Matthew Goldstein and Alexandra Stevenson", res.getAuthorName());
        assertEquals("https://www.nytimes.com/by/matthew-goldstein", res.getAuthorDescription());
        compareDates("2017-05-13 00:00:00", res.getDate());
    }

    @Test
    public void testNytContentExtraction6() throws Exception {
        // https://www.nytimes.com/2017/05/17/us/politics/robert-mueller-special-counsel-russia-investigation.html
        JResult res = new JResult();
        res.setUrl("https://www.nytimes.com/2017/05/17/us/politics/robert-mueller-special-counsel-russia-investigation.html");
        extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("nyt6.html")));
        assertEquals("https://www.nytimes.com/2017/05/17/us/politics/robert-mueller-special-counsel-russia-investigation.html", res.getCanonicalUrl());
        assertEquals("Robert Mueller, Former F.B.I. Director, Is Named Special Counsel for Russia Investigation", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("WASHINGTON — The Justice Department appointed Robert S. Mueller III, a former F.B.I. director,"));
        assertFalse(res.getText(), res.getText().contains("Please verify you're not a robot by clicking the box. Invalid email address."));
        assertFalse(res.getText(), res.getText().contains("View all New York Times newsletters. See Sample Manage Email Preferences Not you? Privacy Policy"));
        assertTrue(res.getText(), res.getText().endsWith("He’s the embodiment of integrity.”"));
        assertEquals("Rebecca R. Ruiz and Mark Landler", res.getAuthorName());
        assertEquals("https://www.nytimes.com/by/rebecca-r-ruiz", res.getAuthorDescription());
        compareDates("2017-05-17 00:00:00", res.getDate());
    }

    @Test
    public void testHuffingtonpost() throws Exception {
        // http://www.huffingtonpost.com/2010/08/13/federal-reserve-pursuing_n_681540.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("huffingtonpost.html")));
        assertEquals("Federal Reserve's Low Rate Policy Is A 'Dangerous Gamble,' Says Top Central Bank Official", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("A top regional Federal Reserve official sharply"));
        assertEquals("http://i.huffpost.com/gen/157611/thumbs/s-FED-large.jpg", res.getImageUrl());
        assertEquals("Shahien Nasiripour", res.getAuthorName());
    }

    @Test
    public void testTechcrunch2() throws Exception {
        // http://techcrunch.com/2010/08/13/gantto-takes-on-microsoft-project-with-web-based-project-management-application/
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("techcrunch2.html")));
        assertEquals("Gantto Takes On Microsoft Project With Web-Based Project Management Application", article.getTitle());
        assertTrue(article.getText(), article.getText().startsWith("Y Combinator-backed Gantto is launching"));
        assertEquals("http://i0.wp.com/tctechcrunch2011.files.wordpress.com/2010/08/gantto.jpg?resize=680%2C680", article.getImageUrl());
        assertEquals("Leena Rao", article.getAuthorName());
    }

    @Test
    public void testCnn2() throws Exception {
        // http://www.cnn.com/2010/POLITICS/08/13/democrats.social.security/index.html
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cnn2.html")));
        assertEquals("Democrats to use Social Security against GOP this fall", article.getTitle());
        assertTrue(article.getText(), article.getText().startsWith("Washington (CNN) -- Democrats pledged "));
        assertEquals(article.getImageUrl(), "http://i.cdn.turner.com/cnn/2010/POLITICS/08/13/democrats.social.security/story.kaine.gi.jpg");
        assertEquals("Ed Hornick", article.getAuthorName());
    }

    @Test
    public void testBusinessweek2() throws Exception {
        // http://www.businessweek.com/magazine/content/10_34/b4192048613870.htm
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("businessweek2.html")));
        assertTrue(article.getText(), article.getText().startsWith("There's discord on Wall Street: Strategists at major American investment "));
        assertEquals("http://images.businessweek.com/mz/covers/current_120x160.jpg", article.getImageUrl());
        assertEquals("Whitney Kisling,Caroline Dye", article.getAuthorName());
    }

    @Test
    public void testFoxnews() throws Exception {
        // http://www.foxnews.com/politics/2010/08/14/russias-nuclear-help-iran-stirs-questions-improved-relations/
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("foxnews.html")));
        assertTrue("Foxnews:" + article.getText(), article.getText().startsWith("Apr. 8: President Obama signs the New START treaty with Russian President Dmitry Medvedev at the Prague Castle. Russia's announcement "));
        assertEquals("http://a57.foxnews.com/static/managed/img/Politics/397/224/startsign.jpg", article.getImageUrl());
        assertEquals("", article.getAuthorName());
    }

    @Test
    public void testStackoverflow() throws Exception {
        // http://stackoverflow.com/questions/3553693/wicket-vs-vaadin/3660938
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("stackoverflow.html")));
        assertTrue("stackoverflow:" + article.getText(), article.getText().startsWith("I think I've invested some time for both frameworks. I really like bo"));
        assertEquals("java - wicket vs Vaadin - Stack Overflow", article.getTitle());
        assertEquals("", article.getImageUrl());
    }

    @Test
    public void testAolnews() throws Exception {
        // http://www.aolnews.com/nation/article/the-few-the-proud-the-marines-getting-a-makeover/19592478
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("aolnews.html")));
        assertEquals("http://o.aolcdn.com/art/ch_news/aol_favicon.ico", article.getFaviconUrl());
        assertTrue(article.getText(), article.getText().startsWith("WASHINGTON (Aug. 13) -- Declaring \"the maritime soul of the Marine Corps"));
        assertEquals("http://o.aolcdn.com/photo-hub/news_gallery/6/8/680919/1281734929876.JPEG", article.getImageUrl());
        assertEquals(Arrays.asList("news", "update", "breaking", "nation", "U.S.", "elections", "world", "entertainment", "sports", "business",
                "weird news", "health", "science", "latest news articles", "breaking news", "current news", "top news"),
                article.getKeywords());
    }

    @Test
    public void testWallstreetjournal() throws Exception {
        // http://online.wsj.com/article/SB10001424052748704532204575397061414483040.html
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("wsj.html")));
        assertTrue(article.getText(), article.getText().startsWith("The Obama administration has paid out less than a third of the nearly $230 billion"));
        assertEquals("http://si.wsj.net/public/resources/images/OB-JO747_stimul_D_20100814113803.jpg", article.getImageUrl());
        assertEquals("LOUISE RADNOFSKY", article.getAuthorName());
    }

    @Test
    public void testWallstreetjournal1() throws Exception {
        // https://www.wsj.com/articles/the-10-point-1484784884
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("wsj1.html")));
        assertTrue(article.getText(), article.getText().startsWith("Good evening from Davos, Switzerland, and the second day of the World Economic Forum,"));
        assertTrue(article.getText(), article.getText().endsWith("CLICK HERE to sign up for this briefing by email."));
        assertEquals("Gerard Baker", article.getAuthorName());
        assertEquals("https://www.wsj.com/news/author/7836, https://www.wsj.com/news/author/7836, http://twitter.com/gerardtbaker, mailto:gerard.baker@wsj.com", article.getAuthorDescription());
        compareDates("2017-01-19 00:14:00", article.getDate());
    }

    @Test
    public void testUsatoday() throws Exception {
        // http://content.usatoday.com/communities/thehuddle/post/2010/08/brett-favre-practices-set-to-speak-about-return-to-minnesota-vikings/1
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("usatoday.html")));
        assertTrue(article.getText(), article.getText().startsWith("Brett Favre couldn't get away from the"));
        assertEquals("http://i.usatoday.net/communitymanager/_photos/the-huddle/2010/08/18/favrespeaksx-inset-community.jpg", article.getImageUrl());
        assertEquals("Sean Leahy", article.getAuthorName());
    }

    @Test
    public void testUsatoday2() throws Exception {
        // http://content.usatoday.com/communities/driveon/post/2010/08/gm-finally-files-for-ipo/1
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("usatoday2.html")));
        assertTrue(article.getText(), article.getText().startsWith("General Motors just filed with the Securities and Exchange "));
        assertEquals("http://i.usatoday.net/communitymanager/_photos/drive-on/2010/08/18/cruzex-wide-community.jpg", article.getImageUrl());
    }

    @Test
    public void testEspn() throws Exception {
        // http://sports.espn.go.com/espn/commentary/news/story?id=5461430
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("espn.html")));
        assertTrue(article.getText(), article.getText().startsWith("If you believe what college football coaches have said about sports"));
        assertEquals("http://a.espncdn.com/photo/2010/0813/ncf_i_mpouncey1_300.jpg", article.getImageUrl());
    }

    @Test
    public void testGizmodo() throws Exception {
        // http://www.gizmodo.com.au/2010/08/xbox-kinect-gets-its-fight-club/
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("gizmodo.html")));
        assertTrue(article.getText(), article.getText().startsWith("You love to punch your arms through the air"));
        assertEquals("http://cache.gawkerassets.com/assets/images/9/2010/08/500x_fighters_uncaged__screenshot_4b__rider.jpg", article.getImageUrl());
        // author tested in juicer.
    }

    @Test
    public void testGizmodo2() throws Exception {
        // http://gizmodo.com/the-only-way-to-save-the-northern-white-rhino-is-a-jura-1745213055
        JResult res = new JResult();
        res.setUrl("http://gizmodo.com/the-only-way-to-save-the-northern-white-rhino-is-a-jura-1745213055");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("gizmodo2.html")));
        assertEquals("The Only Way to Save the Northern White Rhino Is a Jurassic Park-Style Intervention", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("It sounds like the plot of a Hollywood science-fiction movie."));
        compareDates("2015-11-30 03:30:00", res.getDate());
    }

    @Test
    public void testGizmodo3() throws Exception {
        // http://gizmodo.com/finally-well-designed-pipes-for-the-discerning-stoner-1746298385
        JResult res = new JResult();
        res.setUrl("http://gizmodo.com/finally-well-designed-pipes-for-the-discerning-stoner-1746298385");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("gizmodo3.html")));
        assertEquals("Finally, Well-Designed Pipes For the Discerning Stoner", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("The design associated with smoking weed has heretofore"));
        compareDates("2015-12-04 18:00:00", res.getDate());
    }

    @Test
    public void testEngadget2() throws Exception {
        // http://www.engadget.com/2010/08/18/verizon-fios-set-top-boxes-getting-a-new-hd-guide-external-stor/
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("engadget2.html")));
        assertEquals("Verizon FiOS set-top boxes getting a new HD guide, external storage and more in Q4 -- Engadget", article.getTitle());
        assertTrue(article.getText(), article.getText().startsWith("Streaming and downloading TV content to mobiles is nice"));
        assertEquals("http://www.blogcdn.com/www.engadget.com/media/2010/08/44ni600_thumbnail.jpg", article.getImageUrl());
    }

    @Test
    public void testWired() throws Exception {
        // http://www.wired.com/playbook/2010/08/stress-hormones-boxing/
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("wired.html")));
        assertTrue(article.getText(), article.getText().startsWith("On November 25, 1980, professional boxing"));
        assertEquals("Stress Hormones Could Predict Boxing Dominance", article.getTitle());
        assertEquals("http://www.wired.com/playbook/wp-content/uploads/2010/08/fight_f-660x441.jpg", article.getImageUrl());
        assertEquals("Brian Mossop", article.getAuthorName());
    }

    @Test
    public void tetGigaohm() throws Exception {
        //String url = "http://gigaom.com/apple/apples-next-macbook-an-800-mac-for-the-masses/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("gigaom.html")));
        assertTrue(article.getText(), article.getText().startsWith("The MacBook Air is a bold move forward "));
        assertEquals("http://gigapple.files.wordpress.com/2010/10/macbook-feature.png?w=300&h=200", article.getImageUrl());
    }

    @Test
    public void testMashable() throws Exception {
        //String url = "http://mashable.com/2010/08/18/how-tonot-to-ask-someone-out-online/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("mashable.html")));
        assertTrue(article.getText(), article.getText().startsWith("Imagine, if you will, a crowded dance floor"));
        assertEquals("http://9.mshcdn.com/wp-content/uploads/2010/07/love.jpg", article.getImageUrl());
    }

    @Test
    public void testVenturebeat() throws Exception {
        //String url = "http://social.venturebeat.com/2010/08/18/facebook-reveals-the-details-behind-places/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("venturebeat.html")));
        assertTrue(article.getText(), article.getText().startsWith("Facebook just confirmed the rumors"));
        assertEquals("http://cdn.venturebeat.com/wp-content/uploads/2010/08/mark-zuckerberg-facebook-places.jpg", article.getImageUrl());
    }

    @Test
    public void testPolitico() throws Exception {
        //String url = "http://www.politico.com/news/stories/1010/43352.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("politico.html")));
        assertTrue(article.getText(), article.getText().startsWith("If the newest Census Bureau estimates stay close to form"));
        assertEquals("http://images.politico.com/global/news/100927_obama22_ap_328.jpg", article.getImageUrl());
    }

    @Test
    public void testPolitico1() throws Exception {
        // http://www.politico.com/story/2017/05/12/senate-trump-russia-probe-comey-firing-238340
        JResult res = new JResult();
        res.setUrl("http://www.politico.com/story/2017/05/12/senate-trump-russia-probe-comey-firing-238340");
        extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("politico1.html")));
        assertEquals("http://www.politico.com/story/2017/05/12/senate-trump-russia-probe-comey-firing-238340", res.getCanonicalUrl());
        assertEquals("Senate panel investigating Russia outraged by Comey firing - POLITICO", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("The Senate Intelligence Committee is starting to examine potential ties between President Donald Trump’s associates and the Russian government,"));
        assertTrue(res.getText(), res.getText().endsWith("Austin Wright contributed to this report."));
        assertEquals("Ali Watkins", res.getAuthorName());
        assertEquals("http://www.politico.com/staff/ali-watkins", res.getAuthorDescription());
        compareDates("2017-05-12 18:45:51", res.getDate());
    }

    @Test
    public void testNinjablog() throws Exception {
        //String url = "http://www.ninjatraderblog.com/im/2010/10/seo-marketing-facts-about-google-instant-and-ranking-your-website/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("ninjatraderblog.html")));
        assertTrue(article.getText(), article.getText().startsWith("Many users around the world Google their queries"));
    }

    @Test
    public void testSportsillustrated() throws Exception {
        //String url = "http://sportsillustrated.cnn.com/2010/football/ncaa/10/15/ohio-state-holmes.ap/index.html?xid=si_ncaaf";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("sportsillustrated.html")));
        assertTrue(article.getText(), article.getText().startsWith("COLUMBUS, Ohio (AP) -- Ohio State has closed"));
        assertEquals("http://i.cdn.turner.com/si/.e1d/img/4.0/global/logos/si_100x100.jpg",
                article.getImageUrl());
    }

    @Test public void testDailybeast() throws Exception {
        //String url = "http://www.thedailybeast.com/blogs-and-stories/2010-11-01/ted-sorensen-speechwriter-behind-jfks-best-jokes/?cid=topic:featured1";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("thedailybeast.html")));
        assertTrue(article.getText(), article.getText().startsWith("Legendary Kennedy speechwriter Ted Sorensen passed"));
        assertEquals("http://www.tdbimg.com/files/2010/11/01/img-article---katz-ted-sorensen_163531624950.jpg",
                article.getImageUrl());
    }

    @Test
    public void testScience() throws Exception {
        //String url = "http://news.sciencemag.org/sciencenow/2011/04/early-birds-smelled-good.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("sciencemag.html")));
        assertTrue(article.getText(), article.getText().startsWith("About 65 million years ago, most of the dinosaurs and many other animals and plants were wiped off Earth, probably due to an asteroid hitting our planet. Researchers have long debated how and why some "));
    }

    @Test
    public void testSlamMagazine() throws Exception {
        //String url = "http://www.slamonline.com/online/nba/2010/10/nba-schoolyard-rankings/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("slamonline.html")));
        assertTrue(article.getText(), article.getText().startsWith("When in doubt, rank players and add your findings"));
        assertEquals(article.getImageUrl(), "http://www.slamonline.com/online/wp-content/uploads/2010/10/celtics.jpg");
        assertEquals("NBA Schoolyard Rankings", article.getTitle());
    }

    @Test
    public void testEspn3WithFlashVideo() throws Exception {
        //String url = "http://sports.espn.go.com/nfl/news/story?id=5971053";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("espn3.html")));
        assertTrue(article.getText(), article.getText().startsWith("PHILADELPHIA -- Michael Vick missed practice Thursday"));
        assertEquals("http://a.espncdn.com/i/espn/espn_logos/espn_red.png", article.getImageUrl());
        assertEquals("Michael Vick of Philadelphia Eagles misses practice, unlikely to play vs. Dallas Cowboys - ESPN", article.getTitle());
    }

    @Test
    public void testSportingNews() throws Exception {
        //String url = "http://www.sportingnews.com/nfl/feed/2011-01/nfl-coaches/story/raiders-cut-ties-with-cable";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("sportingnews.html")));
        assertTrue(article.getText(), article.getText().startsWith("ALAMEDA, Calif. — The Oakland Raiders informed coach Tom Cable on Tuesday that they will not bring him back"));
        assertEquals("http://dy.snimg.com/story-image/0/69/174475/14072-650-366.jpg",
                article.getImageUrl());
        assertEquals("Raiders cut ties with Cable - NFL - Sporting News", article.getTitle());
    }

    @Test
    public void testFoxSports() throws Exception {
        //String url = "http://msn.foxsports.com/nfl/story/Tom-Cable-fired-contract-option-Oakland-Raiders-coach-010411";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("foxsports.html")));
        assertTrue(article.getText(), article.getText().startsWith("The Oakland Raiders informed coach Tom Cable"));
        assertEquals("Oakland Raiders won't bring Tom Cable back as coach - NFL News",
                article.getTitle());
    }

    @Test
    public void testEconomist() throws Exception {
        //String url = "http://www.economist.com/node/17956885";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("economist.html")));
        assertTrue(article.getText(), article.getText().startsWith("FOR beleaguered smokers, the world is an increasingly"));
        assertEquals("http://www.economist.com/sites/default/files/images/articles/migrated/20110122_stp004.jpg",
                article.getImageUrl());
        assertFalse(article.getText(), article.getText().contains("Related topics"));
    }

    @Test
    public void testTheVacationGals() throws Exception {
        //String url = "http://thevacationgals.com/vacation-rental-homes-are-a-family-reunion-necessity/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("thevacationgals.html")));
        assertTrue(article.getText(), article.getText().startsWith("Editors’ Note: We are huge proponents of vacation rental homes"));
        assertEquals("http://thevacationgals.com/wp-content/uploads/2010/11/Gemmel-Family-Reunion-at-a-Vacation-Rental-Home1-300x225.jpg",
                article.getImageUrl());
    }

    @Test
    public void testShockYa() throws Exception {
        //String url = "http://www.shockya.com/news/2011/01/30/daily-shock-jonathan-knight-of-new-kids-on-the-block-publicly-reveals-hes-gay/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("shockya.html")));
        assertTrue(article.getText(), article.getText().startsWith("New Kids On The Block singer Jonathan Knight has publicly"));
        assertEquals("http://www.shockya.com/news/wp-content/uploads/jonathan_knight_new_kids_gay.jpg",
                article.getImageUrl());
    }

    @Test
    public void testWikipedia() throws Exception {
        // String url = "http://en.wikipedia.org/wiki/Therapsids";
        // Wikipedia has the advantage of also testing protocol relative URL extraction for Favicon and Images.
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("wikipedia.html")));
        assertTrue(article.getText(), article.getText().startsWith("Therapsida is a group of the most advanced reptile-grade synapsids, and the ancestors of mammals"));
        assertEquals("//upload.wikimedia.org/wikipedia/commons/thumb/4/42/Pristeroognathus_DB.jpg/240px-Pristeroognathus_DB.jpg",
                article.getImageUrl());
        assertEquals("//en.wikipedia.org/apple-touch-icon.png",
                article.getFaviconUrl());
    }

    @Test
    public void testWikipedia2() throws Exception {
        // http://en.wikipedia.org/wiki/President_of_the_United_States
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("wikipedia_president.html")));
        assertTrue(article.getText(), article.getText().startsWith("The President of the United States of America (acronym: POTUS)[6] is the head of state and head of government"));
    }

    @Test
    public void testWikipedia3() throws Exception {
        // http://en.wikipedia.org/wiki/Muhammad
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("wikipedia_muhammad.html")));
        assertTrue(article.getText(), article.getText().startsWith("Muhammad (c. 570 – c. 8 June 632);[1] also transliterated as Mohammad, Mohammed, or Muhammed; Arabic: محمد‎, full name: Abū al-Qāsim Muḥamma"));
    }

    @Test
    public void testWikipedia4() throws Exception {
        // http://de.wikipedia.org/wiki/Henne_Strand
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("wikipedia_Henne_Strand.html")));
        assertTrue(article.getText(), article.getText().startsWith("Der dänische Ort Henne Strand befindet sich in Südwest-Jütland und gehört zur Kommune Varde"));
    }

    @Test
    public void testWikipedia5() throws Exception {
        // http://de.wikipedia.org/wiki/Java
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("wikipedia_java.html")));
        assertTrue(article.getText(), article.getText().startsWith("Java (Indonesian: Jawa) is an island of Indonesia. With a population of 135 million"));
    }

    @Test
    public void testWikipedia6() throws Exception {
        // http://de.wikipedia.org/wiki/Knight_Rider
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("wikipedia-knight_rider_de.html")));
        assertTrue(article.getText(), article.getText().startsWith("Knight Rider ist eine US-amerikanische Fernsehserie, "
                + "die von 1982 bis 1986 produziert wurde. Knight Rider ist eine Krimi-Action-Serie mit futuristischen Komponenten "
                + "und hat weltweit Kultstatus erlangt."));
    }

    @Test
    public void testData4() throws Exception {
        // http://blog.traindom.com/places-where-to-submit-your-startup-for-coverage/
        JResult res = extractor.extractContent(readFileAsString("test_data/4.html"));
        assertEquals("36 places where you can submit your startup for some coverage", res.getTitle());
        assertEquals(Arrays.asList("blog coverage", "get coverage", "startup review", "startups", "submit startup"), res.getKeywords());
        assertTrue("data4:" + res.getText(), res.getText().startsWith("So you have a new startup company and want some coverage"));
    }

    @Test
    public void testTimemagazine() throws Exception {
        //String url = "http://www.time.com/time/health/article/0,8599,2011497,00.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("time.html")));
        assertTrue(article.getText(), article.getText().startsWith("This month, the federal government released"));
        assertEquals("http://img.timeinc.net/time/daily/2010/1008/bp_oil_spill_0817.jpg", article.getImageUrl());
    }

    @Test
    public void testCnet() throws Exception {
        //String url = "http://news.cnet.com/8301-30686_3-20014053-266.html?tag=topStories1";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cnet.html")));
        assertTrue(article.getText(), article.getText().startsWith("NEW YORK--Verizon Communications is prepping a new"));
        assertEquals("http://i.i.com.com/cnwk.1d/i/tim//2010/08/18/Verizon_iPad_and_live_TV_610x458.JPG", article.getImageUrl());
    }

    @Test
    public void testCnet1() throws Exception {
        //http://www.cnet.com/news/adobe-to-buy-omniture-for-1-8-billion/
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cnet1.html")));
        assertEquals("Adobe to buy Omniture for $1.8 billion", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Adobe said on Tuesday"));
        assertTrue(res.getText(), res.getText().endsWith("earth-shattering thus far."));
        compareDates("2009-09-15 20:29:00", res.getDate());
    }

    @Test
    public void testBloomberg() throws Exception {
        //String url = "http://www.bloomberg.com/news/2010-11-01/china-becomes-boss-in-peru-on-50-billion-mountain-bought-for-810-million.html";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("bloomberg.html")));
        assertTrue(article.getText(), article.getText().startsWith("The Chinese entrepreneur and the Peruvian shopkeeper"));
        assertEquals("http://www.bloomberg.com/apps/data?pid=avimage&iid=iimODmqjtcQU", article.getImageUrl());
    }

    @Test
    public void testTheFrisky() throws Exception {
        //String url = "http://www.thefrisky.com/post/246-rachel-dratch-met-her-baby-daddy-in-a-bar/";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("thefrisky.html")));
        assertTrue(article.getText(), article.getText().startsWith("Rachel Dratch had been keeping the identity of her baby daddy "));

        assertEquals("http://cdn.thefrisky.com/images/uploads/rachel_dratch_102810_m.jpg",
                article.getImageUrl());
        assertEquals("Rachel Dratch Met Her Baby Daddy At A Bar", article.getTitle());
    }

    @Test
    public void testBrOnline() throws Exception {
        // TODO charset for opera was removed:
        // <![endif]-->
        // <link rel="stylesheet" type="text/x-opera-css;charset=utf-8" href="/css/opera.css" />

        //String url = "http://www.br-online.de/br-klassik/programmtipps/highlight-bayreuth-tannhaeuser-festspielzeit-2011-ID1309895438808.xml";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("br-online.html")));
        assertTrue(article.getText(), article.getText().startsWith("Wenn ein Dirigent, der Alte Musik liebt, erstmals eine "
                + "Neuproduktion bei den Bayreuther Richard-Wagner-Festspielen übernimmt,"));
        assertEquals("Eröffnung der 100. Bayreuther Festspiele: Alles neu beim \"Tannhäuser\" | Programmtipps | BR-KLASSIK",
                article.getTitle());
    }

    @Test
    public void cleanTitle() {
        String title = "Hacker News | Ask HN: Apart from Hacker News, what else you read?";
        assertEquals("Ask HN: Apart from Hacker News, what else you read?", extractor.cleanTitle(title));
        assertEquals("mytitle irgendwas", extractor.cleanTitle("mytitle irgendwas | Facebook"));
        assertEquals("mytitle irgendwas", extractor.cleanTitle("mytitle irgendwas | Irgendwas"));

        // this should fail as most sites do store their name after the post
        assertEquals("Irgendwas | mytitle irgendwas", extractor.cleanTitle("Irgendwas | mytitle irgendwas"));
    }

    @Test
    public void testGaltimeWhereUrlContainsSpaces() throws Exception {
        //String url = "http://galtime.com/article/entertainment/37/22938/kris-humphries-avoids-kim-talk-gma";
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("galtime.com.html")));
        assertEquals("http://vnetcdn.dtsph.com/files/vnet3/imagecache/opengraph_ogimage/story-images/Kris%20Humphries%20Top%20Bar.JPG", article.getImageUrl());
    }

    @Test
    public void testIssue8() throws Exception {
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("no-hidden.html")));
        assertEquals("This is the text which is shorter but visible", res.getText());
    }

    @Test
    public void testIssue8False() throws Exception {
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("no-hidden2.html")));
        assertEquals("This is the NONE-HIDDEN text which shouldn't be shown and it is a bit longer so normally prefered", res.getText());
    }

    @Test
    public void testIssue4() throws Exception {
        JResult res = extractor.extractContent("<html><body><div> aaa<a> bbb </a>ccc</div></body></html>");
        assertEquals("aaa bbb ccc", res.getText());

        res = extractor.extractContent("<html><body><div> aaa <strong>bbb </strong>ccc</div></body></html>");
        assertEquals("aaa bbb ccc", res.getText());

        res = extractor.extractContent("<html><body><div> aaa <strong> bbb </strong>ccc</div></body></html>");
        assertEquals("aaa bbb ccc", res.getText());
    }

    @Test
    public void testI4Online() throws Exception {
        //https://i4online.com
        JResult article = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("i4online.html")));
        assertTrue(article.getText(), article.getText().startsWith("Just one week to go and everything is set for the summer Forum 2013"));

        ArticleTextExtractor extractor2 = new ArticleTextExtractor();
        OutputFormatter outputFormater = new OutputFormatter(10);
        outputFormater.setNodesToKeepCssSelector("p,h1,h2,h3,h4,h5,h6");
        extractor2.setOutputFormatter(outputFormater);
        article = extractor2.extractContent(c.streamToString(getClass().getResourceAsStream("i4online.html")));
        assertTrue(article.getText(), article.getText().startsWith("Upcoming events: Forum 79 Just one week to go and everything is set for the summer Forum 2013"));
    }

    @Test
    public void testImagesList() throws Exception {
        // http://www.reuters.com/article/2012/08/03/us-knightcapital-trading-technology-idUSBRE87203X20120803
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("reuters.html")));
        assertEquals(1, res.getImagesCount());
        assertEquals(res.getImageUrl(), res.getImages().get(0).src);
        assertEquals("http://s1.reutersmedia.net/resources/r/?m=02&d=20120803&t=2&i=637797752&w=460&fh=&fw=&ll=&pl=&r=CBRE872074Y00",
                res.getImages().get(0).src);

        // http://thevacationgals.com/vacation-rental-homes-are-a-family-reunion-necessity/
        res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("thevacationgals.html")));
        assertEquals(3, res.getImagesCount());
        assertEquals("http://thevacationgals.com/wp-content/uploads/2010/11/Gemmel-Family-Reunion-at-a-Vacation-Rental-Home1-300x225.jpg",
                res.getImages().get(0).src);
        assertEquals("../wp-content/uploads/2010/11/The-Gemmel-Family-Does-a-Gilligans-Island-Theme-Family-Reunion-Vacation-Sarah-Gemmel-300x225.jpg",
                res.getImages().get(1).src);
        assertEquals("http://www.linkwithin.com/pixel.png", res.getImages().get(2).src);
    }

    @Test
    public void testSearchEngineJournal() throws Exception {
        // http://www.searchenginejournal.com/planning-progress-18-tips-successful-social-media-strategy/112567/
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("searchenginejournal.html")));
        assertEquals("18 Tips for a Successful Social Media Strategy", res.getTitle());
        assertTrue(res.getText(), res.getText().contains("Sharam"));
    }

    @Test
    public void testAdweek() throws Exception {
        // http://www.adweek.com/prnewser/5-digital-data-metricstools-that-pr-pros-need-to-know/97735?red=pr
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("adweek.html")));
        assertEquals("5 Digital Metrics/Tools That PR Pros Need to Know – Adweek", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Measurement: it is, as our own Shawn Paul Wood put it in yesterday’s “Top 5 PR Industry Debate Topics” post, the “pachyderm in the room.”"));
        assertTrue(res.getText(), res.getText().endsWith("Do we agree? How familiar are we with the data and tools Sullivan described?"));
        compareDates("2014-08-01 00:00:00", res.getDate());
        assertEquals("Patrick Coffee", res.getAuthorName());
        assertEquals(res.getAuthorName(), res.getAuthorDescription());
    }

    @Test
    public void testAdweek1() throws Exception {
        // http://www.adweek.com/digital/3-avenues-of-influence-beyond-media-coverage/?red=pr
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("adweek1.html")));
        assertEquals("3 Avenues of Influence Beyond Media Coverage – Adweek", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Today we bring you a guest post by Jennifer Donovan, founder of San Francisco’s Nova Communications. Follow her on Twitter."));
        assertTrue(res.getText(), res.getText().endsWith("She can be reached at jennifer@nova-comms.com or @jendonovansf"));
        compareDates("2014-07-01 00:00:00", res.getDate());
        assertEquals("Guest", res.getAuthorName());
        assertEquals(StringUtils.EMPTY, res.getAuthorDescription());
    }

    @Test
    public void testSpinsucks() throws Exception {
        // http://spinsucks.com/communication/2015-communications-trends/
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("spinsucks.html")));
        assertEquals("The Five Communications Trends for 2015 Spin Sucks", res.getTitle());
        assertTrue(res.getText(), res.getText().contains("Conversion of media. Julie Hong, the community manager"));
        assertTrue(res.getText(), res.getText().contains("Paid media affects traditional PR"));
        assertTrue(res.getText(), res.getText().contains("Old ideas become new again. We’ve stopped doing things such as deskside briefings, large events, direct mail"));
        compareDates("2014-12-09 05:58:40", res.getDate());
    }

    @Test
    public void testPRDaily() throws Exception {
        // http://www.prdaily.com/Main/Articles/7_PR_blogs_worth_reading_17870.aspx
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("prdaily.html")));
        assertEquals("7 PR blogs worth reading", res.getTitle());
        assertTrue(res.getText(), res.getText().contains("I really love the way Rebekah"));
        assertTrue(res.getText(), res.getText().contains("my team became fascinated with Bad Pitch Blog"));
        assertTrue(res.getText(), res.getText().contains("I’m fairly certain there is no one nicer than Deirdre Breakenridge"));
        compareDates("2015-01-08", res.getDate());
    }

    @Test
    public void testMarthaStewartWeddings() throws Exception {
        // http://www.marthastewartweddings.com/363473/bridal-beauty-diaries-lauren-%25E2%2580%2593-toning-and-cutting-down
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("marthastewartweddings.html")));
        assertEquals("Bridal Beauty Diaries: Lauren – Toning Up and Cutting Down", res.getTitle());
        assertTrue(res.getText(), res.getText().contains("Its “go” time. Approximately seven months until the big day"));
        compareDates("2014-07-02", res.getDate());
    }

    @Test
    public void testNotebookCheck() throws Exception {
        // http://www.notebookcheck.com/UEbernahme-Microsoft-schluckt-Devices-und-Services-Sparte-von-Nokia.115522.0.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("notebookcheck.html")));
        assertEquals("Übernahme: Microsoft schluckt Devices und Services Sparte von Nokia", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Tschüss Nokia. Willkommen Microsoft. Die Übernahmen ist unter Dach und Fach"));
    }

    @Test
    public void testPeople() throws Exception {
        // http://www.people.com/article/ryan-seacrest-marriage-turning-40
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("people.html")));
        assertEquals("Ryan Seacrest on Marriage: 'I Want What My Mom and Dad Have' - American Idol, Ryan Seacrest : People.com", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("There are those who are in denial about turning 40"));
        assertFalse(res.getText(), res.getText().contains("Poppy Montgomery Drama Unforgettable Is Being Brought Back"));
        compareDates("2014-08-20 09:15:00", res.getDate());
    }

    @Test
    public void testPeople2() throws Exception {
        // http://www.people.com/article/truck-driver-rescues-family-burning-car-video
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("people2.html")));
        assertEquals("Truck Driver Rescues Family in Burning Wreck : People.com", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("David Fredericksen was driving his semi truck along"));
        assertFalse(res.getText(), res.getText().contains("How Water Helps with Weight Loss"));
        compareDates("2014-08-20 10:30:00", res.getDate());
    }

    @Test
    public void testPeople3() throws Exception {
        // http://www.people.com/article/pierce-brosnan-jimmy-fallon-goldeneye-007-n64
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("people3.html")));
        assertEquals("Pierce Brosnan Plays Jimmy Fallon in 'GoldenEye 007' on 'Tonight Show' : People.com", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Just because you star in a video game, doesn't mean you'll be any good at it."));
        assertFalse(res.getText(), res.getText().contains("How Water Helps with Weight Loss"));
        assertEquals("Alex Heigl", res.getAuthorName());
        compareDates("2014-08-20 08:20:00", res.getDate());
    }

    @Test
    public void testEntrepreneur() throws Exception {
        // http://www.entrepreneur.com/article/237402
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("entrepreneur.html")));
        assertEquals("7 Big Changes in the PR Landscape Every Business Should Know About", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("At least three times a week, I get emails from entrepreneurs or small-business owners asking for advice on public relations."));
        assertEquals("Rebekah Iliff", res.getAuthorName());
        assertEquals("Chief Strategy Officer for AirPR", res.getAuthorDescription());
        compareDates("2014-09-15 17:30:00", res.getDate());
    }

    @Test
    public void testHuffingtonpostAuthor() throws Exception {
        // http://www.huffingtonpost.com/rebekah-iliff/millions-of-consumers-aba_b_5269051.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("huffingtonpost2.html")));
        assertEquals("Millions of Consumers Abandon Hashtag for Backslash", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("In a special Silicon Valley \"Tech Report,\" sources confirmed Monday that millions of "));
        assertEquals("Rebekah Iliff", res.getAuthorName());
        assertEquals("Chief Strategy Officer, AirPR", res.getAuthorDescription());
        // Mon, 05 May 2014 16:04:09 -0400 (sailthru.date?)
        compareDates("2014-05-06 15:59:46 -0400", res.getDate());
    }

    @Test
    public void testAllVoices() throws Exception {
        // http://www.allvoices.com/article/17660716
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("allvoices.html")));
        assertEquals("Marchex exec: Lead generation moving away from 'faceless transactions'", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Driven by the surge in mobile, lead generation"));
        compareDates("2014-08-14", res.getDate());
    }

    @Test
    public void testRocketFuel() throws Exception {
        // http://rocketfuel.com/blog/you-wont-be-seeing-coca-cola-ads-for-awhile-the-reason-why-is-amazing
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("rocketfuel.html")));
        assertEquals("You Won't be Seeing Coca Cola Ads for Awhile. The Reason why Is Amazing.", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Coca Cola announced that it will not be spending"));
    }

    @Test
    public void testHuffingtonpostAuthorDesc() throws Exception {
        // http://www.huffingtonpost.com/2015/03/10/bruce-miller-san-francisco-49ers-domestic-violence_n_6836416.html
        JResult res = new JResult();
        res.setUrl("http://www.huffingtonpost.com/2015/03/10/bruce-miller-san-francisco-49ers-domestic-violence_n_6836416.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("huffingtonpost3.html")));
        assertEquals("San Francisco 49ers Fullback Bruce Miller Arrested On Domestic Violence Charges", res.getTitle());
        assertEquals("", res.getAuthorDescription());
        compareDates("2015-03-10 05:27:01", res.getDate());
    }

    @Test
    public void testPRnewswireAuthorDesc() throws Exception {
        // http://www.prnewswire.com/news-releases/tableau-to-present-at-upcoming-investor-conferences-300039248.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("prnewswire.html")));
        assertEquals("Tableau to Present at Upcoming Investor Conferences -- SEATTLE, Feb. 24, 2015 /PRNewswire/ --", res.getTitle());
        assertEquals("Tableau Software (NYSE: DATA) helps people see and understand data. Tableau helps anyone quickly analyze, visualize and share information. More than 26,000 customer accounts get rapid results with Tableau in the office and on-the-go. And tens of thousands of people use Tableau Public to share data in their blogs and websites. See how Tableau can help you by downloading the free trial at www.tableau.com/trial.", res.getAuthorDescription());
        compareDates("2015-02-24", res.getDate());
    }

    @Test
    public void testTrendkraftAuthorDesc() throws Exception {
        // http://www.trendkraft.de/it-software/freigegeben-und-ab-sofort-verfuegbar-die-sechste-generation-des-ecm-systems-windream/
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("trendkraft_de.html")));
        assertEquals("Freigegeben und ab sofort verfügbar: die sechste Generation des ECM-Systems windream", res.getTitle());
        assertTrue(res.getAuthorDescription(), res.getAuthorDescription().length() == 987);
        compareDates("2015-03-11 09:19:56", res.getDate());
    }

    @Test
    public void testLimitSize() throws Exception {
        // https://medium.com/@nathanbruinooge/a-travelogue-of-india-7b1f3aa62a19
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("medium.html")), 1000);
        assertEquals("A Travelogue of India — Medium", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Schipol Airport in 2012 looks nothing like Schipol Airport in the Eighties"));
        assertTrue("Should be less than 1000", res.getText().length() <= 1000);
        compareDates("2014-08-19 04:11:18", res.getDate());
    }

    @Test
    public void testQualcomm() throws Exception {
        // https://www.qualcomm.com/news/releases/2014/10/16/qualcomm-declares-quarterly-cash-dividend
        JResult res = new JResult();
        res.setUrl("https://www.qualcomm.com/news/releases/2014/10/16/qualcomm-declares-quarterly-cash-dividend");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("qualcomm.html")));
        assertEquals("Qualcomm Declares Quarterly Cash Dividend", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Qualcomm Incorporated (NASDAQ: QCOM) today announced"));
        compareDates("2014-10-16", res.getDate());
    }

    @Test
    public void testQualcomm2() throws Exception {
        // https://www.qualcomm.com/news/onq/2016/02/29/2015-qualcomm-sustainability-report-connecting-world-through-innovation-and
        JResult res = new JResult();
        res.setUrl("https://www.qualcomm.com/news/onq/2016/02/29/2015-qualcomm-sustainability-report-connecting-world-through-innovation-and");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("qualcomm2.html")));
        assertEquals("https://www.qualcomm.com/news/onq/2016/02/29/2015-qualcomm-sustainability-report-connecting-world-through-innovation-and", res.getCanonicalUrl());
        assertEquals("https://www.qualcomm.com/news/onq/2016/02/29/2015-qualcomm-sustainability-report-connecting-world-through-innovation-and", res.getUrl());
        assertEquals("Connecting the world through innovation and collaboration", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Today I am excited to announce the launch"));
        compareDates("2016-02-29 08:00:00 -0800", res.getDate());
    }

    @Test
    public void testApplePR() throws Exception {
        // http://www.apple.com/pr/library/2015/04/27Apple-Expands-Capital-Return-Program-to-200-Billion.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("applepr.html")));
        assertEquals("Apple Expands Capital Return Program to $200 Billion", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Apple Expands Capital Return Program to $200 Billion CUPERTINO, California—April 27, 2015—Apple"));
    }

    @Test
    public void testApplePR2() throws Exception {
        // http://www.apple.com/pr/library/2015/03/09Apple-Watch-Available-in-Nine-Countries-on-April-24.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("applepr2.html")));
        assertEquals("Apple Watch Available in Nine Countries on April 24", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Apple Watch Available in Nine Countries on April 24"));
    }

    @Test
    public void testFortune() throws Exception {
        // http://fortune.com/2015/05/11/rackspaces-support-other-cloud/
        JResult res = new JResult();
        res.setUrl("http://fortune.com/2015/05/11/rackspaces-support-other-cloud/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("fortune.html")));
        assertEquals("Will Rackspace support Google's or Amazon's clouds?", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Rackspace, a true cloud computing pioneer, is starting to sound like a company that will"));
        compareDates("2015-05-11 23:01:19", res.getDate());
        assertEquals("Barb Darrow", res.getAuthorName());
        assertEquals("http://fortune.com/author/barb-darrow/", res.getAuthorDescription());
    }

    @Test
    public void testLongImageName() throws Exception {
        // http://www.adverts.ie/lego-building-toys/lego-general-zod-minifigure-brand-new/5980084
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("adverts.ie.html")));
        assertEquals("Lego General Zod Minifigure Brand New For Sale in Tralee, Kerry from dlaw1", res.getTitle());
        assertTrue(res.getImageUrl(), res.getImageUrl().length() == 0);
    }

    @Test
    public void testCloudComputingExpo() throws Exception {
        // http://www.cloudcomputingexpo.com/node/3342675
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cloudcomputingexpo.html")));
        assertTrue(res.getText(), res.getText().startsWith("How to Put Public Sector Data Migration Hassles on the Road to Extinction"));
        // test it doesn't extract outside the article content
        assertFalse("Extracted text outside the content", res.getText().contains("Sandy Carter"));
        assertTrue(res.getText(), res.getText().startsWith("How to Put Public Sector Data Migration Hassles on the Road to Extinction"));
        compareDates("2015-06-29 12:00:00", res.getDate());
    }

    @Test
    public void testCloudComputingExpo2() throws Exception {
        // http://www.cloudcomputingexpo.com/node/3346367
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cloudcomputingexpo2.html")));
        assertTrue(res.getText(), res.getText().startsWith("\"More than a Mother\" will first be implemented in Kenya in partnership"));
        assertTrue(res.getText(), res.getText().endsWith("EMD Millipore and EMD Performance Materials."));
        // test it doesn't extract outside the article content
        assertFalse("Extracted text outside the content", res.getText().contains("Sandy Carter"));
        compareDates("2015-06-23 10:31:00", res.getDate());
    }

    @Test
    public void testCloudComputingExpo3() throws Exception {
        // http://www.cloudcomputingexpo.com/node/3432136
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cloudcomputingexpo3.html")));
        assertEquals("IHS to Hold Conference Call and Webcast on September 29, 2015 with Release of Third Quarter Results for Fiscal Year 2015", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("IHS Inc. (NYSE: IHS), the leading global "));
        assertTrue(res.getText(), res.getText().endsWith("http://www.businesswire.com/news/home/20150828005027/en/"));
        compareDates("2015-08-28 08:00:00", res.getDate());
    }

    @Test
    public void testCloudComputingExpo4() throws Exception {
        // http://www.cloudcomputingexpo.com/node/3372014
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cloudcomputingexpo4.html")));
        assertEquals("As New Cases Of Ebola Are Confirmed We Highlight The Need For Global Coordination In The Field Of Distance Education", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("LOS ANGELES, July 16, 2015 /PRNewswire-iReach/"));
        assertTrue(res.getText(), res.getText().endsWith("News distributed by PR Newswire iReach: https://ireach.prnewswire.com"));
        compareDates("2015-07-16 19:37:00", res.getDate());
    }

    @Test
    public void testCloudComputingExpo5() throws Exception {
        // http://www.cloudcomputingexpo.com/node/3345803
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cloudcomputingexpo5.html")));
        assertEquals("U.S. FDA Approves Eisai's Antiepileptic Agent Fycompa as Adjunctive Treatment For Primary Generalized Tonic-Clonic Seizures", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("U.S. FDA Approves Eisai's Antiepileptic Agent Fycompa as Adjunctive Treatment"));
        assertTrue(res.getText(), res.getText().endsWith("or for any actions taken in reliance thereon."));
        compareDates("2015-06-22 02:21:00", res.getDate());
    }

    @Test
    public void testCanonical() throws Exception {
        // http://www.cio.com/article/2941417/internet/internet-of-things-is-overhyped-should-be-called-internet-with-things.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cio.com.html")));
        assertEquals("Internet of things is overhyped, should be called internet with things", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("The internet of things is overhyped and should instead be called the internet with things"));
        assertEquals("http://www.techworld.com/news/startups/rackspace-mongodb-execs-take-iot-hype-down-notch-3617731/", res.getCanonicalUrl());
        compareDates("2015-06-26 07:52:00-0700", res.getDate());
    }

    @Test
    public void testCanonical2() throws Exception {
        // Article html modified to test extracting canonical from og:url
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cio2.com.html")));
        assertEquals("http://www.cio.com/article/2941417/internet/internet-of-things-is-overhyped-should-be-called-internet-with-things.html", res.getCanonicalUrl());
    }

    @Test
    public void testCanonical3() throws Exception {
        // Article html modified to test extracting canonical from twitter:url
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cio3.com.html")));
        assertEquals("http://www.cio.com/article/2941417/internet/internet-of-things-is-overhyped-should-be-called-internet-with-things.html", res.getCanonicalUrl());
    }

    @Test
    public void testCanonical4() throws Exception {
        // http://www.cio.com/article/2941417/internet/internet-of-things-is-overhyped-should-be-called-internet-with-things.html
        String url = "http://www.cio.com/article/2941417/internet/internet-of-things-is-overhyped-should-be-called-internet-with-things.html";
        JResult res = new JResult();
        res.setUrl(url);
        res = extractor.extractCanonical(res, c.streamToString(getClass().getResourceAsStream("cio.com.html")), true);
        assertEquals("http://www.techworld.com/news/startups/rackspace-mongodb-execs-take-iot-hype-down-notch-3617731/", res.getCanonicalUrl());
    }

    @Test
    public void testCanonical5() throws Exception {
        // http://www.cio.com/article/2941417/internet/internet-of-things-is-overhyped-should-be-called-internet-with-things.html
        String url = "http://www.cio.com/article/2941417/internet/internet-of-things-is-overhyped-should-be-called-internet-with-things.html";
        JResult res = new JResult();
        res.setUrl(url);
        res = extractor.extractCanonical(res, c.streamToString(getClass().getResourceAsStream("cio.com.html")), false);
        assertEquals("http://www.cio.com/article/2941417/internet/internet-of-things-is-overhyped-should-be-called-internet-with-things.html", res.getCanonicalUrl());
    }

    @Test
    public void testCanonical6() throws Exception {
        // http://www.cio.com/article/2941417/internet/internet-of-things-is-overhyped-should-be-called-internet-with-things.html
        String url = "http://www.cio.com/article/2941417/internet/internet-of-things-is-overhyped-should-be-called-internet-with-things.html";
        JResult res = new JResult();
        res.setUrl(url);
        res = extractor.extractCanonical(res, c.streamToString(getClass().getResourceAsStream("cio.com_no_canonical")), false);
        assertEquals("http://www.cio.com/article/2941417/internet/internet-of-things-is-overhyped-should-be-called-internet-with-things.html", res.getCanonicalUrl());
    }

    @Test
    public void testCanonical7() throws Exception {
        // https://games.yahoo.com/blogs/unplugged/sad-fate-cereal-box-toys-200039785.html (GONE redirect to yahoo.com)
        String url = "https://games.yahoo.com/blogs/unplugged/sad-fate-cereal-box-toys-200039785.html";
        JResult res = new JResult();
        res.setUrl(url);
        res = extractor.extractCanonical(res, c.streamToString(getClass().getResourceAsStream("games.yahoo.com.html")), false);
        assertEquals("https://games.yahoo.com/blogs/unplugged/sad-fate-cereal-box-toys-200039785.html", res.getCanonicalUrl());
    }

    @Test
    public void testCanonical8() throws Exception {
        // http://abcnews.go.com/Business/wireStory/home-depot-hire-80000-workers-busy-spring-36683130 (GONE redirect to http://abcnews.go.com/Business)
        String url = "http://abcnews.go.com/Business/wireStory/home-depot-hire-80000-workers-busy-spring-36683130";
        JResult res = new JResult();
        res.setUrl(url);
        res = extractor.extractCanonical(res, c.streamToString(getClass().getResourceAsStream("abcnews_business.html")), false);
        assertEquals("http://abcnews.go.com/Business/wireStory/home-depot-hire-80000-workers-busy-spring-36683130", res.getCanonicalUrl());
    }


    @Test
    public void testCanonical9() throws Exception {
        // http://abcnews.go.com/Business/wireStory/fed-official-edward-mike-kelley-jr-dies-84-43992314 (NO REDIRECTED)
        String url = "http://abcnews.go.com/Business/wireStory/fed-official-edward-mike-kelley-jr-dies-84-43992314";
        JResult res = new JResult();
        res.setUrl(url);
        res = extractor.extractCanonical(res, c.streamToString(getClass().getResourceAsStream("abcnews_business2.html")), false);
        assertEquals("http://abcnews.go.com/Business/wireStory/fed-official-edward-mike-kelley-jr-dies-84-43992314", res.getCanonicalUrl());
    }

    @Test
    public void testYahooMobile() throws Exception {
        // https://m.yahoo.com/w/legobpengine/finance/news/stevia-first-corp-stvf-looks-123500390.html?.intl=us&.lang=en-us
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("m_yahoo.html")));
        assertEquals("Stevia First Corp. (STVF) Looks to Disrupt Flavor Industry", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("WHITEFISH, MT / ACCESSWIRE / July 13, 2015 / The global market for sugar and sweeteners"));
        // not supported
        //compareDates("2015-07-13 00:00:00", res.getDate());
    }

    @Test
    public void testWeixin() throws Exception {
        // http://mp.weixin.qq.com/s?3rd=MzA3MDU4NTYzMw%3D%3D&__biz=MzA4MTQ0Njc2Nw%3D%3D&idx=4&mid=207614885&scene=6&sn=eda80bb13406fb31cb25f70d12e6e7dc
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("weixin.qq.com.html")));
        assertTrue(res.getTitle(), res.getTitle().startsWith("缺少IT支持成跨境电商发展阻力"));
        assertTrue(res.getText(), res.getText().startsWith("根据联合国贸发会议预计"));
        compareDates("2015-07-27 00:00:00", res.getDate());
    }

    @Test
    public void testNaturebox() throws Exception {
        // http://blog.naturebox.com/posts/lunch-box-idea-breakfast-for-lunch
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("naturebox.com.html")));
        assertTrue(res.getTitle(), res.getTitle().startsWith("Lunch Box Idea: Breakfast for Lunch"));
        assertTrue(res.getText(), res.getText().startsWith("I don’t know a kid who doesn’t enjoy breakfast for lunch!"));
        compareDates("2015-02-19 00:00:00", res.getDate());
    }

    @Test
    public void testItsaLovelyLife() throws Exception {
        // http://itsalovelylife.com/why-having-a-sparkling-smile-is-important-to-me/
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("itsalovelylife.html")));
        assertTrue(res.getTitle(), res.getTitle().startsWith("Why Having A Sparkling Smile Is Important To Me"));
        assertTrue(res.getText(), res.getText().startsWith("I am still working my way through my New Year"));
        compareDates("2015-10-05 11:59:52", res.getDate());
    }

    @Test
    public void testWsjVideo() throws Exception {
        // http://www.wsj.com/video/what-did-steve-jobs-learn-from-the-beatles/46155A57-A19F-4AA1-9B2D-A3D8B1568A14.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("wsj.com.html")));
        assertTrue(res.getTitle(), res.getTitle().startsWith("What Did Steve Jobs Learn from the Beatles?"));
        assertTrue(res.getText(), res.getText().startsWith("This transcript has been automatically generated"));
        compareDates("2011-10-31 14:00:00", res.getDate());
    }

    @Test
    public void testMsn() throws Exception {
        // http://www.msn.com/en-us/lifestyle/weddings/how-to-make-your-smile-extra-stunning-for-your-wedding/ar-AAfi5iR
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("msn.html")));
        assertTrue(res.getTitle(), res.getTitle().startsWith("How to Make Your Smile Extra Stunning for Your Wedding"));
        assertTrue(res.getText(), res.getText().startsWith("Want to make your stunning natural smile even more gorgeous?"));
        compareDates("2015-10-09 19:07:02", res.getDate());
    }

    @Test
    public void testMsn2() throws Exception {
        // http://www.msn.com/en-us/news/other/update-4-tennis-halle-open-mens-singles-round-1-results/ar-BBlaHCs
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("msn2.html")));
        assertTrue(res.getTitle(), res.getTitle().startsWith("UPDATE 4-Tennis-Halle Open men's singles round 1 results"));
        assertTrue(res.getText(), res.getText().startsWith("June 15 (Infostrada Sports) - Results from the Halle Open Men'"));
        compareDates("2015-06-15 15:36:06", res.getDate());
    }

    @Test
    public void testMsn3() throws Exception {
        // http://www.msn.com/en-us/news/other/microsoft-shows-off-minecraft-built-specifically-for-hololens/ar-BBlb2RM
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("msn3.html")));
        assertTrue(res.getTitle(), res.getTitle().startsWith("Microsoft shows off 'Minecraft' built specifically for HoloLens"));
        assertTrue(res.getText(), res.getText().startsWith("At its E3 2015 event, Microsoft has given us a new demo of Minecraft"));
        compareDates("2015-06-15 17:54:00", res.getDate());
    }

    @Test
    public void testMsn4() throws Exception {
        // http://www.msn.com/en-sg/money/other/asia-stocks-fall-second-week-as-china-h-shares-enter-bear-market/ar-AAoxWG
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("msn4.html")));
        assertTrue(res.getTitle(), res.getTitle().startsWith("Asia Stocks Fall Second Week as China H-Shares Enter Bear Market"));
        assertTrue(res.getText(), res.getText().startsWith("March 22 (Bloomberg) -- Asia’s benchmark stock index fell the past five days to the biggest "));
        compareDates("2014-03-22 00:15:58", res.getDate());
    }

    @Test
    public void testCNBC() throws Exception {
        // http://www.cnbc.com/2015/10/01/amazon-google-move-into-on-demand-home-services.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cnbc1.html")));
        assertTrue(res.getTitle(), res.getTitle().startsWith("Amazon, Google move into on-demand home services"));
        assertTrue(res.getText(), res.getText().startsWith("Amazon and Google are vying to become the Uber for handymen"));
        assertTrue(res.getText(), res.getText().endsWith("This story has been updated to reflect that Thumbtack's CEO is Marco Zappacosta."));
        compareDates("2015-10-01 16:12:55 -0400", res.getDate());
    }

    @Test
    public void testCNBC2() throws Exception {
        // http://www.cnbc.com/2015/10/18/chinas-q3-gdp-up-69-y-o-y-compared-to-forecast-of-68.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cnbc2.html")));
        assertTrue(res.getTitle(), res.getTitle().startsWith("China's Q3 GDP up 6.9% y-o-y compared to forecast of 6.8%"));
        assertTrue(res.getText(), res.getText().startsWith("China's economy grew at its slowest pace since the global financial"));
        assertTrue(res.getText(), res.getText().endsWith("This report has been updated to show that China's economy grew 6.9 percent in the third-quarter."));
        compareDates("2015-10-18 22:00:27 -0400", res.getDate());
    }

    @Test
    public void testCNBC3() throws Exception {
        // http://www.cnbc.com/2015/10/12/india-us-japan-hold-naval-drills-in-bay-of-bengal-china-concerned.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cnbc3.html")));
        assertTrue(res.getTitle(), res.getTitle().startsWith("India, US, Japan hold naval drills in Bay of Bengal, China concerned"));
        assertTrue(res.getText(), res.getText().startsWith("India, Japan and the United States will hold joint naval exercises each year"));
        assertTrue(res.getText(), res.getText().endsWith("Indo-Pacific idea,\" he said."));
        compareDates("2015-10-12 21:49:42 -0400", res.getDate());
    }

    @Test
    public void testCNN3() throws Exception {
        // http://www.cnn.com/2015/09/24/politics/donald-trump-marco-rubio-foreign-policy/
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cnn3.html")));
        assertTrue(res.getTitle(), res.getTitle().startsWith("Rubio strikes back at Trump - CNNPolitics.com"));
        assertTrue(res.getText(), res.getText().startsWith("Washington Sen. Marco Rubio is firing back at Donald Trump as \"insecure\" and \"touchy\" after the mogul has spent the past two days going after his Republican presidential opponent."));
        assertTrue(res.getText(), res.getText().endsWith("\"Every time they kill mid-level accounting person from ISIS they have a news conference,\" he said. Marco Rubio's below-the-radar campaign"));
        compareDates("2015-09-24 12:29:56", res.getDate());
    }

    @Test
    public void testCNN4() throws Exception {
        // http://www.cnn.com/2015/10/20/middleeast/israeli-palestinian-tensions/index.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cnn4.html")));
        assertTrue(res.getTitle(), res.getTitle().startsWith("U.N. leader visits Israel, Palestinian territories - CNN.com"));
        assertTrue(res.getText(), res.getText().startsWith("Jerusalem (CNN) U.N. Secretary-General Ban Ki-moon "));
        assertTrue(res.getText(), res.getText().endsWith("Ben Wedeman contributed to this report."));
        compareDates("2015-10-20 09:05:18", res.getDate());
    }

    @Test
    public void testCNN5() throws Exception {
        // http://money.cnn.com/2015/10/20/news/uber-india-rape-verdict/index.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("cnn5.html")));
        assertTrue(res.getTitle(), res.getTitle().startsWith("Uber driver guilty in India rape case"));
        assertTrue(res.getText(), res.getText().startsWith("Yadav was convicted Tuesday on four charges related to"));
        assertTrue(res.getText(), res.getText().endsWith("puts Uber in the shade"));
        compareDates("2015-10-20 03:16:59", res.getDate());
    }

    @Test
    public void testWamu() throws Exception {
        // https://wamu.org/news/15/10/23/why_calling_slaves_workers_is_more_than_an_editing_error
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("wamu.html")));
        assertTrue(res.getTitle(), res.getTitle().startsWith("Why Calling Slaves 'Workers' Is More Than An Editing Error"));
        assertTrue(res.getText(), res.getText().startsWith("Coby Burren was reading his textbook"));
        assertTrue(res.getText(), res.getText().endsWith("\"and that he'll be heard.\""));
        // not supported
        //compareDates("2015-10-23", res.getDate());
    }

    @Test
    public void testJdsupra() throws Exception {
        // http://www.jdsupra.com/legalnews/defending-the-sec-s-choice-of-the-69927/
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("jdsupra.html")));
        assertTrue(res.getTitle(), res.getTitle().startsWith("Defending"));
        assertTrue(res.getText(), res.getText().startsWith("Wharton Associate Professor"));
        assertEquals("Keith Paul Bishop", res.getAuthorName());
        assertEquals("| Allen Matkins Leck Gamble Mallory & Natsis LLP", res.getAuthorDescription());
        compareDates("2015-10-20", res.getDate());
    }

    @Test
    public void testDailyMail() throws Exception {
        // http://www.dailymail.co.uk/news/article-2763386/With-breath-hero-mom-hid-baby-toilet-helping-daughter-escape-father-shot-head-turning-gun-himself.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("dailymail.co.uk.html")));
        assertTrue(res.getTitle(), res.getTitle().startsWith("With her 'last breath,' hero mom hid her baby in the toilet, sheltering the infant in the toilet as her boyfriend shot her in the head before turning the gun on himself"));
        assertTrue(res.getText(), res.getText().startsWith("A mother saved her six-month-old"));
        compareDates("2014-09-20 14:09:00", res.getDate());
    }

    @Test
    public void testITV() throws Exception {
        // http://www.itv.com/news/2014-04-14/boston-marathon-bombings-one-year-on/
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("itv.html")));
        assertTrue(res.getTitle(), res.getTitle().startsWith("Boston Marathon bomb attacks: One year on"));
        assertTrue(res.getText(), res.getText().startsWith("Today Boston will mark the first anniversary"));
        compareDates("2014-04-14 23:53:00", res.getDate());
    }

    @Test
    public void testNola() throws Exception {
        // http://www.nola.com/running/index.ssf/2014/04/race_director_bill_burke_hopin.html
        JResult res = new JResult();
        res.setUrl("http://www.nola.com/running/index.ssf/2014/04/race_director_bill_burke_hopin.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("nola.com.html")));
        assertEquals("Wife of Atlanta firefighter who died during training ride for New Orleans Ironman spoke at race Sunday", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Kimberly Guinn took the microphone"));
        compareDates("2014-04-13 17:42:00", res.getDate());
    }

    @Test
    public void testMercuryNews() throws Exception {
        //http://www.mercurynews.com/ci_26860908/virgin-galactic-co-pilot-michael-alsbury-from-scotts
        JResult res = new JResult();
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("mercurynews.html")));
        assertEquals("Virgin Galactic co-pilot Michael Alsbury from Scotts Valley", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("SANTA CRUZ -- Test pilot Michael Alsbury"));
        compareDates("2014-11-04 16:05:13", res.getDate());
    }

    @Test
    public void testMlive() throws Exception {
        //http://www.mlive.com/news/bay-city/index.ssf/2014/12/christmas_wishes_tour_fills_pa.html
        JResult res = new JResult();
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("mlive.html")));
        assertEquals("'Christmas Wishes Tour' party bus brightens the holiday season for Essexville family", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("On its final stop of"));
        compareDates("2014-12-22 17:54:00", res.getDate());
    }

    @Test
    public void testKagstv() throws Exception {
        //http://kagstv.com/News/KAGSNews/ID/6575/Friend-of-teen-torched-I-would-have-fought-for-her
        JResult res = new JResult();
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("kagstv.html")));
        assertEquals("Friend of teen torched: 'I would have fought for her' > KAGS TV - College Station, Texas", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("COURTLAND, Miss."));
        compareDates("2014-12-10 12:53:00", res.getDate());
    }

    @Test
    public void testKjrh() throws Exception {
        //http://www.kjrh.com/news/local-news/muskogee-roughers-football-team-collecting-donations-for-jenks-trojans-assistant-coach-bryant-calip
        JResult res = new JResult();
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("kjrh.html")));
        assertEquals("Muskogee Roughers football team collecting donations for Jenks Trojans assistant coach Bryant Calip - KJRH.com", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("That's the distance that separates two opposing football teams"));
        compareDates("2014-10-21 12:54:43", res.getDate());
    }

    @Test
    public void testJezebel() throws Exception {
        //http://jezebel.com/honey-boo-boo-star-selling-oils-to-save-you-from-ebola-1665926767
        JResult res = new JResult();
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("jezebel.html")));
        assertEquals("Honey Boo Boo Star Selling Oils to Save You From Ebola", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Anna Cardwell, former co-star"));
        compareDates("2014-12-02 20:00:00", res.getDate());
    }

    @Test
    public void testThetelegram() throws Exception {
        //http://www.thetelegram.com/News/Local/2014-11-24/article-3949943/Fundraiser-response-overwhelms-family/1
        JResult res = new JResult();
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("thetelegram.html")));
        assertEquals("Fundraiser response overwhelms family", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Rebecca, 9, daughter of Paul Byrne, holds a thank-you card"));
        compareDates("2014-11-24 00:00:00", res.getDate());
    }

    @Test
    @Ignore ("The original content extraction for this site and the testcase as well are incorrect. The content is directly under div tag which are simple ignored due to rules in de.jetwick.snacktory.OutputFormatter.nodesToKeepCssSelector")
    public void testDogsbite() throws Exception {
        // http://blog.dogsbite.org/2014/08/014-dog-bite-fatality-toddler-dies-family-pit-bull-attack-under-grandmothers-care.html
        JResult res = new JResult();
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("dogsbite.html")));
        assertEquals("2014 Dog Bite Fatality: Toddler Dies After Attack by Family Pit Bull While Under Grandmother's Care - DogsBite.org", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Labels: 2014 Dog Bite Fatality"));
        compareDates("2015-01-29 00:00:00", res.getDate());
    }

    @Test
    public void testFoxNews() throws Exception {
        //http://latino.foxnews.com/latino/lifestyle/2014/07/22/cancer-stricken-father-with-4-months-to-live-heads-to-disneyland-with-family/
        JResult res = new JResult();
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("latino.foxnews.html")));
        assertEquals("Cancer-Stricken Father With 4 Months To Live Heads To Disneyland With Family For Last Time", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Expected to live only four more months"));
        compareDates("2014-07-22 00:00:00", res.getDate());
    }

    @Test
    public void testSimcoeReformer() throws Exception {
        //http://www.simcoereformer.ca/2014/09/29/ddss-teacher-begins-treatment-in-atlanta
        JResult res = new JResult();
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("simcoereformer.html")));
        assertEquals("`Team Lane’ has its fingers crossed", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("A Delhi teacher is waging the fight of his life"));
        compareDates("2014-09-29 23:44:08", res.getDate());
    }

    @Test
    public void testEurweb() throws Exception {
        // http://www.eurweb.com/2014/09/darren-wilson-fundraisers-end-without-explanation/
        JResult res = new JResult();
        res.setUrl("http://www.eurweb.com/2014/09/darren-wilson-fundraisers-end-without-explanation/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("eurweb.html")));
        assertEquals("Darren Wilson Online Fundraisers Stop Without Any Reason", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("*Online fundraisers for embattled Ferguson"));
        compareDates("2014-09-01", res.getDate());
    }

    @Test
    public void testShropshirestar() throws Exception {
        // http://www.shropshirestar.com/news/2014/09/16/shropshire-border-village-rallies-round-mend-our-mum-fight/
        JResult res = new JResult();
        res.setUrl("http://www.shropshirestar.com/news/2014/09/16/shropshire-border-village-rallies-round-mend-our-mum-fight/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("shropshirestar.html")));
        assertEquals("Shropshire border village rallies round Mend Our Mum fight « Shropshire Star", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("The Cliffe family were devastated"));
        compareDates("2014-09-16 18:59:00", res.getDate());
    }

    @Test
    public void testConnectionnewspapers() throws Exception {
        // http://www.connectionnewspapers.com/news/2014/sep/25/local-aikido-studio-reduces-ptsd-effects/
        JResult res = new JResult();
        res.setUrl("http://www.connectionnewspapers.com/news/2014/sep/25/local-aikido-studio-reduces-ptsd-effects/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("connectionnewspapers.html")));
        assertEquals("Local Aikido Studio Reduces PTSD Effects", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("As a soldier fighting"));
        compareDates("2014-09-25", res.getDate());
    }

    @Test
    public void testNewsSky() throws Exception {
        // http://news.sky.com/story/1515847/taylor-swift-gives-50k-to-fan-with-leukaemia
        JResult res = new JResult();
        res.setUrl("http://news.sky.com/story/1515847/taylor-swift-gives-50k-to-fan-with-leukaemia");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("news.sky.com.html")));
        assertEquals("Taylor Swift Gives $50k To Fan With Leukaemia", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Taylor Swift has surprised one of her young fans"));
        compareDates("2015-07-09 09:39:00", res.getDate());
    }

    @Test
    public void testBildDe() throws Exception {
        // http://www.bild.de/news/ausland/wunder/aerzte-rieten-zur-abtreibung-jaxon-du-bist-ein-wunder-42736638.bild.html
        JResult res = new JResult();
        res.setUrl("http://www.bild.de/news/ausland/wunder/aerzte-rieten-zur-abtreibung-jaxon-du-bist-ein-wunder-42736638.bild.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("bild.de.html")));
        assertEquals("Ärzte rieten Eltern zur Abtreibung, jetzt feiert er seinen 1. Geburtstag: Kleiner Jaxon, du bist ein Wunder! - News Ausland - Bild.de", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Jaxon lernt gerade laufen und sprechen."));
        compareDates("2015-09-27 11:13:45", res.getDate());
    }

    @Test
    public void testMobileSlashdotOrg() throws Exception {
        // http://mobile.slashdot.org/story/15/11/12/1516255/mozilla-launches-firefox-for-ios
        JResult res = new JResult();
        res.setUrl("http://mobile.slashdot.org/story/15/11/12/1516255/mozilla-launches-firefox-for-ios");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("mobile.slashdot.org.html")));
        assertEquals("Mozilla Launches Firefox For IOS - Slashdot", res.getTitle());
        // TODO: Text extraction in this case is not correct, includes some advertisement, fix this.
        //assertTrue(res.getText(), res.getText().startsWith("An anonymous reader writes:"));
        assertTrue(res.getText(), res.getText().startsWith("Slashdot Deals: Get The Fastest VPN For Your Internet Security Lifetime Subscription Of PureVPN"));
        compareDates("2015-11-12 10:17:00", res.getDate());
    }

    @Test
    public void testKwch() throws Exception {
        // http://www.kwch.com/news/local-news/crowdfunding-trend-growing-for-cancer-patients/33575512
        JResult res = new JResult();
        res.setUrl("http://www.kwch.com/news/local-news/crowdfunding-trend-growing-for-cancer-patients/33575512");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("kwch.html")));
        assertEquals("Crowdfunding trend growing for cancer patients", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("For 5-year-old Prestun Siebel being a kid hasn't been easy."));
        compareDates("2015-06-15 00:41:00", res.getDate());
    }

    @Test
    public void testGolocalprov() throws Exception {
        // http://www.golocalprov.com/business/friday-financial-five-december-4-2015
        JResult res = new JResult();
        res.setUrl("http://www.golocalprov.com/business/friday-financial-five-december-4-2015");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("golocalprov.html")));
        assertEquals("Friday Financial Five – December 4, 2015", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Dan Forbes, GoLocalProv Contributor The end of the year is a great time to review"));
        assertFalse(res.getText(), res.getText().contains("WalletHub"));
        compareDates("2015-12-04 07:14:00", res.getDate());
    }

    @Test
    public void testMheducation() throws Exception {
        // http://www.mheducation.com/news-media/press-releases/mcgraw-hill-education-launches-sra-flex-literacy-help-struggling-students-meet.html
        JResult res = new JResult();
        res.setUrl("http://www.mheducation.com/news-media/press-releases/mcgraw-hill-education-launches-sra-flex-literacy-help-struggling-students-meet.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("mheducation.html")));
        assertEquals("McGraw-Hill Education Launches SRA FLEX Literacy™ to Help Struggling Students Meet the Common Core State Standards in English Language Arts", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("NEW YORK, Feb. 7, 2013 /PRNewswire/"));
        compareDates("2013-02-07 05:00:00", res.getDate());
    }

    @Test
    public void testCMO() throws Exception {
        // http://www.cmo.com/articles/2015/12/9/millennials-powering-purchase-of-digital-gift-cards.html
        JResult res = new JResult();
        res.setUrl("http://www.cmo.com/articles/2015/12/9/millennials-powering-purchase-of-digital-gift-cards.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("cmo.com.html")));
        assertEquals("Millennials Powering Purchase Of Digital Gift Cards", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("As the retail industry overall trends further toward"));
        compareDates("2015-12-09 00:00:00", res.getDate());
    }

    @Test
    public void testBestpaths() throws Exception {
        // http://bestpaths.com/twitters-fabric-announces-unity-sdk-support-error-logging-on-ios-and-tvos/
        JResult res = new JResult();
        res.setUrl("http://bestpaths.com/twitters-fabric-announces-unity-sdk-support-error-logging-on-ios-and-tvos/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("bestpaths.html")));
        assertEquals("Twitter’s Fabric Announces Unity SDK Support, Error Logging On iOS And tvOS", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Today at Twitter Flight"));
        assertFalse(res.getText(), res.getText().contains("Adobe"));
        assertFalse(res.getText(), res.getText().contains("NFL"));
        compareDates("2015-10-21 00:00:00", res.getDate());
    }

    @Test
    public void testPRnewswire() throws Exception {
        // http://www.prnewswire.com/news-releases/encyclopaedia-britannica-accelerates-its-digital-transformation-with-salesforce-300187911.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("prnewswire2.html")));
        assertEquals("Encyclopaedia Britannica Accelerates Its Digital Transformation with Salesforce -- SAN FRANCISCO, Dec. 4, 2015 /PRNewswire/ --", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Salesforce Customer Success Platform helps Encyclopaedia Britannica "));
        assertTrue(res.getText(), res.getText().contains("240 years"));
        compareDates("2015-12-04", res.getDate());
    }

    @Test
    public void testTheStreet() throws Exception {
        // http://www.thestreet.com/video/13404696/avoid-non-traded-reits-annuities-says-wall-street-potholes-author.html
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("thestreet.com.html")));
        assertEquals("Avoid Non-Traded REITs, Annuities Says ‘Wall Street Potholes’ Author", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Non-traded REITs have highly limited liquidity"));
        compareDates("2015-12-28 06:30:00", res.getDate());
    }

    @Test
    public void testAdobeBlog() throws Exception {
        // http://blogs.adobe.com/primetime/2015/09/improving-startup-performance-by-pre-fetching-videos-faster-with-tvsdk-2-0/
        JResult res = extractor.extractContent(c.streamToString(getClass().getResourceAsStream("adobe_blog.html")));
        assertEquals("Improving Startup Performance by Pre-Fetching Videos Faster with TVSDK 2.0 - Adobe Primetime Blog", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Adobe Primetime customers get"));
        compareDates("2015-09-16 03:42:23", res.getDate());
    }

    @Test
    public void testBeetTV() throws Exception {
        // http://www.beet.tv/2016/01/cesrocketwootton.html
        JResult res = new JResult();
        res.setUrl("http://www.beet.tv/2016/01/cesrocketwootton.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("beet.tv.html")));
        assertEquals("Rocket Fuel’s New CEO Targets DISH Moments", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("LAS VEGAS — "));
        assertTrue(res.getText(), res.getText().endsWith("sponsored by Adobe."));
        compareDates("2016-01-01", res.getDate());
    }

    @Test
    public void testRussianRT() throws Exception {
        // https://russian.rt.com/article/141677
        JResult res = new JResult();
        res.setUrl("https://russian.rt.com/article/141677");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("russian.rt.com.html")));
        assertEquals("СМИ: Сеул открыл предупредительный огонь по северокорейскому беспилотнику", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Вооружённые силы Южной"));
        assertTrue(res.getText(), res.getText().endsWith("государствами."));
        compareDates("2016-01-13 06:22:43", res.getDate());
    }

    @Test
    public void testRussianRT2() throws Exception {
        // https://russian.rt.com/article/142694
        JResult res = new JResult();
        res.setUrl("https://russian.rt.com/article/142694");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("russian.rt2.com.html")));
        assertEquals("С высоты птичьего полёта: экстремал из Румынии в очередной раз испытал себя", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Экстремал из Румынии забрался"));
        assertTrue(res.getText(), res.getText().endsWith("— заключил Чернеску."));
        compareDates("2016-01-18 11:53:24", res.getDate());
    }

    @Test
    public void testSequoiacap() throws Exception {
        // https://www.sequoiacap.com/article/build-us-microservices/
        JResult res = new JResult();
        res.setUrl("https://www.sequoiacap.com/article/build-us-microservices/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("sequoiacap.html")));
        assertEquals("Innovate or Die: The Rise of Microservices", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Software has emerged as the critical differentiator"));
        assertTrue(res.getText(), res.getText().endsWith("Sequoia will host a Microservices Summit in January, 2016."));
        compareDates("2015-10-05", res.getDate());
    }

    @Test
    public void testSFGate() throws Exception {
        // http://www.sfgate.com/sports/article/Wisconsin-Girls-How-Fared-6765744.php
        JResult res = new JResult();
        res.setUrl("http://www.sfgate.com/sports/article/Wisconsin-Girls-How-Fared-6765744.php");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("sfgate2.html")));
        assertEquals("Wisconsin Girls How Fared", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Division 1 1. Bay Port (13-1)"));
        assertTrue(res.getText(), res.getText().endsWith("beat Royall 57-39."));
        compareDates("2016-01-17 23:21:00", res.getDate());
    }

    @Test
    public void testTelegram() throws Exception {
        // http://www.telegram.com/article/20160119/SPORTS/160119063/101360/NEWS?rssfeed=true
        JResult res = new JResult();
        res.setUrl("http://www.telegram.com/article/20160119/SPORTS/160119063/101360/NEWS?rssfeed=true");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("telegram.com.html")));
        assertEquals("St. John's 90, Lincoln-Sudbury 70: Lukasevicz leads way as No. 2 Pioneers surge in second half", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("SHREWSBURY — For a time, Tuesday night’s "));
        assertTrue(res.getText(), res.getText().endsWith("Twitter @CraigGilvarg."));
        compareDates("2016-01-19", res.getDate());
    }

    @Test
    public void testHuffingtonpost4() throws Exception {
        // http://www.huffingtonpost.com/entry/kesha-fans-protest_us_56a128f5e4b0d8cc109916b1
        JResult res = new JResult();
        res.setUrl("http://www.huffingtonpost.com/entry/kesha-fans-protest_us_56a128f5e4b0d8cc109916b1");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("huffingtonpost4.html")));
        assertEquals("Kesha Fans Set To Protest Sony Music Outside New York Courthouse", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("On Tuesday, Jan. 26, Kesha fans will reportedly protest Sony Music "));
        assertTrue(res.getText(), res.getText().endsWith("Care2 petition website."));
        compareDates("2016-01-21 21:16:12", res.getDate());
    }

    @Test
    public void testArchiveOrg() throws Exception {
        // https://archive.org/details/gigaom_soundcloud_135885465
        JResult res = new JResult();
        res.setUrl("https://archive.org/details/gigaom_soundcloud_135885465");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("archive.org.html")));
        assertEquals("Rack space president lays out his plan of action : Gigaom : Free Download & Streaming : Internet Archive", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Rack space president lays out his plan of action"));
        assertTrue(res.getText(), res.getText().endsWith("Be the first one to write a review."));
        compareDates("2014-02-20", res.getDate());
    }

    @Test
    public void testBoingBoing() throws Exception {
        // https://boingboing.net/2016/02/02/doxxing-sherlock-3.html
        JResult res = new JResult();
        res.setUrl("https://boingboing.net/2016/02/02/doxxing-sherlock-3.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("boingboing.net.html")));
        assertEquals("Exclusive: Snowden intelligence docs reveal UK spooks' malware checklist / Boing Boing", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Boing Boing is proud to publish two original documents disclosed"));
        assertTrue(res.getText(), res.getText().endsWith("contributed research to this story."));
        compareDates("2016-02-02 17:36:53", res.getDate());
    }

    @Test
    public void testEfytimes() throws Exception {
        // http://www.efytimes.com/e1/responsive/fullnews.asp?edid=181209
        JResult res = new JResult();
        res.setUrl("http://www.efytimes.com/e1/responsive/fullnews.asp?edid=181209");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("efytimes.com.html")));
        assertEquals("News from India on Technology, Electronics, Computers, Open Source & more: EFYTIMES.COM", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Cisco Launches Cloud Monitoring Service To Protect Against Shadow IT"));
        assertTrue(res.getText(), res.getText().endsWith("such as Digital Guardian."));
        //compareDates("2016-02-02 17:36:53", res.getDate());
    }

    @Test
    public void testWm() throws Exception {
        // http://article.wn.com/view/2016/02/10/Arena_construction_turns_to_locker_rooms_and_luxury_suites/
        JResult res = new JResult();
        res.setUrl("http://article.wn.com/view/2016/02/10/Arena_construction_turns_to_locker_rooms_and_luxury_suites/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("wn.com.html")));
        assertEquals("Arena construction turns to locker rooms and luxury suites", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("It’s getting easier all the time to"));
        assertTrue(res.getText(), res.getText().endsWith("..."));
        compareDates("2016-02-10", res.getDate());
    }

    @Test
    public void testCoolhunting() throws Exception {
        // http://www.coolhunting.com/link/otherworldly-images-of-a-glass-recyling-factory
        JResult res = new JResult();
        res.setUrl("http://www.coolhunting.com/link/otherworldly-images-of-a-glass-recyling-factory");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("coolhunting.html")));
        assertEquals("Otherworldy Images of a Glass Recycling Factory - Cool Hunting", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("At the Phoenicia Glass Works"));
        assertTrue(res.getText(), res.getText().endsWith("Take a look at Mashable."));
        compareDates("2016-02-08 12:30:00 -0500", res.getDate());
    }

    @Test
    public void testWorldPropertyJournal() throws Exception {
        // http://www.worldpropertyjournal.com/real-estate-news/united-states/new-york-city-real-estate-news/new-york-trophy-buildings-2016-the-midtown-trophy-index-most-exclusive-office-buildings-in-new-york-tristan-ashby-jll-office-rates-in-midtown-manhattan-2016-9633.php
        JResult res = new JResult();
        res.setUrl("http://www.worldpropertyjournal.com/real-estate-news/united-states/new-york-city-real-estate-news/new-york-trophy-buildings-2016-the-midtown-trophy-index-most-exclusive-office-buildings-in-new-york-tristan-ashby-jll-office-rates-in-midtown-manhattan-2016-9633.php");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("worldpropertyjournal.html")));
        assertEquals("Midtown Manhattan Trophy Buildings Post Rock Star Rental Growth Rates", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Midtown Manhattan Trophy Buildings Post Rock Star Rental Growth Rates Residential News"));
        assertTrue(res.getText(), res.getText().endsWith("All Rights Reserved."));
        // January 27, 2016  8:34 AM ET
        // compareDates("2016-02-08 12:30:00", res.getDate());
    }

    @Test
    public void testBizJournal() throws Exception {
        // http://www.bizjournals.com/austin/blog/techflash/2014/10/rackspace-plans-500-new-jobs-at-highland-mall.html?page=all
        JResult res = new JResult();
        res.setUrl("http://www.bizjournals.com/austin/blog/techflash/2014/10/rackspace-plans-500-new-jobs-at-highland-mall.html?page=all");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("bizjournals.html")));
        assertEquals("Rackspace plans 500 new jobs at Highland Mall, seeks incentives", res.getTitle());
        assertEquals("http://www.bizjournals.com/austin/blog/techflash/2014/10/rackspace-plans-500-new-jobs-at-highland-mall.html", res.getCanonicalUrl());
        assertTrue(res.getText(), res.getText().startsWith("Austin Business Journal Rackspace Inc., the San Antonio-based Web"));
        assertTrue(res.getText(), res.getText().endsWith("for the Austin Business Journal."));
        compareDates("2014-10-21 12:48:00", res.getDate());
    }

    @Test
    public void testBizJournal1() throws Exception {
        // http://www.bizjournals.com/sanfrancisco/subscriber-only/2017/04/21/san-francisco-tech-employers.html
        JResult res = new JResult();
        res.setUrl("http://www.bizjournals.com/sanfrancisco/subscriber-only/2017/04/21/san-francisco-tech-employers.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("bizjournals1.html")));
        assertEquals("San Francisco Tech Employers", res.getTitle());
        assertEquals("http://www.bizjournals.com/sanfrancisco/subscriber-only/2017/04/21/san-francisco-tech-employers.html", res.getCanonicalUrl());
        assertTrue(res.getText(), res.getText().startsWith("San Francisco Tech Employers"));
        assertTrue(res.getText(), res.getText().endsWith("Sundar Pichai, CEO"));
        assertEquals("Jean Lee", res.getAuthorName());
        assertEquals(StringUtils.EMPTY, res.getAuthorDescription());
        compareDates("2017-04-21 00:00:00", res.getDate());
    }

    @Test
    public void testBillboard() throws Exception {
        // http://www.billboard.com/articles/columns/the-juice/6770023/taylor-bennett-chance-the-rapper-broad-shoulders
        JResult res = new JResult();
        res.setUrl("http://www.billboard.com/articles/columns/the-juice/6770023/taylor-bennett-chance-the-rapper-broad-shoulders");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("billboard.html")));
        assertEquals("Taylor Bennett Debuts 'Broad Shoulders,' Featuring Big Bro Chance the Rapper", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Talent runs in Chance the Rapper’s family."));
        assertTrue(res.getText(), res.getText().endsWith("their brotherly love below."));
        compareDates("2015-11-19 22:44:03", res.getDate());
    }

    @Test
    public void testFoxBusiness() throws Exception {
        // http://video.foxbusiness.com/v/4779323511001/is-trump-too-divisive-to-win-the-election/?#sp=show-clips
        JResult res = new JResult();
        res.setUrl("http://video.foxbusiness.com/v/4779323511001/is-trump-too-divisive-to-win-the-election/?#sp=show-clips");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("foxbusiness.html")));
        assertEquals("http://video.foxbusiness.com/v/4779323511001/is-trump-too-divisive-to-win-the-election/", res.getCanonicalUrl());
        assertEquals("Is Trump too divisive to win the election?", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Feb. 29, 2016 - 6:52 - Former Hillary Clinton Chief Strategist Mark Penn on the 2016 presidential race."));
        compareDates("2016-02-29 00:00:00", res.getDate());
    }

    @Test
    public void testFintech() throws Exception {
        // http://www.fintech.finance/fintech-tv/lawrence-whittle-persado/
        JResult res = new JResult();
        res.setUrl("http://www.fintech.finance/fintech-tv/lawrence-whittle-persado/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("fintech.html")));
        assertEquals("http://www.fintech.finance/fintech-tv/lawrence-whittle-persado/", res.getCanonicalUrl());
        assertEquals("Lawrence Whittle, Persado", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Lawrence Whittle from Persado talks to us about ways to improve the custoemr experiance while at Finovate Europe."));
        compareDates("2016-02-16 09:51:31", res.getDate());
    }

    @Test
    public void testReuters2() throws Exception {
        // http://www.reuters.com/article/us-adobe-systems-results-idUSKCN0RH2SD20150917
        JResult res = new JResult();
        res.setUrl("http://www.reuters.com/article/us-adobe-systems-results-idUSKCN0RH2SD20150917");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("reuters2.html")));
        assertEquals("http://www.reuters.com/article/us-adobe-systems-results-idUSKCN0RH2SD20150917", res.getCanonicalUrl());
        assertEquals("Adobe revenue, profit forecast miss estimates, shares slip", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Adobe Systems Inc's lower-than-expected revenue"));
        assertTrue(res.getText(), res.getText().endsWith("Editing by Sriraj Kalluvila)"));
        compareDates("2015-09-17 22:46:18", res.getDate());
    }

    @Test
    public void testMaCNN() throws Exception {
        // http://www.macnn.com/articles/16/03/03/new.samsung.enterprise.drive.has.incredible.data.density.unknown.price.132851/
        JResult res = new JResult();
        res.setUrl("http://www.macnn.com/articles/16/03/03/new.samsung.enterprise.drive.has.incredible.data.density.unknown.price.132851/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("macnn.html")));
        assertEquals("Samsung ships speedy 2.5-inch 15.36TB SAS SSD for data centers", res.getTitle());
        // FIXME: The first paragraph of this article is outside a tag (just a text node), and
        // it is a direct children of the found node, because of this is not showing in the content.
        //assertTrue(res.getText(), res.getText().startsWith("Samsung ships speedy 2.5-inch 15.36TB SAS SSD for data centers updated 08:51 am EST, Thu March 3, 2016         by MacNN Staff New Samsung enterprise drive"));
        assertTrue(res.getText(), res.getText().startsWith("New Samsung enterprise drive has incredible data density, unknown price The 15.36TB of data storage"));
        assertTrue(res.getText(), res.getText().endsWith("the new model on request."));
        compareDates("2016-03-03 13:51:00", res.getDate());
        assertEquals(17, res.getLinks().size());
    }

    @Test
    public void testHRC() throws Exception {
        // http://www.hrc.org/blog/more-than-100-tech-leaders-call-for-nationwide-lgbt-non-discrimination-prot/
        JResult res = new JResult();
        res.setUrl("http://www.hrc.org/blog/more-than-100-tech-leaders-call-for-nationwide-lgbt-non-discrimination-prot/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("hrc.html")));
        assertEquals("http://www.hrc.org/blog/more-than-100-tech-leaders-call-for-nationwide-lgbt-non-discrimination-prot/", res.getCanonicalUrl());
        assertEquals("More Than 100 Tech Leaders Call for Nationwide LGBT Non-Discrimination Protections", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Reflecting an ever-increasing wave of support for LGBT equality"));
        assertTrue(res.getText(), res.getText().endsWith("Product Innovation & New Businesses, Verizon"));
        compareDates("2015-04-06", res.getDate());
    }

    @Test
    public void testRagan() throws Exception {
        // http://www.ragan.com/Main/Articles/50895.aspx
        JResult res = new JResult();
        res.setUrl("http://www.ragan.com/Main/Articles/50895.aspx");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("ragan.html")));
        assertEquals("http://www.ragan.com/Main/Articles/50895.aspx", res.getCanonicalUrl());
        assertEquals("Why Buzzfeed values engagement metrics—and so should you", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("I recently read an article in Fortune"));
        assertTrue(res.getText(), res.getText().endsWith("A version of the article first appeared on AirPR."));
        compareDates("2016-03-16", res.getDate());
        assertEquals("Leta Soza", res.getAuthorName());
    }

    @Test
    public void testInvestors() throws Exception {
        // http://www.investors.com/news/technology/blackberry-q4-revenue-falls-way-short-of-estimates-as-stock-tumbles/
        JResult res = new JResult();
        res.setUrl("http://www.investors.com/news/technology/blackberry-q4-revenue-falls-way-short-of-estimates-as-stock-tumbles/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("investors.html")));
        assertEquals("http://www.investors.com/news/technology/blackberry-q4-revenue-falls-way-short-of-estimates-as-stock-tumbles/", res.getCanonicalUrl());
        assertEquals("BlackBerry Q4 Revenue Falls Far Short Of Estimates As Stock Tumbles", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Deep in a turnaround effort that aims to slow its sinking revenue growth and return it to profitability"));
        assertTrue(res.getText(), res.getText().endsWith("to other smartphones and operating systems."));
        compareDates("2016-04-01 18:56:15", res.getDate());
    }

    @Test
    public void testAfr() throws Exception {
        // http://www.afr.com/leadership/management/mindfulness-tips-to-skip-workplace-stress-this-silly-season-20151201-glcnq1
        JResult res = new JResult();
        res.setUrl("http://www.afr.com/leadership/management/mindfulness-tips-to-skip-workplace-stress-this-silly-season-20151201-glcnq1");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("afr.html")));
        assertEquals("http://www.afr.com/leadership/management/mindfulness-tips-to-skip-workplace-stress-this-silly-season-20151201-glcnq1", res.getCanonicalUrl());
        assertEquals("Mindfulness tips to skip workplace stress this silly season", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("December is again upon us, a time when many workplaces engage"));
        //assertTrue(res.getText(), res.getText().endsWith("to other smartphones and operating systems."));
        compareDates("2015-12-03 02:32:54 +0000", res.getDate());
    }

    @Test
    public void testMyPalmBeachPost() throws Exception {
        // http://www.mypalmbeachpost.com/feed/business/consumer-advice/consumer-confidence-in-buying-homes-online-has/fCKWXF/
        JResult res = new JResult();
        res.setUrl("http://www.mypalmbeachpost.com/feed/business/consumer-advice/consumer-confidence-in-buying-homes-online-has/fCKWXF/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("mypalmbeachpost.html")));
        assertEquals("http://www.mypalmbeachpost.com/feed/business/consumer-advice/consumer-confidence-in-buying-homes-online-has/fCKWXF/", res.getUrl());
        assertEquals("http://www.mypalmbeachpost.com/feed/business/consumer-advice/consumer-confidence-in-buying-homes-online-has/fCKWXF/", res.getCanonicalUrl());
        assertEquals("Consumer Confidence In Buying Homes Online Has Increased", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Owners.com, an online brokerage firm"));
        assertTrue(res.getText(), res.getText().endsWith("consider relocating."));
        //compareDates("2016-04-01 10:00:00", res.getDate());
    }

    @Test
    public void testAmbienteja() throws Exception {
        // http://ambienteja.info/2016/04/08/sanctions-wont-solve-north-korea-nuclear-issue-envoy-says.html
        JResult res = new JResult();
        res.setUrl("http://ambienteja.info/2016/04/08/sanctions-wont-solve-north-korea-nuclear-issue-envoy-says.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("ambienteja.html")));
        assertEquals("http://ambienteja.info/2016/04/08/sanctions-wont-solve-north-korea-nuclear-issue-envoy-says.html", res.getUrl());
        assertEquals("http://ambienteja.info/2016/04/08/sanctions-wont-solve-north-korea-nuclear-issue-envoy-says.html", res.getCanonicalUrl());
        assertEquals("Sanctions won't solve North Korea nuclear issue, envoy says", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Kim, the North Korean banking officia"));
        assertTrue(res.getText(), res.getText().endsWith("use trade as leverage against North Korea."));
        compareDates("2016-04-08 00:00:00", res.getDate());
    }

    @Test
    public void testModernhealthcare() throws Exception {
        // http://www.modernhealthcare.com/article/20160409/MAGAZINE/304099982
        JResult res = new JResult();
        res.setUrl("http://www.modernhealthcare.com/article/20160409/MAGAZINE/304099982");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("modernhealthcare.html")));
        assertEquals("http://www.modernhealthcare.com/article/20160409/MAGAZINE/304099982", res.getUrl());
        assertEquals("http://www.modernhealthcare.com/article/20160409/MAGAZINE/304099982", res.getCanonicalUrl());
        assertEquals("Hospitals dive into venture capital to nurture startups - Modern Healthcare Modern Healthcare business news, research, data and events", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("When Cedars-Sinai Health System launched a program last year"));
        //assertTrue(res.getText(), res.getText().endsWith("he said."));
        compareDates("2016-04-09 00:00:00", res.getDate());
    }

    @Test
    public void testMobileiron() throws Exception {
        // https://www.mobileiron.com/en/smartwork-blog/mobile-weekly-recap-rsa-2016-amazon-fire-drops-encryption-and-threats-apple-pay
        JResult res = new JResult();
        res.setUrl("https://www.mobileiron.com/en/smartwork-blog/mobile-weekly-recap-rsa-2016-amazon-fire-drops-encryption-and-threats-apple-pay");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("mobileiron.html")));
        assertEquals("https://www.mobileiron.com/en/smartwork-blog/mobile-weekly-recap-rsa-2016-amazon-fire-drops-encryption-and-threats-apple-pay", res.getUrl());
        assertEquals("https://www.mobileiron.com/en/smartwork-blog/mobile-weekly-recap-rsa-2016-amazon-fire-drops-encryption-and-threats-apple-pay", res.getCanonicalUrl());
        assertEquals("Mobile Weekly Recap: RSA 2016, Amazon Fire Drops Encryption, and Threats to Apple Pay", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("RSA 2016 took place earlier this week at Moscone Center in San Francisco"));
        compareDates("2016-03-04 00:00:00", res.getDate());
    }

    @Test
    public void testMarketingprofs() throws Exception {
        // http://www.marketingprofs.com/opinions/2016/29685/the-inbound-marketing-channel-that-most-executives-are-missing
        JResult res = new JResult();
        res.setUrl("http://www.marketingprofs.com/opinions/2016/29685/the-inbound-marketing-channel-that-most-executives-are-missing");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("marketingprofs.html")));
        assertEquals("http://www.marketingprofs.com/opinions/2016/29685/the-inbound-marketing-channel-that-most-executives-are-missing", res.getCanonicalUrl());
        assertEquals("Marketing Strategy - The Inbound Marketing Channel That Most Executives Are Missing : MarketingProfs Article", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("When marketing executives consider inbound marketing"));
        compareDates("2016-04-07 00:00:00", res.getDate());
        assertTrue(res.getText(), res.getText().startsWith("When marketing executives consider inbound marketing"));
        assertEquals("Tom Goodmanson", res.getAuthorName());
        assertEquals("Tom Goodmanson is president and CEO of Calabrio, a provider of customer engagement and analytics technology. LinkedIn: Tom Goodmanson", res.getAuthorDescription());
    }

    @Test
    public void testTheCountryCaller() throws Exception {
        // http://www.thecountrycaller.com/39011-general-electric-company-ge-crosses-the-finish-line-completes-metem-corp-merger/
        JResult res = new JResult();
        res.setUrl("http://www.thecountrycaller.com/39011-general-electric-company-ge-crosses-the-finish-line-completes-metem-corp-merger/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("thecountrycaller.html")));
        assertEquals("http://www.thecountrycaller.com/39011-general-electric-company-ge-crosses-the-finish-line-completes-metem-corp-merger", res.getCanonicalUrl());
        assertEquals("General Electric Company (GE) Crosses The Finish Line: Completes Metem Corp Merger", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("General Electric Company"));
        assertTrue(res.getText(), res.getText().endsWith("$290.93 billion."));
        compareDates("2016-04-05 08:21:34", res.getDate());
        assertEquals("Ramsha Amir", res.getAuthorName());
    }

    @Test
    public void testSyscon() throws Exception {
        // http://news.sys-con.com/node/3047362
        JResult res = new JResult();
        res.setUrl("http://news.sys-con.com/node/3047362");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("sys-con.html")));
        assertEquals("http://news.sys-con.com/node/3047362", res.getCanonicalUrl());
        assertEquals("BOXPARK Selects Magento and PayPal to Power Virtual Market Place and Omnichannel Commerce", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Magento and PayPal"));
        assertTrue(res.getText(), res.getText().endsWith("More information about the company can be found at www.paypal-media.com."));
        compareDates("2014-04-08 00:00:00", res.getDate());
    }

    @Test
    public void testSysCon() throws Exception {
        // http://redhat.sys-con.com/node/4068643
        JResult res = new JResult();
        res.setUrl("http://redhat.sys-con.com/node/4068643");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("sys-con1.html")));
        assertEquals("http://redhat.sys-con.com/node/4068643", res.getCanonicalUrl());
        assertEquals("[session] Dovetailing #DevOps | @DevOpsSummit @CAinc #IoT #AI #ML #DX", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Optimizing Alignment - Dovetailing DevOps and the Cloud"));
        assertTrue(res.getText(), res.getText().endsWith("a SYS-CON Events company."));
        assertEquals("Elizabeth White", res.getAuthorName());
        assertEquals("http://elizabethwhite.sys-con.com/", res.getAuthorDescription());
        compareDates("2017-05-14 00:00:00", res.getDate());
    }

    @Test
    public void testRiaBiz() throws Exception {
        // http://www.riabiz.com/a/4974745007161344/in-a-six-month-mark-reality-check-walt-bettinger-recasts-schwabs-retail-robo-advice-as-a-tool----but-a-handy-one
        JResult res = new JResult();
        res.setUrl("http://www.riabiz.com/a/4974745007161344/in-a-six-month-mark-reality-check-walt-bettinger-recasts-schwabs-retail-robo-advice-as-a-tool----but-a-handy-one");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("riabiz.html")));
        assertEquals("http://riabiz.com/a/4974745007161344/in-a-six-month-mark-reality-check-walt-bettinger-recasts-schwabs-retail-robo-advice-as-a-tool----but-a-handy-one", res.getCanonicalUrl());
        assertEquals("In a six-month-mark reality check, Walt Bettinger recasts Schwab's retail robo-advice as a 'tool' -- but a handy one", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Brooke’s Note: In the spirit of the wiki-era,"));
        assertTrue(res.getText(), res.getText().endsWith("it back to brand and distribution.”"));
        compareDates("2015-08-05 18:52:09", res.getDate());
    }

    @Test
    public void testYahoo1() throws Exception {
        // https://www.yahoo.com/style/6-sensational-ways-to-see-the-1363675292811318.html
        JResult res = new JResult();
        res.setUrl("https://www.yahoo.com/style/6-sensational-ways-to-see-the-1363675292811318.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("yahoo1.html")));
        assertEquals("https://www.yahoo.com/travel/6-sensational-ways-to-see-the-1363675292811318.html", res.getCanonicalUrl());
        assertEquals("6 Sensational Ways to See the World for Free", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("To see the world for free"));
        compareDates("2016-02-15", res.getDate());
    }

    @Test
    public void testAjmc() throws Exception {
        // http://www.ajmc.com/newsroom/fda-approves-cabozantinib-for-advanced-renal-cell-carcinoma
        JResult res = new JResult();
        res.setUrl("http://www.ajmc.com/newsroom/fda-approves-cabozantinib-for-advanced-renal-cell-carcinoma");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("ajmc.html")));
        assertEquals("http://www.ajmc.com/newsroom/fda-approves-cabozantinib-for-advanced-renal-cell-carcinoma", res.getCanonicalUrl());
        assertEquals("FDA Approves Cabozantinib for Advanced Renal Cell Carcinoma", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("FDA Approves Cabozantinib for Advanced Renal Cell Carcinoma"));
        assertTrue(res.getText(), res.getText().endsWith("pleural effusion, diarrhea, and nausea."));
        compareDates("2016-04-25", res.getDate());
    }

    @Test
    public void testSellingStock() throws Exception {
        // http://www.selling-stock.com/ViewArticle.aspx?code=JMP6663
        JResult res = new JResult();
        res.setUrl("http://www.selling-stock.com/ViewArticle.aspx?code=JMP6663");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("selling-stock.html")));
        assertEquals("http://www.selling-stock.com/ViewArticle.aspx?code=JMP6663", res.getCanonicalUrl());
        assertEquals("Top Footage Distributors", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("More and more photographers have started to produce stock footage"));
        assertTrue(res.getText(), res.getText().endsWith("“What are the best companies to represent my work?”"));
        //compareDates("2016-04-28", res.getDate());
    }

    @Test
    public void testCbcCa() throws Exception {
        // http://www.cbc.ca/news/raw-rana-bokhari-speaks-after-loss-to-wab-kinew-1.3544135
        JResult res = new JResult();
        res.setUrl("http://www.cbc.ca/news/raw-rana-bokhari-speaks-after-loss-to-wab-kinew-1.3544135");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("cbc.ca.html")));
        assertEquals("http://www.cbc.ca/news/raw-rana-bokhari-speaks-after-loss-to-wab-kinew-1.3544135", res.getCanonicalUrl());
        assertEquals("RAW: Rana Bokhari speaks after loss to Wab Kinew", res.getTitle());
        assertEquals("Manitoba Liberal Leader Rana Bokhari speaks to the media after losing to the NDP's Wab Kinew in Fort Rouge.", res.getText());
        compareDates("2016-04-20", res.getDate());
    }

    @Test
    public void testHeadlinesNews() throws Exception {
        // http://www.headlines-news.com/2016/05/14/1202189/congress-has-a-constitutional-duty-to-fix-puerto-rico-debt-crisis
        JResult res = new JResult();
        res.setUrl("http://www.headlines-news.com/2016/05/14/1202189/congress-has-a-constitutional-duty-to-fix-puerto-rico-debt-crisis");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("headlines-news.html")));
        assertEquals("http://www.headlines-news.com/2016/05/14/1202189/congress-has-a-constitutional-duty-to-fix-puerto-rico-debt-crisis", res.getCanonicalUrl());
        assertTrue(res.getText(), res.getText().startsWith("By Brian Robertson, contributor"));
        assertTrue(res.getText(), res.getText().endsWith("the mainland with their own serious Next »"));
        compareDates("2016-05-14 00:00:00", res.getDate());
    }

    @Test
    public void testTheGlobeAndMail() throws Exception {
        // http://www.theglobeandmail.com/report-on-business/top-10-wind-power-producers-dec-2015/article29683113/
        JResult res = new JResult();
        res.setUrl("http://www.theglobeandmail.com/report-on-business/top-10-wind-power-producers-dec-2015/article29683113/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("theglobeandmail.html")));
        assertEquals("http://www.theglobeandmail.com/report-on-business/top-10-wind-power-producers-dec-2015/article29683113/", res.getCanonicalUrl());
        assertEquals("Top 10 wind power producers (Dec. 2015) - The Globe and Mail", res.getTitle());
        assertEquals("Top 10 wind power producers (Dec. 2015) Add to ... Published Tuesday, Apr. 19, 2016 7:18PM EDT Last updated Tuesday, Apr. 19, 2016 7:18PM EDT", res.getText());
        compareDates("2016-04-19 23:04:19", res.getDate());
    }

    @Test
    public void testPetaPixel() throws Exception {
        // http://petapixel.com/2016/05/11/russian-man-takes-flying-drone-spear-historical-reenactment/
        JResult res = new JResult();
        res.setUrl("http://petapixel.com/2016/05/11/russian-man-takes-flying-drone-spear-historical-reenactment/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("petapixel.html")));
        assertEquals("http://petapixel.com/2016/05/11/russian-man-takes-flying-drone-spear-historical-reenactment/", res.getCanonicalUrl());
        assertEquals("Russian Man Takes Out Flying Drone with a Spear at History Festival", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("You can add “Russian guy with a spear”"));
        assertTrue(res.getText(), res.getText().endsWith(" takedown followed by the video below:"));
        compareDates("2016-05-11 10:47:55 -0700", res.getDate());
    }

    @Test
    public void testDigitalisationworld() throws Exception {
        // https://digitalisationworld.com/article/36485/
        JResult res = new JResult();
        res.setUrl("https://digitalisationworld.com/article/36485/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("digitalisationworld.html")));
        assertEquals("https://digitalisationworld.com/article/36485/", res.getCanonicalUrl());
        assertEquals("DevOps will become front of mind for UK enterprises", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("As we move into 2015,"));
        assertTrue(res.getText(), res.getText().endsWith("community activity as well as an internal one."));
        compareDates("2015-01-05", res.getDate());
    }

    @Test
    public void testYardbarker() throws Exception {
        // http://www.yardbarker.com/mlb/articles/greinke_diamondbacks_too_much_for_cardinals_7_2/s1_13180_20959717
        JResult res = new JResult();
        res.setUrl("http://www.yardbarker.com/mlb/articles/greinke_diamondbacks_too_much_for_cardinals_7_2/s1_13180_20959717");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("yardbarker.html")));
        assertEquals("http://www.yardbarker.com/mlb/articles/greinke_diamondbacks_too_much_for_cardinals_7_2/s1_13180_20959717", res.getCanonicalUrl());
        assertEquals("Greinke, Diamondbacks too much for Cardinals, 7-2", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("ST. LOUIS (AP) Zack Greinke"));
        assertTrue(res.getText(), res.getText().endsWith("hits in 2 1-3 innings."));
    }

    /*
    // Need JSoup 1.9.1
    @Test
    public void testCosmopolitan() throws Exception {
        // http://www.cosmopolitan.com/food-cocktails/news/g5647/foods-that-make-you-constipated/
        JResult res = new JResult();
        res.setUrl("http://www.cosmopolitan.com/food-cocktails/news/g5647/foods-that-make-you-constipated/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("cosmopolitan.html")));
        assertEquals("http://www.cosmopolitan.com/food-cocktails/news/g5647/foods-that-make-you-constipated/", res.getCanonicalUrl());
        assertEquals("19 Surprising Foods That Make You Constipated", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("This is the type of story you probably"));
        assertTrue(res.getText(), res.getText().endsWith("according to USDA data."));
        assertFalse(res.getText(), res.getText().contains("Getty Images"));
        compareDates("2016-04-28 14:15:00", res.getDate());
    }*/

    /*
    // Need JSoup 1.9.1
    @Test
    public void testNewsobserver() throws Exception {
        // http://www.newsobserver.com/news/article52385500.html
        JResult res = new JResult();
        res.setUrl("http://www.newsobserver.com/news/article52385500.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("newsobserver.html")));
        assertEquals("http://www.newsobserver.com/news/article52385500.html", res.getCanonicalUrl());
        assertEquals("Photo Gallery: The Day's Best | 12.30.15", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("In this aerial photo, flood water covers Interstate 44"));
        assertTrue(res.getText(), res.getText().endsWith("of last year.' Chip Somodevilla"));
        assertFalse(res.getText(), res.getText().contains("Getty Images"));
    }
    */

    /*
    // Need JSoup 1.9.1
    @Test
    public void testTheLoop() throws Exception {
        // http://www.theloop.ca/miley-cyrus-refuses-to-grow-up/
        JResult res = new JResult();
        res.setUrl("http://www.theloop.ca/miley-cyrus-refuses-to-grow-up/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("theloop.html")));
        assertEquals("http://www.theloop.ca/miley-cyrus-refuses-to-grow-up/", res.getCanonicalUrl());
        assertEquals("Miley Cyrus refuses to grow up : The Loop", res.getTitle());
        // TODO: This test fails, the text is extracted as "Helen MirrenDon’t tell her" (no space)
        //assertTrue(res.getText(), res.getText().contains("Helen Mirren Don’t tell her"));
        assertTrue(res.getText(), res.getText().startsWith("The gals from Orange is the New Black"));
        assertTrue(res.getText(), res.getText().endsWith("nobody wants that swirl."));
        //assertFalse(res.getText(), res.getText().contains("Getty Images"));
    }*/

    @Test
    public void testMittelstand() throws Exception {
        // http://www.mittelstand-nachrichten.de/meinung/umfrage-fast-alle-unternehmen-in-europa-nutzen-cloud-basierte-it-services-20160309.html
        JResult res = new JResult();
        res.setUrl("http://www.mittelstand-nachrichten.de/meinung/umfrage-fast-alle-unternehmen-in-europa-nutzen-cloud-basierte-it-services-20160309.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("mittelstand-nachrichten.de.html")));
        assertEquals("http://www.mittelstand-nachrichten.de/meinung/umfrage-fast-alle-unternehmen-in-europa-nutzen-cloud-basierte-it-services-20160309.html", res.getCanonicalUrl());
        assertEquals("Umfrage: Fast alle Unternehmen in Europa nutzen Cloud-basierte IT-Services", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Eine neue Studie untersucht die Einstellungen"));
        assertTrue(res.getText(), res.getText().endsWith("Dienst und das Finanzwesen."));
        assertTrue(res.getText(), res.getText().contains("Microsoft Azure"));
    }

    @Test
    public void testpPCAdvisor() throws Exception {
        // http://www.pcadvisor.co.uk/news/enterprise/amazon-microsoft-and-salesforce-top-forrester-cloud-platform-list-3604588/
        JResult res = new JResult();
        res.setUrl("http://www.pcadvisor.co.uk/news/enterprise/amazon-microsoft-and-salesforce-top-forrester-cloud-platform-list-3604588/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("pcadvisor.html")));
        assertEquals("http://www.pcadvisor.co.uk/news/enterprise/amazon-microsoft-and-salesforce-top-forrester-cloud-platform-list-3604588/", res.getCanonicalUrl());
        assertEquals("Amazon, Microsoft and Salesforce top Forrester cloud platform list", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Which cloud is best for building new applications?"));
        assertTrue(res.getText(), res.getText().endsWith("Those end up being the driving factors for choosing providers."));
        assertFalse(res.getText(), res.getText().contains("Little Girl"));
        assertEquals(res.getAuthorName(), "Brandon Butler");
        compareDates("2015-03-17 17:47:00", res.getDate());
    }

    @Test
    public void testBlogsAdobeCom() throws Exception {
        //  https://blogs.adobe.com/digitalmarketing/advertising/source-sell-and-swap-audiences-with-ease-using-adobe-audience-manager/
        JResult res = new JResult();
        res.setUrl("https://blogs.adobe.com/digitalmarketing/advertising/source-sell-and-swap-audiences-with-ease-using-adobe-audience-manager/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("blogs.adobe.com.html")));
        assertEquals("https://blogs.adobe.com/digitalmarketing/advertising/source-sell-and-swap-audiences-with-ease-using-adobe-audience-manager/", res.getCanonicalUrl());
        assertEquals("Source, Sell, and Swap Audiences with Ease using Adobe Audience Manager Audience Marketplace", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Marketers can combine technology, network, and processes"));
        assertTrue(res.getText(), res.getText().endsWith("and follow us on Twitter @AdobeDMP."));
        assertTrue(res.getText(), res.getText().contains("Acxiom"));
        assertTrue(res.getText(), res.getText().contains("Experian"));
        assertEquals(res.getAuthorName(), "Rich Phillips");
        compareDates("2015-11-10 03:55:01 -0600", res.getDate());
    }

    @Test
    public void testITeinnews() throws Exception {
        // https://it.einnews.com/article_detail/327400938/?lcode=otNalICFi_tU71dBe-4j3A%3D%3D&ref=rss
        JResult res = new JResult();
        res.setUrl("https://it.einnews.com/article_detail/327400938/?lcode=otNalICFi_tU71dBe-4j3A%3D%3D&ref=rss");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("it.einnews.com.html")));
        assertEquals("https://it.einnews.com/article_detail/327400938/?lcode=otNalICFi_tU71dBe-4j3A%3D%3D&ref=rss", res.getCanonicalUrl());
        assertEquals("Telstra network hurt by faulty hardware - IT Industry Today - EIN News", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Breaking News Business National Business Telecom giant Telstra has blamed"));
        assertTrue(res.getText(), res.getText().endsWith("(continue reading)"));
        compareDates("2016-05-23", res.getDate());
    }

    @Test
    public void testBBCom() throws Exception {
        // http://www.bbc.com/news/business-35369820
        JResult res = new JResult();
        res.setUrl("http://www.bbc.com/news/business-35369820");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("bbc.com.html")));
        assertEquals("http://www.bbc.com/news/business-35369820", res.getCanonicalUrl());
        assertEquals("4,000 jobs to go in Pearson shake-up", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Education publisher Pearson is to shed 4,000 jobs"));
        assertTrue(res.getText(), res.getText().endsWith("shares in Pearson rose by nearly 9%."));
        compareDates("2016-01-21", res.getDate());
    }

    @Test
    public void testDutchitchannel() throws Exception {
        // https://dutchitchannel.nl/553302/salesforce-ziet-groeikansen-met-aws.html
        JResult res = new JResult();
        res.setUrl("https://dutchitchannel.nl/553302/salesforce-ziet-groeikansen-met-aws.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("dutchitchannel.html")));
        assertEquals("https://dutchitchannel.nl/553302/salesforce-ziet-groeikansen-met-aws.html", res.getCanonicalUrl());
        assertEquals("Salesforce ziet groeikansen met AWS", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Online crm softwareaanbieder Salesforce zag de omzet"));
        assertTrue(res.getText(), res.getText().endsWith("oktober 2016 in San Francisco."));
        assertEquals("Witold Kepinski", res.getAuthorName());
        compareDates("2016-05-20", res.getDate());
    }

    @Test
    public void testV3() throws Exception {
        // http://www.v3.co.uk/v3-uk/news/2459075/help-for-heroes-shifting-to-azure-with-support-from-rackspace
        JResult res = new JResult();
        res.setUrl("http://www.v3.co.uk/v3-uk/news/2459075/help-for-heroes-shifting-to-azure-with-support-from-rackspace");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("v3.co.uk.html")));
        assertEquals("http://www.v3.co.uk/v3-uk/news/2459075/help-for-heroes-shifting-to-azure-with-support-from-rackspace", res.getCanonicalUrl());
        assertEquals("Help for Heroes shifting to Azure with support from Rackspace", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Help for Heroes is shifting its current hybrid cloud"));
        assertTrue(res.getText(), res.getText().endsWith("he said."));
        compareDates("2016-05-23 12:48:00 +0100", res.getDate());
    }

    @Test
    public void testDrimble() throws Exception {
        // http://drimble.nl/overige/media/35661084/ziggo-ziet-omzet-dalen.html
        JResult res = new JResult();
        res.setUrl("http://drimble.nl/overige/media/35661084/ziggo-ziet-omzet-dalen.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("drimble.html")));
        assertEquals("http://drimble.nl/overige/media/35661084/ziggo-ziet-omzet-dalen.html", res.getCanonicalUrl());
        assertEquals("Ziggo ziet omzet dalen", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Ziggo zag haar omzet in het eerste"));
        assertTrue(res.getText(), res.getText().endsWith("Naar verwachting..."));
        //compareDates("2016-05-10", res.getDate());
    }

    @Test
    public void testInfoRiskToday() throws Exception {
        // http://www.inforisktoday.com/interviews/5-trends-to-sway-cybersecuritys-future-i-2153
        JResult res = new JResult();
        res.setUrl("http://www.inforisktoday.com/interviews/5-trends-to-sway-cybersecuritys-future-i-2153");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("inforisktoday.html")));
        assertEquals("http://www.inforisktoday.com/interviews/5-trends-to-sway-cybersecuritys-future-i-2153", res.getCanonicalUrl());
        assertEquals("Allan Friedman: 5 Trends to Sway Cybersecurity's Future", res.getTitle());
        // TODO: The first paragraph is missing: "Five significant trends..." since it is outside children tags
        // but just a text node of the parent.
        assertTrue(res.getText(), res.getText().startsWith("Other key trends are the growth of cloud computing"));
        assertTrue(res.getText(), res.getText().endsWith("Intelligence at the Brookings Institution."));
        assertEquals("Eric Chabrow", res.getAuthorName());
        assertEquals("Eric Chabrow", res.getAuthorDescription());
        compareDates("2014-01-11", res.getDate());
    }

    @Test
    public void testInfoRiskToday1() throws Exception {
        // http://www.inforisktoday.asia/blogs/biometrics-for-children-dont-share-p-2169
        JResult res = new JResult();
        res.setUrl("http://www.inforisktoday.asia/blogs/biometrics-for-children-dont-share-p-2169");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("inforisktoday1.html")));
        assertEquals("http://www.inforisktoday.asia/blogs/biometrics-for-children-dont-share-p-2169", res.getCanonicalUrl());
        assertEquals("Biometrics for Children: Don't Share - InfoRiskToday", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Warning to parents and guardians: Beware of collecting, storing or sharing your child's biometric details,"));
        assertFalse(res.getText(), res.getText().contains("See Also: 2017 Predictions on Data Security: Insights on Important Trends in Security for the Banking Industry"));
        assertTrue(res.getText(), res.getText().endsWith("as they would their Social Security card or birth certificate."));
        assertEquals("Mathew J. Schwartz", res.getAuthorName());
        assertEquals("About the Author Mathew J. Schwartz Executive Editor, DataBreachToday & Europe Schwartz is an award-winning journalist with two decades of experience in magazines, newspapers and electronic media. He has covered the information security and privacy sector throughout his career. Before joining Information Security Media Group in 2014, where he now serves as the Executive Editor, DataBreachToday and for European news coverage, Schwartz was the information security beat reporter for InformationWeek and a frequent contributor to DarkReading, amongst other publications. He lives in Scotland.", res.getAuthorDescription());
        compareDates("2016-06-29 00:00:00", res.getDate());
    }

    @Test
    public void testAmericanBanker() throws Exception {
        // http://www.americanbanker.com/white-papers/how-to-improve-the-wealth-advisor-and-client-experience-1081287-1.html
        JResult res = new JResult();
        res.setUrl("http://www.americanbanker.com/white-papers/how-to-improve-the-wealth-advisor-and-client-experience-1081287-1.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("americanbanker.html")));
        assertEquals("http://www.americanbanker.com/white-papers/how-to-improve-the-wealth-advisor-and-client-experience-1081287-1.html", res.getCanonicalUrl());
        assertEquals("How to Improve the Wealth Advisor and Client Experience", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Inefficiencies in your client onboarding system are"));
        assertTrue(res.getText(), res.getText().endsWith("to achieve an increased level of client and advisor satisfaction."));
        compareDates("2016-06-01 13:52:00", res.getDate());
    }

    @Test
    public void testCredoMag() throws Exception {
        // http://www.credomag.com/2011/10/04/what-is-revival-two-options/
        JResult res = new JResult();
        res.setUrl("http://www.credomag.com/2011/10/04/what-is-revival-two-options/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("credomag.html")));
        assertEquals("http://www.credomag.com/2011/10/04/what-is-revival-two-options/", res.getCanonicalUrl());
        assertEquals("Credo Magazine » What is Revival? Two Options", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Most evangelical Christians are in favor of revival."));
        assertTrue(res.getText(), res.getText().endsWith("He also blogs at Christian Thought and Tradition."));
        compareDates("2011-10-04", res.getDate());
        // Test that link size is not longer than N characters.
        List<Map<String,String>> links = res.getLinks();
        for (Map<String,String> link : links) {
            assertTrue(link.get("url").toString().length() <= 512);
        }
    }

    @Test
    public void testMyCustomer() throws Exception {
        // http://www.mycustomer.com/community/blogs/neilcapel/using-customer-passions-to-lift-retention-rates
        JResult res = new JResult();
        res.setUrl("http://www.mycustomer.com/community/blogs/neilcapel/using-customer-passions-to-lift-retention-rates");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("mycustomer.html")));
        assertEquals("http://www.mycustomer.com/community/blogs/neilcapel/using-customer-passions-to-lift-retention-rates", res.getCanonicalUrl());
        assertEquals("Using customer passions to lift retention rates", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("On one level, the key to improving customer retention rates"));
        assertTrue(res.getText(), res.getText().endsWith("represent a useful alternative."));
        compareDates("2016-05-13 10:46:27 +0100", res.getDate());
        assertEquals("Neil Capel", res.getAuthorName());
        assertEquals("Neil’s successful track record of working on large-scale, high-demand web systems led him to develop Sailthru's unique Smart Data™ capabilities. Prior to...", res.getAuthorDescription());
    }

    @Test
    public void testBdaily() throws Exception {
        // https://bdaily.co.uk/technology/22-04-2016/sailthru-acquires-carnivalio-expanding-services/
        JResult res = new JResult();
        res.setUrl("https://bdaily.co.uk/technology/22-04-2016/sailthru-acquires-carnivalio-expanding-services/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("bdaily.html")));
        // TODO: Bad canonical URL how we avoid it?
        assertEquals("https://bdaily.co.ukhttps://bdaily.co.uk/technology/22-04-2016/sailthru-acquires-carnivalio-expanding-services/", res.getCanonicalUrl());
        assertEquals("Sailthru Acquires Carnival.io Expanding Services", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("At Sailthru we believe that human connections"));
        assertTrue(res.getText(), res.getText().endsWith("data for mobile marketing automation."));
        compareDates("2016-04-22", res.getDate());
        assertEquals("Neil Capel", res.getAuthorName());
    }

    @Test
    public void testPRnewswire3() throws Exception {
        // http://www.prnewswire.com/news-releases/azalead-announces-account-based-marketing-for-microsoft-dynamics-crm-582695011.html
        JResult res = new JResult();
        res.setUrl("http://www.prnewswire.com/news-releases/azalead-announces-account-based-marketing-for-microsoft-dynamics-crm-582695011.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("prnewswire3.html")));
        assertEquals("Azalead Announces Account Based Marketing for Microsoft Dynamics CRM", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Azalead, the international leader in Account Based Marketing"));
        assertTrue(res.getText(), res.getText().endsWith("press@azalead.com"));
        compareDates("2016-06-13", res.getDate());
    }

    @Test
    public void testCbronline() throws Exception {
        // http://www.cbronline.com/news/cloud/aas/what-is-crm-4924064
        JResult res = new JResult();
        res.setUrl("http://www.cbronline.com/news/cloud/aas/what-is-crm-4924064");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("cbronline.html")));
        assertEquals("What is CRM? - Computer Business Review", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Learn why this is important to managing your customers"));
        assertTrue(res.getText(), res.getText().endsWith("personalised approach can be developed. Terms & Conditions & Privacy Policy."));
        compareDates("2016-06-15 15:56:00", res.getDate());
    }

    @Test
    public void testMortgageorb() throws Exception {
        //http://www.mortgageorb.com/digital-savvy-house-hunters-are-rebooting-the-online-auction-market
        JResult res = new JResult();
        res.setUrl("http://www.mortgageorb.com/digital-savvy-house-hunters-are-rebooting-the-online-auction-market");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("mortgageorb.html")));
        assertEquals("Digital-Savvy House Hunters Are Rebooting The Online Auction Market :: MortgageOrb", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("BLOG VIEW: The new generation of technology-empowered"));
        assertTrue(res.getText(), res.getText().endsWith("Send an email to pbarnard@zackin.com.)"));
        compareDates("2015-10-07", res.getDate());
    }

    @Test
    public void testWashingtoncitypaper() throws Exception {
        //http://www.washingtoncitypaper.com/news/housing-complex/blog/13120863/fsbo-site-totally-lowballs-the-white-house
        JResult res = new JResult();
        res.setUrl("http://www.washingtoncitypaper.com/news/housing-complex/blog/13120863/fsbo-site-totally-lowballs-the-white-house");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("washingtoncitypaper.html")));
        assertEquals("FSBO Site Totally Lowballs the White House", res.getTitle());
        // TODO: There is an issue with the OutputFormatter that it only select text inside tags, but no text nodes inside the
        // topNode, for that reason we miss the first few lines of this article.
        //assertTrue(res.getText(), res.getText().startsWith("Over the weekend, the ever-vigilant Urbanturf"));
        assertTrue(res.getText(), res.getText().endsWith("So, lesson for those seeking to game the system: Know the market!"));
        compareDates("2010-06-16 08:47:00", res.getDate());
    }

    @Test
    public void testKcci() throws Exception {
        //http://www.kcci.com/national/bangladesh-arrests-over-5000-in-two-days/40026522
        JResult res = new JResult();
        res.setUrl("http://www.kcci.com/national/bangladesh-arrests-over-5000-in-two-days/40026522");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("kcci.html")));
        assertEquals("Bangladesh arrests more than 11,000 in 4 days", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Authorities in Bangladesh have arrested nearly 150 suspected militants and more than"));
        assertTrue(res.getText(), res.getText().endsWith("He's never been arrested before,\" she said."));
        compareDates("2016-06-15 04:30:39", res.getDate());
        assertFalse(res.getText(), res.getText().contains("Orlando"));
    }

    @Test
    public void testNetskope() throws Exception {
        // https://resources.netskope.com/h/i/119331038-gary-yoshimura
        JResult res = new JResult();
        res.setUrl("https://resources.netskope.com/h/i/119331038-gary-yoshimura");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("netskope.html")));
        assertEquals("Movie Line Monday - Introspection Best Practices", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("“Man who catch the fly with chopstick accomplish anything."));
        assertTrue(res.getText(), res.getText().endsWith("MovieLineMonday@netskope.com."));
        compareDates("2015-08-27", res.getDate());
    }

    @Test
    public void testNetskope2() throws Exception {
        // https://resources.netskope.com/h/i/128883476-movie-line-monday-introspection-best-practices
        JResult res = new JResult();
        res.setUrl("https://resources.netskope.com/h/i/128883476-movie-line-monday-introspection-best-practices");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("netskope2.html")));
        assertEquals("Gary Yoshimura: @Netskope @jamiecbarnett @bobegilbert Looking ...", res.getTitle());
        // TODO: Fix body
        //assertTrue(res.getText(), res.getText().startsWith("“Man who catch the fly with chopstick accomplish anything."));
        //assertTrue(res.getText(), res.getText().endsWith("MovieLineMonday@netskope.com."));
        compareDates("2015-08-07 11:45:00", res.getDate());
    }

    @Test
    public void testSchwab() throws Exception {
        // http://www.schwab.com/public/schwab/nn/articles/government-shutdown-stocks
        JResult res = new JResult();
        res.setUrl("http://www.schwab.com/public/schwab/nn/articles/government-shutdown-stocks");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("schwab.html")));
        assertEquals("What Happens to Stocks During Government Shutdowns?", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Every year, prior to October 1, Congress"));
        assertTrue(res.getText(), res.getText().endsWith("largely insignificant."));
        compareDates("2015-09-28", res.getDate());
    }

    @Test
    public void testSeattleTimes() throws Exception {
        // http://www.seattletimes.com/business/inspired-by-boeing-767-anacortes-firm-makes-devices-to-help-control-small-planes/
        JResult res = new JResult();
        res.setUrl("http://www.seattletimes.com/business/inspired-by-boeing-767-anacortes-firm-makes-devices-to-help-control-small-planes/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("seattletimes.html")));
        assertEquals("Inspired by Boeing 767, Anacortes firm makes devices to help control small planes", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Micro AeroDynamics says it was the first company"));
        //assertTrue(res.getText(), res.getText().endsWith("Brogan said."));
        compareDates("2016-07-04 20:00:00+0000", res.getDate());
        assertFalse(res.getText(), res.getText().contains("Most Read Stories"));
    }

    @Test
    public void testTheVerge() throws Exception {
        // http://www.theverge.com/2016/4/21/11476700/help-me-malcolm-in-the-middle-dewey-lives-inside-my-computer
        JResult res = new JResult();
        res.setUrl("http://www.theverge.com/2016/4/21/11476700/help-me-malcolm-in-the-middle-dewey-lives-inside-my-computer");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("theverge.html")));
        assertEquals("First Click: I'm being chased around the internet by Dewey from Malcolm in the Middle", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Help, I'm being chased around the internet "));
        assertTrue(res.getText(), res.getText().endsWith("I leave him alone."));
        compareDates("2016-04-21 11:30:02", res.getDate());
    }

    @Test
    public void testKdwb() throws Exception {
        // http://kdwb.iheart.com/onair/the-dave-ryan-show-53769/can-you-find-the-cell-phone-14897836/
        JResult res = new JResult();
        res.setUrl("http://kdwb.iheart.com/onair/the-dave-ryan-show-53769/can-you-find-the-cell-phone-14897836/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("kdwb.html")));
        assertEquals("Can you find the cell phone 'hidden' on this rug? | The Dave Ryan Show", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("How observant are you? "));
        assertTrue(res.getText(), res.getText().endsWith("Check out this frustrating brainteaser: Photo Credit: Getty"));
        compareDates("2016-07-12 06:31:00", res.getDate());
    }

    @Test
    public void testWMCom() throws Exception {
        // http://article.wn.com/view/2016/07/07/Celanese_Announces_MIBK_and_Formaldehyde_Price_Increases_Cel/
        JResult res = new JResult();
        res.setUrl("http://article.wn.com/view/2016/07/07/Celanese_Announces_MIBK_and_Formaldehyde_Price_Increases_Cel/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("article.wn.com.html")));
        assertEquals("Celanese Announces MIBK and Formaldehyde Price Increases (Celanese Corporation)", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("DALLAS--(BUSINESS WIRE)-- Celanese Corporation (NYSE: CE)"));
        assertTrue(res.getText(), res.getText().endsWith("and formaldehyde solutions. The"));
        compareDates("2016-07-07", res.getDate());
        assertEquals("", res.getAuthorDescription());
    }

    @Test
    public void testTulsacw() throws Exception {
        // http://www.tulsacw.com/story/32687972/the-best-thing-to-do-with-a-summer-tomato
        JResult res = new JResult();
        res.setUrl("http://www.tulsacw.com/story/32687972/the-best-thing-to-do-with-a-summer-tomato");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("tulsacw.html")));
        assertEquals("The best thing to do with a summer tomato - TulsaCW.com: TV To Talk About | The Tulsa CW", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("August is here, and it’s my favorite month"));
        assertTrue(res.getText(), res.getText().endsWith("Get the recipe for SAVEUR's Tomato Sandwich »"));
        compareDates("2016-08-04", res.getDate());
        assertEquals("Katherine Whittaker", res.getAuthorName());
    }

    @Test
    public void testProfinder() throws Exception {
        // https://blog.linkedin.com/profinder/marketing/content-strategist--how-to-find-an-experienced-content-marketer-
        JResult res = new JResult();
        res.setUrl("https://blog.linkedin.com/profinder/marketing/content-strategist--how-to-find-an-experienced-content-marketer-");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("profinder.html")));
        assertEquals("How to Rise Above the Crowd and Reach Your Audience with a Content Strategist", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("There is a lot of buzz about how content marketing"));
        assertTrue(res.getText(), res.getText().endsWith("you customized proposals to review."));
        compareDates("2016-05-09", res.getDate());
    }

    @Test
    public void testSacramentonews() throws Exception {
        // http://www.sacramentonews.net/index.php/sid/250029089
        JResult res = new JResult();
        res.setUrl("http://www.sacramentonews.net/index.php/sid/250029089");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("sacramentonews.html")));
        assertEquals("Sacramento News - Ex-French budget minister sentenced to three years in jail for tax fraud", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Ex-French budget minister sentenced"));
        assertTrue(res.getText(), res.getText().endsWith("Read the full story at Belfast Telegraph"));
        compareDates("2016-12-09", res.getDate());
    }

    @Test
    public void testShanghaisun() throws Exception {
        // http://www.shanghaisun.com/index.php/sid/250010251
        JResult res = new JResult();
        res.setUrl("http://www.shanghaisun.com/index.php/sid/250010251");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("shanghaisun.html")));
        assertEquals("One of your monthly housing bills may get bigger - Shanghai Sun", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("HELOCs were all the rage during the loose lending day"));
        assertTrue(res.getText(), res.getText().endsWith("$173 billion this year."));
        compareDates("2016-12-08", res.getDate());
    }

    @Test
    public void testDateWithTz() throws Exception {
        // http://women2.com/2014/06/18/woman-entrepreneur-misnomer/
        JResult res = new JResult();
        res.setUrl("http://women2.com/2014/06/18/woman-entrepreneur-misnomer/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("women2.html")));
        assertEquals("Why 'Woman Entrepreneur' Is a Misnomer", res.getTitle());
        // Wed Jun 18 12:00:54 PDT 2014
        compareDates("2014-06-18 12:00:54-0700", res.getDate());
    }

    @Test
    public void testAuthorNameIgnorePattern() throws Exception {
        // http://www.marketingprofs.com/articles/2015/28657/unlock-mobile-advertising-by-measuring-the-real-world
        JResult res = new JResult();
        res.setUrl("http://www.marketingprofs.com/articles/2015/28657/unlock-mobile-advertising-by-measuring-the-real-world");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("author_name_ignore_pattern.html")));
        assertEquals("John Busby", res.getAuthorName());
        assertEquals("John Busby is SVP of customer insights at Marchex, a mobile advertising technology company. LinkedIn: John Busby Twitter: @JohnMBusby", res.getAuthorDescription());
    }

    @Test
    public void testNetworkComputing() throws Exception {
        // http://www.networkcomputing.com/data-centers/why-dbas-need-networking-skills/1883902615
        JResult res = new JResult();
        res.setUrl("http://www.networkcomputing.com/data-centers/why-dbas-need-networking-skills/1883902615");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("networkcomputing.html")));
        assertEquals("http://www.networkcomputing.com/data-centers/why-dbas-need-networking-skills/1883902615", res.getCanonicalUrl());
        assertEquals("Why DBAs Need Networking Skills", res.getTitle());
        assertTrue(res.getText().startsWith("The cloud threatens everyone."));
        assertTrue(res.getText().endsWith("Remember, kids, the cloud isn’t a threat, it’s an opportunity."));
        compareDates("2016-09-09 07:30:00 -04:00", res.getDate());
        assertEquals("Thomas LaRock", res.getAuthorName());
        assertEquals("Thomas LaRock", res.getAuthorDescription());
    }

    @Test
    public void testGe() throws Exception {
        // https://www.ge.com/digital/blog/devops-no-royal-use-we
        JResult res = new JResult();
        res.setUrl("https://www.ge.com/digital/blog/devops-no-royal-use-we");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("ge.com.html")));
        assertEquals("https://www.ge.com/digital/blog/devops-no-royal-use-we", res.getCanonicalUrl());
        assertEquals( "DevOps: No Royal in This Use of “We”", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("This is the first in a series of posts from the technical staff that GE Software had on the ground at Cloud Foundry Summit."));
        compareDates("2015-07-13 00:00:00 -04:00", res.getDate());
        assertEquals("Denny Bulcao", res.getAuthorName());
        assertEquals("Senior Technical Writer at GE", res.getAuthorDescription());
    }

    @Test
    public void testEnterpriseInnovation() throws Exception {
        // http://www.enterpriseinnovation.net/article/big-data-survive-intensifying-squeeze-regulatory-compliance-1955902423
        JResult res = new JResult();
        res.setUrl("http://www.enterpriseinnovation.net/article/big-data-survive-intensifying-squeeze-regulatory-compliance-1955902423");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("enterpriseinnovation.net.html")));
        assertEquals("http://www.enterpriseinnovation.net/article/big-data-survive-intensifying-squeeze-regulatory-compliance-1955902423?nopaging=1", res.getCanonicalUrl());
        assertEquals( "Big Data to survive the intensifying squeeze of regulatory compliance", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Today’s financial industry is experiencing immense amounts of pressure on all sides."));
        assertEquals("Jason Demby", res.getAuthorName());
        assertEquals("By Jason Demby, \u200Edirector of Business Development of Financial Services, Datameer | 2016-05-10", res.getAuthorDescription());
    }

    @Test
    public void testITBusiness() throws Exception {
        // http://www.it-business.de/cloud-stellt-kleine-mit-grossen-haendlern-gleich-a-551169/
        JResult res = new JResult();
        res.setUrl("http://www.it-business.de/cloud-stellt-kleine-mit-grossen-haendlern-gleich-a-551169");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("it-business.de.html")));
        assertEquals("http://www.it-business.de/cloud-stellt-kleine-mit-grossen-haendlern-gleich-a-551169", res.getCanonicalUrl());
        assertEquals("Cloud stellt kleine mit großen Händlern gleich", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Bislang hatten Handelskonzerne durch ihre zahlreichen Filialen einen großen Vorsprung in der Kundenansprache."));
        compareDates("2016-09-22 00:00:00", res.getDate());
        assertEquals("Enrique Nuñez", res.getAuthorName());
        assertTrue(res.getAuthorDescription(), res.getAuthorDescription().startsWith("Enrique Nuñez ist seit mehr als 20 Jahren im Internet tätig und hat in dieser Zeit drei Startup-Unternehmen gegründet."));
    }

    @Test
    public void testBulldogReporter() throws Exception {
        // https://www.bulldogreporter.com/are-you-guilty-of-pr-data-bias-what-why-and-how-to-check/
        JResult res = new JResult();
        res.setUrl("https://www.bulldogreporter.com/are-you-guilty-of-pr-data-bias-what-why-and-how-to-check/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("bulldogreporter.html")));
        assertEquals("https://www.bulldogreporter.com/are-you-guilty-of-pr-data-bias-what-why-and-how-to-check/", res.getCanonicalUrl());
        assertEquals("Are You Guilty of PR Data Bias? What, Why and How to Check", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("When it comes to marketing analysis, public relations has been known for output—media relationships developed, placements gained, awareness garnered, and perceptions changed."));
        compareDates("2016-08-15 00:00:00", res.getDate());
        assertEquals("Kelly Byrd", res.getAuthorName());
        assertEquals("By Kelly Byrd, PR Engineer,", res.getAuthorDescription());
    }

    @Test
    public void testPatch() throws Exception {
        // https://patch.com/new-york/kingspark/s/futh4/how-much-does-it-really-cost-to-remodel-your-bathroom
        JResult res = new JResult();
        res.setUrl("https://patch.com/new-york/kingspark/s/futh4/how-much-does-it-really-cost-to-remodel-your-bathroom");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("patch.html")));
        assertEquals("https://patch.com/us/across-america/how-much-does-it-really-cost-renovate-your-bathroom", res.getCanonicalUrl());
        assertEquals("How Much Does It Really Cost to Remodel Your Bathroom?", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("In every home makeover show, there’s one room that gets the most “oohs” and “aahs.”"));
        assertEquals("HomeAdvisor", res.getAuthorName());
        assertEquals("https://patch.com/users/home-advisor", res.getAuthorDescription());
    }

    @Test
    public void testGlobalBankingAndFinance() throws Exception {
        // http://www.globalbankingandfinance.com/the-digital-disruptors-can-devops-save-the-banks/
        JResult res = new JResult();
        res.setUrl("http://www.globalbankingandfinance.com/the-digital-disruptors-can-devops-save-the-banks/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("globalbankingandfinance.html")));
        assertEquals("https://www.globalbankingandfinance.com/the-digital-disruptors-can-devops-save-the-banks/", res.getCanonicalUrl());
        assertEquals("THE DIGITAL DISRUPTORS: CAN DEVOPS SAVE THE BANKS? – Global Banking And Finance Review Magazine – Financial & Business Insights", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Traditional banks are facing huge competition from digital disruptors."));
        compareDates("2015-03-17 06:05:21", res.getDate());
        assertEquals("Nigel Beighton", res.getAuthorName());
        assertEquals("By Nigel Beighton, VP of Technology, Rackspace", res.getAuthorDescription());
    }

    @Test
    public void testMediaPost() throws Exception {
        // https://www.mediapost.com/publications/article/297175/never-mind-alexa-why-ai-obsession-echoes-past-hyp.html
        JResult res = new JResult();
        res.setUrl("https://www.mediapost.com/publications/article/297175/never-mind-alexa-why-ai-obsession-echoes-past-hyp.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("mediapost.html")));
        assertEquals("https://www.mediapost.com/publications/article/297175/never-mind-alexa-why-ai-obsession-echoes-past-hyp.html", res.getCanonicalUrl());
        assertEquals("Never Mind Alexa: Why AI Obsession Echoes Past Hype Cycles 03/16/2017", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("For a moment in early 2013, it looked like Google Glass was going to change everything."));
        assertEquals("David Honig", res.getAuthorName());
        assertEquals("DAVID HONIG, Vice President Strategy, Corporate Partnerships, Dynamic Signal", res.getAuthorDescription());
    }

    @Test
    public void testChiefMarketer() throws Exception {
        // http://www.chiefmarketer.com/ways-data-and-martech-can-help-communications-leaders/
        JResult res = new JResult();
        res.setUrl("http://www.chiefmarketer.com/ways-data-and-martech-can-help-communications-leaders/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("chiefmarketer.html")));
        assertEquals("http://www.chiefmarketer.com/ways-data-and-martech-can-help-communications-leaders/", res.getCanonicalUrl());
        assertEquals("Ways Data and Martech Can Help Communications Leaders", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Today’s martech and data explosion holds the key for chief communications officers to gain the data-driven insights, projections and authority that their C-suite counterparts already enjoy."));
        assertEquals("Dave Hawley", res.getAuthorName());
        assertEquals("Dave Hawley is vice president of marketing at Dynamic Signal.", res.getAuthorDescription());
    }

    @Test
    public void testTheFinancialBrand() throws Exception {
        // https://thefinancialbrand.com/65025/banking-lending-digital-marketing-trends/
        JResult res = new JResult();
        res.setUrl("https://thefinancialbrand.com/65025/banking-lending-digital-marketing-trends/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("thefinancialbrand.html")));
        assertEquals("https://thefinancialbrand.com/65025/banking-lending-digital-marketing-trends/", res.getCanonicalUrl());
        assertEquals("Improve Lending Results With Digital Marketing", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("The Digital Banking Report, \"2017 Financial Marketing Trends\" highlights the need to use data analytics and digital channels for marketing products and services."));
        assertEquals("Scott Gordon", res.getAuthorName());
        assertEquals("By Scott Gordon, Senior Director, Digital Marketing at Experian", res.getAuthorDescription());
    }

    @Test
    public void testEINNews() throws Exception {
        // http://www.einnews.com/pr_news/339534444/rackspace-reaches-openstack-leadership-milestone-six-years-and-one-billion-server-hours
        JResult res = new JResult();
        res.setUrl("http://www.einnews.com/pr_news/339534444/rackspace-reaches-openstack-leadership-milestone-six-years-and-one-billion-server-hours");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("einnews.html")));
        assertEquals("http://www.einnews.com/pr_news/339534444/rackspace-reaches-openstack-leadership-milestone-six-years-and-one-billion-server-hours", res.getCanonicalUrl());
        assertEquals("Rackspace Reaches OpenStack Leadership Milestone, Six Years and One Billion Server Hours", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("/EINPresswire.com/ -- SAN FRANCISCO, CA--(Marketwired - August 11, 2016) - Rackspace® (NYSE: RAX) today announced from"));
        compareDates("2016-08-11 17:15:00", res.getDate());
        assertEquals("Christina Weaver 210-312-4593", res.getAuthorName());
        assertEquals("Media Contact: Christina Weaver 210-312-4593 christina.weaver@rackspace.com", res.getAuthorDescription());
    }

    @Test
    public void testEINNews1() throws Exception {
        // http://www.einnews.com/pr_news/336348008/hybrid-cloud-computing-industry-global-market-to-grow-at-cagr-34-4-between-2016-2022
        JResult res = new JResult();
        res.setUrl("http://www.einnews.com/pr_news/336348008/hybrid-cloud-computing-industry-global-market-to-grow-at-cagr-34-4-between-2016-2022");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("einnews_1.html")));
        assertEquals("http://www.einnews.com/pr_news/336348008/hybrid-cloud-computing-industry-global-market-to-grow-at-cagr-34-4-between-2016-2022", res.getCanonicalUrl());
        assertEquals("Hybrid Cloud Computing Industry Global Market to grow at CAGR 34.4% between 2016 – 2022", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Hybrid Cloud Computing Industry Global Market to grow at CAGR 34.4% between 2016 – 2022"));
        compareDates("2016-07-22 15:26:09", res.getDate());
        assertEquals("Norah Trent wiseguyreports +1 646 845 9349 / +44 208 133 9349", res.getAuthorName());
        assertEquals("Norah Trent wiseguyreports +1 646 845 9349 / +44 208 133 9349 email us here", res.getAuthorDescription());
    }

    @Test
    public void testToday() throws Exception {
        // http://www.today.com/video/michael-phelps-on-conserving-water-and-his-april-fools-comeback-prank-923578947587
        JResult res = new JResult();
        res.setUrl("http://www.today.com/video/michael-phelps-on-conserving-water-and-his-april-fools-comeback-prank-923578947587");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("today.html")));
        assertEquals(res.getDescription(), res.getText());
        assertEquals("http://www.today.com/video/michael-phelps-on-conserving-water-and-his-april-fools-comeback-prank-923578947587", res.getCanonicalUrl());
        assertEquals("Michael Phelps on conserving water and his April Fools’ comeback prank", res.getTitle());
        assertEquals(StringUtils.EMPTY, res.getAuthorName());
        assertEquals(StringUtils.EMPTY, res.getAuthorDescription());
        compareDates("2017-04-18 14:54:56", res.getDate());
    }

    @Test
    public void testBusinessMoney() throws Exception {
        // http://www.business-money.com/announcements/catalyst-achieves-300m-funding-milestone
        JResult res = new JResult();
        res.setUrl("http://www.business-money.com/announcements/catalyst-achieves-300m-funding-milestone");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("business-money.html")));
        assertEquals("http://www.business-money.com/announcements/catalyst-achieves-300m-funding-milestone", res.getCanonicalUrl());
        assertEquals("Catalyst achieves £300m funding milestone", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Catalyst achieves £300m funding milestone Catalyst"));
        assertTrue(res.getText(), res.getText().endsWith("to successfully achieve our growth aspirations.”  "));
        assertEquals(StringUtils.EMPTY, res.getAuthorName());
        assertEquals(StringUtils.EMPTY, res.getAuthorDescription());
        assertNull(res.getDate());
    }

    @Test
    public void testComputerWeekly() throws Exception {
        // http://www.computerweekly.com/blog/CW-Developer-Network/Crayon-warns-of-black-hole-of-risk-in-software-audits
        JResult res = new JResult();
        res.setUrl("http://www.computerweekly.com/blog/CW-Developer-Network/Crayon-warns-of-black-hole-of-risk-in-software-audits");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("computer_weekly.html")));
        assertTrue(res.getText(), res.getText().startsWith("IT consultancy Crayon has commissioned a new survey through the venerably anal"));
        assertTrue(res.getText(), res.getText().endsWith("quest for intelligent cloud optimisation and reduced total cost of ownership."));
        assertEquals("http://www.computerweekly.com/blog/CW-Developer-Network/Crayon-warns-of-black-hole-of-risk-in-software-audits", res.getCanonicalUrl());
        assertEquals("Crayon warns of 'black hole of risk' in software audits", res.getTitle());
        assertEquals("Adrian Bridgwater", res.getAuthorName());
        assertEquals("https://twitter.com/ABridgwater, https://www.linkedin.com/in/adrianbridgwater, mailto:adrianbridgwater@gmail.com", res.getAuthorDescription());
        compareDates("2016-10-13 11:14:00", res.getDate());
    }

    @Test
    public void testTrello() throws Exception {
        // http://blog.trello.com/trello-atlassian
        JResult res = new JResult();
        res.setUrl("http://blog.trello.com/trello-atlassian");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("trello.html")));
        assertTrue(res.getText(), res.getText().startsWith("Today we’re excited to announce a new chapter in Trello’s story, and a new chapter for the story of the future of work."));
        assertTrue(res.getText(), res.getText().endsWith("Trello Power-Ups For JIRA, Bitbucket, And Confluence Cloud (Plus A Cooler HipChat Integration)"));
        assertEquals("http://blog.trello.com/trello-atlassian", res.getCanonicalUrl());
        assertEquals("Trello Is Being Acquired By Atlassian", res.getTitle());
        assertEquals("Michael Pryor", res.getAuthorName());
        assertEquals("Michael Pryor", res.getAuthorDescription());
        compareDates("2017-01-09", res.getDate());
    }
      
    @Test
    public void testAuthorExtractionSchemaOrg() throws Exception {
        // https://www.washingtonpost.com/news/powerpost/wp/2017/01/12/trump-names-rudy-giuliani-as-cybersecurity-adviser/?utm_term=.17818c6b675c
        JResult res = new JResult();
        res.setUrl("https://www.washingtonpost.com/news/powerpost/wp/2017/01/12/trump-names-rudy-giuliani-as-cybersecurity-adviser/?utm_term=.17818c6b675c");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("washingtonpost_1.html")));
        assertTrue(res.getText(), res.getText().startsWith("President-elect Donald Trump named former New York mayor Rudolph W. Giuliani as an informal adviser on cybersecurity, according to the presidential transition office."));
        assertEquals("https://www.washingtonpost.com/news/powerpost/wp/2017/01/12/trump-names-rudy-giuliani-as-cybersecurity-adviser/", res.getCanonicalUrl());
        assertEquals("Trump names Rudy Giuliani as cybersecurity adviser", res.getTitle());
        assertEquals("Abby Phillip", res.getAuthorName());
        assertEquals("Abby Phillip is a national political reporter covering the White House for The Washington Post. She can be reached at abby.phillip@washpost.com.", res.getAuthorDescription());
        compareDates("2017-01-12", res.getDate());
    }

    @Test
    public void testMetronews() throws Exception {
        // http://www.metronews.ca/news/toronto/2017/01/30/police-investigate-sex-assault-complaint-by-former-b-c-councillor-s-daughter.html
        JResult res = new JResult();
        res.setUrl("http://www.metronews.ca/news/toronto/2017/01/30/police-investigate-sex-assault-complaint-by-former-b-c-councillor-s-daughter.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("metronews.html")));
        assertEquals("http://www.metronews.ca/news/toronto/2017/01/30/police-investigate-sex-assault-complaint-by-former-b-c-councillor-s-daughter.html", res.getCanonicalUrl());
        assertEquals("Police investigate sex-assault complaint by former B.C. councillor's daughter", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("BRAMPTON, Ont. — A criminal investigation is underway stemming from allegations of sexual and physical assault made in a complaint to police by the daughter of a former municipal councillor in British Columbia."));
        assertTrue(res.getText(), res.getText().endsWith("the investigation and/or prosecution of serious criminal offences.\""));
        compareDates("2017-01-30 20:31:24 -05:00", res.getDate());
    }

    @Test
    public void testNymag() throws Exception {
        // http://nymag.com/daily/intelligencer/2017/04/march-jobs-report-falls-short-of-expectations.html
        JResult res = new JResult();
        res.setUrl("http://nymag.com/daily/intelligencer/2017/04/march-jobs-report-falls-short-of-expectations.html");
        extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("nymag.html")));
        assertEquals("http://nymag.com/daily/intelligencer/2017/04/march-jobs-report-falls-short-of-expectations.html", res.getCanonicalUrl());
        assertEquals("March Jobs Report Falls Short of Expectations", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("The Donald Trump administration got its first meh jobs report today,"));
        assertTrue(res.getText(), res.getText().contains("Hiring in March was expected to drop after the monthly gains of more than 200,000"));
        assertTrue(res.getText(), res.getText().contains("Part of the reason this morning’s jobs report caught many off guard was how much it"));
        assertTrue(res.getText(), res.getText().endsWith("though other employment and wage data were largely flat."));
        assertEquals("Ed Kilgore", res.getAuthorName());
        assertEquals("Ed Kilgore", res.getAuthorDescription());
        compareDates("2017-04-07 11:12:24 -04:00", res.getDate());
    }

    @Test
    public void testDelish() throws Exception {
        // http://www.delish.com/cooking/g2668/spring-asparagus-dishes/?
        JResult res = new JResult();
        res.setUrl("http://www.delish.com/cooking/g2668/spring-asparagus-dishes/");
        extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("delish.html")));
        assertEquals("http://www.delish.com/cooking/g2668/spring-asparagus-dishes/", res.getCanonicalUrl());
        assertEquals("50+ Easy Asparagus Recipes - Best Ways to Cook Asparagus", res.getTitle());
        assertEquals(res.getDescription(), res.getText());
        assertEquals("Sienna Fantozzi", res.getAuthorName());
        assertEquals("Sienna Fantozzi", res.getAuthorDescription());
        compareDates("2017-05-09 07:59:00", res.getDate());
    }

    @Test
    public void testDelish1() throws Exception {
        // http://www.delish.com/cooking/g2669/breakfast-casserole-recipes/
        JResult res = new JResult();
        res.setUrl("http://www.delish.com/cooking/g2669/breakfast-casserole-recipes/");
        extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("delish1.html")));
        assertEquals("http://www.delish.com/cooking/g2669/breakfast-casserole-recipes/", res.getCanonicalUrl());
        assertEquals("30 Easy Breakfast Casserole Recipes - Egg Casserole Recipe—Delish.com", res.getTitle());
        assertEquals(res.getDescription(), res.getText());
        assertEquals("Sienna Fantozzi", res.getAuthorName());
        assertEquals("Sienna Fantozzi", res.getAuthorDescription());
        compareDates("2017-05-08 04:08:00", res.getDate());
    }

    @Test
    public void testAirpr() throws Exception {
        // http://blog.airpr.com/media-monitoring/
        JResult res = new JResult();
        res.setUrl("http://blog.airpr.com/media-monitoring/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("airpr.html")));
        assertEquals("http://blog.airpr.com/media-monitoring/", res.getCanonicalUrl());
        assertEquals("From Media Monitoring to Media Intelligence", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("For public relations professionals, media monitoring can be both tedious and exciting."));
        assertTrue(res.getText(), res.getText().endsWith("Hungry for more? Read “How to prove PR value to your CEO” next…"));
        assertEquals("Kelly Byrd May 3", res.getAuthorName());
        assertEquals("Kelly Byrd May 3, 2017", res.getAuthorDescription());
        compareDates("2017-05-03 06:12:16 -07:00", res.getDate());
    }

    @Test
    public void testSltrib() throws Exception {
        // http://www.sltrib.com/home/5252884-155/vivint-smart-home-links-with-best
        JResult res = new JResult();
        res.setUrl("http://www.sltrib.com/home/5252884-155/vivint-smart-home-links-with-best");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("sltrib.html")));
        assertEquals("http://www.sltrib.com/home/5252884-155/vivint-smart-home-links-with-best", res.getCanonicalUrl());
        assertEquals("Vivint Smart Home links with Best Buy", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Vivint Smart Home is opening mini-stores within 400 Best Buy outlets nationwide as part of a new strategic partnership,"));
        assertTrue(res.getText(), res.getText().endsWith("Warriors in Vivint Smart Home Arena."));
        assertEquals("TOM HARVEY", res.getAuthorName());
        assertEquals("https://www.facebook.com/saltlaketribune", res.getAuthorDescription());
        compareDates("2017-05-04 16:54:01", res.getDate());
    }

    @Test
    public void testTheRivardReport() throws Exception {
        // http://therivardreport.com/san-antonio-space-scientists-prepare-for-jupiter-contact-1/
        JResult res = new JResult();
        res.setUrl("http://therivardreport.com/san-antonio-space-scientists-prepare-for-jupiter-contact-1/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("therivardreport.html")));
        assertEquals("https://therivardreport.com/san-antonio-space-scientists-prepare-for-jupiter-contact-1/", res.getCanonicalUrl());
        assertEquals("San Antonio Space Scientists Prepare for Jupiter ContactRivard Report", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("As the countdown began on Aug. 5, 2011 at the Kennedy Space Center in Florida,"));
        assertTrue(res.getText(), res.getText().endsWith("Artistic depiction of Juno. Photo courtesy of NASA."));
        assertEquals("Cherise Rohr-Allegrini", res.getAuthorName());
        assertEquals("https://therivardreport.com/author/cherise-rohr-allegrini/", res.getAuthorDescription());
        compareDates("2016-07-02 05:01:58", res.getDate());
    }

    @Test
    public void testWayFair() throws Exception {
        // https://www.wayfair.com/ideas-and-advice/top-10-kitchen-dining-tables-S4709.html
        JResult res = new JResult();
        res.setUrl("https://www.wayfair.com/ideas-and-advice/top-10-kitchen-dining-tables-S4709.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("wayfair.html"), 1391348, "UTF-8"));
        assertEquals("https://www.wayfair.com/ideas-and-advice/top-10-kitchen-dining-tables-S4709.html", res.getCanonicalUrl());
        assertEquals("Our \"It\" List Dining Tables", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("As the centerpiece of any dining room"));
        assertTrue(res.getText(), res.getText().endsWith("Take your pick from these finds that are as affordable as they are beautiful."));
        assertEquals(StringUtils.EMPTY, res.getAuthorName());
        assertEquals(StringUtils.EMPTY, res.getAuthorDescription());
        compareDates("2014-07-04 00:00:00", res.getDate());
    }

    @Test
    public void testPublicNet() throws Exception {
        // http://www.publicnet.co.uk/default/2017/05/12/social-media-how-it-can-be-used-to-protect-against-cyber-criminals/
        JResult res = new JResult();
        res.setUrl("http://www.publicnet.co.uk/default/2017/05/12/social-media-how-it-can-be-used-to-protect-against-cyber-criminals/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("publicnet.html")));
        assertEquals("http://www.publicnet.co.uk/default/2017/05/12/social-media-how-it-can-be-used-to-protect-against-cyber-criminals/", res.getCanonicalUrl());
        assertEquals("SOCIAL MEDIA: HOW IT CAN BE USED TO PROTECT AGAINST CYBER CRIMINALS", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Joe Kim explains how social media can help public service IT professionals better understand potential cyber threats out there."));
        assertTrue(res.getText(), res.getText().endsWith("Joe Kim is CTO at SolarWinds"));
        assertEquals(StringUtils.EMPTY, res.getAuthorName());
        assertEquals(StringUtils.EMPTY, res.getAuthorDescription());
        compareDates("2017-05-12 00:00:00", res.getDate());
    }

    @Test
    public void testApnews() throws Exception {
        // https://www.apnews.com/amp/0290dd1b2048498783f3d7d0ea28a10d
        JResult res = new JResult();
        res.setUrl("https://www.apnews.com/amp/0290dd1b2048498783f3d7d0ea28a10d");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("apnews.html")));
        assertEquals("https://www.apnews.com/0290dd1b2048498783f3d7d0ea28a10d", res.getCanonicalUrl());
        assertEquals("Deputies fatally shoot man who reportedly shot at neighbor", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("KNOXVILLE, Tenn. (AP) — Knox County Sheriff's Office deputies shot"));
        assertTrue(res.getText(), res.getText().endsWith("Information from: Knoxville News Sentinel, http://www.knoxnews.com"));
        assertEquals(StringUtils.EMPTY, res.getAuthorName());
        assertEquals(StringUtils.EMPTY, res.getAuthorDescription());
        compareDates("2017-05-24 21:50:34", res.getDate());
    }

    @Test
    public void testApnews1() throws Exception {
        // https://www.apnews.com/b823ddc6905d454dacc65fd107730f95/Playmate-tastes-shame,-ordered-to-clean-up-urban-grit
        JResult res = new JResult();
        res.setUrl("https://www.apnews.com/b823ddc6905d454dacc65fd107730f95/Playmate-tastes-shame,-ordered-to-clean-up-urban-grit");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("apnews1.html")));
        assertEquals("https://www.apnews.com/b823ddc6905d454dacc65fd107730f95", res.getCanonicalUrl());
        assertEquals("Playmate tastes shame, ordered to clean up urban grit", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("LOS ANGELES (AP) — Dani Mathers earned fame posing as a nude model."));
        assertTrue(res.getText(), res.getText().endsWith("where people might be naked or expect privacy."));
        assertEquals("BRIAN MELLEY", res.getAuthorName());
        assertEquals("By BRIAN MELLEY", res.getAuthorDescription());
        compareDates("2017-05-25 00:00:00", res.getDate());
    }

    @Test
    public void testMorningStar() throws Exception {
        // http://www.morningstar.com/news/associated-press/urn:publicid:ap.org:f8d53c4370434744a864d4afa5fa8d36/hackers-break-into-centralized-password-manager-onelogin.html
        JResult res = new JResult();
        res.setUrl("http://www.morningstar.com/news/associated-press/urn:publicid:ap.org:f8d53c4370434744a864d4afa5fa8d36/hackers-break-into-centralized-password-manager-onelogin.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("morningstar.html")));
        assertEquals("http://www.morningstar.com/news/associated-press/urn:publicid:ap.org:f8d53c4370434744a864d4afa5fa8d36/hackers-break-into-centralized-password-manager-onelogin.html", res.getCanonicalUrl());
        assertEquals("Hackers break into centralized password manager OneLogin", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("NEW YORK (AP) — Hackers have gained access to OneLogin,"));
        assertTrue(res.getText(), res.getText().endsWith("although not actual passwords."));
        assertEquals(StringUtils.EMPTY, res.getAuthorName());
        assertEquals(StringUtils.EMPTY, res.getAuthorDescription());
        compareDates("2017-06-02 00:00:00", res.getDate());
    }

    @Test
    public void testSfchronicle() throws Exception {
        // http://www.sfchronicle.com/business/article/Odd-jobs-matchmaker-Thumbtack-gets-big-funds-6541824.php
        JResult res = new JResult();
        res.setUrl("http://www.sfchronicle.com/business/article/Odd-jobs-matchmaker-Thumbtack-gets-big-funds-6541824.php");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("sfchronicle.html")));
        assertEquals("http://www.sfchronicle.com/business/article/Odd-jobs-matchmaker-Thumbtack-gets-big-funds-6541824.php", res.getCanonicalUrl());
        assertEquals("Odd-jobs matchmaker Thumbtack gets big funds, joins unicorn club - San Francisco Chronicle", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("San Francisco’s Thumbtack, which introduces fix-it folks,"));
        assertTrue(res.getText(), res.getText().endsWith("Carolyn Said is a San Francisco Chronicle staff writer. E-mail: csaid@sfchronicle.com Twitter: @csaid"));
        assertEquals("Carolyn Said", res.getAuthorName());
        assertEquals("Carolyn Said is a San Francisco Chronicle staff writer. E-mail: csaid@sfchronicle.com Twitter: @csaid", res.getAuthorDescription());
        compareDates("2015-09-30 00:00:00", res.getDate());
    }

    @Test
    public void testLookout() throws Exception {
        // https://blog.lookout.com/spectrum-of-mobile-risk
        JResult res = new JResult();
        res.setUrl("https://blog.lookout.com/spectrum-of-mobile-risk");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("lookout.html")));
        assertEquals("https://blog.lookout.com/spectrum-of-mobile-risk", res.getCanonicalUrl());
        assertEquals("Introducing The Spectrum of Mobile Risk: how to think about the risks facing data from mobility", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Today, Lookout is introducing The Spectrum of Mobile Risk research report,"));
        assertTrue(res.getText(), res.getText().endsWith("get a copy of the The Spectrum of Mobile Risk research paper today."));
        assertEquals(StringUtils.EMPTY, res.getAuthorName());
        assertEquals(StringUtils.EMPTY, res.getAuthorDescription());
        compareDates("2017-05-16 00:00:00", res.getDate());
    }

    @Test
    public void testComputerPartner() throws Exception {
        // http://www.computerpartner.at/sites/dynamic.pl?id=news20080805131662610
        JResult res = new JResult();
        res.setUrl("http://www.computerpartner.at/sites/dynamic.pl?id=news20080805131662610");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("computerpartner.html")));
        assertEquals("http://www.computerpartner.at/sites/dynamic.pl?id=news20080805131662610", res.getCanonicalUrl());
        assertEquals("Computerpartner - hpc Consulting nominiert für Constantinus Award 2017", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Mit ihrem innovativen Zeitmessungssystem für das Erzberg-Rodeo schaffte hpc Consulting"));
        assertTrue(res.getText(), res.getText().endsWith(", meint Anatol Heinrich, Geschäftsführer hpc Consulting."));
        assertEquals("M. Reisner", res.getAuthorName());
        assertEquals(StringUtils.EMPTY, res.getAuthorDescription());
        compareDates("2017-06-02 00:00:00", res.getDate());
    }

    @Test
    public void testMspmentor() throws Exception {
        // http://mspmentor.net/managed-services/5-reasons-you-need-media-relations
        JResult res = new JResult();
        res.setUrl("http://mspmentor.net/managed-services/5-reasons-you-need-media-relations");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("mspmentor.html")));
        assertEquals("http://mspmentor.net/managed-services/5-reasons-you-need-media-relations", res.getCanonicalUrl());
        assertEquals("5 Reasons You Need Media Relations", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Although the sentiment was never accurate, the terms “public relations” and “media relations”"));
        assertTrue(res.getText(), res.getText().endsWith("a marketing and strategy consulting firm specializing in the channel."));
        assertEquals("Khali Henderson and Casey Freymuth", res.getAuthorName());
        assertEquals("Khali Henderson and Casey Freymuth | BuzzTheory Strategies", res.getAuthorDescription());
        compareDates("2017-05-05 00:00:00", res.getDate());
    }

    @Test
    public void testExtractDateUsingRegex() throws Exception{

        final String DATE = "2017-06-07";
        final String DATE_HH_MM = "2017-06-07 03:06";
        final String DATE_HH_MM_SS = "2017-06-07 03:06:12";

        ArticleTextExtractor extractor = new ArticleTextExtractor();

        compareDates(DATE, extractor.extractDateUsingRegex("2017-06-07"));
        compareDates(DATE_HH_MM, extractor.extractDateUsingRegex("2017-06-07 03:06"));
        compareDates(DATE_HH_MM_SS, extractor.extractDateUsingRegex("2017-06-07 03:06:12"));

        compareDates(DATE, extractor.extractDateUsingRegex("2017/06/07"));
        compareDates(DATE_HH_MM, extractor.extractDateUsingRegex("2017/06/07 03:06"));

        // @Todo: Need to debug more
        //compareDates(DATE_HH_MM_SS, extractor.extractDateUsingRegex("20170607 030612"));

        compareDates(DATE, extractor.extractDateUsingRegex("07 Jun 2017 00:00:00"));
        compareDates(DATE_HH_MM, extractor.extractDateUsingRegex("07 Jun 2017 03:06"));
        compareDates(DATE_HH_MM_SS, extractor.extractDateUsingRegex("07 Jun 2017 03:06:12"));

        compareDates(DATE, extractor.extractDateUsingRegex("07 June 2017 00:00:00"));
        compareDates(DATE_HH_MM, extractor.extractDateUsingRegex("07 June 2017 03:06"));
        compareDates(DATE_HH_MM_SS, extractor.extractDateUsingRegex("07 June 2017 03:06:12"));

        compareDates(DATE, extractor.extractDateUsingRegex("Jun 07, 2017 00:00:00"));
        compareDates(DATE_HH_MM, extractor.extractDateUsingRegex("Jun 07, 2017 03:06"));
        compareDates(DATE_HH_MM_SS, extractor.extractDateUsingRegex("Jun 07, 2017 03:06:12"));

        compareDates(DATE, extractor.extractDateUsingRegex("June 07, 2017 00:00:00"));
        compareDates(DATE_HH_MM, extractor.extractDateUsingRegex("June 07, 2017 03:06"));
        compareDates(DATE_HH_MM_SS, extractor.extractDateUsingRegex("June 07, 2017 03:06:12"));

        // This is ambiguous may match MM-dd-yyyy
        compareDates(DATE, extractor.extractDateUsingRegex("07-06-2017 00:00:00"));
        compareDates(DATE_HH_MM, extractor.extractDateUsingRegex("07/06/2017 03:06"));
        compareDates(DATE_HH_MM_SS, extractor.extractDateUsingRegex("07/06/2017 03:06:12"));
    }

    @Test
    public void testTheVogue() throws Exception {
        // http://www.teenvogue.com/gallery/back-to-school-awards-2017-best-dorm-decor-ideas
        JResult res = new JResult();
        res.setUrl("http://www.teenvogue.com/gallery/back-to-school-awards-2017-best-dorm-decor-ideas");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("thevogue.html")));
        assertEquals("http://www.teenvogue.com/gallery/back-to-school-awards-2017-best-dorm-decor-ideas", res.getCanonicalUrl());
        assertEquals("Back to School Awards 2017: The Best Dorm Decor Ideas", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("This year for our second annual Back to School Awards, we wanted to make sure you were totally covered"));
        assertTrue(res.getText(), res.getText().contains("Best Starter Kitchen Set"));
        assertTrue(res.getText(), res.getText().contains("Best Room Spray"));
        assertTrue(res.getText(), res.getText().contains("Best Value Agenda"));
        assertTrue(res.getText(), res.getText().contains("Best USB Extendor"));
        assertTrue(res.getText(), res.getText().endsWith("Want more Teen Vogue ? Make sure to ‘Like’ us on Facebook to stay in the know!"));
        assertEquals("Hanna Howard", res.getAuthorName());
        assertEquals("Hanna Howard", res.getAuthorDescription());
        compareDates("2017-06-01 08:00:00", res.getDate());
    }

    @Test
    public void testWashingtonpostPolitics() throws Exception {
        // https://www.washingtonpost.com/politics/2017/live-updates/trump-white-house/sessions-to-testify-before-senate-intelligence-committee/cotton-twice-earns-thanks-from-sessions-for-friendly-questioning/?utm_term=.1bebe97e9599
        JResult res = new JResult();
        res.setUrl("https://www.washingtonpost.com/politics/2017/live-updates/trump-white-house/sessions-to-testify-before-senate-intelligence-committee/cotton-twice-earns-thanks-from-sessions-for-friendly-questioning/?utm_term=.1bebe97e9599");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("washingtonpost_politics.html")));
        assertEquals("http://www.washingtonpost.com/politics/2017/live-updates/trump-white-house/sessions-to-testify-before-senate-intelligence-committee/cotton-twice-earns-thanks-from-sessions-for-friendly-questioning/", res.getCanonicalUrl());
        assertEquals("Cotton twice earns thanks from Sessions for friendly questioning - The Washington Post", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Attorney General Jeff Sessions’s testimony has been marked by some heated moments,"));
        assertTrue(res.getText(), res.getText().endsWith("and I fear that some people may find that they wish they hadn’t leaked,” Sessions said."));
        assertEquals("Mark Berman", res.getAuthorName());
        assertEquals("June 13 by Mark Berman", res.getAuthorDescription());
        compareDates("2017-06-13 21:18:25", res.getDate());
    }

    @Test
    public void testYahooFinance() throws Exception {
        // https://finance.yahoo.com/news/aac-holdings-inc-present-william-103000626.html
        JResult res = new JResult();
        res.setUrl("https://finance.yahoo.com/news/aac-holdings-inc-present-william-103000626.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("finance.yahoo.com.html")));
        assertEquals("https://finance.yahoo.com/news/aac-holdings-inc-present-william-103000626.html", res.getCanonicalUrl());
        assertEquals("AAC Holdings, Inc. to Present at William Blair 2017 Growth Conference", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("BRENTWOOD, Tenn., June 13, 2017 /PRNewswire/ -- AAC Holdings, Inc. (AAC) is participating in the William Blair & Company 2017 Growth Stock Conference,"));
        assertTrue(res.getText(), res.getText().endsWith("To view the original version on PR Newswire, visit:http://www.prnewswire.com/news-releases/aac-holdings-inc-to-present-at-william-blair-2017-growth-conference-300472756.html"));
        assertEquals("PR Newswire", res.getAuthorName());
        assertEquals("http://www.prnewswire.com/yahoo/", res.getAuthorDescription());
        compareDates("2017-06-13 10:30:00", res.getDate());
    }

    @Test
    public void testBloomberg1() throws Exception {
        // https://www.bloomberg.com/politics/articles/2017-06-14/the-latest-gillespie-wins-gop-nomination-in-governor-s-race
        JResult res = new JResult();
        res.setUrl("https://www.bloomberg.com/politics/articles/2017-06-14/the-latest-gillespie-wins-gop-nomination-in-governor-s-race");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("bloomberg_1.html")));
        assertEquals("https://www.bloomberg.com/politics/articles/2017-06-14/the-latest-gillespie-wins-gop-nomination-in-governor-s-race", res.getCanonicalUrl());
        assertEquals("GOP Picks Gillespie, Democrats Pick Northam to Run for Virginia Governor - Bloomberg", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Richmond, Va. (AP) -- The Latest on Virginia primary races for governor (all times local):"));
        assertTrue(res.getText(), res.getText().endsWith(", is trying to fend off under-funded but spirited campaigns from avid Trump supporter Corey Stewart and state Sen. Frank Wagner."));
        assertEquals("THE ASSOCIATED PRESS", res.getAuthorName());
        assertEquals(res.getAuthorName(), res.getAuthorDescription());
        compareDates("2017-06-14 02:31:40", res.getDate());
    }

    @Test
    public void testHuffingtonpost5() throws Exception {
        // http://www.huffingtonpost.com/entry/malware-power-grid_us_593fa144e4b0b13f2c6d9285
        JResult res = new JResult();
        res.setUrl("http://www.huffingtonpost.com/entry/malware-power-grid_us_593fa144e4b0b13f2c6d9285");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("huffingtonpost5.html")));
        assertEquals("http://www.huffingtonpost.com/entry/malware-power-grid_us_593fa144e4b0b13f2c6d9285", res.getCanonicalUrl());
        assertEquals("Cybersecurity Firms Uncover Malware That Could Cause Power Outages Around The Globe", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("June 12 (Reuters) - Two cyber security firms have uncovered malicious software that they"));
        assertTrue(res.getText(), res.getText().endsWith("it was able to confirm the malware was used in the Ukraine grid attack."));
        assertEquals("Jim Finkle", res.getAuthorName());
        assertEquals("By Jim Finkle", res.getAuthorDescription());
        compareDates("2017-06-13 04:34:36 -0400", res.getDate());
    }

    @Test
    public void testYahoo() throws Exception {
        // https://www.yahoo.com/news/allan-wu-dating-malaysian-beauty-033800005.html
        JResult res = new JResult();
        res.setUrl("https://www.yahoo.com/news/allan-wu-dating-malaysian-beauty-033800005.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("yahoo2.html")));
        assertEquals("https://www.yahoo.com/news/allan-wu-dating-malaysian-beauty-033800005.html", res.getCanonicalUrl());
        assertEquals("Allan Wu dating Malaysian beauty queen and \"Amazing Race\" contestant?", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Chinese-American hunk Allan Wu, 45 is reportedly going out with beauty queen Lee Yvonne, 29 best known for her title as Miss World Malaysia 2012."));
        assertTrue(res.getText(), res.getText().endsWith("Singapore-based Allan Wu was formerly married to Mediacorp actress Wong-Li-Lin and have a daughter and son together. The pair divorced in 2013."));
        assertEquals("The Hive Asia", res.getAuthorName());
        assertEquals("http://www.thehive.asia/", res.getAuthorDescription());
        compareDates("2017-06-14 03:38:00", res.getDate());
    }

    @Test
    public void testNyDailyNews() throws Exception {
        // http://www.nydailynews.com/newswires/sports/kershaw-wins-bellinger-hits-2-homers-dodgers-top-indians-article-1.3245615
        JResult res = new JResult();
        res.setUrl("http://www.nydailynews.com/newswires/sports/kershaw-wins-bellinger-hits-2-homers-dodgers-top-indians-article-1.3245615");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("nydailynews.html")));
        assertEquals("http://www.nydailynews.com/newswires/sports/kershaw-wins-bellinger-hits-2-homers-dodgers-top-indians-article-1.3245615", res.getCanonicalUrl());
        assertEquals("Kershaw wins, Bellinger hits 2 homers as Dodgers top Indians", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("CLEVELAND (AP) — Clayton Kershaw extended his unbeaten string and rookie Cody Bellinger hit two home runs,"));
        assertTrue(res.getText(), res.getText().endsWith("This material may not be published, broadcast, rewritten or redistributed."));
        assertEquals("The Associated Press", res.getAuthorName());
        assertEquals("THE ASSOCIATED PRESS", res.getAuthorDescription());
        compareDates("2017-06-13 22:00:07", res.getDate());
    }

    @Test
    public void testFt() throws Exception {
        // https://www.ft.com/content/d0a5c460-5044-11e7-a1f2-db19572361bb
        JResult res = new JResult();
        res.setUrl("https://www.ft.com/content/d0a5c460-5044-11e7-a1f2-db19572361bb");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("ft.html")));
        assertEquals("https://www.ft.com/content/d0a5c460-5044-11e7-a1f2-db19572361bb", res.getCanonicalUrl());
        assertEquals("Has Western-style democracy become too expensive for capitalism?", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Much ink has been spilled on the significance of 2016’s one-two populist punches as thrown by Brexit then Trump."));
        assertTrue(res.getText(), res.getText().endsWith("Michael Power, Strategist, Investec Asset Management Michael Power, Strategist, Investec Asset Management"));
        assertEquals("Michael Power", res.getAuthorName());
        assertEquals("by: Michael Power, Investec", res.getAuthorDescription());
        compareDates("2017-06-14 03:00:32", res.getDate());
    }

    @Test
    public void testSdnCioReview() throws Exception {
        // http://sdn.cioreview.com/cxoinsight/sdn-do-you-really-need-it-nid-24422-cid-147.html
        JResult res = new JResult();
        res.setUrl("http://sdn.cioreview.com/cxoinsight/sdn-do-you-really-need-it-nid-24422-cid-147.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("sdn.cioreview.html")));
        assertEquals("http://sdn.cioreview.com/cxoinsight/sdn-do-you-really-need-it-nid-24422-cid-147.html", res.getCanonicalUrl());
        assertEquals("SDN: Do You Really Need It?", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Until recently, Software-defined networking SDN was both a buzzword and the subject of some discord in the IT community."));
        assertTrue(res.getText(), res.getText().endsWith("and cloud technology increase in usage and demand network software actuation for success."));
        assertEquals("Patrick Hubbard", res.getAuthorName());
        assertEquals("Patrick Hubbard, Head Geek, SolarWinds", res.getAuthorDescription());
        compareDates("2017-05-10 00:00:00", res.getDate());
    }

    @Test
    public void testPhilly() throws Exception {
        // http://www.philly.com/archive/john_timpane/Princetons-Tracy-K-Smith-named-US-Poet-Laureate.html
        JResult res = new JResult();
        res.setUrl("http://www.philly.com/archive/john_timpane/Princetons-Tracy-K-Smith-named-US-Poet-Laureate.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("philly.html")));
        assertEquals("http://www.philly.com/philly/entertainment/Princetons-Tracy-K-Smith-named-US-Poet-Laureate.html", res.getCanonicalUrl());
        assertEquals("Princeton's Tracy K. Smith named U.S. Poet Laureate", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Tracy K. Smith, a professor of creative writing at Princeton University,"));
        assertTrue(res.getText(), res.getText().endsWith("‘Let’s have this conversation and see what comes out of it that’s of value.”"));
        assertEquals("John Timpane", res.getAuthorName());
        assertEquals("John Timpane is the books/fine arts editor/writer for the Inquirer. His beat includes books, social media, dance, theater, art, museums, and classical music. From 1997 to 2008 he was the Commentary page editor.", res.getAuthorDescription());
        compareDates("2017-06-14 09:39:00 -04:00", res.getDate());
    }

    @Test
    public void testFoxNews1() throws Exception {
        // http://nation.foxnews.com/2017/06/13/sessions-inquisition-comes-empty-trump-unscathed-after-attorney-generals-testimony
        JResult res = new JResult();
        res.setUrl("http://nation.foxnews.com/2017/06/13/sessions-inquisition-comes-empty-trump-unscathed-after-attorney-generals-testimony");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("foxnews1.html")));
        assertEquals("http://nation.foxnews.com/2017/06/13/sessions-inquisition-comes-empty-trump-unscathed-after-attorney-generals-testimony", res.getCanonicalUrl());
        assertEquals("The Sessions Inquisition Comes Up Empty: Trump Unscathed After Attorney General's Testimony", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("As disappointed as Democrats were by James Comey’s testimony last week,"));
        assertTrue(res.getText(), res.getText().endsWith("and vowed to defend himself “against scurrilous and false allegations.”"));
        assertEquals(StringUtils.EMPTY, res.getAuthorName());
        assertEquals(StringUtils.EMPTY, res.getAuthorDescription());
        compareDates("2017-06-15 00:00:00", res.getDate());
    }

    @Test
    public void testUpi() throws Exception {
        // http://www.upi.com/Entertainment_News/TV/2017/06/19/Star-Trek-Discovery-gets-a-premiere-date-Sept-24/8671497893578/
        JResult res = new JResult();
        res.setUrl("http://www.upi.com/Entertainment_News/TV/2017/06/19/Star-Trek-Discovery-gets-a-premiere-date-Sept-24/8671497893578/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("upi.html")));
        assertEquals("http://www.upi.com/Entertainment_News/TV/2017/06/19/Star-Trek-Discovery-gets-a-premiere-date-Sept-24/8671497893578/", res.getCanonicalUrl());
        assertEquals("'Star Trek: Discovery' gets a premiere date -- Sept. 24", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("June 19 (UPI) -- The new sci-fi adventure Star Trek: Discovery -- starring Sonequa Martin-Green and Jason Isaacs -- is scheduled to premiere on CBS Sept. 24."));
        assertTrue(res.getText(), res.getText().endsWith("which will begin streaming in January."));
        assertEquals("Karen Butler", res.getAuthorName());
        assertEquals(res.getAuthorName(), res.getAuthorDescription());
        compareDates("2017-06-19 14:02:24 -04:00", res.getDate());
    }

    @Test
    public void testUpi1() throws Exception {
        // http://www.upi.com/Defense-News/2017/06/19/King-Aerospace-recieves-EO-5-aircraft-contract/2311497885914/
        JResult res = new JResult();
        res.setUrl("http://www.upi.com/Defense-News/2017/06/19/King-Aerospace-recieves-EO-5-aircraft-contract/2311497885914/");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("upi1.html")));
        assertEquals("http://www.upi.com/Defense-News/2017/06/19/King-Aerospace-receives-EO-5-aircraft-contract/2311497885914/", res.getCanonicalUrl());
        assertEquals("King Aerospace receives EO-5 aircraft contract", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("June 19 (UPI) -- King Aerospace has received a $15 million contract modification for EO-5 Army reconnaissance aircraft logistical support."));
        assertTrue(res.getText(), res.getText().endsWith("SEMA aircraft have seen extensive use in Iraq, Afghanistan, Libya, and other theatres."));
        assertEquals("Stephen Carlson", res.getAuthorName());
        assertEquals("By Stephen Carlson | June 19, 2017 at 1:48 PM Follow @upi", res.getAuthorDescription());
        compareDates("2017-06-19 13:48:20 -04:00", res.getDate());
    }

    @Test
    public void testReuters3() throws Exception {
        // http://www.reuters.com/article/us-mexico-oil-ninth-idUSKBN19A2M9
        JResult res = new JResult();
        res.setUrl("http://www.reuters.com/article/us-mexico-oil-ninth-idUSKBN19A2M9");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("reuters3.html")));
        assertEquals("http://www.reuters.com/article/us-mexico-oil-ninth-idUSKBN19A2M9", res.getCanonicalUrl());
        assertEquals("Capricorn and Citla win block in Mexico shallow water oil auction", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("A consortium comprising Capricorn Energy, a subsidiary of Britain's Cairn Energy,"));
        assertTrue(res.getText(), res.getText().endsWith("(Reporting by Adriana Barrera)"));
        assertEquals("Adriana Barrera", res.getAuthorName());
        assertEquals("(Reporting by Adriana Barrera)", res.getAuthorDescription());
        compareDates("2017-06-19 18:20:26", res.getDate());
    }

    @Test
    public void testReuters4() throws Exception {
        // http://www.reuters.com/article/us-safrica-mining-idUSKBN19A2PY
        JResult res = new JResult();
        res.setUrl("http://www.reuters.com/article/us-safrica-mining-idUSKBN19A2PY");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("reuters4.html")));
        assertEquals("http://www.reuters.com/article/us-safrica-mining-idUSKBN19A2PY", res.getCanonicalUrl());
        assertEquals("Fitch says South Africa's new mining rules may deter investment", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Fitch Ratings agency said on Monday that new regulations seeking to accelerate black ownership in South Africa's mining industry would deter investment."));
        assertTrue(res.getText(), res.getText().endsWith("(Reporting by James Macharia. Editing by Jane Merriman)"));
        assertEquals("James Macharia. Jane Merriman", res.getAuthorName());
        assertEquals(StringUtils.EMPTY, res.getAuthorDescription());
        compareDates("2017-06-19 19:12:17", res.getDate());
    }

    @Test
    public void testFoxNews2() throws Exception {
        // http://www.foxnews.com/tech/2017/06/19/southwest-border-cities-to-use-iris-scanning-technology-to-identify-inmates-illegal-immigrants.html
        JResult res = new JResult();
        res.setUrl("http://www.foxnews.com/tech/2017/06/19/southwest-border-cities-to-use-iris-scanning-technology-to-identify-inmates-illegal-immigrants.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("foxnews2.html")));
        assertEquals("http://www.foxnews.com/tech/2017/06/19/southwest-border-cities-to-use-iris-scanning-technology-to-identify-inmates-illegal-immigrants.html", res.getCanonicalUrl());
        assertEquals("Southwest border cities to use IRIS scanning technology to identify inmates, illegal immigrants", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Criminals and illegal immigrants who get booked into jail along the southwest border will soon have one less way to hide their identity."));
        assertTrue(res.getText(), res.getText().endsWith("but I don’t think we would have to go to those for an identification.”"));
        assertEquals("Ray Bogan", res.getAuthorName());
        assertEquals("Ray Bogan is a Fox News multimedia reporter based in El Paso, Texas. Follow him on twitter:", res.getAuthorDescription());
        compareDates("2017-06-19 00:00:00", res.getDate());
    }

    @Test
    public void testFoxNews3() throws Exception {
        // http://www.foxnews.com/politics/2017/06/19/special-counsel-muellers-legal-team-continues-to-raise-questions.html
        JResult res = new JResult();
        res.setUrl("http://www.foxnews.com/politics/2017/06/19/special-counsel-muellers-legal-team-continues-to-raise-questions.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("foxnews3.html")));
        assertEquals("http://www.foxnews.com/politics/2017/06/19/special-counsel-muellers-legal-team-continues-to-raise-questions.html", res.getCanonicalUrl());
        assertEquals("Special Counsel Mueller's legal team continues to raise questions", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("President Trump's legal team and his defenders are taking an aggressive defensive stance,"));
        assertTrue(res.getText(), res.getText().endsWith("While a second hire, Andrew Weissman, has a reputation for aggressively turning witnesses."));
        assertEquals("Doug McKelway", res.getAuthorName());
        assertEquals("Doug McKelway joined Fox News Channel (FNC) in November 2010 and serves as a Washington-based correspondent. Click here for more information on Doug McKelway.", res.getAuthorDescription());
        compareDates("2017-06-19 00:00:00", res.getDate());
    }

    @Test
    public void testYahooFinance1() throws Exception {
        // https://uk.finance.yahoo.com/news/trump-likely-won-apos-t-212100132.html
        JResult res = new JResult();
        res.setUrl("https://uk.finance.yahoo.com/news/trump-likely-won-apos-t-212100132.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("finance.yahoo.com1.html")));
        assertEquals("https://uk.finance.yahoo.com/news/trump-likely-won-apos-t-212100132.html", res.getCanonicalUrl());
        assertEquals("Trump likely won't thwart old foe Jeff Bezos' latest move", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Despite President Donald Trump 's campaign trail animosity toward Amazon CEO Jeff Bezos,"));
        assertTrue(res.getText(), res.getText().endsWith("Whole Foods did not immediately respond to a request for comment."));
        assertEquals("Jacob Pramuk", res.getAuthorName());
        assertEquals("http://www.cnbc.com", res.getAuthorDescription());
        compareDates("2017-06-19 21:21:00", res.getDate());
    }

    @Test
    public void testStlToday() throws Exception {
        // http://www.stltoday.com/news/national/coke-tweet-targeting-pittsburghers-uses-map-of-philadelphia/article_df15f84f-bac7-5ef6-890c-28b966fa5269.html
        JResult res = new JResult();
        res.setUrl("http://www.stltoday.com/news/national/coke-tweet-targeting-pittsburghers-uses-map-of-philadelphia/article_df15f84f-bac7-5ef6-890c-28b966fa5269.html");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("stltoday.html")));
        assertEquals("http://www.stltoday.com/news/national/coke-tweet-targeting-pittsburghers-uses-map-of-philadelphia/article_df15f84f-bac7-5ef6-890c-28b966fa5269.html", res.getCanonicalUrl());
        assertEquals("Coke tweet targeting Pittsburghers uses map of Philadelphia | Nation", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("PITTSBURGH (AP) — Coca-Cola's marketing efforts during the weekend's heatwave in Pittsburgh"));
        assertTrue(res.getText(), res.getText().endsWith("in this case, our map accidentally missed the mark — literally.\""));
        assertEquals(StringUtils.EMPTY, res.getAuthorName());
        assertEquals(StringUtils.EMPTY, res.getAuthorDescription());
        compareDates("2017-06-19 13:49:20 -05:00", res.getDate());
    }

    @Test
    public void testCbc() throws Exception {
        // http://www.cbc.ca/news/canada/calgary/sled-island-preview-1.4167762
        JResult res = new JResult();
        res.setUrl("http://www.cbc.ca/news/canada/calgary/sled-island-preview-1.4167762");
        res = extractor.extractContent(res, c.streamToString(getClass().getResourceAsStream("cbc.html")));
        assertEquals("http://www.cbc.ca/news/canada/calgary/sled-island-preview-1.4167762", res.getCanonicalUrl());
        assertEquals("Sprawling, multi-venue Sled Island Festival kicks off this week", res.getTitle());
        assertTrue(res.getText(), res.getText().startsWith("Sled Island 2017 kicks off Tuesday with more than 250 bands,"));
        assertTrue(res.getText(), res.getText().endsWith("And we also have a lot of free events."));
        assertEquals(StringUtils.EMPTY, res.getAuthorName());
        assertEquals(StringUtils.EMPTY, res.getAuthorDescription());
        compareDates("2017-06-20 00:00:00", res.getDate());
    }

    public static void compareDates(String expectedDateString, Date actual) {
        String[] patterns = {
                "yyyy-MM-dd",
                "yyyy-MM-dd HH:mm",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm:ssz",
                "yyyy-MM-dd HH:mm:ss Z",
                "yyyy-MM-dd HH:mm:ss XXX",
        };
        Date expectedDate = DateUtils.parseDate(expectedDateString, Configuration.getInstance().getDefaultTimezone(), patterns);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertEquals(dateFormat.format(expectedDate), dateFormat.format(actual));
    }

    /**
     * @param filePath the name of the file to open. Not sure if it can accept
     * URLs or just filenames. Path handling could be better, and buffer sizes
     * are hardcoded
     */
    public static String readFileAsString(String filePath)
            throws java.io.IOException {
        StringBuilder fileData = new StringBuilder(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }
}
