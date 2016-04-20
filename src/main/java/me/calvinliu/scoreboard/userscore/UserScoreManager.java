package me.calvinliu.scoreboard.userscore;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Stores and manages the user scores for every level.
 */
public class UserScoreManager {

    private static volatile UserScoreManager instance = null;
    private ConcurrentHashMap<Integer, ConcurrentSkipListMap<Integer, AtomicInteger>> levelScores;

    private UserScoreManager() {
        levelScores = new ConcurrentHashMap<>();
    }

    /**
     * Puts a user score into a specific level.
     * This method is synchronized to ensure thread-safety access to the user scores map which is the critical-resource.
     *
     * @param levelId to assign the user score to
     * @param userScore to be put in the map
     */
    public synchronized void postScore(Integer levelId, UserScore userScore) {
        ConcurrentSkipListMap<Integer, AtomicInteger> scoreMap = levelScores.get(levelId);
        if (scoreMap == null) {
            // TODO:
            scoreMap = new ConcurrentSkipListMap<>();
            scoreMap.putIfAbsent(userScore.getUserId(), new AtomicInteger(userScore.getScore()));
            levelScores.putIfAbsent(levelId, scoreMap);
        } else {
            AtomicInteger max = scoreMap.get(userScore.getUserId());
            if (max == null) {
                scoreMap.putIfAbsent(userScore.getUserId(), new AtomicInteger(userScore.getScore()));
            } else {
                // Use compareAndSet method of AtomicInteger to update high score
                while (true) {
                    int maxVal = max.get();
                    if (userScore.getScore() <= maxVal) {
                        break;
                    }
                    boolean success = max.compareAndSet(maxVal, userScore.getScore());
                    if (success) {
                        break;
                    }
                }
            }
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
        ConcurrentSkipListMap<Integer, AtomicInteger> scoreMap = levelScores.get(levelId);
        StringBuilder sb = new StringBuilder();
        if (scoreMap != null) {
            int i = 0;
            Iterator<Integer> it = scoreMap.keySet().iterator();
            while (it.hasNext() && i < limit) {
                Integer userId = it.next();
                sb.append(userId);
                sb.append("=");
                sb.append(scoreMap.get(userId));
                sb.append(",");
                i++;
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        return sb.toString();
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
}
