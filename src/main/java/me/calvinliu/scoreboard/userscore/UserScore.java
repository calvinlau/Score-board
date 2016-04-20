package me.calvinliu.scoreboard.userscore;


/**
 * Created by ioannis.metaxas on 2015-11-28.
 *
 * Model of the user score relation
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
    public int compareTo(UserScore o) {
        if (o == null) return 1;
        return this.score - o.getScore();
    }
}
