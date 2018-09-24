package com.atlassian.plugins.codegen.modules.common.component;

import com.atlassian.plugins.codegen.AbstractCodegenTestCase;
import com.atlassian.plugins.codegen.ClassId;
import com.atlassian.plugins.codegen.ComponentDeclaration;

import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;

import static com.atlassian.fugue.Option.some;
import static org.junit.Assert.assertEquals;

/**
 * @since 3.6
 */
public class ComponentTest extends AbstractCodegenTestCase<ComponentProperties>
{
    @Before
    public void setup() throws Exception
    {
        setCreator(new ComponentModuleCreator());
        
        setProps(new ComponentProperties(PACKAGE_NAME + ".CustomComponent"));

        props.setFullyQualifiedInterface(PACKAGE_NAME + ".CustomInterface");
        props.setGenerateClass(true);
        props.setGenerateInterface(true);
        props.setIncludeExamples(false);
    }

    @Test
    public void classFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "CustomComponent");
    }

    @Test
    public void interfaceFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "CustomInterface");
    }

    @Test
    public void unitTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(TEST_PACKAGE_NAME, "CustomComponentTest");
    }

    @Test
    public void functionalTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(FUNC_TEST_PACKAGE_NAME, "CustomComponentFuncTest");
    }

    @Test
    public void componentDeclarationIsGenerated() throws Exception
    {
        assertEquals("expected a component declaration", 1, getChangesetForModule(ComponentDeclaration.class).size());
    }
    
    @Test
    public void componentDeclarationHasDefaultKey() throws Exception
    {
        assertEquals("custom-component", getChangesetForModule(ComponentDeclaration.class).get(0).getKey());
    }
    
    @Test
    public void componentDeclarationHasSpecifiedKey() throws Exception
    {
        props.setModuleKey("newkey");
        assertEquals("newkey", getChangesetForModule(ComponentDeclaration.class).get(0).getKey());
    }
    
    @Test
    public void componentDeclarationHasName() throws Exception
    {
        assertEquals(some("Custom Component"), getChangesetForModule(ComponentDeclaration.class).get(0).getName());
    }

    @Test
    public void componentDeclarationHasNameI18nKey() throws Exception
    {
        props.setNameI18nKey("name-key");
        assertEquals(some("name-key"), getChangesetForModule(ComponentDeclaration.class).get(0).getNameI18nKey());
    }

    @Test
    public void componentDeclarationHasClass() throws Exception
    {
        assertEquals(ClassId.packageAndClass(PACKAGE_NAME, "CustomComponent"), getChangesetForModule(ComponentDeclaration.class).get(0).getClassId());
    }

    @Test
    public void componentDeclarationHasInterface() throws Exception
    {
        assertEquals(some(ClassId.packageAndClass(PACKAGE_NAME, "CustomInterface")), getChangesetForModule(ComponentDeclaration.class).get(0).getInterfaceId());
    }

    @Test
    public void componentDeclarationHasDescription() throws Exception
    {
        props.setDescription("desc");
        assertEquals(some("desc"), getChangesetForModule(ComponentDeclaration.class).get(0).getDescription());
    }

    @Test
    public void componentDeclarationHasDescriptionI18nKey() throws Exception
    {
        props.setDescriptionI18nKey("desc-key");
        assertEquals(some("desc-key"), getChangesetForModule(ComponentDeclaration.class).get(0).getDescriptionI18nKey());
    }
    
    @Test
    public void componentDeclarationHasServiceProperties() throws Exception
    {
        props.setServiceProps(ImmutableMap.of("prop1", "value1"));
        assertEquals("value1", getChangesetForModule(ComponentDeclaration.class).get(0).getServiceProperties().get("prop1"));
    }
}
