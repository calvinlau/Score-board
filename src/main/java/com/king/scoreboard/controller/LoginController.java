package com.king.scoreboard.controller;

import com.king.scoreboard.service.ServicesEnum;
import com.king.scoreboard.session.SessionManager;
import com.king.scoreboard.util.HttpStatus;
import com.king.scoreboard.util.InvalidParamException;
import com.king.scoreboard.util.Validator;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Handles the login requests.
 */
public class LoginController implements HttpController {

    private static final Logger LOGGER = Logger.getLogger("confLogger");

    private String userId;

    public LoginController(String userId) {
        this.userId = userId;
    }

    @Override
    public void handleRequest(HttpExchange httpExchange) throws IOException {
        String sessionKey = SessionManager.getInstance().createSession(Integer.valueOf(userId)).getSessionKey();
        httpExchange.sendResponseHeaders(HttpStatus.OK.getCode(), sessionKey.length());
        httpExchange.getResponseBody().write(sessionKey.getBytes());
        LOGGER.info("LOGIN SERVICE CALL (userId=" + userId + ") RETURNS: sessionKey=" + sessionKey);
    }

    @Override
    public void validate() {
        if (!Validator.isUnsignedInteger31Bit(userId)) {
            throw new InvalidParamException("");
        }
    }

    @Override
    public ServicesEnum getService() {
        return ServicesEnum.LOGIN;
    }
}
