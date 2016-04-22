/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.model;

import me.calvinliu.scoreboard.manager.PropertiesManager;

import java.util.Date;

/**
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
        return new Date().getTime() - createdDate.getTime() > PropertiesManager.getInstance().getExpirationTime();
    }

    @Override
    public String toString() {
        return "UserSession{" + "userId=" + userId + ", sessionKey='" + sessionKey + ", createdDate=" + createdDate + '}';
    }
}
