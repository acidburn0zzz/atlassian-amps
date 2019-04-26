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
}
