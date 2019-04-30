package com.atlassian.maven.plugins.amps;

import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.util.Optional;
import static org.junit.Assert.assertEquals;

public class TestFecruFullVersionGetter
{

    RunStandaloneMojo rsm;

    @Before
    public void setUp() {
        rsm = new RunStandaloneMojo();
    }

    @Test
    public void TestInvalidVersion() {
        try {
            assertEquals(Optional.empty(),rsm.getFullVersion("4.2"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestValidShortVersion() {
        try {
            assertEquals("4.7.0-20190214100246",rsm.getFullVersion("4.7.0").orElse("failed to find"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
