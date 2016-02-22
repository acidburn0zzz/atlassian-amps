package com.atlassian.maven.plugins.amps.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class VersionTest {

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfNull() throws Exception {
        Version.valueOf(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfEmpty() throws Exception {
        Version.valueOf("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfInvalid() throws Exception {
        Version.valueOf("abc");
    }

    @Test
    public void testValueOfMajorMinorPatchParseSuffixDash() throws Exception {
        Version v400 = Version.valueOf("3.9.1-SNAPSHOT");
        assertThat("Major part is not correct", v400.getMajor(), is(3));
        assertThat("Minor part is not correct", v400.getMinor(), is(9));
        assertThat("Patch part is not correct", v400.getPatch(), is(1));
    }

    @Test
    public void testValueOfMajorMinorPatchParseSuffixPart() throws Exception {
        Version v400 = Version.valueOf("4.0.0.RELEASE");
        assertThat("Major part is not correct", v400.getMajor(), is(4));
        assertThat("Minor part is not correct", v400.getMinor(), is(0));
        assertThat("Patch part is not correct", v400.getPatch(), is(0));
    }

    @Test
    public void testValueOfMajorMinorPatch() throws Exception {
        Version v400 = Version.valueOf("4.0.0");
        assertThat("Major part is not correct", v400.getMajor(), is(4));
        assertThat("Minor part is not correct", v400.getMinor(), is(0));
        assertThat("Patch part is not correct", v400.getPatch(), is(0));
    }

    @Test
    public void testValueOfMajorMinorPatchSuffix() throws Exception {
        Version v400 = Version.valueOf("4.0.0-SNAPSHOT");
        assertThat("Suffix part in 4.0.0-SNAPSHOT is not correct", v400.getQualifier(), is("SNAPSHOT"));


        v400 = Version.valueOf("4.0.0.RELEASE");
        assertThat("Suffix part in 4.0.0.RELEASE is not correct", v400.getQualifier(), is("RELEASE"));


        v400 = Version.valueOf("4.0.RELEASE");
        assertThat("Suffix part in 4.0.RELEASE is not correct", v400.getQualifier(), is("RELEASE"));
    }

    @Test
    public void testValueOfMajorMinor() throws Exception {
        Version v400 = Version.valueOf("4.0");
        assertThat("Major part is not correct", v400.getMajor(), is(4));
        assertThat("Minor part is not correct", v400.getMinor(), is(0));
        assertThat("Patch part is not correct", v400.getPatch(), is(0));
    }

    @Test
    public void testValueOfMajor() throws Exception {
        Version v400 = Version.valueOf("4");
        assertThat("Major part is not correct", v400.getMajor(), is(4));
        assertThat("Minor part is not correct", v400.getMinor(), is(0));
        assertThat("Patch part is not correct", v400.getPatch(), is(0));
    }

    @Test
    public void testIsGreaterThan() throws Exception {
        assertTrue(Version.valueOf("4.0.0").isGreaterThan(Version.valueOf("3.13.291")));
        assertTrue("RELEASE > SNAPSHOT", Version.valueOf("4.0.0.RELEASE").isGreaterThan(Version.valueOf("4.0.0-SNAPSHOT")));
        assertTrue("No Qualifier > SNAPSHOT", Version.valueOf("4.0.0").isGreaterThan(Version.valueOf("4.0.0-SNAPSHOT")));
        assertTrue("RELEASE > ALPHA", Version.valueOf("4.0.0.RELEASE").isGreaterThan(Version.valueOf("4.0.0.ALPHA")));
        assertTrue("RELEASE > BETA", Version.valueOf("4.0.0.RELEASE").isGreaterThan(Version.valueOf("4.0.0.BETA")));
    }

    @Test
    public void testIsGreaterOrEqualTo() throws Exception {
        assertTrue(Version.valueOf("4.0.0").isGreaterOrEqualTo(Version.valueOf("4.0.0")));
        assertTrue(Version.valueOf("4.0.0").isGreaterOrEqualTo(Version.valueOf("4.0.0-SNAPSHOT")));
        assertTrue(Version.valueOf("4.0.0.RELEASE").isGreaterOrEqualTo(Version.valueOf("4.0.0-SNAPSHOT")));
    }

    @Test
    public void testIsLessThan() throws Exception {
        assertFalse(Version.valueOf("4.0.0").isLessThan(Version.valueOf("3.13.291")));
    }
}