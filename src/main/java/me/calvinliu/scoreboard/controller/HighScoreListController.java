/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.controller;

import me.calvinliu.scoreboard.manager.PropertiesManager;
import me.calvinliu.scoreboard.manager.ScoreManager;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Handles the get-high-score-list requests.
 */
public class HighScoreListController implements HttpController {

    private static final Logger LOGGER = Logger.getLogger("confLogger");

    HighScoreListController() {
    }

    @Override
    public String getRequestMethod() {
        return GET;
    }

    @Override
    public String getUrlRegexPattern() {
        return "/\\d+/highscorelist";
    }

    @Override
    public String processRequest(Map<String, String> urlParameters, Integer postBody, int levelId) {
        String response = ScoreManager.getInstance().getHighScoreList(levelId, PropertiesManager.getInstance().getMaxHighScoresReturnedDefault());
        LOGGER.info("GET-HIGHSCORE-LIST SERVICE CALL (levelId=" + levelId + ") RETURNS: response=" + response);
        return response;
    }
}
