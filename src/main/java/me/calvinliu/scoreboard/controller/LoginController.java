package me.calvinliu.scoreboard.controller;

import me.calvinliu.scoreboard.session.SessionManager;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Handles the login requests.
 */
public class LoginController implements HttpController {

    private static final Logger LOGGER = Logger.getLogger("confLogger");

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
        LOGGER.info("LOGIN SERVICE CALL (userId=" + userId + ") RETURNS: sessionKey=" + sessionKey);
        return sessionKey;
    }
}
