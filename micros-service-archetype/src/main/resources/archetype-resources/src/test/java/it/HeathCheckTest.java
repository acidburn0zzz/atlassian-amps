package it.${package};

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class HeathCheckTest
{
    private static final String BASE_URL = "http://localhost:8080";

    private WebResource baseWebResource;

    @Before
    public void setUp()
    {
        baseWebResource = Client.create().resource(BASE_URL);
    }

    @Test
    public void verifyHealthCheck()
    {
        final ClientResponse response = baseWebResource.path("healthcheck").get(ClientResponse.class);
        assertThat(response.getStatus(), equalTo(200));
    }
}
