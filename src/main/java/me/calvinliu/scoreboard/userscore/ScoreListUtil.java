package me.calvinliu.scoreboard.userscore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ioannis.metaxas on 2015-11-28.
 *
 * Convenient utilities for handling the user scores
 */
public class ScoreListUtil {

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
        StringBuilder buffer = new StringBuilder();
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
}
