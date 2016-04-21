/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.controller;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LoginControllerTest {

    private static final int USER_ID = 1234;

    @InjectMocks
    private HttpController controller = new LoginController();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void processRequestTest() {
        String key1 = controller.processRequest(null, null, USER_ID);
        assertNotNull(key1);
        String key2 = controller.processRequest(null, null, USER_ID);
        assertNotEquals(key1, key2);
    }

    @Test
    public void verifyValidUrls() {
        assertTrue("/0/login".matches(controller.getUrlRegexPattern()));
        assertTrue("/1/login".matches(controller.getUrlRegexPattern()));
        assertTrue("/12345/login".matches(controller.getUrlRegexPattern()));
    }

    @Test
    public void verifyInvalidsUrls() {
        assertFalse("/login".matches(controller.getUrlRegexPattern()));
        assertFalse("//login".matches(controller.getUrlRegexPattern()));
        assertFalse("/1/logina".matches(controller.getUrlRegexPattern()));
        assertFalse("/-1/login".matches(controller.getUrlRegexPattern()));
        assertFalse("/-12345/login".matches(controller.getUrlRegexPattern()));
        assertFalse("/a/login".matches(controller.getUrlRegexPattern()));
        assertFalse("/abcde/login".matches(controller.getUrlRegexPattern()));
        assertFalse("/12345/login      ".matches(controller.getUrlRegexPattern()));
    }

    @Test
    public void requestMethodTest() {
        Assert.assertEquals(HttpController.GET, controller.getRequestMethod());
    }
}
