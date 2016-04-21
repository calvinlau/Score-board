/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.server;

import com.sun.net.httpserver.HttpServer;
import me.calvinliu.scoreboard.manager.SessionManager;

import java.util.Timer;
import java.util.logging.Logger;

/**
 * Server hook for terminating the server properly in case of an sudden shutdown.
 * Since it is a separate thread, it could also perform other actions like sending notifications to administrators.
 */
public class ShutdownHook extends Thread {

    private static final Logger LOGGER = Logger.getLogger("confLogger");

    private HttpServer httpServer;
    private Timer timer;

    public ShutdownHook(HttpServer httpServer) {
        this.httpServer = httpServer;
        this.timer = SessionManager.getInstance().getTimer();
    }

    public void run() {
        if (timer != null) {
            timer.cancel();
        }
        if (httpServer != null) {
            httpServer.stop(0);
        }
        LOGGER.warning("Shutting down the Scoreboard server...");
        LOGGER.info("Sending notifications to the system administrators...");
    }
}
