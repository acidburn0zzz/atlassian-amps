package com.atlassian.maven.plugins.amps.product;

import java.net.URISyntaxException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CrowdProductHandlerTest
{
    @Test
    public void crowdServerUriConvertedToUseLocalhost() throws URISyntaxException
    {
        String uri = "http://example.test:8080/prefix/crowd?query#fragment";
        assertEquals("http://localhost:8080/prefix/crowd?query#fragment", CrowdProductHandler.withLocalhostAsHostname(uri));
    }

    @Test
    public void crowdServerUriConvertedToUseLocalhostWithHttps() throws URISyntaxException
    {
        String uri = "https://example.test:8080/prefix/crowd?query#fragment";
        assertEquals("https://localhost:8080/prefix/crowd?query#fragment", CrowdProductHandler.withLocalhostAsHostname(uri));
    }
}
