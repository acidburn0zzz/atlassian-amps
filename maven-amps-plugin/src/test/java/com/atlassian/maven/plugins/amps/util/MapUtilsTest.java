package com.atlassian.maven.plugins.amps.util;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MapUtilsTest {

    @Test
    public void emptyMapShouldJoinIntoAnEmptyString() {
        assertThat(MapUtils.join(emptyMap(), "foo", "bar"), is(""));
    }

    @Test
    public void singletonMapShouldJoinIntoKeyPlusEqualsPlusValue() {
        assertThat(MapUtils.join(singletonMap(1, 2L), ";", ":"), is("1;2"));
    }

    @Test
    public void keyValueSeparatorShouldBeAbleToBeEmpty() {
        assertThat(MapUtils.join(singletonMap(3, true), "", ":"), is("3true"));
    }

    @Test
    public void shouldBeAbleToJoinMultipleEntryMap() {
        final Map<?, ?> map = ImmutableMap.of("foo", 41, "bar", 42);
        assertThat(MapUtils.join(map, "#", "@"), is("foo#41@bar#42"));
    }
}