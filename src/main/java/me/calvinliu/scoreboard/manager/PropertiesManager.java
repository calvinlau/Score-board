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
    private static final String LOGOUT_TIMEOUT = "LOGOUT_TIMEOUT";
    private static final int LOGOUT_TIMEOUT_DEFAULT = 10 * 60 * 1000;
    private static final String LOGOUT_TIMEOUT_PERIOD_CHECK = "LOGOUT_TIMEOUT_PERIOD_CHECK";
    private static final int LOGOUT_TIMEOUT_PERIOD_CHECK_DEFAULT = 1 * 60 * 1000;
    private static final String LOGOUT_TIMEOUT_PERIOD_DELAY = "LOGOUT_TIMEOUT_PERIOD_DELAY";
    private static final int LOGOUT_TIMEOUT_PERIOD_DELAY_DEFAULT = 0;
    private static final String MAX_HIGH_SCORES_RETURNED = "MAX_HIGH_SCORES_RETURNED";
    private static final int MAX_HIGH_SCORES_RETURNED_DEFAULT = 15;

    private static volatile PropertiesManager instance = null;

    private final Properties properties;

    private PropertiesManager() {
        properties = new Properties();
        load();
    }

    /**
     * Creates or reuses the manager's instance.
     * Ensures that only a Singleton instance is used.
     *
     * @return the manager's instance.
     */
    public static PropertiesManager getInstance() {
        if (instance == null) {
            synchronized (PropertiesManager.class) {
                if (instance == null)
                    instance = new PropertiesManager();
            }
        }
        return instance;
    }

    /**
     * Loads the configuration properties from a file
     */
    private void load() {
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

    public int getLogoutTimeout() {
        return getInt(LOGOUT_TIMEOUT, LOGOUT_TIMEOUT_DEFAULT);
    }

    public int getLogoutTimeoutCheck() {
        return getInt(LOGOUT_TIMEOUT_PERIOD_CHECK, LOGOUT_TIMEOUT_PERIOD_CHECK_DEFAULT);
    }

    public int getLogoutTimeoutDelay() {
        return getInt(LOGOUT_TIMEOUT_PERIOD_DELAY, LOGOUT_TIMEOUT_PERIOD_DELAY_DEFAULT);
    }

    public int getMaxHighScoresReturnedDefault() {
        return getInt(MAX_HIGH_SCORES_RETURNED, MAX_HIGH_SCORES_RETURNED_DEFAULT);
    }
}
