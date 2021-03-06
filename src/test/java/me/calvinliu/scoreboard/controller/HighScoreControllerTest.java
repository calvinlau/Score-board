/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.controller;

import me.calvinliu.scoreboard.manager.ScoreManager;
import me.calvinliu.scoreboard.manager.SessionManager;
import me.calvinliu.scoreboard.model.UserScore;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class HighScoreControllerTest {

    private static final int LEVEL_ID = 1234;
    private static final int USER_ID_1 = 2;
    private static final int SCORE_1 = 1000;
    private static final int USER_ID_2 = 3;
    private static final int SCORE_2 = 1500;
    private static final String HIGH_SCORE = "3=1500,2=1000";
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private SessionManager sessionManager = SessionManager.getInstance();
    private ScoreManager scoreManager = ScoreManager.getInstance();
    private HttpController controller = new HighScoreListController();

    @Test
    public void testProcessRequest() {
        sessionManager.createSession(USER_ID_1).getSessionKey();
        scoreManager.postScore(LEVEL_ID, new UserScore(USER_ID_1, new AtomicInteger(SCORE_1)));
        sessionManager.createSession(USER_ID_2).getSessionKey();
        scoreManager.postScore(LEVEL_ID, new UserScore(USER_ID_2, new AtomicInteger(SCORE_2)));
        String response = controller.processRequest(null, null, LEVEL_ID);
        assertEquals(HIGH_SCORE, response);

        scoreManager.getUserScores().clear();
    }

    @Test
    public void testValidUrls() {
        assertTrue("/0/highscorelist".matches(controller.getUrlRegexPattern()));
        assertTrue("/1/highscorelist".matches(controller.getUrlRegexPattern()));
        assertTrue("/12345/highscorelist".matches(controller.getUrlRegexPattern()));
    }

    @Test
    public void testInvalidsUrls() {
        assertFalse("/highscorelist".matches(controller.getUrlRegexPattern()));
        assertFalse("//highscorelist".matches(controller.getUrlRegexPattern()));
        assertFalse("/-1/highscorelist".matches(controller.getUrlRegexPattern()));
        assertFalse("/-12345/highscorelist".matches(controller.getUrlRegexPattern()));
        assertFalse("/-12345678901234/highscorelist".matches(controller.getUrlRegexPattern()));
        assertFalse("/12345/highscorelist      ".matches(controller.getUrlRegexPattern()));
        assertFalse("/-1.1/highscorelist".matches(controller.getUrlRegexPattern()));
        assertFalse("/a/highscorelist".matches(controller.getUrlRegexPattern()));
        assertFalse("/abcde/highscorelist".matches(controller.getUrlRegexPattern()));
    }

    @Test
    public void testRequestMethod() {
        Assert.assertEquals(HttpController.GET, controller.getRequestMethod());
    }
}
