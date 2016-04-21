/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.controller;

import me.calvinliu.scoreboard.manager.SessionManager;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Handles the login requests.
 */
public class LoginController implements HttpController {

    private static final Logger LOGGER = Logger.getLogger("confLogger");

    LoginController() {
    }

    @Override
    public String getRequestMethod() {
        return GET;
    }

    @Override
    public String getUrlRegexPattern() {
        return "/\\d+/login";
    }

    @Override
    public String processRequest(Map<String, String> urlParameters, Integer postBody, int userId) {
        String sessionKey = SessionManager.getInstance().createSession(userId).getSessionKey();
        LOGGER.info("[LOGIN](userId=" + userId + ") RETURNS: sessionKey=" + sessionKey);
        return sessionKey;
    }
}
