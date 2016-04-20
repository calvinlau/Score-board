package me.calvinliu.scoreboard.util;

/**
 * Created by ioannis.metaxas on 2015-12-01.
 *
 * Convenient utility for validating values
 */
public class ParameterVerifier {

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
            return number >= 0;
        } catch(NumberFormatException nfe) {
            return false;
        }
    }

    public static int getValueAsUnsignedInt(String value) {
        if (value == null || "".equals(value.trim())) {
            throw new InvalidParamException("The integer cannot be empty");
        }
        int integerValue;
        try {
            integerValue = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new InvalidParamException("Invalid integer value (probably too long?): " + value, ex);
        }
        if (integerValue < 0) {
            throw new InvalidParamException("The integer value cannot be negative: " + value);
        }
        return integerValue;
    }
}
