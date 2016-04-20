package com.king.scoreboard.controller;

import com.king.scoreboard.service.ServicesEnum;
import com.king.scoreboard.util.InvalidParamException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * The interface of all the service handlers
 */
public interface HttpController {

    ServicesEnum getService();

    void handleRequest(HttpExchange httpExchange) throws IOException;

    void validate() throws InvalidParamException;
}
