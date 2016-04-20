package com.king.scoreboard.util;

/**
 * Created by ioannis.metaxas on 2015-12-01.
 *
 * Convenient utility for validating values
 */
public class Validator {

    /**
     * Validates whether the given value is an 31 bit unsigned integer
     *
     * 32 bit signed    from: −(2^31) to (2^31)−1 ~ −2,147,483,648 to 2,147,483,647
     * 32 bit unsigned  from: 0 to (2^32)−1 ~ 0 to 4,294,967,295
     * 31 bit unsigned  from: 0 to (2^31)−1 ~ 0 to 2,147,483,647
     *
     * @param value to validate
     * @return true if the given value is an 31 bit unsigned integer, false otherwise
     */
    public static boolean isUnsignedInteger31Bit(String value) {
        try {
            int number = Integer.parseInt(value);
            if (number < 0) {
                return false;
            } else {
                return true;
            }
        } catch(NumberFormatException nfe) {
            return false;
        }
    }
}
