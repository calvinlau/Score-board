/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.util;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidatorTest {

    @Test
    public void testIsUnsignedInteger31BitTrue() {
        assertTrue(ParameterVerifier.isUnsignedInteger31Bit(String.valueOf(Integer.MAX_VALUE)));
        assertTrue(ParameterVerifier.isUnsignedInteger31Bit(String.valueOf(0)));
    }

    @Test
    public void testIsUnsignedInteger31BitFalse() {
        assertFalse(ParameterVerifier.isUnsignedInteger31Bit("123456789123"));
        assertFalse(ParameterVerifier.isUnsignedInteger31Bit(String.valueOf(-1)));
    }

    @Test
    public void testIsUnsignedInteger31BitValueNotANumber() {
        assertFalse(ParameterVerifier.isUnsignedInteger31Bit(Mockito.any(String.class)));
    }
}