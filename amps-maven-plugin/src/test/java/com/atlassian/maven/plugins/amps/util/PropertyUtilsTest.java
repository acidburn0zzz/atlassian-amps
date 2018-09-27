package com.atlassian.maven.plugins.amps.util;

import org.junit.Test;

import java.util.Properties;

import static com.atlassian.maven.plugins.amps.util.PropertyUtils.parse;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PropertyUtilsTest
{
    private static final char KEY_VALUE_DELIMITER = '-';
    private static final char PROPERTY_DELIMITER = ';';

    @Test
    public void shouldTreatNullInputStringAsNoProperties()
    {
        // Invoke
        final Properties properties = parse(null, KEY_VALUE_DELIMITER, PROPERTY_DELIMITER);

        // Check
        assertThat(properties.size(), is(0));
    }

    @Test
    public void shouldTreatEmptyInputStringAsNoProperties()
    {
        // Invoke
        final Properties properties = parse("", KEY_VALUE_DELIMITER, PROPERTY_DELIMITER);

        // Check
        assertThat(properties.size(), is(0));
    }

    @Test
    public void shouldParseSingleKeyValuePair()
    {
        // Invoke
        final Properties properties = parse("a-b", KEY_VALUE_DELIMITER, PROPERTY_DELIMITER);

        // Check
        assertThat(properties.size(), is(1));
        assertThat(properties.getProperty("a"), is("b"));

    }

    @Test
    public void shouldParseMultipleKeyValuePairs()
    {
        // Invoke
        final Properties properties = parse("a-b;c-d", KEY_VALUE_DELIMITER, PROPERTY_DELIMITER);

        // Check
        assertThat(properties.size(), is(2));
        assertThat(properties.getProperty("a"), is("b"));
        assertThat(properties.getProperty("c"), is("d"));

    }
}