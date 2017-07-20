package de.jetwick.snacktory.utils;

import com.google.common.base.CharMatcher;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import javax.swing.text.html.parser.Entity;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by admin- on 19/7/17.
 */
public class AirPRExtractorUtils {

    private static final Configuration config = Configuration.getInstance();

    private static String urlBuilder(String baseUrl, String path, String parameters) {

        StringBuffer url = new StringBuffer();

        url.append(StringUtils.stripEnd(baseUrl, "/"))
                .append("/")
                .append(path)
                .append("?")
                .append(parameters);

        System.out.println("Final URL: " + url);
        return url.toString();
    }

    public static List<String> extractEntities(String text) throws UnsupportedEncodingException {

//
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
                = new UsernamePasswordCredentials(config.getExtractorUserName(), config.getExtractorPassword());
        credentialsProvider.setCredentials(AuthScope.ANY, credentials);


        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(HTTPUtils.getConnectionManager())
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();

        String url = urlBuilder(config.getExtractorBaseUrl(), config.getExtractorEntityPath(), "text=" + URLEncoder.encode(text, "UTF-8"));
        HttpPost httpPost = new HttpPost(url);
        try {

            HttpResponse response = client.execute(httpPost);
            System.out.println("Status Code: " + response.getStatusLine());
            String responseText = EntityUtils.toString(response.getEntity());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            final Pattern NAME_PATTERN = Pattern.compile("[^\\w\\.\\-\\' ]+", Pattern.UNICODE_CHARACTER_CLASS);

            TreeMap<Integer, String> matchedEntities = new TreeMap<>();

            Entities entities = objectMapper.readValue(responseText, Entities.class);
            for (NamedEntity entity : entities.getEntities()) {
                if (entity.getType().equals("Person")) {
                    String name = entity.getRepresentative();
                    if (text.contains(name)) {
                        name = NAME_PATTERN.matcher(name).replaceAll("").trim();
                        name = CharMatcher.JAVA_UPPER_CASE.matchesAllOf(name) ?
                                WordUtils.capitalizeFully(name, new char[]{' ', '-'}) :
                                name;
                        matchedEntities.put(text.indexOf(name), name);
                    }
                }
            }

            if (!matchedEntities.isEmpty()) {
                List<String> result = new LinkedList<>();
                result.addAll(matchedEntities.values());
                return  result;
            }
                List<String> extractedEntities = entities.getEntities().stream()
                        .filter(entity -> entity.getType().equals("Organization"))
                        .filter(entity -> text.contains(entity.getRepresentative()))
                        .map(entity -> entity.getRepresentative())
                        .collect(Collectors.toList());

            return extractedEntities;

            //System.out.println("Response Text: " + entities);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println( extractEntities("Abhishek Mulay"));
    }
}
