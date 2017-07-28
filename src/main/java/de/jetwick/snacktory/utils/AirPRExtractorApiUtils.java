package de.jetwick.snacktory.utils;

import de.jetwick.snacktory.models.Configuration;
import de.jetwick.snacktory.models.EntitiesResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Class enables to call AirPR Extractor APIs
 *
 * @author Abhishek Mulay
 */
final public class AirPRExtractorApiUtils {

    private static final Configuration config = Configuration.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(AirPRExtractorApiUtils.class);

    private AirPRExtractorApiUtils() {
    }

    /**
     * Builds a final url from the base url, path and query parameters
     *
     * @param baseUrl    {@link String}: e.g. http://www.extractor.airpr.com/api
     * @param path       {@link String}: e.g. /entities
     * @param parameters {@link String}: Url encoded string e.g. text=by+%7C+Chana+Joffe-Walt
     * @return {@link String}
     */
    private static String urlBuilder(String baseUrl, String path, String parameters) {
        StringBuffer url = new StringBuffer()
                .append(StringUtils.stripEnd(baseUrl, "/"))
                .append("/")
                .append(StringUtils.stripStart(path, "/"))
                .append("?")
                .append(parameters);
        logger.info("Final API URL: " + url);
        return url.toString();
    }


    /**
     * Extract named entities from the given text
     *
     * @param text {@link String}: Text to fetch named entities from
     * @return {@link EntitiesResponse} Null if failed to get named entities
     * @throws {@link IOException}
     */
    public static EntitiesResponse getEntities(String text) {
        EntitiesResponse entitiesResponse = null;
        try {
            // Generate final API Url
            String url = urlBuilder(config.getExtractorBaseUrl(),
                    config.getExtractorEntityPath(),
                    "text=" + URLEncoder.encode(text, "UTF-8"));

            // Do API Call
            HttpResponse response = HTTPUtils
                    .getHttpClient(config.getExtractorUserName(), config.getExtractorPassword())
                    .execute(new HttpPost(url));
            entitiesResponse = deserializeIntoEntitiesResponse(response);
        } catch (UnsupportedEncodingException e) {
            logger.error("Unable to build the API Url", e);
        } catch (IOException e) {
            logger.error("Unable to fetch named entities", e);
        }
        return entitiesResponse;
    }

    /**
     * Convert the API Response into {@link EntitiesResponse}
     *
     * @param response {@link HttpResponse}
     * @return {@link EntitiesResponse}
     */
    public static EntitiesResponse deserializeIntoEntitiesResponse(HttpResponse response) {
        EntitiesResponse entitiesResponse = null;
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            logger.info("API call completed successfully.");
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                entitiesResponse = mapper.readValue(EntityUtils.toString(response.getEntity()), EntitiesResponse.class);
                logger.debug("Received named entities " + entitiesResponse);
            } catch (IOException e) {
                logger.error("Unable to extract named entities", e);
            }
        } else {
            logger.info("Failed to execute API call. Response: " + response.getStatusLine());
        }
        return entitiesResponse;
    }
}
