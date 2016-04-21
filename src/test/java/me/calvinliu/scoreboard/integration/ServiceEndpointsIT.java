/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.integration;

import me.calvinliu.scoreboard.server.ScoreboardServer;
import me.calvinliu.scoreboard.util.ResponseCode;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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

    private static final String postWrongScoreUrl = "http://localhost:8081/2/score";
    private static final String requestNegativeBodyScore = "-1";
    private static final String postScoreUrl = "http://localhost:8081/2/score?"; //sessionkey=UICSNDK";
    private static final String requestBodyScore = "1000";
    private static final String requestTooBigBodyScore = String.valueOf(Long.MAX_VALUE);
    private static final String requestStringBodyScore = "an arbitrary string";
    private static final String getLoginUrl = "http://localhost:8081/2/login";
    private static final String getWrongLoginUrl = "http://localhost:8081/2/login1";
    private static final String getHighScoreListUrl = "http://localhost:8081/2/highscorelist";
    private static final String getWrongHighScoreListUrl = "http://localhost:8081/2/highscorelist?";

    ServiceEndpointsIT http = null;

    @BeforeClass
    public static void setUpClass() {
        ScoreboardServer.start();
    }

    @AfterClass
    public static void tearDownClass() {
        ScoreboardServer.stop();
    }

    @Before
    public void setUp() {
        http = new ServiceEndpointsIT();
    }

    @Test
    public void testLoginService() {
        assertEquals(HttpURLConnection.HTTP_OK, http.sendGet(getLoginUrl).getCode());
    }

    @Test
    public void testLoginServiceWrongUri() {
        HttpResult result = http.sendGet(getWrongLoginUrl);
        assertEquals(HttpURLConnection.HTTP_OK, result.getCode());
        assertEquals(ResponseCode.ERR_WRONG_URL, result.getResponse());
    }

    @Test
    public void testPostUserScoreService() {
        HttpResult result = http.sendGet(getLoginUrl);
        assertEquals(HttpURLConnection.HTTP_OK, http.sendPost(postScoreUrl + "sessionkey=" + result.getResponse(), requestBodyScore).getCode());
    }

    @Test
    public void testPostUserScoreServiceWrongUri() {
        HttpResult result = http.sendGet(getLoginUrl);
        assertNotEquals(HttpURLConnection.HTTP_OK, http.sendPost(postWrongScoreUrl + "sessionkey=" + result.getResponse(), requestBodyScore).getCode());
    }

    @Test
    public void testPostUserScoreServiceNegativeRequestBody() {
        HttpResult result = http.sendGet(getLoginUrl);
        assertNotEquals(HttpURLConnection.HTTP_OK, http.sendPost(postScoreUrl + "sessionkey=" + result.getResponse(), requestNegativeBodyScore).getCode());
    }

    @Test
    public void testPostUserScoreServiceTooBigScoreRequestBody() {
        HttpResult result = http.sendGet(getLoginUrl);
        assertNotEquals(HttpURLConnection.HTTP_OK, http.sendPost(postScoreUrl + "sessionkey=" + result.getResponse(), requestTooBigBodyScore).getCode());
    }

    @Test
    public void testPostUserScoreServiceStringScoreRequestBody() {
        HttpResult result = http.sendGet(getLoginUrl);
        assertNotEquals(HttpURLConnection.HTTP_OK, http.sendPost(postScoreUrl + "sessionkey=" + result.getResponse(), requestStringBodyScore).getCode());
    }

    @Test
    public void testGetHighScoreListService() {
        HttpResult result = http.sendGet(getLoginUrl);
        http.sendPost(postScoreUrl + "sessionkey=" + result.getResponse(), requestBodyScore);
        assertEquals(HttpURLConnection.HTTP_OK, http.sendGet(getHighScoreListUrl).getCode());
    }

    @Test
    public void testGetHighScoreListServiceWrongUri() {
        HttpResult result = http.sendGet(getWrongHighScoreListUrl);
        assertEquals(HttpURLConnection.HTTP_OK, result.getCode());
        assertEquals(ResponseCode.ERR_WRONG_URL, result.getResponse());
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
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return new HttpResult("", responseCode);
            }
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            //print result
            System.out.println("Response: " + response.toString());
        } catch (IOException | HTTPException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
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
        } catch (IOException | HTTPException ioe) {
            System.out.println(ioe.getMessage());
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new HttpResult("", responseCode);
    }
}

