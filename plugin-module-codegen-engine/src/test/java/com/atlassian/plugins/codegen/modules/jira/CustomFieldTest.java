package com.atlassian.plugins.codegen.modules.jira;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;
import com.atlassian.plugins.codegen.modules.common.Resource;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @since 3.6
 */
public class CustomFieldTest extends AbstractModuleCreatorTestCase<CustomFieldProperties>
{
    public CustomFieldTest()
    {
        super("customfield-type", new CustomFieldModuleCreator());
    }
    
    @Before
    public void setupProps() throws Exception
    {
        setProps(new CustomFieldProperties(PACKAGE_NAME + ".MyCustomField"));
        props.setIncludeExamples(false);
    }

    @Test
    public void classFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "MyCustomField");
    }

    @Test
    public void unitTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(TEST_PACKAGE_NAME, "MyCustomFieldTest");
    }
    
    @Test
    public void moduleHasDefaultKey() throws Exception
    {
        assertEquals("my-custom-field",
                     getGeneratedModule().attributeValue("key"));
    }
    
    @Test
    public void moduleHasClass() throws Exception
    {
        assertEquals(PACKAGE_NAME + ".MyCustomField",
                     getGeneratedModule().attributeValue("class"));
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
}
