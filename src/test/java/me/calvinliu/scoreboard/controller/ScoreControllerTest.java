/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.controller;

import me.calvinliu.scoreboard.manager.SessionManager;
import me.calvinliu.scoreboard.util.InvalidParamException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScoreControllerTest {

    private static final String SESSION_KEY = "sessionkey";
    private static final String KEY = "ASDFABCDF";
    private static final int LEVEL_ID = 1234;
    private static final int USER_ID = 2;
    private static final int SCORE = 30;

    private SessionManager sessionManager = SessionManager.getInstance();

    @InjectMocks
    private HttpController controller = new ScoreController();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    // input parameters
    private int integerFromUrl;
    private Map<String, String> urlParameters;
    private Integer postBody;

    // output parameters
    private String response;

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
    public void verifyValidUrls() {
        assertTrue("/0/score  ".matches(controller.getUrlRegexPattern()));
        assertTrue("/0/score".matches(controller.getUrlRegexPattern()));
        assertTrue("/1/score?".matches(controller.getUrlRegexPattern()));
        assertTrue("/12345/score?sessionkey=ADBBDCDD".matches(controller.getUrlRegexPattern()));
        assertTrue("/12345/score?sessionkey=ADBBDCDD    ".matches(controller.getUrlRegexPattern()));
    }

    @Test
    public void verifyInvalidsUrls() {
        assertFalse("/score".matches(controller.getUrlRegexPattern()));
        assertFalse("//score".matches(controller.getUrlRegexPattern()));
        assertFalse("/-1/score".matches(controller.getUrlRegexPattern()));
        assertFalse("/-12345/score".matches(controller.getUrlRegexPattern()));
        assertFalse("/a/score".matches(controller.getUrlRegexPattern()));
        assertFalse("/abcde/score".matches(controller.getUrlRegexPattern()));
    }

    @Test
    public void requestMethod_ShouldReturnPost() {
        Assert.assertEquals(HttpController.POST, controller.getRequestMethod());
    }
}
