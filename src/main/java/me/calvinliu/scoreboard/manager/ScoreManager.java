/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.manager;

import me.calvinliu.scoreboard.model.UserScore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

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


        NavigableSet<UserScore> set = levelScores.computeIfAbsent(levelId, n -> new ConcurrentSkipListSet<>());
        for (UserScore model : set) {
            if (model.getUserId().equals(userScore.getUserId())) {
                // Use compareAndSet method of AtomicInteger to update high score
                AtomicInteger max = model.getScore();
                while (true) {
                    int maxVal = max.get();
                    if (userScore.getScore().get() <= maxVal) {
                        break;
                    }
                    boolean success = max.compareAndSet(maxVal, userScore.getScore().get());
                    if (success) {
                        break;
                    }
                }
            }
        }

        NavigableSet<UserScore> scoreSet = levelScores.get(levelId);
        if (scoreSet == null) {
            NavigableSet<UserScore> value = new ConcurrentSkipListSet<>();
            scoreSet = levelScores.putIfAbsent(levelId, value);
            if (scoreSet == null) {
                scoreSet = value;
            }
        }
        scoreSet.add(userScore);
    }

    /**
     * Returns the high score list for the given level.
     *
     * @param levelId to retrieve the high score list from
     * @param limit   is the maximum scores to be returned
     * @return the high score list for the given level
     */
    public String getHighScoreList(Integer levelId, int limit) {
        String response = "";
        NavigableSet<UserScore> set = levelScores.get(levelId);
        if (set != null) {
            response = convertToCSV(set.descendingIterator(), limit);
        }
        return response;
    }

    /**
     * Convert high score list To CSV
     *
     * @param it to retrieve the high score list from
     * @param limit   is the maximum scores to be returned
     * @return the high score list for the given level
     */
    private String convertToCSV(Iterator<UserScore> it, int limit) {
        StringBuilder buffer = new StringBuilder();
        List<Integer> usedUserScoreIdList = new ArrayList<>();
        int i = 0;
        while (it.hasNext() && i < limit) {
            UserScore userScore = it.next();
            if (!usedUserScoreIdList.contains(userScore.getUserId())) {
                usedUserScoreIdList.add(userScore.getUserId());
                buffer.append(userScore.getUserId());
                buffer.append("=");
                buffer.append(userScore.getScore());
                buffer.append(",");
                i++;
            }
        }
        if (buffer.length() > 0) {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        return buffer.toString();
    }

    ConcurrentHashMap<Integer, NavigableSet<UserScore>> getUserScores() {
        return levelScores;
    }
}
