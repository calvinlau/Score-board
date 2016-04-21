/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.manager;

import me.calvinliu.scoreboard.model.UserScore;

/**
 * Represents a user score thread for test-stretch the user-scores map
 */
public class ScoreManagerThread extends Thread {

    private Integer levelId;
    private UserScore userScore;
    private ScoreManager scoreManager;

    public ScoreManagerThread(ScoreManager scoreManager, int levelId, UserScore userScore) {
        this.scoreManager = scoreManager;
        this.levelId = levelId;
        this.userScore = userScore;
    }

    @Override
    public void run() {
        scoreManager.postScore(levelId, userScore);
    }

    public Integer getLevelId() {
        return levelId;
    }
}
