package com.king.scoreboard;

import com.king.scoreboard.server.ScoreboardServer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Application  {

    private static final Logger LOGGER = Logger.getLogger("confLogger");

    public static void main(String[] args) {

        try {
            ScoreboardServer server = new ScoreboardServer();
            server.start();
            System.out.println("Press Enter to stop the server. ");
            System.in.read();
            server.stop();
            LOGGER.warning("Scoreboard server stopped!");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Scoreboard server terminated unexpectedly!", ex);
        }
    }
}
