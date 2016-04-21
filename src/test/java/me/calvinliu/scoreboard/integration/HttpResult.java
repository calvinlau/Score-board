/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.integration;

/**
 * Http Result
 */
public class HttpResult {

    private String response;
    private int code;

    public HttpResult(String response, int code) {
        this.response = response;
        this.code = code;
    }

    public String getResponse() {
        return response;
    }

    public int getCode() {
        return code;
    }
}
