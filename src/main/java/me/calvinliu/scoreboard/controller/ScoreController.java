package me.calvinliu.scoreboard.controller;

import me.calvinliu.scoreboard.session.SessionManager;
import me.calvinliu.scoreboard.session.UserSession;
import me.calvinliu.scoreboard.userscore.UserScore;
import me.calvinliu.scoreboard.userscore.UserScoreManager;
import me.calvinliu.scoreboard.util.InvalidParamException;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Handles the user-score level requests.
 * Returns Http Code 400 if the score is a not supported value
 */
public class ScoreController implements HttpController {

    private static final Logger LOGGER = Logger.getLogger("confLogger");
    private static final String SESSIONKEY_PARAMETER = "sessionkey";

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
        UserSession session = SessionManager.getInstance().getSession(urlParameters.get(SESSIONKEY_PARAMETER));
        if (session == null || session.hasExpired()) {
            throw new InvalidParamException("");
        }
        UserScoreManager.getInstance().postScore(levelId, new UserScore(session.getUserId(), score));
        LOGGER.info("USER_SCORE_LEVEL SERVICE CALL (levelId=" + levelId + ", sessionKey=" + session.getSessionKey() + ", score=" + score + ")");
        return EMPTY;
    }
}
