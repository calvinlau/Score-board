package me.calvinliu.scoreboard.integration;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.ws.http.HTTPException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@Category(ServiceEndpointsIT.class)
public class ServiceEndpointsIT {

    private static String postScoreUrl = "http://localhost:8081/2/score?"; //sessionkey=UICSNDK";
    private static final String postWrongScoreUrl = "http://localhost:8081/2/score";
    private static String requestBodyScore = "1000";
    private static final String requestNegativeBodyScore = "-1";
    private static String requestTooBigBodyScore = String.valueOf(Long.MAX_VALUE);
    private static String requestStringBodyScore = "an arbitrary string";

    private static String getLoginUrl = "http://localhost:8081/2/login";
    private static String getWrongLoginUrl = "http://localhost:8081/2/login1";

    private static String getHighScoreListUrl = "http://localhost:8081/2/highscorelist";
    private static String getWrongHighScoreListUrl = "http://localhost:8081/2/highscorelist?";

    ServiceEndpointsIT http = null;

    @Before
    public void setUp() {
        http = new ServiceEndpointsIT();
    }

    @Test
    public void testLoginService() {
        assertEquals(200, http.sendGet(getLoginUrl).getCode());
    }

    @Test
    public void testLoginServiceWrongUri() {
        assertNotEquals(200, http.sendGet(getWrongLoginUrl).getCode());
    }

    @Test
    public void testPostUserScoreService() {
        HttpResult result = http.sendGet(getLoginUrl);
        assertEquals(200, http.sendPost(postScoreUrl + "sessionkey=" + result.getResponse(), requestBodyScore).getCode());
    }

    @Test
    public void testPostUserScoreServiceWrongUri() {
        HttpResult result = http.sendGet(getLoginUrl);
        assertNotEquals(200, http.sendPost(postWrongScoreUrl + "sessionkey=" + result.getResponse(), requestBodyScore).getCode());
    }

    @Test
    public void testPostUserScoreServiceNegativeRequestBody() {
        HttpResult result = http.sendGet(getLoginUrl);
        assertNotEquals(200, http.sendPost(postScoreUrl + "sessionkey=" + result.getResponse(), requestNegativeBodyScore).getCode());
    }

    @Test
    public void testPostUserScoreServiceTooBigScoreRequestBody() {
        HttpResult result = http.sendGet(getLoginUrl);
        assertNotEquals(200, http.sendPost(postScoreUrl + "sessionkey=" + result.getResponse(), requestTooBigBodyScore).getCode());
    }

    @Test
    public void testPostUserScoreServiceStringScoreRequestBody() {
        HttpResult result = http.sendGet(getLoginUrl);
        assertNotEquals(200, http.sendPost(postScoreUrl + "sessionkey=" + result.getResponse(), requestStringBodyScore).getCode());
    }

    @Test
    public void testGetHighScoreListService() {
        HttpResult result = http.sendGet(getLoginUrl);
        http.sendPost(postScoreUrl + "sessionkey=" + result.getResponse(), requestBodyScore);
        assertEquals(200, http.sendGet(getHighScoreListUrl).getCode());
    }

    @Test
    public void testGetHighScoreListServiceWrongUri() {
        assertNotEquals(200, http.sendGet(getWrongHighScoreListUrl).getCode());
    }

    /**
     * Does HTTP a GET request
      */
    private HttpResult sendGet(String url) {

        BufferedReader reader = null;
        HttpURLConnection conn = null;
        StringBuilder response = new StringBuilder();
        int responseCode = -1;
        try {
            System.out.println("\nSending 'GET' request to URL : " + url);
            conn = (HttpURLConnection) (new URL(url)).openConnection();

            // optional default is GET
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            responseCode = conn.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            if(responseCode != 200) {
                return new HttpResult("", responseCode);
            }

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }

            //print result
            System.out.println("Response: " + response.toString());

        } catch(IOException | HTTPException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if(conn != null) {
                    conn.disconnect();
                }
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return new HttpResult(response.toString(), responseCode);
    }

    /**
     * Does a HTTP POST request
     */

    private HttpResult sendPost(String url, String requestBody) {

        HttpURLConnection conn = null;
        DataOutputStream writer = null;
        int responseCode = -1;
        try {
            conn = (HttpURLConnection) (new URL(url)).openConnection();

            //add reuqest header
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            // Send post request
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            writer = new DataOutputStream(conn.getOutputStream());
            writer.writeBytes(requestBody);
            writer.flush();
            writer.close();

            responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch(HTTPException he) {
            System.err.println("Response code NOT 200!");
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if(conn != null) {
                    conn.disconnect();
                }
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return new HttpResult("", responseCode);
    }
}

class HttpResult {

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
