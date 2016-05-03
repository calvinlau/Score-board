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

    private static final int THRESHOLD_NUM = PropertiesManager.getInstance().getHighScoresThresholdLimit();
    private static volatile ScoreManager instance = null;
    private Map<Integer, Map<Integer, UserScore>> userScores;
    private Map<Integer, NavigableSet<UserScore>> levelScores;
    private Map<String, Integer> cache;

    /**
     * Private constructor for singleton and init
     */
    private ScoreManager() {
        userScores = new ConcurrentHashMap<>();
        levelScores = new ConcurrentHashMap<>();
        cache = new ConcurrentHashMap<>();
    }

    /**
     * Creates or reuses the manager's instance.
     * Ensures that only a Singleton instance is used.
     *
     * @return the manager's instance.
     */
    public static ScoreManager getInstance() {
        // The effect of this result is that in cases where instance is already initialized
        // the volatile field is only accessed once which can improve overall performance
        ScoreManager result = instance;
        if (result == null) {
            synchronized (ScoreManager.class) {
                result = instance;
                if (result == null)
                    instance = result = new ScoreManager();
            }
        }
        return result;
    }

    /**
     * Puts a user score into a specific level.
     * This method is lock-free to ensure thread-safety access to the user scores map.
     *
     * @param levelId   to assign the user score to
     * @param userScore to be put in the map
     */
    public void postScore(Integer levelId, UserScore userScore) {
        Map<Integer, UserScore> scoreMap = userScores.computeIfAbsent(levelId, n -> new ConcurrentHashMap<>());
        Integer userId = userScore.getUserId();

        // Avoid post one user's lower score
        if (scoreMap.containsKey(userId) && scoreMap.get(userId).getScore().get() >= userScore.getScore().get()) {
            return;
        }

        NavigableSet<UserScore> scoreSet = levelScores.computeIfAbsent(levelId, n -> new ConcurrentSkipListSet<>());
        // Keep the score set's size smaller than THRESHOLD_NUM
        if (scoreSet.size() > THRESHOLD_NUM) {
            UserScore remove = scoreSet.pollFirst();
            if (userScore.getScore().get() < remove.getScore().get()) {
                return;
            }
        }

        // Save to set
        scoreMap.put(userId, userScore);
        scoreSet.add(userScore);
    }

    /**
     * Puts a user score into a specific level.
     * This method is lock-free to ensure thread-safety access to the user scores map.
     * <p/>
     * Thread Safe for same user/level concurrent update
     *
     * @param levelId   to assign the user score to
     * @param userScore to be put in the map
     */
    public void postScore2(Integer levelId, UserScore userScore) {
        NavigableSet<UserScore> scoreSet = levelScores.computeIfAbsent(levelId, n -> new ConcurrentSkipListSet<>());
        Map<Integer, UserScore> scoreMap = userScores.computeIfAbsent(levelId, n -> new ConcurrentHashMap<>());

        // Keep the score set's size smaller than THRESHOLD_NUM
        while (scoreSet.size() >= THRESHOLD_NUM) {
            UserScore remove = scoreSet.pollFirst();
            scoreMap.remove(remove.getUserId());
        }

        // Use compareAndSet method of AtomicInteger to update high score lock free
        while (true) {
            UserScore oldUserScore = scoreMap.putIfAbsent(userScore.getUserId(), userScore);
            if (oldUserScore == null) {
                scoreSet.add(userScore);
                break;
            } else {
                AtomicInteger oldScore = oldUserScore.getScore();
                int oldVal = oldScore.get();
                if (userScore.getScore().get() <= oldVal) {
                    break;
                }
                scoreSet.remove(oldUserScore);
                boolean success = oldScore.compareAndSet(oldVal, userScore.getScore().get());
                if (success) {
                    scoreSet.add(userScore);
                    break;
                }
            }
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

