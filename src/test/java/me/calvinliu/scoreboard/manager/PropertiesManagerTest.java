/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.manager;

import org.junit.Test;

import static org.junit.Assert.assertSame;

public class PropertiesManagerTest {

    @Test
    public void testSingletonInstances() {
        assertSame(PropertiesManager.getInstance(), PropertiesManager.getInstance());
    }
}
