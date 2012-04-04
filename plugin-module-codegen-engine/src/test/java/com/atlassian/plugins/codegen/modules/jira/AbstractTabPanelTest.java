package com.atlassian.plugins.codegen.modules.jira;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;
import com.atlassian.plugins.codegen.ResourceFile;
import com.atlassian.plugins.codegen.SourceFile;
import com.atlassian.plugins.codegen.modules.PluginModuleCreator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @since 3.8
 */
public abstract class AbstractTabPanelTest extends AbstractModuleCreatorTestCase<TabPanelProperties>
{
    public static final String PACKAGE_NAME = "com.atlassian.plugins.jira.tabpanels";
    
    public AbstractTabPanelTest(String type, PluginModuleCreator<TabPanelProperties> creator)
    {
        super(type, creator);
    }
    
    @Before
    public void setupObjects() throws Exception
    {
        setProps(new TabPanelProperties(PACKAGE_NAME + ".MyTabPanel"));
        props.setIncludeExamples(false);
    }

    @Test
    public void customClassFileIsGenerated() throws Exception
    {
        props.setUseCustomClass(true);

        getSourceFile(PACKAGE_NAME, "MyTabPanel");
    }

    @Test
    public void customUnitTestFileIsGenerated() throws Exception
    {
        props.setUseCustomClass(true);

        getTestSourceFile(PACKAGE_NAME, "MyTabPanelTest");
    }

    @Test
    public void customTemplateFileIsGenerated() throws Exception
    {
        props.setUseCustomClass(true);

        getResourceFile("templates/tabpanels", "my-tab-panel.vm");
    }
    
    @Test
    public void genericClassFilesAreNotGenerated() throws Exception
    {
        props.setUseCustomClass(false);

        assertTrue(getChangesetForModule(SourceFile.class).isEmpty());
    }

    @Test
    public void customModuleHasClass() throws Exception
    {
        props.setUseCustomClass(true);
        
        assertEquals(PACKAGE_NAME + ".MyTabPanel", getGeneratedModule().attributeValue("class"));
    }
    
    @Test
    public void genericModuleHasGenericClass() throws Exception
    {
        props.setFullyQualifiedClassname(ComponentTabPanelModuleCreator.FQ_GENERIC_CLASS);
        props.setUseCustomClass(true);
        
        assertEquals(ComponentTabPanelModuleCreator.FQ_GENERIC_CLASS, getGeneratedModule().attributeValue("class"));
    }
    
    @Test
    public void moduleHasOrder() throws Exception
    {
        props.setOrder(10);
        
        assertEquals("10", getGeneratedModule().selectSingleNode("order").getText());
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
        
        getI18nString(label.getKey(), label.getValue());
    }

    @Test
    public void labelIsUsedInViewTemplate() throws Exception
    {
        props.setLabel(label);
        props.setUseCustomClass(true);
        
        Document viewDoc = DocumentHelper.parseText(getChangesetForModule(ResourceFile.class).get(0).getContent());
        assertNotNull(viewDoc.selectSingleNode("/div/h3[text() = \"$i18n.getText('" + label.getKey() + "')\"]"));
    }
}
