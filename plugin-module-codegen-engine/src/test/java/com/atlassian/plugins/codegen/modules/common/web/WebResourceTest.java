package com.atlassian.plugins.codegen.modules.common.web;

import com.atlassian.plugins.codegen.modules.common.AbstractConditionTest;
import com.atlassian.plugins.codegen.modules.common.Resource;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @since 3.6
 */
public class WebResourceTest extends AbstractConditionTest<WebResourceProperties>
{
    public static final String MODULE_NAME = "Awesome Web Resource";
    public static final String XPATH_RESOURCE = "/atlassian-plugin/web-resource/resource";
    public static final String DEPENDENCY1 = "jira.web.resources:ajs";
    public static final String DEPENDENCY2 = "jira.web.resources:jquery";
    public static final String CONTEXT1 = "atl.general";
    public static final String CONTEXT2 = "atl.userprofile";
    
    public WebResourceTest()
    {
        super("web-resource", new WebResourceModuleCreator());
    }
    
    @Before
    public void setupProps() throws Exception
    {
        setProps(new WebResourceProperties(MODULE_NAME));
        props.setIncludeExamples(false);
    }

    @Test
    public void moduleHasDefaultKey() throws Exception
    {
        assertEquals("awesome-web-resource", getGeneratedModule().attributeValue("key"));
    }
    
    @Test
    public void dependenciesAreAdded() throws Exception
    {
        createDependencies();
        
        assertEquals(2, getGeneratedModule().selectNodes("//dependency").size());
    }

    @Test
    public void dependency1HasValue() throws Exception
    {
        createDependencies();
        
        assertNotNull(getGeneratedModule().selectSingleNode("//dependency[text() = '" + DEPENDENCY1 + "'"));
    }

    @Test
    public void dependency2HasValue() throws Exception
    {
        createDependencies();
        
        assertNotNull(getGeneratedModule().selectSingleNode("//dependency[text() = '" + DEPENDENCY2 + "'"));
    }

    @Test
    public void contextsAreAdded() throws Exception
    {
        createContexts();

        assertEquals(2, getGeneratedModule().selectNodes("//context").size());
    }

    @Test
    public void context1HasValue() throws Exception
    {
        createContexts();
        
        assertNotNull(getGeneratedModule().selectSingleNode("//context[text() = '" + CONTEXT1 + "'"));
    }

    @Test
    public void context2HasValue() throws Exception
    {
        createContexts();
        
        assertNotNull(getGeneratedModule().selectSingleNode("//context[text() = '" + CONTEXT2 + "'"));
    }

    @Test
    public void transformationWithSingleTransformerAdded() throws Exception
    {
        createSingleTransformer();

        assertNotNull(getGeneratedModule().selectSingleNode("//transformation"));
    }

    @Test
    public void transformationWithSingleTransformerHasTransformer() throws Exception
    {
        createSingleTransformer();

        assertNotNull(getGeneratedModule().selectSingleNode("//transformation/transformer"));
    }

    @Test
    public void transformationWithSingleTransformerHasExtension() throws Exception
    {
        createSingleTransformer();

        assertEquals("txt", getGeneratedModule().selectSingleNode("//transformation/@extension").getText());
    }

    @Test
    public void transformationWithSingleTransformerHasTransformerKey() throws Exception
    {
        createSingleTransformer();

        assertEquals("template", getGeneratedModule().selectSingleNode("//transformation/transformer/@key").getText());
    }

    @Test
    public void singleTransformationWithMultipleTransformersAdded() throws Exception
    {
        createMultipleTransformers();

        assertEquals(2, getGeneratedModule().selectNodes("//transformation/transformer").size());
    }

    @Test
    public void singleTransformationWithMultipleTransformersHasTransformer1Key() throws Exception
    {
        createMultipleTransformers();

        assertEquals("template", getGeneratedModule().selectSingleNode("//transformation/transformer[1]/@key").getText());
    }

    @Test
    public void singleTransformationWithMultipleTransformersHasTransformer2Key() throws Exception
    {
        createMultipleTransformers();

        assertEquals("prefix", getGeneratedModule().selectSingleNode("//transformation/transformer[2]/@key").getText());
    }

    @Test
    public void multipleTransformationsAdded() throws Exception
    {
        createMultipleTransformations();

        assertEquals(2, getGeneratedModule().selectNodes("//transformation").size());
    }

    @Test
    public void multipleTransformationsHasTransformation1Extension() throws Exception
    {
        createMultipleTransformations();
        
        assertEquals("txt", getGeneratedModule().selectSingleNode("//transformation[1]/@extension").getText());
    }

    @Test
    public void multipleTransformationsHasTransformation2Extension() throws Exception
    {
        createMultipleTransformations();
        
        assertEquals("css", getGeneratedModule().selectSingleNode("//transformation[2]/@extension").getText());
    }
    
    @Test
    public void multipleTransformationsHasTransformer1Key() throws Exception
    {
        createMultipleTransformations();
        
        assertEquals("template", getGeneratedModule().selectSingleNode("//transformation[1]/transformer/@key").getText());
    }

    @Test
    public void multipleTransformationsHasTransformer2Key() throws Exception
    {
        createMultipleTransformations();
        
        assertEquals("prefix", getGeneratedModule().selectSingleNode("//transformation[2]/transformer/@key").getText());
    }

    @Test
    public void resourceIsAdded() throws Exception
    {
        props.getResources().add(cssResource);
        
        assertNotNull(getGeneratedModule().selectSingleNode("resource"));
    }
    
