/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.server;

import com.sun.net.httpserver.HttpExchange;
import me.calvinliu.scoreboard.controller.HttpController;
import me.calvinliu.scoreboard.util.InvalidParamException;
import me.calvinliu.scoreboard.util.ParameterVerifier;
import me.calvinliu.scoreboard.util.ResponseCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that retrieves from HttpExchange the url parameters and the post body
 */
public class HttpParameterHelper {

    private static final String PARAM_VALUE_SEPARATOR_REGEX = "[=]";
    private static final String URL_PARAMETER_SEPARATOR_REGEX = "[?]";
    private static final String FILE_ENCODING = System.getProperty("file.encoding");

    public static RequestParameter retrieveParameters(HttpExchange exchange) {
        try {
            RequestParameter parameter = new RequestParameter();
            parameter.setUrlParameters(retrieveUrlParametersMap(exchange));
            if (HttpController.POST.equalsIgnoreCase(exchange.getRequestMethod())) {
                parameter.setPostBody(retrievePostBodyInteger(exchange));
            }
            parameter.setIntegerFromUrl(getIntegerFromURI(exchange.getRequestURI()));
            return parameter;
        } catch (IOException ex) {
            throw new InvalidParamException("Error trying to retrieve the parameters from the request", ex);
        }
    }

    private static Map<String, String> retrieveUrlParametersMap(HttpExchange exchange) throws UnsupportedEncodingException {
        URI requestedUri = exchange.getRequestURI();
        String query = requestedUri.getRawQuery();
        return parseQueryRetrievingUrlParams(query);
    }

    private static Integer retrievePostBodyInteger(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String postBodyAsString = br.readLine();
        Integer postBody = null;
        if (postBodyAsString != null) {
            postBody = ParameterVerifier.getValueAsUnsignedInt(postBodyAsString);
        }
        return postBody;
    }

    private static Map<String, String> parseQueryRetrievingUrlParams(String query) throws UnsupportedEncodingException {
        Map<String, String> parameters = new HashMap<>();
        if (query != null) {
            String pairs[] = query.split(URL_PARAMETER_SEPARATOR_REGEX);
            for (String pair : pairs) {
                String param[] = pair.split(PARAM_VALUE_SEPARATOR_REGEX);
                String key = null;
                String value = null;
                if (param.length > 0) {
                    key = URLDecoder.decode(param[0], FILE_ENCODING);
                }
                if (param.length > 1) {
                    value = URLDecoder.decode(param[1], FILE_ENCODING);
                }
                parameters.put(key, value);
            }
        }
        return parameters;
    }

    private static int getIntegerFromURI(URI uri) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(uri.toString());
        if (matcher.find()) {
            return ParameterVerifier.getValueAsUnsignedInt(matcher.group());
        } else {
            throw new InvalidParamException(ResponseCode.ERR_INVALID_INTEGER);
        }
    }
}
