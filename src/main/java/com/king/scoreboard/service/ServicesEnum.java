package com.king.scoreboard.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ioannis.metaxas on 2015-11-30.
 *
 * The supported services enumeration and their operations
 */
public enum ServicesEnum {

    LOGIN(Pattern.compile("/\\d+/login", Pattern.CASE_INSENSITIVE)),
    SCORE(Pattern.compile("/\\d+/score\\?sessionkey=\\w+", Pattern.CASE_INSENSITIVE)),
    HIGH_SCORE_LIST(Pattern.compile("/\\d+/highscorelist", Pattern.CASE_INSENSITIVE));

    private Pattern uriPattern;

    ServicesEnum(Pattern uriPattern) {
        this.uriPattern = uriPattern;
    }

    /**
     * Returns true if the path corresponds to the login service endpoint
     *
     * @param path to validate
     * @return true if the path corresponds to the login service endpoint, false otherwise
     */
    public static boolean isLoginService(String path) {
        return isValidServiceUri(path, LOGIN);
    }

    /**
     * Returns true if the path corresponds to the get high-score list service endpoint
     *
     * @param path to validate
     * @return true if the path corresponds to the get high-score list service endpoint, false otherwise
     */
    public static boolean isHighScoreListService(String path) {
        return isValidServiceUri(path, HIGH_SCORE_LIST);
    }

    /**
     * Returns true if the path corresponds to the user-score-level service endpoint
     *
     * @param path to validate
     * @return true if the path corresponds to the user-score-level service endpoint, false otherwise
     */
    public static boolean isUserScoreLevelService(String path) {
        return isValidServiceUri(path, SCORE);
    }

    /**
     * Validates whether the given path corresponds to a valid service endpoint
     *
     * @param path to validate
     * @return true if the given path corresponds to a valid service endpoint, false otherwise
     */
    public static boolean isValidService(String path) {
        if(isHighScoreListService(path) || isLoginService(path) || isUserScoreLevelService(path)){
            return true;
        }
        return false;
    }

    /**
     * Validates whether the given path corresponds to the given service endpoint
     *
     * @param path to validate
     * @param service to validate
     * @return true if the given path corresponds to the given service endpoint, false otherwise
     */
    public static boolean isValidServiceUri(String path, ServicesEnum service) {
        Matcher matcher = service.getUriPattern().matcher(path);
        return matcher.matches();
    }

    public Pattern getUriPattern() {
        return uriPattern;
    }
}
