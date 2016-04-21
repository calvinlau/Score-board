/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.controller;

/**
 * Factory to grant access to all the different url controllers
 */
public class ControllerFactory {

    public HttpController createLoginController() {
        return new LoginController();
    }

    public HttpController createHighScoreController() {
        return new HighScoreListController();
    }

    public HttpController createUserScoreController() {
        return new ScoreController();
    }
}
