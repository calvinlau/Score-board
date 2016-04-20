package me.calvinliu.scoreboard;

import me.calvinliu.scoreboard.server.ScoreboardServer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Application {

    private static final Logger LOGGER = Logger.getLogger("confLogger");

    public static void main(String[] args) {
        try {
            ScoreboardServer server = new ScoreboardServer();
            server.start();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Scoreboard server terminated unexpectedly!", ex);
        }
    }
}
