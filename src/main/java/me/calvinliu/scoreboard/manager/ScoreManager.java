/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.manager;

import me.calvinliu.scoreboard.model.UserScore;

import java.util.HashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Stores and manages the user scores for every level.
 */
public class ScoreManager {

    private static volatile ScoreManager instance = null;
    private static final int THRESHOLD_NUM = PropertiesManager.getInstance().getHighScoresThresholdLimit();
    private Map<Integer, Map<Integer, UserScore>> userScores;
    private Map<Integer, NavigableSet<UserScore>> levelScores;

    /**
     * Private constructor for singleton and init
     */
    private ScoreManager() {
        userScores = new ConcurrentHashMap<>();
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
     * This method is lock-free to ensure thread-safety access to the user scores map.
     *
     * @param levelId   to assign the user score to
     * @param userScore to be put in the map
     */
    public void postScore(Integer levelId, UserScore userScore) {
        NavigableSet<UserScore> scoreSet = levelScores.computeIfAbsent(levelId, n -> new ConcurrentSkipListSet<>());
        // Keep the score set's size smaller than THRESHOLD_NUM
        while (scoreSet.size() >= THRESHOLD_NUM) {
            UserScore remove = scoreSet.pollFirst();
            userScores.computeIfAbsent(levelId, n -> new ConcurrentHashMap<>()).remove(remove.getUserId());
        }

        // Update high score if needed
        Map<Integer, UserScore> scoreMap = userScores.computeIfAbsent(levelId, n -> new ConcurrentHashMap<>());
        if (scoreMap.containsKey(userScore.getUserId())) {
            UserScore oldUserScore = scoreMap.get(userScore.getUserId());
            // Use compareAndSet method of AtomicInteger to update high score lock free
            AtomicInteger oldScore = oldUserScore.getScore();
            while (true) {
                int oldVal = oldScore.get();
                if (userScore.getScore().get() <= oldVal) {
                    break;
                }
                boolean success = oldScore.compareAndSet(oldVal, userScore.getScore().get());
                if (success) {
                    break;
                }
            }
        } else {
            scoreSet.add(userScore);
            scoreMap.put(userScore.getUserId(), userScore);
        }
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
            // User Id Set for de-duplicated same user
            Set<Integer> userIdSet = new HashSet<>();
            for (UserScore userScore : set.descendingSet()) {
                if (i >= limit) {
                    break;
                }
                if (!userIdSet.contains(userScore.getUserId())) {
                    userIdSet.add(userScore.getUserId());
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

    /**
     * Directly get the level score map for unit testing.
     * DO NOT Call it!
     *
     * @return level score map
     */
    public Map<Integer, NavigableSet<UserScore>> getUserScores() {
        return levelScores;
    }
}
