package com.atlassian.maven.plugins.amps.util;

import com.atlassian.maven.plugins.amps.ProductArtifact;
import org.junit.Test;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import static com.atlassian.maven.plugins.amps.util.ProductHandlerUtil.toArtifacts;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

public class ProductHandlerUtilTest
{
    @Test
    public void testPickFreePort() throws IOException
    {
        try (final ServerSocket ignored = new ServerSocket(16829))
        {
            // Pick any
            int port = ProductHandlerUtil.pickFreePort(0);
            assertTrue(16829 != port);
            assertTrue(port > 0);

            // Pick taken
            port = ProductHandlerUtil.pickFreePort(16829);
            assertTrue(16829 != port);
            assertTrue(port > 0);

            // Pick free
            assertEquals(16828, ProductHandlerUtil.pickFreePort(16828));
        }
    }

    @Test
    public void testPickFreePortWithBindAddress() throws Exception
    {
        // Even on IPv6-enabled systems, use the IPv4 loopback address
        InetAddress loopback = InetAddress.getByAddress("localhost", new byte[]{0x7f,0x00,0x00,0x01});

        InetAddress other = getRandomAddress();
        assumeFalse("Could not find a non-loopback address to test with", other == null);

        try (final ServerSocket ignored = new ServerSocket(16829, 1, loopback))
        {
            // Pick any on loopback
            int port = ProductHandlerUtil.pickFreePort(0, loopback);
            assertTrue(16829 != port);
            assertTrue(port > 0);

            // Pick taken
            port = ProductHandlerUtil.pickFreePort(16829, loopback);
            assertTrue(16829 != port);
            assertTrue(port > 0);

            // Pick taken, but on a different interface
            assertEquals(16829, ProductHandlerUtil.pickFreePort(16829, other));

            // Pick free
            assertEquals(16828, ProductHandlerUtil.pickFreePort(16828, loopback));
        }
    }

    @Test
    public void toArtifactsShouldReturnEmptyArrayForBlankString() throws Exception
    {
        assertThat(toArtifacts(null).size(), equalTo(0));
        assertThat(toArtifacts(" ").size(), equalTo(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void toArtifactsThrowExceptionForIncompleteArtifactPattern()
    {
        toArtifacts("groupId:");
    }

    @Test(expected = IllegalArgumentException.class)
    public void toArtifactsThrowExceptionForInvalidArtifactPattern()
    {
        toArtifacts("groupId:artifactId:version:more");
    }

    @Test
    public void toArtifactsVersionDefaultToLatestIfMissing()
    {
        final ProductArtifact artifact = toArtifacts("groupId:artifactId").get(0);
        assertThat(artifact.getGroupId(), equalTo("groupId"));
        assertThat(artifact.getArtifactId(), equalTo("artifactId"));
        assertThat(artifact.getVersion(), equalTo("LATEST"));
    }

    @Test
    public void toArtifactsShouldReturnMultipleArtifactsFromString()
    {
        final List<ProductArtifact> productArtifacts = toArtifacts("group1:artifact1:11,group2:artifact2:22,group3:artifact3:33");
        assertThat(productArtifacts.size(), equalTo(3));

        assertThat(productArtifacts.get(0).getGroupId(), equalTo("group1"));
        assertThat(productArtifacts.get(0).getArtifactId(), equalTo("artifact1"));
        assertThat(productArtifacts.get(0).getVersion(), equalTo("11"));

        assertThat(productArtifacts.get(1).getGroupId(), equalTo("group2"));
        assertThat(productArtifacts.get(1).getArtifactId(), equalTo("artifact2"));
        assertThat(productArtifacts.get(1).getVersion(), equalTo("22"));

        assertThat(productArtifacts.get(2).getGroupId(), equalTo("group3"));
        assertThat(productArtifacts.get(2).getArtifactId(), equalTo("artifact3"));
        assertThat(productArtifacts.get(2).getVersion(), equalTo("33"));
    }

    /**
     * Just to make helen happy
     */
    @Test
    public void toArtifactsShouldReturnMultipleArtifactsFromStringWithSpacePadding()
    {
        final List<ProductArtifact> productArtifacts = toArtifacts("group1:artifact1:11, group2:artifact2:22 , group3 : artifact3: 33");
        assertThat(productArtifacts.size(), equalTo(3));

        assertThat(productArtifacts.get(0).getGroupId(), equalTo("group1"));
        assertThat(productArtifacts.get(0).getArtifactId(), equalTo("artifact1"));
        assertThat(productArtifacts.get(0).getVersion(), equalTo("11"));

        assertThat(productArtifacts.get(1).getGroupId(), equalTo("group2"));
        assertThat(productArtifacts.get(1).getArtifactId(), equalTo("artifact2"));
        assertThat(productArtifacts.get(1).getVersion(), equalTo("22"));

        assertThat(productArtifacts.get(2).getGroupId(), equalTo("group3"));
        assertThat(productArtifacts.get(2).getArtifactId(), equalTo("artifact3"));
        assertThat(productArtifacts.get(2).getVersion(), equalTo("33"));
    }

    /**
     * Loops over the available network interfaces searching for a non-loopback IPv4 address, and returns the first
     * one found.
     *
     * @return the first non-loopback IPv4 address found, or {@code null} if no such address can be found
     * @throws SocketException if enumerating network interfaces fails
     */
    private static InetAddress getRandomAddress() throws SocketException
    {
        for (final NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces()))
        {
            for (final InetAddress address : Collections.list(iface.getInetAddresses()))
            {
                if (address instanceof Inet4Address && !address.isLoopbackAddress())
                {
                    return address;
                }
            }
        }

        return null;
    }
}