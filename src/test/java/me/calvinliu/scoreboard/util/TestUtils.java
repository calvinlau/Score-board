/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test Utils
 */
public class TestUtils {

    private static SecureRandom random = new SecureRandom();

    public static int getRandomLevelId() {
        return getRandomId(8);
    }

    public static int getRandomUserId() {
        return getRandomId(10);
    }

    public static AtomicInteger getRandomScore() {
        return new AtomicInteger(getRandomId(20));
    }

    private static int getRandomId(int numBits) {
        return Math.abs(new BigInteger(numBits, random).intValue());
    }
}
