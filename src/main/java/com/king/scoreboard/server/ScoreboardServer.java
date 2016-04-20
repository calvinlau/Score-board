package com.king.scoreboard.server;

import com.king.scoreboard.property.ConfigProperties;
import com.king.scoreboard.service.ServiceFilter;
import com.king.scoreboard.session.SessionManager;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The score server
 */
public class ScoreboardServer {

    private HttpServer httpServer;
    private ExecutorService serverExecutor;
    private String hostName;
    private int port;

    public ScoreboardServer() throws IOException {

        port = ConfigProperties.getInstance().getPort();
        hostName = InetAddress.getLocalHost().getCanonicalHostName();

        // Create the server
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        HttpContext context = httpServer.createContext("/", new ScoreboardHandler());

        // Add the endpoint filter
        context.getFilters().add(new ServiceFilter());

        // Set an Executor for the multi-threading
        serverExecutor = Executors.newCachedThreadPool();

        httpServer.setExecutor(serverExecutor);
    }

    /**
     * Starts the server
     */
    public void start() {
        httpServer.start();
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(httpServer));
        printHelp();
    }

    /**
     * Stops the server
     */
    public void stop() {
        if (this.httpServer != null) {
            this.httpServer.stop(0);
            this.serverExecutor.shutdown();
            SessionManager.getInstance().getTimer().cancel();
        }
    }

    /**
     * Prints the current service endpoints
     */
    private void printHelp() {
        System.out.println("Operations:");
        System.out.println("---------------------------------------------------------------");
        System.out.println("GET http://" + hostName + ":" + port + "/<userid>/login");
        System.out.println("POST http://" + hostName + ":" + port + "/<levelid>/score?sessionkey=<sessionkey>&score=<score>");
        System.out.println("GET http://" + hostName + ":" + port + "/<levelid>/highscorelist");
        System.out.println("---------------------------------------------------------------");
    }
}