    @Test
    public void resourceHasName() throws Exception
    {
        props.getResources().add(cssResource);
        
        assertEquals("style.css", getGeneratedModule().selectSingleNode("resource/@name").getText());
    }

    @Test
    public void resourceHasLocation() throws Exception
    {
        props.getResources().add(cssResource);

        assertEquals("com/example/plugin/style.css", getGeneratedModule().selectSingleNode("resource/@location").getText());
    }
    
    @Test
    public void resourceHasType() throws Exception
    {
        props.getResources().add(cssResource);

        assertEquals("download", getGeneratedModule().selectSingleNode("resource/@type").getText());
    }

    @Test
    public void namePatternResourceAdded() throws Exception
    {
        props.getResources().add(cssNamePatternResource);

        assertNotNull(getGeneratedModule().selectSingleNode("resource"));
    }
    
    @Test
    public void namePatternResourceHasNamePattern() throws Exception
    {
        props.getResources().add(cssNamePatternResource);
        
        assertEquals("*.css", getGeneratedModule().selectSingleNode("resource/@namePattern").getText());
    }

    @Test
    public void namePatternResourceHasLocation() throws Exception
    {
        props.getResources().add(cssNamePatternResource);
        
        assertEquals("com/example/plugin", getGeneratedModule().selectSingleNode("resource/@location").getText());
    }

    @Test
    public void namePatternResourceHasType() throws Exception
    {
        props.getResources().add(cssNamePatternResource);

        assertEquals("download", getGeneratedModule().selectSingleNode("resource/@type").getText());
    }

    @Test
    public void resourceNameChosenOverPattern() throws Exception
    {
        cssResource.setNamePattern("*.css");
        props.getResources().add(cssResource);

        assertNull(getGeneratedModule().selectSingleNode("resource/@namePattern"));
    }

    @Test
    public void resourceParamHasName() throws Exception
    {
        cssResource.getParams().put("content-type", "text/css");
        props.getResources().add(cssResource);

        assertEquals("content-type", getGeneratedModule().selectSingleNode("resource/param/@name").getText());
    }

    @Test
    public void resourceParamHasValue() throws Exception
    {
        cssResource.getParams().put("content-type", "text/css");
        props.getResources().add(cssResource);

        assertEquals("text/css", getGeneratedModule().selectSingleNode("resource/param/@value").getText());
    }
    
    @Test
    public void secondResourceParamAdded() throws Exception
    {
        cssResource.getParams().put("content-type", "text/css");
        cssResource.getParams().put("awesome", "me");
        props.getResources().add(cssResource);
        
        assertEquals("me", getGeneratedModule().selectSingleNode("resource/param[@name='awesome']/@value").getText());
    }

    @Test
    public void allResourceParamsAdded() throws Exception
    {
        cssResource.getParams().put("content-type", "text/css");
        cssResource.getParams().put("awesome", "me");
        props.getResources().add(cssResource);
        
        assertEquals(2, getGeneratedModule().selectNodes("resource/param").size());
    }

    @Test
    public void secondResourceAdded() throws Exception
    {
        Resource resource2 = new Resource();
        resource2.setName("custom.js");
        resource2.setLocation("com/example/plugin/custom.js");
        resource2.setType("download");

        props.getResources().add(cssResource);
        props.getResources().add(resource2);
        
        assertEquals("com/example/plugin/custom.js",
                     getGeneratedModule().selectSingleNode("resource[@name='custom.js']/@location").getText());
    }

    @Test
    public void allResourcesAdded() throws Exception
    {
        Resource resource2 = new Resource();
        resource2.setName("custom.js");
        resource2.setLocation("com/example/plugin/custom.js");
        resource2.setType("download");

        props.getResources().add(cssResource);
        props.getResources().add(resource2);
        
        assertEquals(2, getGeneratedModule().selectNodes("resource").size());
    }

    @Test
    public void nonBatchResourceAdded() throws Exception
    {
        cssResource.setBatch(false);
        
        props.getResources().add(cssResource);
        
        assertEquals("false", getGeneratedModule().selectSingleNode("//resource/param[@name='batch']/@value").getText());
    }

    protected void createDependencies()
    {
        props.getResources().add(cssResource);

        props.addDependency(DEPENDENCY1);
        props.addDependency(DEPENDENCY2);
    }
    
    protected void createContexts()
    {
        props.getResources().add(cssResource);

        props.addContext(CONTEXT1);
        props.addContext(CONTEXT2);
    }
    
    protected void createSingleTransformer()
    {
        props.getResources().add(cssResource);

        WebResourceTransformation transformation = new WebResourceTransformation("txt");
        addTransformerWithKey(transformation, "template");

        props.addTransformation(transformation);
    }

    protected void createMultipleTransformers()
    {
        props.getResources().add(cssResource);

        WebResourceTransformation transformation = new WebResourceTransformation("txt");
        addTransformerWithKey(transformation, "template");
        addTransformerWithKey(transformation, "prefix");

        props.addTransformation(transformation);
    }
    
    protected void createMultipleTransformations()
    {
        props.getResources().add(cssResource);

        WebResourceTransformation txtTrans = new WebResourceTransformation("txt");
        addTransformerWithKey(txtTrans, "template");

        WebResourceTransformation cssTrans = new WebResourceTransformation("css");
        addTransformerWithKey(cssTrans, "prefix");

        props.addTransformation(txtTrans);
        props.addTransformation(cssTrans);
    }

    private void addTransformerWithKey(WebResourceTransformation transformation, String transformerKey)
    {
        WebResourceTransformer transformer = new WebResourceTransformer();
        transformer.setModuleKey(transformerKey);
        transformation.addTransformer(transformer);
    }
}
