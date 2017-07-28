package de.jetwick.snacktory.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Class enables to load the snacktory configuration from external resources
 *
 * @author Abhishek Mulay
 */
final public class Configuration {

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    private static Configuration configuration;

    static {
        try {
            logger.info("Loading snacktory config.yml");
            InputStream resourceStream = Configuration.class.getClassLoader().getResourceAsStream("config.yml");
            configuration = new Yaml().loadAs(resourceStream, Configuration.class);
        } catch (Throwable t) {
            logger.error("Unable to load snacktory config : ", t);
            configuration = new Configuration();
        }
    }

    private String defaultTimezone;
    private String extractorBaseUrl;
    private String extractorEntityPath;
    private String extractorUserName;
    private String extractorPassword;

    private boolean useNamedEntityForAuthorExtraction;

    // Entity Name --> Entity Type
    private Map<String, String> nerExclusion;

    private Map<String, String> bestElementForAuthor;

    private Configuration() {
    }

    public static Configuration getInstance() {
        return configuration;
    }

    public String getDefaultTimezone() {
        return defaultTimezone;
    }

    public void setDefaultTimezone(String defaultTimezone) {
        this.defaultTimezone = defaultTimezone;
    }

    public String getExtractorBaseUrl() {
        return extractorBaseUrl;
    }

    public void setExtractorBaseUrl(String extractorBaseUrl) {
        this.extractorBaseUrl = extractorBaseUrl;
    }

    public String getExtractorEntityPath() {
        return extractorEntityPath;
    }

    public void setExtractorEntityPath(String extractorEntityPath) {
        this.extractorEntityPath = extractorEntityPath;
    }

    public String getExtractorUserName() {
        return extractorUserName;
    }

    public void setExtractorUserName(String extractorUserName) {
        this.extractorUserName = extractorUserName;
    }

    public String getExtractorPassword() {
        return extractorPassword;
    }

    public void setExtractorPassword(String extractorPassword) {
        this.extractorPassword = extractorPassword;
    }

    public boolean isUseNamedEntityForAuthorExtraction() {
        return useNamedEntityForAuthorExtraction;
    }

    public void setUseNamedEntityForAuthorExtraction(boolean useNamedEntityForAuthorExtraction) {
        this.useNamedEntityForAuthorExtraction = useNamedEntityForAuthorExtraction;
    }

    public Map<String, String> getNerExclusion() {
        return nerExclusion;
    }

    public void setNerExclusion(Map<String, String> nerExclusion) {
        this.nerExclusion = nerExclusion;
    }

    public Map<String, String> getBestElementForAuthor() {
        return bestElementForAuthor;
    }

    public void setBestElementForAuthor(Map<String, String> bestElementForAuthor) {
        this.bestElementForAuthor = bestElementForAuthor;
    }
}
