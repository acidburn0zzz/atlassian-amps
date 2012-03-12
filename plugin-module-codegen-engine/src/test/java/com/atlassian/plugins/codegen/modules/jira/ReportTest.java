package com.atlassian.plugins.codegen.modules.jira;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;
import com.atlassian.plugins.codegen.modules.common.Resource;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @since 3.6
 */
public class ReportTest extends AbstractModuleCreatorTestCase<ReportProperties>
{
    public ReportTest()
    {
        super("report", new ReportModuleCreator());
    }
    
    @Before
    public void setupProps() throws Exception
    {
        setProps(new ReportProperties(PACKAGE_NAME + ".MyReport"));
        props.setIncludeExamples(false);
    }

    @Test
    public void classFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "MyReport");
    }

    @Test
    public void unitTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(PACKAGE_NAME, "MyReportTest");
    }
    
    @Test
    public void moduleHasDefaultKey() throws Exception
    {
        assertEquals("my-report",
                     getGeneratedModule().attributeValue("key"));
    }
    
    @Test
    public void moduleHasClass() throws Exception
    {
        assertEquals(PACKAGE_NAME + ".MyReport",
                     getGeneratedModule().attributeValue("class"));
    }

    @Test
    public void i18nResourceIsGenerated() throws Exception
    {
        Resource resource = new Resource();
        resource.setName("i18n");
        resource.setLocation("MyReport");
        resource.setType("i18n");

        props.getResources().add(resource);

        getResourceFile("", "MyReport.properties");
    }

    @Test
    public void labelIsAdded() throws Exception
    {
        props.setLabel(label);

        assertNotNull(getGeneratedModule().selectSingleNode("label"));
    }
    
    @Test
    public void labelHasI18nKey() throws Exception
    {
        props.setLabel(label);
        
        assertEquals(label.getKey(), getGeneratedModule().selectSingleNode("label/@key").getText());
    }

    @Test
    public void labelHasParams() throws Exception
    {
        props.setLabel(label);
        
        assertEquals(2, getGeneratedModule().selectNodes("label/param").size());
    }

    @Test
    public void labelParam0HasName() throws Exception
    {
        props.setLabel(label);
        
        assertEquals("param0", getGeneratedModule().selectSingleNode("label/param[1]/@name").getText());
    }

    @Test
    public void labelParam0HasValue() throws Exception
    {
        props.setLabel(label);
        
        assertEquals(label.getParams().get("param0"), getGeneratedModule().selectSingleNode("label/param[1]/@value").getText());
    }

    @Test
    public void labelParam1HasName() throws Exception
    {
        props.setLabel(label);
        
        assertEquals("param1", getGeneratedModule().selectSingleNode("label/param[2]/@name").getText());
    }

    @Test
    public void labelParam1HasValue() throws Exception
    {
        props.setLabel(label);
        
        assertEquals(label.getParams().get("param1"), getGeneratedModule().selectSingleNode("label/param[2]/@value").getText());
    }

    @Test
    public void labelStringIsAddedToI18nProperties() throws Exception
    {
        props.setLabel(label);
        
        assertEquals(label.getValue(), getChangesetForModule().getI18nProperties().get(label.getKey()));
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
