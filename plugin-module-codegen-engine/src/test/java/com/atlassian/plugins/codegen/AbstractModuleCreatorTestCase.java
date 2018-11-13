package com.atlassian.plugins.codegen;

import com.atlassian.plugins.codegen.modules.NameBasedModuleProperties;
import com.atlassian.plugins.codegen.modules.PluginModuleCreator;
import com.atlassian.plugins.codegen.modules.common.Icon;
import com.atlassian.plugins.codegen.modules.common.Label;
import com.atlassian.plugins.codegen.modules.common.Link;
import com.atlassian.plugins.codegen.modules.common.Resource;
import com.atlassian.plugins.codegen.modules.common.Tooltip;

import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public abstract class AbstractModuleCreatorTestCase<T extends NameBasedModuleProperties> extends AbstractCodegenTestCase<T>
{
    protected String moduleType;
    
    // useful objects, not used in all tests
    protected Resource cssResource;
    protected Resource cssNamePatternResource;
    protected Icon icon;
    protected Label label;
    protected Link link;
    protected Tooltip tooltip;
    
    public AbstractModuleCreatorTestCase(String moduleType, PluginModuleCreator<T> creator)
    {
        this.moduleType = moduleType;
        setCreator(creator);
    }

    @Before
    public final void setupUsefulObjects()
    {
        cssResource = new Resource();
        cssResource.setName("style.css");
        cssResource.setLocation("com/example/plugin/style.css");
        cssResource.setType("download");

        cssNamePatternResource = new Resource();
        cssNamePatternResource.setNamePattern("*.css");
        cssNamePatternResource.setLocation("com/example/plugin");
        cssNamePatternResource.setType("download");
        
        label = new Label("my.label.key", "this is a label");
        label.addParam("$helper.project.name");
        label.addParam("$helper.project.description");

        icon = new Icon(32, 16);
        icon.setLink(new Link("/images/myicon.png"));

        link = new Link("/secure/CreateIssue!default.jspa");
        link.setLinkId("create link");

        tooltip = new Tooltip("my.tooltip.key", "this is a tooltip");
    }
    
    @Test
    public void moduleIsCreated() throws Exception
    {
        getGeneratedModule();
    }
    
    @Test
    public void moduleHasSpecifiedKey() throws Exception
    {
        props.setModuleKey("newkey");
        assertEquals("newkey", getGeneratedModule().attributeValue("key"));
    }

    @Test
    public void moduleHasName() throws Exception
    {
        props.setModuleName("newname");
        assertEquals("newname", getGeneratedModule().attributeValue("name"));
    }

    @Test
    public void moduleHasNameI18nKey() throws Exception
    {
        props.setNameI18nKey("name-key");
        assertEquals("name-key", getGeneratedModule().attributeValue("i18n-name-key"));
    }

    @Test
    public void moduleHasDescription() throws Exception
    {
        props.setDescription("desc");
        assertEquals("desc", getGeneratedModule().selectSingleNode("description").getText());
    }

    @Test
    public void moduleHasDescriptionI18nKey() throws Exception
    {
        props.setDescriptionI18nKey("desc-key");
        assertEquals("desc-key", getGeneratedModule().selectSingleNode("description/@key").getText());
    }
    
    @Test
    public void i18nStringsContainNameKey() throws Exception
    {
        props.setModuleName("my-name");
        props.setNameI18nKey("name-key");
        assertEquals("my-name", getI18nString("name-key").getValue());
    }

    @Test
    public void i18nStringsContainDescriptionKey() throws Exception
    {
        props.setDescription("desc");
        props.setDescriptionI18nKey("desc-key");
        assertEquals("desc", getI18nString("desc-key").getValue());
    }

    protected Element getGeneratedModule() throws Exception
    {
        return getGeneratedModule(moduleType);
    }
}
