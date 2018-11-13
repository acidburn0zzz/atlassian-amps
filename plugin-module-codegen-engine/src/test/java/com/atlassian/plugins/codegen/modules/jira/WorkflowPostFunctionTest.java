package com.atlassian.plugins.codegen.modules.jira;

import java.io.File;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @since 3.6
 */
public class WorkflowPostFunctionTest extends AbstractModuleCreatorTestCase<WorkflowPostFunctionProperties>
{
    public WorkflowPostFunctionTest()
    {
        super("workflow-function", new WorkflowPostFunctionModuleCreator());
    }

    @Before
    public void setupProps() throws Exception
    {
        setProps(new WorkflowPostFunctionProperties(PACKAGE_NAME + ".MyWorkflowFunction"));
        props.setIncludeExamples(false);
    }

    @Test
    public void classFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "MyWorkflowFunction");
    }

    @Test
    public void factoryClassFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "MyWorkflowFunctionFactory");
    }
    
    @Test
    public void unitTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(TEST_PACKAGE_NAME, "MyWorkflowFunctionTest");
    }
   
    @Test
    public void viewTemplateIsGenerated() throws Exception
    {
        getResourceFile("templates" + File.separatorChar + "postfunctions", "my-workflow-function-input.vm");
    }
    
    @Test
    public void inputTemplateIsGenerated() throws Exception
    {
        getResourceFile("templates" + File.separatorChar + "postfunctions", "my-workflow-function-input.vm");
    }
    
    @Test
    public void moduleHasDefaultKey() throws Exception
    {
        assertEquals("my-workflow-function",
                     getGeneratedModule().attributeValue("key"));
    }
    
    @Test
    public void moduleHasClass() throws Exception
    {
        assertEquals(PACKAGE_NAME + ".MyWorkflowFunctionFactory",
                     getGeneratedModule().attributeValue("class"));
    }
    
    @Test
    public void moduleHasFunctionClass() throws Exception
    {
        assertEquals(PACKAGE_NAME + ".MyWorkflowFunction", getGeneratedModule().selectSingleNode("function-class").getText());
    }
    
    @Test
    public void moduleIsNotOrderableByDefault() throws Exception
    {
        assertNull(getGeneratedModule().selectSingleNode("orderable"));
    }

    @Test
    public void moduleIsNotUniqueByDefault() throws Exception
    {
        assertNull(getGeneratedModule().selectSingleNode("unique"));
    }

    @Test
    public void moduleIsNotDeletableByDefault() throws Exception
    {
        assertNull(getGeneratedModule().selectSingleNode("deletable"));
    }

    @Test
    public void moduleIsNotAddableByDefault() throws Exception
    {
        assertNull(getGeneratedModule().selectSingleNode("addable"));
    }

    @Test
    public void moduleHasOrderable() throws Exception
    {
        props.setOrderable(true);
        
        assertEquals("true", getGeneratedModule().selectSingleNode("orderable").getText());
    }

    @Test
    public void moduleHasDeletable() throws Exception
    {
        props.setDeletable(false);

        assertEquals("false", getGeneratedModule().selectSingleNode("deletable").getText());
    }

    @Test
    public void moduleHasUnique() throws Exception
    {
        props.setUnique(true);

        assertEquals("true", getGeneratedModule().selectSingleNode("unique").getText());
    }

    @Test
    public void moduleHasAddable() throws Exception
    {
        props.setAddable("global,common");

        assertEquals("global,common", getGeneratedModule().selectSingleNode("addable").getText());
    }

    @Test
    public void moduleWithMultipleFlagsHasAddable() throws Exception
    {
        props.setAddable("global,common");
        props.setUnique(true);

        assertEquals("global,common", getGeneratedModule().selectSingleNode("addable").getText());
    }
    
    @Test
    public void moduleWithMultipleFlagsHasUnique() throws Exception
    {
        props.setAddable("global,common");
        props.setUnique(true);

        assertEquals("true", getGeneratedModule().selectSingleNode("unique").getText());
    }
}
