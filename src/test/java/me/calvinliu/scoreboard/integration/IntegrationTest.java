/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.integration;

import me.calvinliu.scoreboard.controller.HttpController;
import me.calvinliu.scoreboard.manager.ScoreManager;
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

@Category(IntegrationTest.class)
public class IntegrationTest {

    private static final String BODY_SCORE = "1000";
    private static final String BODY_SCORE_OVERFLOW = String.valueOf(Long.MAX_VALUE);
    private static final String BODY_SCORE_STRING = "string";
    private static final String LOGIN_URL = "http://localhost:8081/2/login";
    private static final String INVALID_LOGIN_URL = "http://localhost:8081/2/login1";
    private static final String SCORE_URL = "http://localhost:8081/2/score?"; //sessionkey=UICSNDK";
    private static final String SCORE_URL_INVALID = "http://localhost:8081/2/score";
    private static final String HIGH_SCORE_LIST_URL = "http://localhost:8081/2/highscorelist";
    private static final String HIGH_SCORE_LIST_URL_INVALID = "http://localhost:8081/2/highscorelist?";

    private IntegrationTest http = null;
    private ScoreManager scoreManager;

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
        http = new IntegrationTest();
        scoreManager = ScoreManager.getInstance();
    }

    @Test
    public void testLogin() {
        assertEquals(HttpURLConnection.HTTP_OK, http.sendGet(LOGIN_URL).getCode());
    }

    @Test
    public void testLogin_InValid() {
        HttpResult result = http.sendGet(INVALID_LOGIN_URL);
        assertEquals(HttpURLConnection.HTTP_OK, result.getCode());
        assertEquals(ResponseCode.ERR_INVALID_URL, result.getResponse());
    }

    @Test
    public void testPostScore() {
        HttpResult loginResult = http.sendGet(LOGIN_URL);
        HttpResult scoreResult = http.sendPost(SCORE_URL + "sessionkey=" + loginResult.getResponse(), BODY_SCORE);
        assertEquals(HttpURLConnection.HTTP_OK, scoreResult.getCode());
        assertEquals(HttpController.EMPTY, scoreResult.getResponse());

        scoreManager.getUserScores().clear();
    }

    @Test
    public void testPostScore_Invalid() {
        HttpResult loginResult = http.sendGet(LOGIN_URL);
        HttpResult scoreResult = http.sendPost(SCORE_URL_INVALID + "sessionkey=" + loginResult.getResponse(), BODY_SCORE);
        assertEquals(HttpURLConnection.HTTP_OK, scoreResult.getCode());
        assertEquals(HttpController.EMPTY, scoreResult.getResponse());
    }

    @Test
    public void testPostScore_Overflow() {
        HttpResult result = http.sendGet(LOGIN_URL);
        assertEquals(HttpURLConnection.HTTP_OK, http.sendPost(SCORE_URL + "sessionkey=" + result.getResponse(), BODY_SCORE_OVERFLOW).getCode());
    }

    @Test
    public void testPostScore_String() {
        HttpResult result = http.sendGet(LOGIN_URL);
        assertEquals(HttpURLConnection.HTTP_OK, http.sendPost(SCORE_URL + "sessionkey=" + result.getResponse(), BODY_SCORE_STRING).getCode());
    }

    @Test
    public void testGetHighScoreList() {
        HttpResult result = http.sendGet(LOGIN_URL);
        http.sendPost(SCORE_URL + "sessionkey=" + result.getResponse(), "SESSIONKEY");
        assertEquals(HttpURLConnection.HTTP_OK, http.sendGet(HIGH_SCORE_LIST_URL).getCode());
    }

    @Test
    public void testGetHighScoreList_Invalid() {
        HttpResult result = http.sendGet(HIGH_SCORE_LIST_URL_INVALID);
        assertEquals(HttpURLConnection.HTTP_OK, result.getCode());
        assertEquals(ResponseCode.ERR_INVALID_URL, result.getResponse());
    }

    /**
     * Make HTTP GET
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
     * Make HTTP POST
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