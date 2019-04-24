package com.atlassian.maven.plugins.amps.product;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class TestProductContainerVersionMapper
{
    @Test
    public void testBambooVersionMapping()
    {
        assertVersion("bamboo", "1", "tomcat6x");
        assertVersion("bamboo", "5.1", "tomcat7x");
        assertVersion("bamboo", "5.10.0", "tomcat8x");
    }

    @Test
    public void testConfluenceVersionMapping()
    {
        assertVersion("confluence", "1", "tomcat6x");
        assertVersion("confluence", "5.5", "tomcat7x");
        assertVersion("confluence", "5.8.0", "tomcat8x");
        assertVersion("confluence", "6.10.0", "tomcat9x");
    }

    @Test
    public void testCrowdVersionMapping()
    {
        assertThat(ProductContainerVersionMapper.containerForProductVersion("crowd", "1"), is("tomcat6x"));
        assertThat(ProductContainerVersionMapper.containerForProductVersion("crowd", "2.7"), is("tomcat7x"));
        assertThat(ProductContainerVersionMapper.containerForProductVersion("crowd", "3.1.0"), is("tomcat85x"));
    }

    @Test
    public void testJiraVersionMapping()
    {
        assertThat(ProductContainerVersionMapper.containerForProductVersion("jira", "1"), is("tomcat6x"));
        assertThat(ProductContainerVersionMapper.containerForProductVersion("jira", "5.2"), is("tomcat7x"));
        assertThat(ProductContainerVersionMapper.containerForProductVersion("jira", "7.0.0"), is("tomcat8x"));
        assertThat(ProductContainerVersionMapper.containerForProductVersion("jira", "7.2.9"), is("tomcat8x"));
        assertThat(ProductContainerVersionMapper.containerForProductVersion("jira", "7.3.0"), is("tomcat85_6"));
        assertThat(ProductContainerVersionMapper.containerForProductVersion("jira", "7.6.0"), is("tomcat85_29"));
        assertThat(ProductContainerVersionMapper.containerForProductVersion("jira", "7.12.3"), is("tomcat85_32"));
        assertThat(ProductContainerVersionMapper.containerForProductVersion("jira", "7.13.2"), is("tomcat85_35"));
        assertThat(ProductContainerVersionMapper.containerForProductVersion("jira", "8.1.2"), is("tomcat85x"));
    }

    @Test
    public void testBitbucketVersionMapping()
    {
        assertThat(ProductContainerVersionMapper.containerForProductVersion("bitbucket", "1"), is("tomcat8x"));
        assertThat(ProductContainerVersionMapper.containerForProductVersion("bitbucket", "4.0.0"), is("tomcat8x"));
        assertThat(ProductContainerVersionMapper.containerForProductVersion("bitbucket", "5.11.0"), is("tomcat8x"));
    }

    @Test
    public void testSpecialVersions()
    {
        assertThat(ProductContainerVersionMapper.containerForProductVersion("jira", "7.0.0-SNAPSHOT"), is("tomcat7x"));
        assertThat(ProductContainerVersionMapper.containerForProductVersion("jira", "7.0-rc1"), is("tomcat7x"));
        assertThat(ProductContainerVersionMapper.containerForProductVersion("jira", "7.0-beta2"), is("tomcat7x"));
        assertThat(ProductContainerVersionMapper.containerForProductVersion("jira", "7.0-m01"), is("tomcat7x"));
        assertThat(ProductContainerVersionMapper.containerForProductVersion("jira", "7.6.7-QR-20180626090405"), is("tomcat85_29"));
    }

    @Test
    public void testMissingApp()
    {
        assertThat(ProductContainerVersionMapper.containerForProductVersion("elephant", "7.0.0-SNAPSHOT"), is("tomcat85x"));
    }

    @Test
    public void testODJiraVersions()
    {
        assertThat(ProductContainerVersionMapper.containerForProductVersion("jira", "7.0.0-OD-02-004"), is("tomcat8x"));
    }


    @Test
    public void testQRJiraVersions()
    {
        assertThat(ProductContainerVersionMapper.containerForProductVersion("jira", "7.0.0-QR20150831134256"), is("tomcat8x"));
    }

    private void assertVersion(String product, String version, String container)
    {
        assertThat(ProductContainerVersionMapper.containerForProductVersion(product, version), is(container));
    }
}
