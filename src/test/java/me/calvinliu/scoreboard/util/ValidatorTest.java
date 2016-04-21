/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ValidatorTest {

    @Test
    public void testIsUnsignedInteger31Bit() {
        assertEquals(ParameterVerifier.getValueAsUnsignedInt(String.valueOf(Integer.MAX_VALUE)), Integer.MAX_VALUE);
        assertEquals(ParameterVerifier.getValueAsUnsignedInt(String.valueOf(0)), 0);
    }

    @Test(expected = InvalidParamException.class)
    public void testIsUnsignedInteger31Bit_Ex1() {
        assertNotNull(ParameterVerifier.getValueAsUnsignedInt("123456789123"));
    }

    @Test(expected = InvalidParamException.class)
    public void testIsUnsignedInteger31Bit_Ex2() {
        ParameterVerifier.getValueAsUnsignedInt("-1");
    }

    @Test(expected = InvalidParamException.class)
    public void testIsUnsignedInteger31Bit_Ex3() {
        ParameterVerifier.getValueAsUnsignedInt("aa");
    }

    @Test(expected = InvalidParamException.class)
    public void testIsUnsignedInteger31Bit_Ex4() {
        ParameterVerifier.getValueAsUnsignedInt(" ");
    }

    @Test(expected = InvalidParamException.class)
    public void testIsUnsignedInteger31Bit_Ex5() {
        ParameterVerifier.getValueAsUnsignedInt("1.1");
    }

    @Test(expected = InvalidParamException.class)
    public void testIsUnsignedInteger31Bit_Ex6() {
        ParameterVerifier.getValueAsUnsignedInt("");
    }
}
