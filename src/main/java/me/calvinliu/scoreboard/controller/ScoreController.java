/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.controller;

import me.calvinliu.scoreboard.manager.ScoreManager;
import me.calvinliu.scoreboard.manager.SessionManager;
import me.calvinliu.scoreboard.model.UserScore;
import me.calvinliu.scoreboard.model.UserSession;
import me.calvinliu.scoreboard.util.InvalidParamException;
import me.calvinliu.scoreboard.util.ResponseCode;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Handles the user-score level requests.
 */
public class ScoreController implements HttpController {

    private static final Logger LOGGER = Logger.getLogger("confLogger");
    private static final String SESSION_KEY_PARAMETER = "sessionkey";

    /**
     * Default scope constructor for factory creating and unit testing
     */
    ScoreController() {
    }

    @Override
    public String getRequestMethod() {
        return POST;
    }

    @Override
    public String getUrlRegexPattern() {
        return "/\\d+/score.*";
    }

    @Override
    public String processRequest(Map<String, String> urlParameters, Integer score, int levelId) {
        if (urlParameters == null || !urlParameters.containsKey(SESSION_KEY_PARAMETER)) {
            throw new InvalidParamException(ResponseCode.ERR_INVALID_SESSION);
        }
        UserSession session = SessionManager.getInstance().getSession(urlParameters.get(SESSION_KEY_PARAMETER));
        if (session == null || session.hasExpired()) {
            throw new InvalidParamException(ResponseCode.ERR_INVALID_SESSION);
        }
        ScoreManager.getInstance().postScore(levelId, new UserScore(session.getUserId(), score));
        LOGGER.info("[SCORE](levelId=" + levelId + ", sessionKey=" + session.getSessionKey() + ", score=" + score + ")");
        return EMPTY;
    }
}
