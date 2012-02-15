package com.atlassian.plugins.codegen.modules.common;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @since 3.6
 */
public class TemplateContextItemTest extends AbstractModuleCreatorTestCase<TemplateContextItemProperties>
{
    public TemplateContextItemTest()
    {
        super("template-context-item", new TemplateContextItemModuleCreator());
    }
    
    public static final String MODULE_NAME = "My Template Context Item";
    public static final String COMPONENT_REF = "i18nResolver";
    public static final String CLASSNAME = "com.atlassian.component.SomeSingleton";
    public static final String CONTEXT_KEY = "i18n";

    @Before
    public void setupProps() throws Exception
    {
        setProps(new TemplateContextItemProperties(MODULE_NAME, CONTEXT_KEY));
        props.setIncludeExamples(false);
        props.setComponentRef(COMPONENT_REF);
    }

    @Test
    public void moduleHasContextKey() throws Exception
    {
        assertEquals(CONTEXT_KEY, getGeneratedModule().attributeValue("context-key"));
    }
    
    @Test
    public void componentRefModuleHasName() throws Exception
    {
        assertEquals(COMPONENT_REF, getGeneratedModule().attributeValue("component-ref"));
    }
    
    @Test
    public void componentRefModuleIsNotGlobal() throws Exception
    {
        assertEquals("false", getGeneratedModule().attributeValue("global"));
    }

    @Test
    public void globalModuleHasName() throws Exception
    {
        props.setGlobal(true);

        assertEquals(COMPONENT_REF, getGeneratedModule().attributeValue("component-ref"));
    }
    
    @Test
    public void globalModuleIsGlobal() throws Exception
    {
        props.setGlobal(true);

        assertEquals("true", getGeneratedModule().attributeValue("global"));
    }
}
