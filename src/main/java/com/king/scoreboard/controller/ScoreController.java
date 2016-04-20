package com.king.scoreboard.controller;

import com.king.scoreboard.service.ServicesEnum;
import com.king.scoreboard.session.SessionManager;
import com.king.scoreboard.session.UserSession;
import com.king.scoreboard.userscore.UserScore;
import com.king.scoreboard.userscore.UserScoreManager;
import com.king.scoreboard.util.HttpStatus;
import com.king.scoreboard.util.InvalidParamException;
import com.king.scoreboard.util.Validator;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Handles the user-score level requests.
 * Returns Http Code 400 if the score is a not supported value
 */
public class ScoreController implements HttpController {

    private static final Logger LOGGER = Logger.getLogger("confLogger");

    private String levelId;
    private String sessionKey;
    private String score;

    public ScoreController(String levelId, String sessionKey, String score) {
        this.levelId = levelId;
        this.sessionKey = sessionKey;
        this.score = score;
    }

    @Override
    public void handleRequest(HttpExchange httpExchange) throws IOException {
        UserSession session = SessionManager.getInstance().getSession(sessionKey);
        if (session == null || session.hasExpired()) {
            throw new InvalidParamException("");
        }
        UserScoreManager.getInstance().postScore(Integer.parseInt(levelId), new UserScore(session.getUserId(), Integer.parseInt(score)));
        httpExchange.sendResponseHeaders(HttpStatus.OK.getCode(), 0);
        LOGGER.info("USER_SCORE_LEVEL SERVICE CALL (levelId=" + levelId + ", sessionKey=" + sessionKey + ", score=" + score + ")");
    }

    @Override
    public void validate() throws InvalidParamException {
        if (!Validator.isUnsignedInteger31Bit(levelId)) {
            throw new InvalidParamException("");
        }
        if (!Validator.isUnsignedInteger31Bit(score)) {
            throw new InvalidParamException("");
        }
    }

    @Override
    public ServicesEnum getService() {
        return ServicesEnum.SCORE;
    }

    @Override
    public String toString() {
        return "UserScoreLevelBaseHandler{" +
                "levelId=" + levelId +
                ", sessionKey='" + sessionKey + '\'' +
                ", score=" + score +
                '}';
    }
}
