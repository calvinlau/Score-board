package com.king.scoreboard.server;

import com.king.scoreboard.session.SessionManager;
import com.sun.net.httpserver.HttpServer;

import java.util.Timer;
import java.util.logging.Logger;

/**
 * Created by ioannis.metaxas on 2015-11-29.
 *
 * Server hook for terminating the server properly in case of an sudden shutdown.
 * Since it is a separate thread, it could also perform other actions like sending notifications to administrators.
 *
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
        if(timer != null) {
            timer.cancel();
        }
        if(httpServer != null){
            httpServer.stop(0);
        }
        LOGGER.warning("Shutting down the Scoreboard server...");
        LOGGER.info("Sending notifications to the system administrators...");
    }
}
