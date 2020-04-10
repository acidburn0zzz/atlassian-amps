package com.atlassian.maven.plugins.updater;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MarketplaceSdkResourceJsonParsingTest {

    public MarketplaceSdkResource marketplaceSdkResource = new MarketplaceSdkResource();

    @Test
    public void shouldCorrectlyParseJson() throws Exception {
        final String json = FileUtils.readFileToString(new File(MarketplaceSdkResource.class.getResource("marketplace_response.json").toURI()),
                UTF_8);

        final Map<?, ?> parsedJson = marketplaceSdkResource.parseJsonToMap(json);

        final Map<?, ?> versionElement = (Map<?, ?>) parsedJson.get("version");
        assertThat(versionElement.get("version"), is("8.0.16"));
        final Map<?, ?> versionsElement = (Map<?, ?>) parsedJson.get("versions");
        final List<Map<?, ?>> versions = (List<Map<?, ?>>) versionsElement.get("versions");
        final Optional<Map<?, ?>> maybeVersion8016 = versions
                .stream()
                .filter(map -> Objects.equals(map.get("version"), "8.0.16"))
                .findAny();
        assertThat(maybeVersion8016.isPresent(), is(true));
        final List<Map<?, ?>> links = (List<Map<?, ?>>) maybeVersion8016.get().get("links");
        assertThat(links, hasItem(allOf(
                hasEntry("href", "https://marketplace.atlassian.com/download/apps/1210993/version/42480"),
                hasEntry("rel", "binary")
        )));
    }
}