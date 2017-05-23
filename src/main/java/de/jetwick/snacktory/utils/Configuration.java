package de.jetwick.snacktory.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

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
}
