/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.controller;

import java.util.Map;

/**
 * The interface of all the service handlers
 */
public interface HttpController {

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String EMPTY = "";

    String getUrlRegexPattern();

    String processRequest(Map<String, String> urlParameters, Integer postBody, int urlInteger);

    String getRequestMethod();
}
