package com.king.scoreboard.session;

import com.king.scoreboard.property.ConfigProperties;

import java.util.Date;

/**
 * Created by ioannis.metaxas on 2015-11-29.
 * <p>
 * Model of the user session
 */
public class UserSession {
    private final Integer userId;
    private final String sessionKey;
    private Date createdDate;

    public UserSession(Integer userId, String sessionKey) {
        this.userId = userId;
        this.sessionKey = sessionKey;
        this.createdDate = new Date();
    }

    public Integer getUserId() {
        return userId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public Date getCreatedDate() {
        return (Date) createdDate.clone();
    }

    public boolean hasExpired() {
        return new Date().getTime() - createdDate.getTime() > ConfigProperties.getInstance().getLogoutTimeout();
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "userId=" + userId +
                ", sessionKey='" + sessionKey + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
