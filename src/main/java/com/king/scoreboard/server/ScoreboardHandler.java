package com.king.scoreboard.server;

import com.king.scoreboard.controller.HighScoreListController;
import com.king.scoreboard.controller.HttpController;
import com.king.scoreboard.controller.LoginController;
import com.king.scoreboard.controller.ScoreController;
import com.king.scoreboard.service.ServicesEnum;
import com.king.scoreboard.util.InvalidParamException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * The http server handler for handling requests.
 */
public class ScoreboardHandler implements HttpHandler {

    private static final Logger LOGGER = Logger.getLogger("confLogger");

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        try {
            HttpController handler;
            String requestedUri = httpExchange.getRequestURI().toString();
            String id = requestedUri.split("/")[1];
            if (ServicesEnum.isValidServiceUri(requestedUri, ServicesEnum.LOGIN)) {
                handler = new LoginController(id);
            } else if (ServicesEnum.isValidServiceUri(requestedUri, ServicesEnum.SCORE)) {
                String sessionKey = requestedUri.split("=")[1];
                handler = new ScoreController(id, sessionKey, httpExchange.getRequestBody().toString());
            } else if (ServicesEnum.isValidServiceUri(requestedUri, ServicesEnum.HIGH_SCORE_LIST)) {
                handler = new HighScoreListController(id);
            } else {
                return;
            }
            handler.validate();
            handler.handleRequest(httpExchange);
        } catch (NumberFormatException | InvalidParamException e) {
            LOGGER.warning("URL Handler error, " + e.getMessage());
        } finally {
            httpExchange.getResponseBody().close();
        }
    }
}
