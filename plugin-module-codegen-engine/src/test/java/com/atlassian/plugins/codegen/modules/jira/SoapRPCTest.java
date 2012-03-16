package com.atlassian.plugins.codegen.modules.jira;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @since 3.8
 */
public class SoapRPCTest extends AbstractRPCTest
{
    public SoapRPCTest()
    {
        super("rpc-soap", true);
    }
    
    @Test
    public void moduleHasInterface() throws Exception
    {
        assertEquals(PACKAGE_NAME + ".MyEndpoint", getGeneratedModule().selectSingleNode("published-interface").getText());
    }
}
