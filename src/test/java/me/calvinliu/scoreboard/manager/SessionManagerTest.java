/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.manager;

import me.calvinliu.scoreboard.model.UserSession;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;

import static org.junit.Assert.*;

public class SessionManagerTest {

    private static SecureRandom random;
    private SessionManager sessionManager;

    @Before
    public void setUp() {
        sessionManager = SessionManager.getInstance();
        random = new SecureRandom();
    }

    @Test
    public void testSingletonInstance() {
        assertSame(SessionManager.getInstance(), SessionManager.getInstance());
    }

    @Test
    public void testCreateUserSessionNotLoggedInUser() {
        int userId = getRandomUserId();
        UserSession session = sessionManager.createSession(userId);
        assertNotNull(session);
        assertNotNull(session.getSessionKey());
        assertNotNull(session.getCreatedDate());
        assertNotNull(session.getUserId());
    }

    @Test
    public void testCreateUserSessionLoggedInUser() {
        // Login a user
        int userId = getRandomUserId();
        UserSession session1 = sessionManager.createSession(userId);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UserSession session2 = sessionManager.createSession(userId);
        assertEquals(session1.getUserId(), session2.getUserId());
        assertNotEquals(session1.getCreatedDate(), session2.getCreatedDate());
    }

    @Test
    public void testGetUserSessionLoggedInUser() {
        // Login a user
        int userId = getRandomUserId();
        UserSession session = sessionManager.createSession(userId);
        assertSame(session, sessionManager.getSession(session.getSessionKey()));
    }

    @Test
    public void testGetUserSessionNotLoggedInUser() {
        String sessionKey = getRandomSessionKey();
        assertNull(sessionManager.getSession(sessionKey));
    }

    @Test
    public void testRemoveUserSessionsValidTimeout() {
        // Sets timeout to 0.1 s
        int timeout = 100;
        // Login a user
        int userId = getRandomUserId();
        UserSession session = sessionManager.createSession(userId);
        assertNotNull(sessionManager.getSession(session.getSessionKey()));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sessionManager.removeUserSessions(timeout);
        assertNull(sessionManager.getSession(session.getSessionKey()));
    }

    private int getRandomUserId() {
        return Math.abs(new BigInteger(130, random).intValue());
    }

    private String getRandomSessionKey() {
        return sessionManager.generateKey();
    }
}
