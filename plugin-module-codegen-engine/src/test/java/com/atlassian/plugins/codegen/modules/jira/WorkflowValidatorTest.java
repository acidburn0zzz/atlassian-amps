package com.atlassian.plugins.codegen.modules.jira;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @since 3.6
 */
public class WorkflowValidatorTest extends AbstractModuleCreatorTestCase<WorkflowElementProperties>
{
    public WorkflowValidatorTest()
    {
        super("workflow-validator", new WorkflowValidatorModuleCreator());
    }

    @Before
    public void setupProps() throws Exception
    {
        setProps(new WorkflowElementProperties(PACKAGE_NAME + ".MyWorkflowValidator"));
        props.setIncludeExamples(false);
    }

    @Test
    public void classFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "MyWorkflowValidator");
    }

    @Test
    public void factoryClassFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "MyWorkflowValidatorFactory");
    }
    
    @Test
    public void unitTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(TEST_PACKAGE_NAME, "MyWorkflowValidatorTest");
    }
   
    @Test
    public void moduleHasDefaultKey() throws Exception
    {
        assertEquals("my-workflow-validator",
                     getGeneratedModule().attributeValue("key"));
    }
    
    @Test
    public void moduleHasClass() throws Exception
    {
        assertEquals(PACKAGE_NAME + ".MyWorkflowValidatorFactory",
                     getGeneratedModule().attributeValue("class"));
    }
    
    @Test
    public void moduleHasValidatorClass() throws Exception
    {
        assertEquals(PACKAGE_NAME + ".MyWorkflowValidator", getGeneratedModule().selectSingleNode("validator-class").getText());
    }
}
