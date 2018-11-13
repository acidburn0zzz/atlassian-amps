package com.atlassian.plugins.codegen.modules.jira;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;
import com.atlassian.plugins.codegen.SourceFile;
import com.atlassian.plugins.codegen.modules.common.Resource;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @since 3.6
 */
public class CustomFieldSearcherTest extends AbstractModuleCreatorTestCase<CustomFieldSearcherProperties>
{
    
    public CustomFieldSearcherTest()
    {
        super("customfield-searcher", new CustomFieldSearcherModuleCreator());
    }

    @Before
    public void setupProps() throws Exception
    {
        setProps(new CustomFieldSearcherProperties(PACKAGE_NAME + ".MyCustomFieldSearcher"));
        props.setIncludeExamples(false);
    }

    @Test
    public void customClassFileIsGenerated() throws Exception
    {
        props.setGenerateClass(true);
        
        getSourceFile(PACKAGE_NAME, "MyCustomFieldSearcher");
    }

    @Test
    public void customUnitTestFileIsGenerated() throws Exception
    {
        props.setGenerateClass(true);
        
        getTestSourceFile(TEST_PACKAGE_NAME, "MyCustomFieldSearcherTest");
    }

    @Test
    public void genericClassFilesAreNotGenerated() throws Exception
    {
        props.setGenerateClass(false);
        
        assertTrue(getChangesetForModule(SourceFile.class).isEmpty());
    }
    
    @Test
    public void moduleHasDefaultKey() throws Exception
    {
        assertEquals("my-custom-field-searcher",
                     getGeneratedModule().attributeValue("key"));
    }
    
    @Test
    public void moduleHasClass() throws Exception
    {
        assertEquals(PACKAGE_NAME + ".MyCustomFieldSearcher",
                     getGeneratedModule().attributeValue("class"));
    }

    @Test
    public void moduleHasValidCustomFieldPackage() throws Exception
    {
        props.setValidCustomFieldPackage("com.atlassian.customfields");
        props.setValidCustomFieldKey("some-searcher");

        assertEquals("com.atlassian.customfields", getGeneratedModule().selectSingleNode("valid-customfield-type/@package").getText());
    }

    @Test
    public void moduleHasValidCustomFieldKey() throws Exception
    {
        props.setValidCustomFieldPackage("com.atlassian.customfields");
        props.setValidCustomFieldKey("some-searcher");

        assertEquals("some-searcher", getGeneratedModule().selectSingleNode("valid-customfield-type/@key").getText());
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
