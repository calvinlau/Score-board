/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.server;

import com.sun.net.httpserver.HttpServer;
import me.calvinliu.scoreboard.controller.ControllerFactory;
import me.calvinliu.scoreboard.manager.PropertiesManager;
import me.calvinliu.scoreboard.manager.SessionManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * The score server
 */
public class ScoreboardServer {

    private static final Logger LOGGER = Logger.getLogger("confLogger");

    private static HttpServer httpServer;
    private static ExecutorService serverExecutor;
    private static String hostName;
    private static int port;

    public static void init() throws IOException {

        port = PropertiesManager.getInstance().getPort();
        hostName = PropertiesManager.getInstance().getHost();

        // Create the server
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        ScoreboardHandler router = createRequestRouter();
        httpServer.createContext("/", router);

        // Set an Executor for the multi-threading
        serverExecutor = Executors.newCachedThreadPool();

        httpServer.setExecutor(serverExecutor);
    }

    /**
     * Starts the server
     */
    public static void start() {
        try {
            init();
        } catch (IOException e) {
            LOGGER.warning("Server start error, ex: " + e.getMessage());
        }
        httpServer.start();
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(httpServer));
        printHelp();
    }

    /**
     * Stops the server
     */
    public static void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            serverExecutor.shutdown();
            SessionManager.getInstance().getTimer().cancel();
        }
    }

    /**
     * Prints the current service endpoints
     */
    private static void printHelp() {
        System.out.println("Operations:");
        System.out.println("---------------------------------------------------------------");
        System.out.println("GET http://" + hostName + ":" + port + "/<userid>/login");
        System.out.println("POST http://" + hostName + ":" + port + "/<levelid>/score?sessionkey=<sessionkey>&score=<score>");
        System.out.println("GET http://" + hostName + ":" + port + "/<levelid>/highscorelist");
        System.out.println("---------------------------------------------------------------");
    }

    /**
     * Init Controller factory to add router for url request
     */
    private static ScoreboardHandler createRequestRouter() {
        ScoreboardHandler requestRouter = new ScoreboardHandler();
        ControllerFactory controllerFactory = new ControllerFactory();
        requestRouter.addController(controllerFactory.createLoginController());
        requestRouter.addController(controllerFactory.createHighScoreController());
        requestRouter.addController(controllerFactory.createUserScoreController());
        return requestRouter;
    }
}
