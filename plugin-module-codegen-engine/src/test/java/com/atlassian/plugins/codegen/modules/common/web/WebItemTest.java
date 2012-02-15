package com.atlassian.plugins.codegen.modules.common.web;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @since 3.6
 */
public class WebItemTest extends AbstractWebFragmentTest<WebItemProperties>
{
    public static final String GLOBAL_SETTINGS_SECTION = "system.admin/globalsettings";

    public WebItemTest()
    {
        super("web-item", new WebItemModuleCreator());
    }
    
    @Before
    public void setupProps() throws Exception
    {
        setProps(new WebItemProperties(MODULE_NAME, GLOBAL_SETTINGS_SECTION));
        props.setIncludeExamples(false);
    }

    @Test
    public void moduleHasDefaultKey() throws Exception
    {
        assertEquals(MODULE_KEY, getGeneratedModule().attributeValue("key"));
    }
    
    @Test
    public void moduleHasSection() throws Exception
    {
        assertEquals(GLOBAL_SETTINGS_SECTION, getGeneratedModule().attributeValue("section"));
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
    public void iconIsAdded() throws Exception
    {
        props.setIcon(icon);

        assertNotNull(getGeneratedModule().selectSingleNode("icon"));
    }

    @Test
    public void iconHasLink() throws Exception
    {
        props.setIcon(icon);

        assertEquals(icon.getLink().getValue(), getGeneratedModule().selectSingleNode("icon/link").getText());
    }
    
    @Test
    public void iconHasWidth() throws Exception
    {
        props.setIcon(icon);

        assertEquals(String.valueOf(icon.getWidth()), getGeneratedModule().selectSingleNode("icon/@width").getText());
    }

    @Test
    public void iconHasHeight() throws Exception
    {
        props.setIcon(icon);

        assertEquals(String.valueOf(icon.getHeight()), getGeneratedModule().selectSingleNode("icon/@height").getText());
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
    public void linkIsAdded() throws Exception
    {
        props.setLink(link);

        assertNotNull(getGeneratedModule().selectSingleNode("link"));
    }
    
    @Test
    public void linkHasId() throws Exception
    {
        props.setLink(link);
        
        assertEquals(link.getLinkId(), getGeneratedModule().selectSingleNode("link/@linkId").getText());
    }

    @Test
    public void linkHasLink() throws Exception
    {
        props.setLink(link);
        
        assertEquals(link.getValue(), getGeneratedModule().selectSingleNode("link").getText());
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

        assertEquals("true", getGeneratedModule().selectSingleNode("param[@name='isPopupLink']/@value").getText());
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
        props.addParam("isPopupLink", "true");
        props.addParam("isSuperAwesome", "false");
    }
}
