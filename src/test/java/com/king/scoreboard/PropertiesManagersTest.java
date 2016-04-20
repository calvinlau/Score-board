package com.king.scoreboard;

import com.king.scoreboard.property.ConfigProperties;
import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * Created by ioannis.metaxas on 2015-12-02.
 */
public class PropertiesManagersTest {

    @Test
    public void testSingletonInstances() {
        assertSame(ConfigProperties.getInstance(), ConfigProperties.getInstance());
    }
}
