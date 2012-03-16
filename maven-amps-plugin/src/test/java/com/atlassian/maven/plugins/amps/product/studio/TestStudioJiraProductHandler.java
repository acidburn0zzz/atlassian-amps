package com.atlassian.maven.plugins.amps.product.studio;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import org.junit.Test;
import org.mockito.Mockito;

public class TestStudioJiraProductHandler
{
    @Test
    public void testFixMemorySettingsWithEmptyArgs() throws Exception
    {
        Product product = new Product();
        StudioJiraProductHandler handler = new StudioJiraProductHandler(Mockito.mock(MavenContext.class), Mockito.mock(MavenGoals.class));

        product.setJvmArgs(null);
        handler.fixJvmArgs(product);

        org.junit.Assert.assertEquals("-Xms256m -Xmx768m -XX:MaxPermSize=512m", product.getJvmArgs());
    }

    @Test
    public void testFixMemorySettingsWithMs() throws Exception
    {
        Product product = new Product();
        StudioJiraProductHandler handler = new StudioJiraProductHandler(Mockito.mock(MavenContext.class), Mockito.mock(MavenGoals.class));

        product.setJvmArgs("-Xms1024m");
        handler.fixJvmArgs(product);

        org.junit.Assert.assertEquals("-Xms1024m -Xmx768m -XX:MaxPermSize=512m", product.getJvmArgs());
    }

    @Test
    public void testFixMemorySettingsWithMx() throws Exception
    {
        Product product = new Product();
        StudioJiraProductHandler handler = new StudioJiraProductHandler(Mockito.mock(MavenContext.class), Mockito.mock(MavenGoals.class));

        product.setJvmArgs("-Xmx1024m");
        handler.fixJvmArgs(product);

        org.junit.Assert.assertEquals("-Xmx1024m -Xms256m -XX:MaxPermSize=512m", product.getJvmArgs());
    }

    @Test
    public void testFixMemorySettingsWithPermgenMxAndOther() throws Exception
    {
        Product product = new Product();
        StudioJiraProductHandler handler = new StudioJiraProductHandler(Mockito.mock(MavenContext.class), Mockito.mock(MavenGoals.class));

        product.setJvmArgs("-XX:MaxPermSize=512m -Xmx1024m -Dother=val");
        handler.fixJvmArgs(product);

        org.junit.Assert.assertEquals("-XX:MaxPermSize=512m -Xmx1024m -Dother=val -Xms256m", product.getJvmArgs());
    }
}
