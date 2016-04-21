/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.manager;

import me.calvinliu.scoreboard.model.UserScore;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Stores and manages the user scores for every level.
 */
public class ScoreManager {

    private static volatile ScoreManager instance = null;
    private ConcurrentHashMap<Integer, NavigableSet<UserScore>> levelScores;

    private ScoreManager() {
        levelScores = new ConcurrentHashMap<>();
    }

    /**
     * Creates or reuses the manager's instance.
     * Ensures that only a Singleton instance is used.
     *
     * @return the manager's instance.
     */
    public static ScoreManager getInstance() {
        if (instance == null) {
            synchronized (ScoreManager.class) {
                if (instance == null)
                    instance = new ScoreManager();
            }
        }
        return instance;
    }

    /**
     * Puts a user score into a specific level.
     * This method is synchronized to ensure thread-safety access to the user scores map which is the critical-resource.
     *
     * @param levelId   to assign the user score to
     * @param userScore to be put in the map
     */
    public void postScore(Integer levelId, UserScore userScore) {
        levelScores.computeIfAbsent(levelId, n -> new ConcurrentSkipListSet<>()).add(userScore);
    }

    /**
     * Returns the high score list for the given level.
     *
     * @param levelId to retrieve the high score list from
     * @param limit   is the maximum scores to be returned
     * @return the high score list for the given level
     */
    public String getHighScoreList(Integer levelId, int limit) {
        StringBuilder response = new StringBuilder();
        NavigableSet<UserScore> set = levelScores.get(levelId);
        if (set != null) {
            int i = 0;
            List<Integer> usedUserScoreIdList = new ArrayList<>();
            for (UserScore userScore : set.descendingSet()) {
                if (i >= limit) {
                    break;
                }
                if (!usedUserScoreIdList.contains(userScore.getUserId())) {
                    usedUserScoreIdList.add(userScore.getUserId());
                    response.append(userScore.getUserId());
                    response.append("=");
                    response.append(userScore.getScore());
                    response.append(",");
                    i++;
                }
            }
            if (response.length() > 0) {
                response.deleteCharAt(response.length() - 1);
            }
        }
        return response.toString();
    }

    ConcurrentHashMap<Integer, NavigableSet<UserScore>> getUserScores() {
        return levelScores;
    }
}
