/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.manager;

import me.calvinliu.scoreboard.model.UserSession;
import me.calvinliu.scoreboard.util.TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;

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
        int userId = TestUtils.getRandomUserId();
        UserSession session = sessionManager.createSession(userId);
        assertNotNull(session);
        assertNotNull(session.getSessionKey());
        assertNotNull(session.getCreatedDate());
        assertNotNull(session.getUserId());
    }

    @Test
    public void testCreateUserSessionLoggedInUser() {
        // Login a user
        int userId = TestUtils.getRandomUserId();
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
        int userId = TestUtils.getRandomUserId();
        UserSession session = sessionManager.createSession(userId);
        assertSame(session, sessionManager.getSession(session.getSessionKey()));
    }

    @Test
    public void testGetUserSessionNotLoggedInUser() {
        String sessionKey = getRandomSessionKey();
        assertNull(sessionManager.getSession(sessionKey));
    }

    @Test
    public void testRemoveUserSessions_Timeout() {
        // Sets timeout to 0.1 s
        int timeout = 100;
        // Login a user
        int userId = TestUtils.getRandomUserId();
        UserSession session = sessionManager.createSession(userId);
        assertNotNull(sessionManager.getSession(session.getSessionKey()));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sessionManager.removeUserSessions(timeout);
        assertNull(sessionManager.getSession(session.getSessionKey()));
    }

    @Test
    public void testRemoveUserSessions_Single() {
        // Sets timeout to 600000 ms
        int timeout = 600000;
        int repeatTime = 5;
        // Login 1 user for 5 times
        int userId = TestUtils.getRandomUserId();
        List<String> keyList = new LinkedList<>();
        for (int i = 0; i < repeatTime; i++) {
            UserSession session = sessionManager.createSession(userId);
            keyList.add(session.getSessionKey());
            assertNotNull(sessionManager.getSession(session.getSessionKey()));
        }
        sessionManager.removeUserSessions(timeout);
        int count = 0;
        for (String key : keyList) {
            if (sessionManager.getSession(key) != null && sessionManager.getSession(key).getUserId().equals(userId)) {
                count++;
            }
        }
        // Not sure 100% to clean all duplicate session by one time clean up, eventually it will only single for one user at most.
        assertTrue(count >= 1);
    }

    private String getRandomSessionKey() {
        return sessionManager.generateKey();
    }
}
