package me.calvinliu.scoreboard;

import me.calvinliu.scoreboard.property.ConfigProperties;
import org.junit.Test;

import static org.junit.Assert.assertSame;

public class PropertiesManagersTest {

    @Test
    public void testSingletonInstances() {
        assertSame(ConfigProperties.getInstance(), ConfigProperties.getInstance());
    }
}
