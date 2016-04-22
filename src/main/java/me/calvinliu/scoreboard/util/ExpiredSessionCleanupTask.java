/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.util;

import me.calvinliu.scoreboard.manager.SessionManager;

import java.util.TimerTask;

/**
 * Task for removing expired user sessions.
 */
public class ExpiredSessionCleanupTask extends TimerTask {

    public final long expiredTime;

    public ExpiredSessionCleanupTask(long expiredTime) {
        this.expiredTime = expiredTime;
    }

    @Override
    public void run() {
        SessionManager.getInstance().removeUserSessions(expiredTime);
    }
}
