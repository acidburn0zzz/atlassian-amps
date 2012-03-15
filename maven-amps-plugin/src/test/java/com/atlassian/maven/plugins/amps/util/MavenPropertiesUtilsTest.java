package com.atlassian.maven.plugins.amps.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MavenPropertiesUtilsTest
{
    @Test
    public void testGetGoalPrefixReturnsCorrectPrevixForOldStyle()
    {
        assertEquals("amps", MavenPropertiesUtils.getGoalPrefix("maven-amps-plugin"));
    }
    
    @Test
    public void testGetGoalPrefixReturnsCorrectPrevixForNewStyle()
    {
        assertEquals("amps", MavenPropertiesUtils.getGoalPrefix("amps-maven-plugin"));
    }

    @Test
    public void testGetGoalPrefixReturnsCorrectPrevixForCompoundedNouns()
    {
        assertEquals("tricky-code-generator", MavenPropertiesUtils.getGoalPrefix("tricky-code-generator-maven-plugin"));
    }

    @Test
    public void testGetGoalPrefixReturnsOriginalStringForUnrecognizedNames()
    {
        assertEquals("special-string", MavenPropertiesUtils.getGoalPrefix("special-string"));
    }

}