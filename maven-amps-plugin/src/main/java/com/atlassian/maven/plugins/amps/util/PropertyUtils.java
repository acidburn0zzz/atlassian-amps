package com.atlassian.maven.plugins.amps.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Properties;

import static java.util.Arrays.stream;
import static org.apache.commons.lang3.StringUtils.split;

/**
 * Utility methods relating to {@link Properties}.
 *
 * @since 6.2.9
 */
public final class PropertyUtils
{
    private PropertyUtils() {}

    /**
     * Parses the given String of property keys and values into a {@link Properties} object.
     *
     * @param stringOfProperties the string to parse
     * @param keyValueDelimiter the delimiter within each property's key-value string, e.g. "=" in "a=1"
     * @param propertyDelimiter the delimiter between each property pair, e.g. "," in "a=1,b=2"
     * @return see above
     */
    @Nonnull
    public static Properties parse(
            @Nullable final String stringOfProperties, final char keyValueDelimiter, final char propertyDelimiter)
    {
        final Properties properties = new Properties();
        final String[] keyValuePairs = split(stringOfProperties, propertyDelimiter);
        if (keyValuePairs != null) {
            stream(keyValuePairs)
                    .map(pair -> split(pair, keyValueDelimiter))
                    .filter(parts -> parts.length == 2)
                    .forEach(keyAndValue ->
                            properties.put(keyAndValue[0], keyAndValue[1]));
        }
        return properties;
    }
}
