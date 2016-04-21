/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.util;

/**
 * Convenient utility for validating values
 */
public class ParameterVerifier {

    /**
     * Get the given value is an 31 bit unsigned integer
     *
     * @param value to validate
     * @return true if the given value is an 31 bit unsigned integer, false otherwise
     */
    public static int getValueAsUnsignedInt(String value) {
        if (value == null || "".equals(value.trim())) {
            throw new InvalidParamException(ResponseCode.ERR_INVALID_INTEGER);
        }
        int integerValue;
        try {
            integerValue = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new InvalidParamException(ResponseCode.ERR_INVALID_INTEGER + value, ex);
        }
        if (integerValue < 0) {
            throw new InvalidParamException(ResponseCode.ERR_INVALID_INTEGER + value);
        }
        return integerValue;
    }
}
