package com.atlassian.maven.plugins.amps.util;

import org.junit.Test;

import static com.atlassian.maven.plugins.amps.util.VersionUtils.*;
import static org.junit.Assert.assertTrue;

public class VersionUtilsTest {

    @Test
    public void getVersionFromString() {
        assertTrue(versionFromString("4.0") > versionFromString("3.0"));
        assertTrue(versionFromString("3.11") > versionFromString("3.10"));
        assertTrue(versionFromString("3.11.1") > versionFromString("3.11"));
        assertTrue(versionFromString("3.12.5") > versionFromString("3.11.6"));
    }

}
