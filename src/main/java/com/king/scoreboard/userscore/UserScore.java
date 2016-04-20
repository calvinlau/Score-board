package com.king.scoreboard.userscore;

import java.util.Comparator;

/**
 * Created by ioannis.metaxas on 2015-11-28.
 *
 * Model of the user score relation
 */
public class UserScore implements Comparator<UserScore> {

    private Integer userId;
    private Integer score;

    public UserScore(Integer userId, Integer score) {
        this.userId = userId;
        this.score = score;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "UserScore{" +
                "userId='" + userId + '\'' +
                ", score=" + score +
                '}';
    }

    @Override
    public int compare(UserScore userScore1, UserScore userScore2) {
        return userScore1.getScore() - userScore2.getScore();
    }
}
