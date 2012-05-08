package com.atlassian.maven.plugins.amps.product.studio;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.google.inject.internal.util.ImmutableMap;
import com.google.inject.internal.util.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.Map;

import static com.atlassian.maven.plugins.amps.product.ProductHandlerFactory.STUDIO;
import static com.atlassian.maven.plugins.amps.product.ProductHandlerFactory.STUDIO_BAMBOO;
import static com.atlassian.maven.plugins.amps.product.ProductHandlerFactory.STUDIO_CONFLUENCE;
import static com.atlassian.maven.plugins.amps.product.ProductHandlerFactory.STUDIO_CROWD;
import static com.atlassian.maven.plugins.amps.product.ProductHandlerFactory.STUDIO_FECRU;
import static com.atlassian.maven.plugins.amps.product.ProductHandlerFactory.STUDIO_JIRA;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test case for {@link com.atlassian.maven.plugins.amps.product.studio.StudioProductHandler}.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TestStudioProductHandler
{
    private static final String TEST_ONDEMAND_VERSION = "131";

    @Mock private MavenContext mockContext;

    @Mock private MavenProject mockProject;

    @Mock private Build mockBuild;

    @Mock private Log mockLog;

    @Mock private MavenGoals mockGoals;

    private File mockBuildDir;

    @Before
    public void initMocks()
    {
        mockBuildDir = new File(System.getProperty("java.io.tmpdir"), "TestStudioProductHandler");
        when(mockBuild.getDirectory()).thenReturn(mockBuildDir.getAbsolutePath());
        when(mockProject.getBuild()).thenReturn(mockBuild);
        when(mockContext.getProject()).thenReturn(mockProject);
        when(mockContext.getLog()).thenReturn(mockLog);
    }

    @After
    public void deleteMockBuildDir()
    {
        FileUtils.deleteQuietly(mockBuildDir);
    }

    @Ignore("AMPS-757 - Studio products are disabled")
    @Test
    public void configureProductsShouldSetCorrectJiraVersionFromOnDemandPom() throws MojoExecutionException
    {
        final Model model = new Model();
        model.addProperty("jira.version", "5.0");
        final TestedStudioProductHandler testedHandler = new TestedStudioProductHandler(mockContext, mockGoals, model);
        final Product jira = createProduct(STUDIO_JIRA, null);
        Map<String,Product> products = createProductsWith(STUDIO_JIRA, jira);
        initDefaultValues(products);
        testedHandler.configureStudioProducts(products);
        assertEquals("5.0", jira.getVersion());
    }

    @Ignore("AMPS-757 - Studio products are disabled")
    @Test
    public void configureProductsShouldSetCorrectConfluenceVersionFromOnDemandPom() throws MojoExecutionException
    {
        final Model model = new Model();
        model.addProperty("confluence.version", "4.2");
        final TestedStudioProductHandler testedHandler = new TestedStudioProductHandler(mockContext, mockGoals, model);
        final Product confluence = createProduct(STUDIO_CONFLUENCE, null);
        Map<String,Product> products = createProductsWith(STUDIO_CONFLUENCE, confluence);
        initDefaultValues(products);
        testedHandler.configureStudioProducts(products);
        assertEquals("4.2", confluence.getVersion());
    }

    @Ignore("AMPS-757 - Studio products are disabled")
    @Test
    public void configureProductsShouldSetOnDemandFecruVersion() throws MojoExecutionException
    {
        final Model model = new Model();
        model.addProperty("crucible.version", "2.8");
        final TestedStudioProductHandler testedHandler = new TestedStudioProductHandler(mockContext, mockGoals, model);
        final Product fecru = createProduct(STUDIO_FECRU, null);
        Map<String,Product> products = createProductsWith(STUDIO_FECRU, fecru);
        initDefaultValues(products);
        testedHandler.configureStudioProducts(products);
        assertEquals(TEST_ONDEMAND_VERSION, fecru.getVersion());
    }

    @Ignore("AMPS-757 - Studio products are disabled")
    @Test
    public void configureProductsShouldLogWarningIfJiraVersionNotPresentInOnDemandPom() throws MojoExecutionException
    {
        final Model model = new Model();
        model.addProperty("confluence.version", "4.2");
        final TestedStudioProductHandler testedHandler = new TestedStudioProductHandler(mockContext, mockGoals, model);
        testedHandler.configureStudioProducts(createDefaultStudioProducts());
        verify(mockLog).warn("Expected property 'jira.version' in the OnDemand fireball POM (version "
                        + TEST_ONDEMAND_VERSION + ") not found. OnDemand version will be used instead");
    }

    @Ignore("AMPS-757 - Studio products are disabled")
    @Test
    public void configureProductsShouldLogWarningIfConfluenceVersionNotPresentInOnDemandPom() throws MojoExecutionException
    {
        final Model model = new Model();
        model.addProperty("jira.version", "5.1");
        final TestedStudioProductHandler testedHandler = new TestedStudioProductHandler(mockContext, mockGoals, model);
        testedHandler.configureStudioProducts(createDefaultStudioProducts());
        verify(mockLog).warn("Expected property 'confluence.version' in the OnDemand fireball POM (version "
                        + TEST_ONDEMAND_VERSION + ") not found. OnDemand version will be used instead");
    }

    private void initDefaultValues(Map<String,Product> products)
    {
        for (Product product : products.values())
        {
            StudioProductHandler.setDefaultValues(mockContext, product);
        }
    }

    private Map<String,Product> createProductsWith(String productId, Product product)
    {
        final Map<String,Product> products = Maps.newHashMap();
        products.putAll(createDefaultStudioProducts());
        products.put(productId, product);
        return ImmutableMap.copyOf(products);
    }

    private Map<String,Product> createDefaultStudioProducts()
    {
        return ImmutableMap.<String,Product>builder()
            .put(STUDIO, createProduct(STUDIO, TEST_ONDEMAND_VERSION))
            .put(STUDIO_JIRA, createProduct(STUDIO_JIRA, "5.0"))
            .put(STUDIO_CROWD, createProduct(STUDIO_CROWD, TEST_ONDEMAND_VERSION))
            .put(STUDIO_CONFLUENCE, createProduct(STUDIO_CONFLUENCE, "4.1"))
            .put(STUDIO_BAMBOO, createProduct(STUDIO_BAMBOO, TEST_ONDEMAND_VERSION))
            .put(STUDIO_FECRU, createProduct(STUDIO_FECRU, TEST_ONDEMAND_VERSION))
            .build();
    }

    private Product createProduct(String id, String version)
    {
        final Product answer = new Product();
        answer.setId(id);
        answer.setInstanceId(id);
        answer.setVersion(version);
        return answer;
    }

    private static class TestedStudioProductHandler extends StudioProductHandler
    {
        private final Model onDemandModelMock;

        public TestedStudioProductHandler(MavenContext context, MavenGoals goals, Model onDemandModelMock)
        {
            super(context, goals);
            this.onDemandModelMock = onDemandModelMock;
        }

        @Override
        protected Model getOnDemandPomModel(Product ondemand, StudioProperties properties) throws MojoExecutionException
        {
            return onDemandModelMock;
        }
    }
}
