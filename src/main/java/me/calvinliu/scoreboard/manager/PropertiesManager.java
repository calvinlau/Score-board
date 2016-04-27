/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.manager;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuration properties
 */
public class PropertiesManager {
    private static final Logger LOGGER = Logger.getLogger("confLogger");

    private static final String CONFIGURATION_PROPERTIES = "configuration.properties";

    private static final String BASE_URI = "BASE_URI";
    private static final String BASE_URI_DEFAULT = "localhost";
    private static final String SERVER_PORT = "SERVER_PORT";
    private static final int SERVER_PORT_DEFAULT = 8081;
    private static final String EXPIRATION_TIME = "EXPIRATION_TIME";
    private static final int EXPIRATION_TIME_DEFAULT = 10 * 60 * 1000;
    private static final String EXPIRATION_TIME_PERIOD_CHECK = "EXPIRATION_TIME_PERIOD_CHECK";
    private static final int EXPIRATION_TIME_PERIOD_CHECK_DEFAULT = 1 * 60 * 1000;
    private static final String EXPIRATION_TIME_PERIOD_DELAY = "EXPIRATION_TIME_PERIOD_DELAY";
    private static final int EXPIRATION_TIME_PERIOD_DELAY_DEFAULT = 0;
    private static final String HIGH_SCORES_LIMITATION = "HIGH_SCORES_LIMITATION";
    private static final int HIGH_SCORES_LIMITATION_DEFAULT = 15;
    private static final String HIGH_SCORES_THRESHOLD_LIMITATION = "HIGH_SCORES_THRESHOLD_IMITATION";
    private static final int HIGH_SCORES_THRESHOLD_IMITATION_DEFAULT = 1000;

    private static volatile PropertiesManager instance = null;

    private final Properties properties;

    /**
     * Private constructor for singleton and init
     */
    private PropertiesManager() {
        properties = new Properties();
        loadFile();
    }

    /**
     * Creates or reuses the manager's instance.
     * Ensures that only a Singleton instance is used.
     *
     * @return the manager's instance.
     */
    public static PropertiesManager getInstance() {
        // The effect of this result is that in cases where instance is already initialized
        // the volatile field is only accessed once which can improve overall performance
        PropertiesManager result = instance;
        if (result == null) {
            synchronized (PropertiesManager.class) {
                result = instance;
                if (result == null)
                    instance = result = new PropertiesManager();
            }
        }
        return result;
    }

    /**
     * Load the configuration properties from file
     */
    private void loadFile() {
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream(CONFIGURATION_PROPERTIES));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Couldn't load configuration properties...", e);
        }
    }

    private String getProperty(String key, String def) {
        String val = properties.getProperty(key);
        if (val == null) {
            return def;
        } else {
            return val;
        }
    }

    private int getInt(String key, int def) {
        String val = properties.getProperty(key);
        if (val == null) {
            return def;
        } else {
            try {
                return Integer.parseInt(val);
            } catch (NumberFormatException e) {
                return def;
            }
        }
    }

    public String getHost() {
        return getProperty(BASE_URI, BASE_URI_DEFAULT);
    }

    public int getPort() {
        return getInt(SERVER_PORT, SERVER_PORT_DEFAULT);
    }

    public int getExpirationTime() {
        return getInt(EXPIRATION_TIME, EXPIRATION_TIME_DEFAULT);
    }

    public int getExpirationTimeCheck() {
        return getInt(EXPIRATION_TIME_PERIOD_CHECK, EXPIRATION_TIME_PERIOD_CHECK_DEFAULT);
    }

    public int getExpirationTimeDelay() {
        return getInt(EXPIRATION_TIME_PERIOD_DELAY, EXPIRATION_TIME_PERIOD_DELAY_DEFAULT);
    }

    public int getHighScoresLimit() {
        return getInt(HIGH_SCORES_LIMITATION, HIGH_SCORES_LIMITATION_DEFAULT);
    }

    public int getHighScoresThresholdLimit() {
        return getInt(HIGH_SCORES_THRESHOLD_LIMITATION, HIGH_SCORES_THRESHOLD_IMITATION_DEFAULT);
    }
}
