package com.atlassian.plugins.codegen.modules.common;

import com.atlassian.plugins.codegen.AbstractCodegenTestCase;

import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @since 3.6
 */
public class DownloadablePluginResourceTest extends AbstractCodegenTestCase<DownloadablePluginResourceProperties>
{
    protected Resource cssResource;
    protected Resource cssNamePatternResource;
    
    @Before
    public void setup()
    {
        setCreator(new DownloadablePluginResourceModuleCreator());
        
        cssResource = new Resource();
        cssResource.setName("style.css");
        cssResource.setLocation("com/example/plugin/style.css");
        cssResource.setType("download");

        cssNamePatternResource = new Resource();
        cssNamePatternResource.setNamePattern("*.css");
        cssNamePatternResource.setLocation("com/example/plugin");
        cssNamePatternResource.setType("download");
        
        setProps(new DownloadablePluginResourceProperties(cssResource));
    }
    
    @Test
    public void resourceIsAdded() throws Exception
    {
        getGeneratedModule();
    }
    
    @Test
    public void resourceHasName() throws Exception
    {
        assertEquals("style.css", getGeneratedModule().attributeValue("name"));
    }

    @Test
    public void resourceHasLocation() throws Exception
    {
        assertEquals("com/example/plugin/style.css", getGeneratedModule().attributeValue("location"));
    }
    
    @Test
    public void resourceHasType() throws Exception
    {
        assertEquals("download", getGeneratedModule().attributeValue("type"));
    }

    @Test
    public void namePatternResourceAdded() throws Exception
    {
        setProps(new DownloadablePluginResourceProperties(cssNamePatternResource));

        getGeneratedModule();
    }
    
    @Test
    public void namePatternResourceHasNamePattern() throws Exception
    {
        setProps(new DownloadablePluginResourceProperties(cssNamePatternResource));
        
        assertEquals("*.css", getGeneratedModule().attributeValue("namePattern"));
    }

    @Test
    public void namePatternResourceHasLocation() throws Exception
    {
        setProps(new DownloadablePluginResourceProperties(cssNamePatternResource));
        
        assertEquals("com/example/plugin", getGeneratedModule().attributeValue("location"));
    }

    @Test
    public void namePatternResourceHasType() throws Exception
    {
        setProps(new DownloadablePluginResourceProperties(cssNamePatternResource));

        assertEquals("download", getGeneratedModule().attributeValue("type"));
    }

    @Test
    public void resourceNameChosenOverPattern() throws Exception
    {
        cssResource.setNamePattern("*.css");

        assertNull(getGeneratedModule().attributeValue("namePattern"));
    }

    @Test
    public void resourceParamHasName() throws Exception
    {
        cssResource.getParams().put("content-type", "text/css");

        assertEquals("content-type", getGeneratedModule().selectSingleNode("param/@name").getText());
    }

    @Test
    public void resourceParamHasValue() throws Exception
    {
        cssResource.getParams().put("content-type", "text/css");

        assertEquals("text/css", getGeneratedModule().selectSingleNode("param/@value").getText());
    }
    
    @Test
    public void secondResourceParamAdded() throws Exception
    {
        cssResource.getParams().put("content-type", "text/css");
        cssResource.getParams().put("awesome", "me");
        
        assertEquals("me", getGeneratedModule().selectSingleNode("param[@name='awesome']/@value").getText());
    }

    @Test
    public void allResourceParamsAdded() throws Exception
    {
        cssResource.getParams().put("content-type", "text/css");
        cssResource.getParams().put("awesome", "me");
        
        assertEquals(2, getGeneratedModule().selectNodes("param").size());
    }
    
    protected Element getGeneratedModule() throws Exception
    {
        return getGeneratedModule("resource");
    }
}
