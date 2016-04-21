/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.manager;

import me.calvinliu.scoreboard.model.UserScore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Stores and manages the user scores for every level.
 */
public class ScoreManager {

    private static volatile ScoreManager instance = null;
    private ConcurrentHashMap<Integer, ConcurrentSkipListSet<UserScore>> levelScores;

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
        ConcurrentSkipListSet<UserScore> scoreSet = levelScores.get(levelId);
        if (scoreSet == null) {
            ConcurrentSkipListSet<UserScore> value = new ConcurrentSkipListSet<>();
            scoreSet = levelScores.putIfAbsent(levelId, value);
            if (scoreSet == null) {
                scoreSet = value;
            }
        }
        scoreSet.add(userScore);
//        if (scoreSet == null) {
//            // TODO:
//            scoreSet = new ConcurrentSkipListMap<Integer, AtomicInteger>(new Comparator<Map.Entry<Integer, AtomicInteger>>() {
//                @Override
//                public int compare(Map.Entry<Integer, AtomicInteger> e1,
//                                   Map.Entry<Integer, AtomicInteger> e2) {
//                    return e1.getValue().get() - e2.getValue().get();
//                }
//            });
//            scoreSet.putIfAbsent(userScore.getUserId(), new AtomicInteger(userScore.getScore()));
//            levelScores.putIfAbsent(levelId, scoreSet);
//        } else {
//            AtomicInteger max = scoreSet.get(userScore.getUserId());
//            if (max == null) {
//                scoreSet.putIfAbsent(userScore.getUserId(), new AtomicInteger(userScore.getScore()));
//            } else {
//                // Use compareAndSet method of AtomicInteger to update high score
//                while (true) {
//                    int maxVal = max.get();
//                    if (userScore.getScore() <= maxVal) {
//                        break;
//                    }
//                    boolean success = max.compareAndSet(maxVal, userScore.getScore());
//                    if (success) {
//                        break;
//                    }
//                }
//            }
//        }
    }

    /**
     * Returns the high score list for the given level.
     * The limit value affects the performance of the operation.
     *
     * @param levelId to retrieve the high score list from
     * @param limit   is the maximum scores to be returned
     * @return the high score list for the given level
     */
    public String getHighScoreList(Integer levelId, int limit) {
//        ConcurrentSkipListSet<UserScore> scoreSet = levelScores.get(levelId);
//        StringBuilder sb = new StringBuilder();
//        if (scoreSet != null) {
//            int i = 0;
//            Iterator<Integer> it = scoreMap.keySet().iterator();
//            while (it.hasNext() && i < limit) {
//                Integer userId = it.next();
//                sb.append(userId);
//                sb.append("=");
//                sb.append(scoreMap.get(userId));
//                sb.append(",");
//                i++;
//            }
//            if (sb.length() > 0) {
//                sb.deleteCharAt(sb.length() - 1);
//            }
//        }
        String response = "";
        ConcurrentSkipListSet<UserScore> userScoreLevelSkipListSet = levelScores.get(levelId);
        if (userScoreLevelSkipListSet != null) {
            response = convertToCSV(userScoreLevelSkipListSet.descendingIterator(), limit);
        }
        return response;
    }

    private String convertToCSV(Iterator<UserScore> it, Integer limit) {
        if (limit == null) {
            limit = Integer.MAX_VALUE;
        }
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

    ConcurrentHashMap<Integer, ConcurrentSkipListSet<UserScore>> getUserScores() {
        return levelScores;
    }
}
