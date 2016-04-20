package com.king.scoreboard;

import com.king.scoreboard.userscore.ScoreListUtil;
import com.king.scoreboard.userscore.UserScore;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by ioannis.metaxas on 2015-12-02.
 */
public class ScoreListUtilTest {

    private ConcurrentHashMap<Integer, ConcurrentSkipListSet<UserScore>> userScores;
    private static SecureRandom random;

    @Before
    public void setUp() {
        userScores = new ConcurrentHashMap<>();
        random = new SecureRandom();
    }

    @Test
    public void testConvertToCSVNoLimit() {
        convertToCVS(null);
    }

    @Test
    public void testConvertToCSVLimit() {
        int LIMIT = 3;
        String returnedStr = convertToCVS(LIMIT);

        assertEquals(LIMIT, returnedStr.split(",").length);
    }

    @Test
    public void testConvertToCSVEmptyIterator() {
        ConcurrentSkipListSet<UserScore> concurrentSkipListSet = new ConcurrentSkipListSet<>();
        assertEquals("", ScoreListUtil.convertToCSV(concurrentSkipListSet.iterator()));
    }

    private String convertToCVS(Integer limit) {
        int DEFAULT_MAX_LIMIT = 15;
        if(limit == null) {
            limit = DEFAULT_MAX_LIMIT;
        }

        ConcurrentSkipListSet<UserScore> concurrentSkipListSet = new ConcurrentSkipListSet<>();
        // Add user score
        String validatedString = "";
        for(int i = 0; i < limit; i++){
            int userId = getRandomUserId();
            int score = getRandomId();
            validatedString += userId + "=" + score + ",";
            concurrentSkipListSet.add(new UserScore(userId, score));
        }
        if (validatedString.length() > 0) {
            validatedString = validatedString.substring(0, validatedString.length() - 1);
        }

        // Select the appropriate call
        String returnedStr;
        if(limit != null) {
            returnedStr = ScoreListUtil.convertToCSV(concurrentSkipListSet.iterator(), limit);
        } else {
            returnedStr = ScoreListUtil.convertToCSV(concurrentSkipListSet.iterator());
        }

        // assert every sub string that is part of the returned string
        String[] expectedSubstrings = validatedString.split(",");
        for(int i = 0; i < expectedSubstrings.length; i++) {
            assertTrue(returnedStr.contains(expectedSubstrings[i]));
        }

        return returnedStr;
    }

    private int getRandomId() {
        return Math.abs(new BigInteger(130, random).intValue());
    }

    private int getRandomUserId() {
        return Math.abs(new BigInteger(130, random).intValue());
    }
}
