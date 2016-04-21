/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.server;

import java.util.Map;

public class RequestParameter {

    private Map<String, String> urlParameters;
    private Integer postBody;
    private int integerFromUrl;

    public Map<String, String> getUrlParameters() {
        return urlParameters;
    }

    public void setUrlParameters(Map<String, String> urlParameters) {
        this.urlParameters = urlParameters;
    }

    public Integer getPostBody() {
        return postBody;
    }

    public void setPostBody(Integer postBody) {
        this.postBody = postBody;
    }

    public int getIntegerFromUrl() {
        return integerFromUrl;
    }

    public void setIntegerFromUrl(int integerFromUrl) {
        this.integerFromUrl = integerFromUrl;
    }

}
