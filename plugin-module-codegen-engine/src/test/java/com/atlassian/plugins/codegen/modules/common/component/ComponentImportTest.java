package com.atlassian.plugins.codegen.modules.common.component;

import com.atlassian.plugins.codegen.AbstractCodegenTestCase;
import com.atlassian.plugins.codegen.ComponentImport;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @since 3.6
 */
public class ComponentImportTest extends AbstractCodegenTestCase<ComponentImportProperties>
{
    @Before
    public void setup() throws Exception
    {
        setCreator(new ComponentImportModuleCreator());

        setProps(new ComponentImportProperties("com.atlassian.SomeInterface"));
        props.setIncludeExamples(false);
    }

    @Test
    public void createdComponentImport() throws Exception
    {
        assertEquals("expected a component import declaration", 1, getChangesetForModule(ComponentImport.class).size());
    }
    
    @Test
    public void componentImportHasInterface() throws Exception
    {
        assertEquals("com.atlassian.SomeInterface", getChangesetForModule(ComponentImport.class).get(0).getInterfaceClass().getFullName());
    }
}
