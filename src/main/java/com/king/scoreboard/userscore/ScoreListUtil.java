package com.king.scoreboard.userscore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ioannis.metaxas on 2015-11-28.
 *
 * Convenient utilities for handling the user scores
 */
public class ScoreListUtil {

    private static final Logger LOGGER = Logger.getLogger("confLogger");

    /**
     * Converts and returns the values of an iterator as a CSV string.
     *
     * @param iter with the user scores to be converted
     * @return the values of an iterator as a CSV string
     */
    public static String convertToCSV(Iterator<UserScore> iter) {
        return convertToCSV(iter, null);
    }

    /**
     * Converts and returns the values of an iterator as a CSV string using a limit.
     * The limit indicates the maximum number of elements that will be returned
     *
     * @param iter with the user scores to be converted
     * @param limit which indicates the maximum number of elements that will be returned
     * @return the values of an iterator as a CSV string using a limit.
     */
    public static String convertToCSV(Iterator<UserScore> iter, Integer limit) {
        if(limit == null) {
            limit = Integer.MAX_VALUE;
        }
        StringBuffer buffer = new StringBuffer();
        List<Integer> usedUserScoreIdList = new ArrayList<>();
        int i = 0;
        while(iter.hasNext() && i < limit) {
            UserScore userScore = iter.next();
            if(!usedUserScoreIdList.contains(userScore.getUserId())) {
                usedUserScoreIdList.add(userScore.getUserId());
                buffer.append(userScore.getUserId());
                buffer.append("=");
                buffer.append(userScore.getScore());
                buffer.append(",");
                i++;
            }
        }
        if (buffer.length() > 0) {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        return buffer.toString();
    }

    /**
     * Returns the score, read from the request body of an input stream.
     *
     * @param inputStream to read the score from
     * @return the score read from the request body.
     */
    public static String getScore(InputStream inputStream) {
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                buffer.append(inputLine);
            }
        } catch(IOException ioe) {
            LOGGER.log(Level.SEVERE, null, ioe);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch(IOException ioe) {
                LOGGER.log(Level.SEVERE, null, ioe);
            }
        }
        return buffer.toString();
    }
}
