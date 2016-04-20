package com.king.scoreboard.userscore;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by ioannis.metaxas on 2015-12-01.
 *
 * Stores and manages the user scores for every level.
 *
 */
public class UserScoreManager {

    private static volatile UserScoreManager instance = null;
    private ConcurrentHashMap<Integer, ConcurrentSkipListSet<UserScore>> userScores;

    private UserScoreManager() {
        userScores = new ConcurrentHashMap<>();
    }

    /**
     * Puts a user score into a specific level.
     * This method is synchronized to ensure thread-safety access to the user scores map which is the critical-resource.
     *
     * @param levelId to assign the user score to
     * @param userScore to be put in the map
     */
    public synchronized void postScore(Integer levelId, UserScore userScore) {
        ConcurrentSkipListSet<UserScore> userScoreLevelSkipListSet = userScores.get(levelId);
        if(userScoreLevelSkipListSet != null){
            userScoreLevelSkipListSet.add(userScore);
            userScores.replace(levelId, userScoreLevelSkipListSet);
        } else {
            userScoreLevelSkipListSet = new ConcurrentSkipListSet<>();
            userScoreLevelSkipListSet.add(userScore);
            userScores.putIfAbsent(levelId, userScoreLevelSkipListSet);
        }
    }

    /**
     * Returns the high score list for the given level.
     * The limit value affects the performance of the operation.
     *
     * @param levelId to retrieve the high score list from
     * @param limit is the maximum scores to be returned
     * @return the high score list for the given level
     */
    public String getHighScoreList(Integer levelId, int limit) {
        String response = "";
        ConcurrentSkipListSet<UserScore> userScoreLevelSkipListSet = userScores.get(levelId);
        if(userScoreLevelSkipListSet != null) {
            Iterator<UserScore> iter = userScoreLevelSkipListSet.descendingIterator();
            response = ScoreListUtil.convertToCSV(iter, limit);
        }
        return response;
    }

    /**
     * Creates or reuses the manager's instance.
     * Ensures that only a Singleton instance is used.
     *
     * @return the manager's instance.
     */
    public static UserScoreManager getInstance() {
        if (instance == null) {
            synchronized (UserScoreManager.class) {
                if (instance == null)
                    instance = new UserScoreManager();
            }
        }
        return instance;
    }

    /**
     * Returns the user scores map
     *
     * @return the user scores map
     */
    public ConcurrentHashMap<Integer, ConcurrentSkipListSet<UserScore>> getUserScores() {
        return userScores;
    }

    @Override
    public String toString() {
        return "UserScoreManager{" +
                "userScores=" + userScores.toString() +
                '}';
    }
}
