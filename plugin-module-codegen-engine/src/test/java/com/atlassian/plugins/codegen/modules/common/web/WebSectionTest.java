package com.atlassian.plugins.codegen.modules.common.web;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @since 3.6
 */
public class WebSectionTest extends AbstractWebFragmentTest<WebSectionProperties>
{
    public static final String CUSTOM_LOCATION = "system.admin/mysection";

    public WebSectionTest()
    {
        super("web-section", new WebSectionModuleCreator());
    }
    
    @Before
    public void setupProps() throws Exception
    {
        setProps(new WebSectionProperties(MODULE_NAME, CUSTOM_LOCATION));
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
    public void paramsAreAdded() throws Exception
    {
        createParams();

        assertEquals(2, getGeneratedModule().selectNodes("param").size());
    }
    
    @Test
    public void param0HasValue() throws Exception
    {
        createParams();

        assertEquals("true", getGeneratedModule().selectSingleNode("param[@name='isAwesomeSection']/@value").getText());
    }

    @Test
    public void param1HasValue() throws Exception
    {
        createParams();

        assertEquals("false", getGeneratedModule().selectSingleNode("param[@name='isSuperAwesome']/@value").getText());
    }

    @Test
    public void tooltipIsAdded() throws Exception
    {
        props.setTooltip(tooltip);

        assertNotNull(getGeneratedModule().selectSingleNode("tooltip"));
    }
    
    @Test
    public void tooltipHasKey() throws Exception
    {
        props.setTooltip(tooltip);
        
        assertEquals(tooltip.getKey(), getGeneratedModule().selectSingleNode("tooltip/@key").getText());
    }
    
    @Test
    public void tooltipStringIsAddedToI18nProperties() throws Exception
    {
        props.setTooltip(tooltip);
        
        assertEquals(tooltip.getValue(), getChangesetForModule().getI18nProperties().get(tooltip.getKey()));
    }
    
    protected void createParams()
    {
        props.addParam("isAwesomeSection", "true");
        props.addParam("isSuperAwesome", "false");
    }
}
