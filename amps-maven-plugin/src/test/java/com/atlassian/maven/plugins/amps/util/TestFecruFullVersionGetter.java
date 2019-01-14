package com.atlassian.maven.plugins.amps.util;

import org.junit.Test;
import java.io.IOException;
import java.util.Optional;

import static com.atlassian.maven.plugins.amps.util.FecruFullVersionGetter.getFullVersion;
import static org.junit.Assert.assertEquals;

public class TestFecruFullVersionGetter
{
    @Test
    public void TestInvalidVersion() {
        try {
            assertEquals(Optional.empty(),getFullVersion("4.2"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestValidShortVersion() {
        try {
            assertEquals("4.2.0-20160928073034",getFullVersion("4.2.0").orElse("failed to find"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
