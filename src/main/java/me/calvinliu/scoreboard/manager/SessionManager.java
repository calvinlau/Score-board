/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.manager;

import me.calvinliu.scoreboard.model.UserSession;
import me.calvinliu.scoreboard.util.LogoutTimerTask;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Stores and manages the user sessions.
 */
public class SessionManager {

    private static final Logger LOGGER = Logger.getLogger("confLogger");

    private static volatile SessionManager instance = null;

    private Map<String, UserSession> userSessions;
    private Timer timer;

    /**
     * Private constructor for singleton and init
     */
    private SessionManager() {
        userSessions = new ConcurrentHashMap<>();
        // Schedules and starts the logout daemon task
        LogoutTimerTask task = new LogoutTimerTask(PropertiesManager.getInstance().getExpirationTime());
        this.timer = new Timer(true);
        timer.scheduleAtFixedRate(task, PropertiesManager.getInstance().getExpirationTimeDelay(), PropertiesManager.getInstance().getExpirationTimeCheck());
    }

    /**
     * Creates or reuses the manager's instance.
     * Ensures that only a Singleton instance is used.
     *
     * @return the manager's instance.
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null)
                    instance = new SessionManager();
            }
        }
        return instance;
    }

    public Timer getTimer() {
        return timer;
    }

    /**
     * This works by UUID.
     * Package score for unit test.
     *
     * @return a new unique session key
     */
    String generateKey() {
        final String uuid = UUID.randomUUID().toString();
        return new BigInteger(uuid.replaceAll("-", ""), 16).toString(32);
    }

    /**
     * Returns a new user session from the given userId.
     *
     * @param userId user Id
     * @return a new user session from the given userId
     */
    public UserSession createSession(int userId) {
        String key = generateKey();
        UserSession userSession = new UserSession(userId, key);
        userSessions.put(key, userSession);
        return userSession;
    }

    /**
     * Returns a user session from the given sessionKey.
     * Returns null, if the user is not logged in.
     *
     * @param sessionKey to retrieve a user session
     * @return a user session from the given sessionKey
     */
    public UserSession getSession(String sessionKey) {
        return userSessions.get(sessionKey);
    }

    /**
     * Removes the sessions that are expired based on the given timeout
     *
     * @param timeout is the criterion for removing a user session
     */
    public void removeUserSessions(long timeout) {
        final Date now = new Date();
        for (UserSession userSession : userSessions.values()) {
            if (now.getTime() - userSession.getCreatedDate().getTime() > timeout) {
                userSession = userSessions.remove(userSession.getSessionKey());
                LOGGER.info("[REMOVING SESSION] " + userSession);
            }
        }
    }
}
