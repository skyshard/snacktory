package de.jetwick.snacktory.utils;


import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Abhishek Mulay
 */
public class AuthorUtilsTest {
    @Test
    public void cleanupFacebookProfileUrl() throws Exception {

        // ============= Prepare =============
        final String EXPECTED_AUTHOR_NAME = "Bbcnews";

        HashMap<String, String> fixtures = new LinkedHashMap() {{
            put("https://www.facebook.com/bbcnews", EXPECTED_AUTHOR_NAME);
            put("https://www.facebook.com/bbcnews", EXPECTED_AUTHOR_NAME);
            put("http://facebook.com/bbcnews", EXPECTED_AUTHOR_NAME);
            put("www.facebook.com/bbcnews", EXPECTED_AUTHOR_NAME);
            put("facebook.com/bbcnews", EXPECTED_AUTHOR_NAME);
        }};

        // ============= Execute and verify =============
        for (Map.Entry<String, String> entry : fixtures.entrySet()) {
            Assert.assertEquals(entry.getValue(), AuthorUtils.cleanup(entry.getKey()));
        }
    }
}