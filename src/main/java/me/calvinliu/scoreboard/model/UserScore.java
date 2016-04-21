/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.model;

/**
 * Model of the user score
 */
public class UserScore implements Comparable<UserScore> {

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
    public int compareTo(UserScore that) {
        if (this.score == null)
            if (that.score == null)
                return 0;
            else
                return -1;
        else if (that.score == null)
            return 1;
        else
            return this.score.compareTo(that.score);
    }
}
