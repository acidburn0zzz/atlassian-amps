package it.com.atlassian.amps;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Check if product is up and running. Also check if plugin endpoints are correctly loaded into product
 */
public class IntegrationTest
{
    @Test
    public void anIntegrationTest() throws Exception
    {
        assertPresent("/plugins/servlet/it");
    }

    @Test
    public void anIntegrationTestForTestPlugin() throws Exception
    {
        assertPresent("/plugins/servlet/it-tests");
    }

    private void assertPresent(String resourceUrl) throws Exception
    {
        String httpPort = System.getProperty("http.port");
        String contextPath = System.getProperty("context.path");

        assert httpPort != null;
        assert contextPath != null;

        String url = "http://localhost:" + httpPort + contextPath + resourceUrl;

        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(url);
        client.executeMethod(method);

        assertEquals("Should have 200: " + url, 200, method.getStatusCode());
    }
}
