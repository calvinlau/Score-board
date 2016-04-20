package me.calvinliu.scoreboard.util;

import me.calvinliu.scoreboard.session.SessionManager;

import java.util.TimerTask;

/**
 * Task for removing user sessions.
 */
public class LogoutTimerTask extends TimerTask {

    public final long LOGOUT_TIMEOUT;

    public LogoutTimerTask(long logoutTimeout) {
        this.LOGOUT_TIMEOUT = logoutTimeout;
    }

    @Override
    public void run() {
        SessionManager.getInstance().removeUserSessions(LOGOUT_TIMEOUT);
    }
}
