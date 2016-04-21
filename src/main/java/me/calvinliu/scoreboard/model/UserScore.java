/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Model of the user score
 */
public class UserScore implements Comparable<UserScore> {

    private Integer userId;
    private AtomicInteger score;

    public UserScore(Integer userId, AtomicInteger score) {
        this.userId = userId;
        this.score = score;
    }

    public Integer getUserId() {
        return userId;
    }

    public AtomicInteger getScore() {
        return score;
    }

    @Override
    public int compareTo(UserScore that) {
        if (this.score == null) {
            if (that.score == null) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (that.score == null) {
                return 1;
            } else {
                return this.score.get() - that.score.get();
            }
        }
    }
}
