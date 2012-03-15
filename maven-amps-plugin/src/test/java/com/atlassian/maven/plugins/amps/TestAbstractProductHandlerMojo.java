package com.atlassian.maven.plugins.amps;

import org.apache.maven.model.Build;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.Parameter;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.atlassian.maven.plugins.amps.util.MavenPropertiesUtils;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.matchers.JUnitMatchers.containsString;

public class TestAbstractProductHandlerMojo
{
    @Test
    public void testMakeProductsInheritDefaultConfiguration() throws Exception
    {
        SomeMojo mojo = new SomeMojo("foo");

        Product fooProd = new Product();
        fooProd.setInstanceId("foo");
        fooProd.setVersion("1.0");

        Product barProd = new Product();
        barProd.setInstanceId("bar");
        barProd.setVersion("2.0");

        Map<String,Product> prodMap = new HashMap<String, Product>();
        mojo.makeProductsInheritDefaultConfiguration(asList(fooProd, barProd), prodMap);
        assertEquals(2, prodMap.size());
        assertEquals("1.0", prodMap.get("foo").getVersion());
        assertEquals("/foo", prodMap.get("foo").getContextPath());
        assertEquals("2.0", prodMap.get("bar").getVersion());
        assertEquals("/foo", prodMap.get("bar").getContextPath());
    }

    @Test
    public void testMakeProductsInheritDefaultConfigurationDifferentInstanceIds() throws Exception
    {
        SomeMojo mojo = new SomeMojo("baz");

        Product fooProd = new Product();
        fooProd.setInstanceId("foo");
        fooProd.setVersion("1.0");

        Product barProd = new Product();
        barProd.setInstanceId("bar");
        barProd.setVersion("2.0");

        Map<String,Product> prodMap = new HashMap<String, Product>();
        mojo.makeProductsInheritDefaultConfiguration(asList(fooProd, barProd), prodMap);
        assertEquals(3, prodMap.size());
        assertEquals("1.0", prodMap.get("foo").getVersion());
        assertEquals("/foo", prodMap.get("foo").getContextPath());
        assertEquals("2.0", prodMap.get("bar").getVersion());
        assertEquals("/foo", prodMap.get("bar").getContextPath());
        assertEquals(null, prodMap.get("baz").getVersion());
        assertEquals("/foo", prodMap.get("baz").getContextPath());
    }

    @Test
    public void testMakeProductsInheritDefaultConfigurationNoProducts() throws Exception
    {
        SomeMojo mojo = new SomeMojo("foo");

        Map<String,Product> prodMap = new HashMap<String, Product>();
        mojo.makeProductsInheritDefaultConfiguration(Collections.<Product>emptyList(), prodMap);
        assertEquals(1, prodMap.size());
        assertEquals("/foo", prodMap.get("foo").getContextPath());
    }

    @Test
    public void testUnusedConfigurationIsWarned() throws MojoExecutionException
    {
        Xpp3Dom configuration = new Xpp3Dom("configuration");
        configuration.addChild(new Xpp3Dom("version"));

        SomeMojo mojo = new SomeMojo("refapp", Collections.<Product>emptyList(), configuration);
        PluginDescriptor pluginDescriptors = new PluginDescriptor();
        pluginDescriptors.setComponents(Lists.newArrayList());
        
        Log log = Mockito.mock(Log.class);
        mojo.setLog(log);
        
        MavenPropertiesUtils.checkUnusedConfiguration(mojo, mojo.getMavenContext());
        
        Mockito.verify(log).warn(Matchers.contains("Unused element in <configuration>: <version/>"));
    }


    @Test
    public void testInexistingMojoThrowsException() throws MojoExecutionException
    {
        SomeMojo mojo = new SomeMojo("refapp", Collections.<Product>emptyList(), null);
        PluginDescriptor pluginDescriptors = new PluginDescriptor();
        pluginDescriptors.setComponents(Lists.newArrayList());

        try
        {
            MavenPropertiesUtils.checkUsingTheRightAmpsPlugin(mojo.getMavenContext());
            fail("Shouldn't succeed");
        }
        catch (MojoExecutionException mee)
        {
            assertThat(mee.getMessage(), containsString("You are using amps:some but maven-jira-plugin is defined in the pom.xml"));
        }
    }

    public static class SomeMojo extends AbstractProductHandlerMojo
    {
        private final String defaultProductId;

        private Xpp3Dom configuration;
        
        public SomeMojo(String defaultProductId, List<Product> products, Xpp3Dom configuration)
        {
            this.defaultProductId = defaultProductId;
            contextPath = "/foo";
            this.products = products;
            this.configuration = configuration;
        }

        public SomeMojo(String defaultProductId, List<Product> products)
        {
            this.defaultProductId = defaultProductId;
            contextPath = "/foo";
            this.products = products;
            this.configuration = null;
        }

        public SomeMojo(String defaultProductId)
        {
            this(defaultProductId, Collections.<Product>emptyList());
        }

        @Override
        protected String getDefaultProductId() throws MojoExecutionException
        {
            return defaultProductId;
        }

        @Override
        protected void doExecute() throws MojoExecutionException, MojoFailureException
        {

        }

        @Override
        protected MavenContext getMavenContext()
        {
            MavenProject project = mock(MavenProject.class);
            Build build = mock(Build.class);
            
            // Mock the directories
            when(build.getTestOutputDirectory()).thenReturn(".");
            when(project.getBuild()).thenReturn(build);
            when(project.getBasedir()).thenReturn(new File("."));
            
            // Create the mojo descriptor
            PluginDescriptor pluginDescriptor = new PluginDescriptor();
            pluginDescriptor.setGoalPrefix("amps");
            MojoDescriptor mojoDescriptor = new MojoDescriptor();
            mojoDescriptor.setPluginDescriptor(pluginDescriptor);
            mojoDescriptor.setGoal("some");
            Parameter parameter = new Parameter();
            parameter.setName("exampleOfParameter");
            try
            {
                pluginDescriptor.addMojo(mojoDescriptor);
                mojoDescriptor.addParameter(parameter);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
            
            
            MojoExecution mojoExecution = new MojoExecution(mojoDescriptor);            
            when(project.getGoalConfiguration(anyString(), anyString(), anyString(), anyString())).thenReturn(configuration);
            
            // LIst of build plugins defined in the pom.xml
            Plugin plugin = new Plugin();
            plugin.setArtifactId("maven-jira-plugin");
            plugin.setGroupId("com.atlassian.maven.plugins");
            when(project.getBuildPlugins()).thenReturn(Lists.newArrayList(plugin));
            
            return new MavenContext(project, null, null, mojoExecution, (PluginManager) null, null);
        }

        @Override
        protected PluginInformation getPluginInformation() {
            return new PluginInformation("test-product", "Test SDK Version");
        }
    }
}