/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.controller;

import me.calvinliu.scoreboard.manager.ScoreManager;
import me.calvinliu.scoreboard.manager.SessionManager;
import me.calvinliu.scoreboard.util.InvalidParamException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScoreControllerTest {

    private static final String SESSION_KEY = "sessionkey";
    private static final String KEY = "UICSNDK";
    private static final int LEVEL_ID = 2121;
    private static final int USER_ID = 222;
    private static final int SCORE = 32;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private ScoreManager scoreManager;
    private SessionManager sessionManager;
    private HttpController controller = new ScoreController();
    // input parameters
    private int integerFromUrl;
    private Map<String, String> urlParameters;
    private Integer postBody;

    // output parameters
    private String response;

    @Before
    public void setUp() {
        scoreManager = ScoreManager.getInstance();
        sessionManager = SessionManager.getInstance();
    }

    private void setupLevel(int level) {
        integerFromUrl = level;
    }

    private void setupNullUrlParams() {
        urlParameters = null;
    }

    private void setupScore(Integer aScore) {
        postBody = aScore;
    }

    private void processRequest() {
        response = controller.processRequest(urlParameters, postBody, integerFromUrl);
    }

    private void setupUrlParameters(String[] paramNames, String[] paramValues) {
        urlParameters = new HashMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            urlParameters.put(paramNames[i], paramValues[i]);
        }
    }

    @Test
    public void processRequest_NullUrlParameters_ThrowsEx() {
        expectedException.expect(InvalidParamException.class);
        setupLevel(LEVEL_ID);
        setupNullUrlParams();
        setupScore(SCORE);
        processRequest();
    }

    @Test
    public void processRequest_EmptyUrlParameters_ThrowsEx() {
        expectedException.expect(InvalidParamException.class);
        setupLevel(LEVEL_ID);
        setupUrlParameters(new String[0], new String[0]);
        setupScore(SCORE);
        processRequest();
    }

    @Test
    public void processRequest_NoSessionKeyParameter_ThrowsEx() {
        expectedException.expect(InvalidParamException.class);
        setupLevel(LEVEL_ID);
        String[] paramNames = {"param1", "param2"};
        String[] paramValues = {"1", "2"};
        setupUrlParameters(paramNames, paramValues);
        setupScore(SCORE);
        processRequest();
    }

    @Test
    public void processRequest_NoScoreInBody_ThrowsEx() {
        expectedException.expect(InvalidParamException.class);
        setupLevel(LEVEL_ID);
        String[] paramNames = {SESSION_KEY};
        String[] paramValues = {KEY};
        setupUrlParameters(paramNames, paramValues);
        setupScore(null);
        processRequest();
    }

    @Test
    public void processRequest_AddScore() {
        setupLevel(LEVEL_ID);
        String[] paramNames = {SESSION_KEY};
        String key = sessionManager.createSession(USER_ID).getSessionKey();
        String[] paramValues = {key};
        setupUrlParameters(paramNames, paramValues);
        setupScore(SCORE);
        processRequest();
        Assert.assertEquals("", response);

        scoreManager.getUserScores().clear();
    }

    @Test
    public void processRequest_ResponseShouldBeEmpty() {
        expectedException.expect(InvalidParamException.class);
        setupLevel(LEVEL_ID);
        String[] paramNames = {SESSION_KEY};
        String[] paramValues = {KEY};
        setupUrlParameters(paramNames, paramValues);
        setupScore(SCORE);
        processRequest();
    }

    @Test
    public void testValidUrls() {
        assertTrue("/0/score  ".matches(controller.getUrlRegexPattern()));
        assertTrue("/0/score".matches(controller.getUrlRegexPattern()));
        assertTrue("/1/score?".matches(controller.getUrlRegexPattern()));
        assertTrue("/12345/score?sessionkey=ADBBDCDD".matches(controller.getUrlRegexPattern()));
        assertTrue("/12345/score?sessionkey=ADBBDCDD    ".matches(controller.getUrlRegexPattern()));
    }

    @Test
    public void testInvalidsUrls() {
        assertFalse("/score".matches(controller.getUrlRegexPattern()));
        assertFalse("//score".matches(controller.getUrlRegexPattern()));
        assertFalse("/-1/score".matches(controller.getUrlRegexPattern()));
        assertFalse("/-12345/score".matches(controller.getUrlRegexPattern()));
        assertFalse("/a/score".matches(controller.getUrlRegexPattern()));
        assertFalse("/abcde/score".matches(controller.getUrlRegexPattern()));
    }

    @Test
    public void testRequestMethod() {
        Assert.assertEquals(HttpController.POST, controller.getRequestMethod());
    }
}
