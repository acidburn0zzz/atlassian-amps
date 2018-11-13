package com.atlassian.plugins.codegen.modules.common.moduletype;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @since 3.6
 */
public class ModuleTypeTest extends AbstractModuleCreatorTestCase<ModuleTypeProperties>
{
    public static final String MODULE_TYPE = "module-type";

    public ModuleTypeTest()
    {
        super(MODULE_TYPE, new ModuleTypeModuleCreator());
    }
    
    @Before
    public void setupProps() throws Exception
    {
        setProps(new ModuleTypeProperties(PACKAGE_NAME + ".DictionaryModuleDescriptor"));        
        props.setFullyQualifiedInterface(PACKAGE_NAME + ".Dictionary");
        props.setIncludeExamples(false);
    }

    @Test
    public void classFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "DictionaryModuleDescriptor");
    }

    @Test
    public void interfaceFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "Dictionary");
    }

    @Test
    public void unitTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(TEST_PACKAGE_NAME, "DictionaryModuleDescriptorTest");
    }

    @Test
    public void functionalTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(FUNC_TEST_PACKAGE_NAME, "DictionaryModuleDescriptorFuncTest");
    }

    @Test
    public void moduleHasClass() throws Exception
    {
        assertEquals(PACKAGE_NAME + ".DictionaryModuleDescriptor",
                     getGeneratedModule().attributeValue("class"));
    }

    @Test
    public void moduleHasDefaultKey() throws Exception
    {
        assertEquals("dictionary-module-descriptor",
                     getGeneratedModule().attributeValue("key"));
    }
}
