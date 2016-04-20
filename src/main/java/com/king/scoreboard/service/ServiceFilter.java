package com.king.scoreboard.service;

import com.king.scoreboard.util.HttpStatus;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by ioannis.metaxas on 2015-11-30.
 *
 * Filters the requests based on the supported services.
 * It returns Http code 400 Bad Request if a request is not supported.
 *
 */
public class ServiceFilter extends Filter {

    private static final Logger LOGGER = Logger.getLogger("confLogger");

    @Override
    public void doFilter(HttpExchange httpExchange, Chain chain) throws IOException {
        if(ServicesEnum.isValidService(httpExchange.getRequestURI().toString())){
            chain.doFilter(httpExchange);
        } else {
            LOGGER.warning("No service for the request: " + httpExchange.getRequestURI().toString());
            httpExchange.sendResponseHeaders(HttpStatus.BAD_REQUEST.getCode(), -1);
            httpExchange.getResponseBody().close();
        }
    }

    @Override
    public String description() {
        return "Filters the requests based on the supported services";
    }
}
