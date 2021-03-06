/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.calvinliu.scoreboard.controller.HttpController;
import me.calvinliu.scoreboard.util.InvalidParamException;
import me.calvinliu.scoreboard.util.ResponseCode;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The http server handler for handling requests.
 */
public class ScoreboardHandler implements HttpHandler {

    private static final Logger LOGGER = Logger.getLogger("confLogger");
    private List<HttpController> controllers = new ArrayList<>();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            for (HttpController controller : controllers) {
                if (matchesRequestMethod(exchange, controller) && matchesUrl(exchange, controller)) {
                    writeResponseFromController(exchange, controller);
                    return;
                }
            }
            LOGGER.info(exchange.getRequestMethod() + " Url not mapped: " + exchange.getRequestURI());
            writeResponse(exchange, ResponseCode.ERR_INVALID_URL, HttpURLConnection.HTTP_OK);
        } catch (InvalidParamException e) {
            LOGGER.warning("URL Handler warning, Code: " + e.getMessage() + ", Url: " + exchange.getRequestURI());
            writeResponse(exchange, e.getMessage(), HttpURLConnection.HTTP_OK);
        } finally {
            exchange.getResponseBody().close();
        }
    }

    void addController(HttpController controller) {
        controllers.add(controller);
    }

    private boolean matchesRequestMethod(HttpExchange exchange, HttpController controller) {
        return controller.getRequestMethod().equals(exchange.getRequestMethod());
    }

    private boolean matchesUrl(HttpExchange exchange, HttpController controller) {
        Pattern regex = Pattern.compile(controller.getUrlRegexPattern());
        Matcher matcher = regex.matcher(exchange.getRequestURI().toString());
        return matcher.matches();
    }

    private void writeResponseFromController(HttpExchange exchange, HttpController controller) {
        RequestParameter requestParameters = RequestParameterHelper.retrieveParameters(exchange);
        String response = controller.processRequest(requestParameters.getUrlParameters(), requestParameters.getPostBody(),
                requestParameters.getIntegerFromUrl());
        writeResponse(exchange, response, HttpURLConnection.HTTP_OK);
    }

    private void writeResponse(HttpExchange exchange, String content, int status) {
        try {
            Headers header = exchange.getResponseHeaders();
            header.set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(status, content.length());
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (IOException ex) {
            // Nothing else we can do, just wait for other requests
            LOGGER.warning("Exception while writing the response" + ex);
        }
    }
}
