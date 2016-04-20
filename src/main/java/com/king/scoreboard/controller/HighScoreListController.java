package com.king.scoreboard.controller;

import com.king.scoreboard.property.ConfigProperties;
import com.king.scoreboard.service.ServicesEnum;
import com.king.scoreboard.userscore.UserScoreManager;
import com.king.scoreboard.util.HttpStatus;
import com.king.scoreboard.util.InvalidParamException;
import com.king.scoreboard.util.Validator;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Handles the get-high-score-list requests.
 */
public class HighScoreListController implements HttpController {

    private static final Logger LOGGER = Logger.getLogger("confLogger");

    private String levelId;

    public HighScoreListController(String levelId) {
        this.levelId = levelId;
    }

    @Override
    public void handleRequest(HttpExchange httpExchange) throws IOException {
        String response = UserScoreManager.getInstance().getHighScoreList(Integer.parseInt(levelId), ConfigProperties.getInstance().getMaxHighScoresReturnedDefault());
        httpExchange.sendResponseHeaders(HttpStatus.OK.getCode(), response.length());
        httpExchange.getResponseBody().write(response.getBytes());
        LOGGER.info("GET-HIGHSCORE-LIST SERVICE CALL (levelId=" + levelId + ") RETURNS: response=" + response);
    }

    @Override
    public void validate() throws InvalidParamException {
        if (!Validator.isUnsignedInteger31Bit(levelId)) {
            throw new InvalidParamException("");
        }
    }

    @Override
    public ServicesEnum getService() {
        return ServicesEnum.HIGH_SCORE_LIST;
    }
}
