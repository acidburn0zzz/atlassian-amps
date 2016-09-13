package com.atlassian.maven.plugins.amps.util;

import javax.annotation.Nonnull;
import java.util.Map;

import static java.util.stream.Collectors.joining;

/**
 * {@link java.util.Map}-related utility methods.
 *
 * @since 6.2.8
 */
public final class MapUtils {

    /**
     * Joins the given map into a string using the given delimiters.
     *
     * @param map the map to stringify
     * @param keyValueSeparator the delimiter between the key and value of each map entry
     * @param entrySeparator the delimiter between successive map entries
     * @return see above
     */
    @Nonnull
    public static String join(@Nonnull final Map<?, ?> map,
                              @Nonnull final String keyValueSeparator,
                              @Nonnull final CharSequence entrySeparator) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + keyValueSeparator + entry.getValue())
                .collect(joining(entrySeparator));
    }

    private MapUtils() {}
}
