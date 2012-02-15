package com.atlassian.plugins.codegen.modules.common.web;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @since 3.6
 */
public class WebPanelTest extends AbstractWebFragmentTest<WebPanelProperties>
{
    public static final String CUSTOM_LOCATION = "system.admin/mysection";

    public WebPanelTest()
    {
        super("web-panel", new WebPanelModuleCreator());
    }
    
    @Before
    public void setupProps() throws Exception
    {
        setProps(new WebPanelProperties(MODULE_NAME, CUSTOM_LOCATION));
        props.setIncludeExamples(false);
    }

    @Test
    public void moduleHasDefaultKey() throws Exception
    {
        assertEquals(MODULE_KEY, getGeneratedModule().attributeValue("key"));
    }

    @Test
    public void moduleHasLocation() throws Exception
    {
        assertEquals(CUSTOM_LOCATION, getGeneratedModule().attributeValue("location"));
    }
    
    @Test
    public void moduleHasDefaultWeight() throws Exception
    {
        assertEquals("1000", getGeneratedModule().attributeValue("weight"));
    }

    @Test
    public void moduleHasSpecifiedWeight() throws Exception
    {
        props.setWeight(20);

        assertEquals("20", getGeneratedModule().attributeValue("weight"));
    }
}
