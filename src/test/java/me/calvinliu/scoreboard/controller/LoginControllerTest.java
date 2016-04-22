/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.controller;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class LoginControllerTest {

    private static final int USER_ID = 1234;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private HttpController controller = new LoginController();

    @Test
    public void testProcessRequest() {
        String key1 = controller.processRequest(null, null, USER_ID);
        assertNotNull(key1);
        String key2 = controller.processRequest(null, null, USER_ID);
        assertNotEquals(key1, key2);
    }

    @Test
    public void testValidUrls() {
        assertTrue("/0/login".matches(controller.getUrlRegexPattern()));
        assertTrue("/1/login".matches(controller.getUrlRegexPattern()));
        assertTrue("/12345/login".matches(controller.getUrlRegexPattern()));
    }

    @Test
    public void testInvalidsUrls() {
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
    public void testRequestMethod() {
        Assert.assertEquals(HttpController.GET, controller.getRequestMethod());
    }
}
