package com.atlassian.plugins.codegen.modules.jira;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @since 3.6
 */
public class WorkflowConditionTest extends AbstractModuleCreatorTestCase<WorkflowElementProperties>
{
    public WorkflowConditionTest()
    {
        super("workflow-condition", new WorkflowConditionModuleCreator());
    }

    @Before
    public void setupProps() throws Exception
    {
        setProps(new WorkflowElementProperties(PACKAGE_NAME + ".MyWorkflowCondition"));
        props.setIncludeExamples(false);
    }

    @Test
    public void classFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "MyWorkflowCondition");
    }

    @Test
    public void factoryClassFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "MyWorkflowConditionFactory");
    }
    
    @Test
    public void unitTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(TEST_PACKAGE_NAME, "MyWorkflowConditionTest");
    }
   
    @Test
    public void viewTemplateIsGenerated() throws Exception
    {
        getResourceFile("templates/conditions", "my-workflow-condition.vm");
    }
    
    @Test
    public void inputTemplateIsGenerated() throws Exception
    {
        getResourceFile("templates/conditions", "my-workflow-condition-input.vm");
    }
    
    @Test
    public void moduleHasDefaultKey() throws Exception
    {
        assertEquals("my-workflow-condition",
                     getGeneratedModule().attributeValue("key"));
    }
    
    @Test
    public void moduleHasClass() throws Exception
    {
        assertEquals(PACKAGE_NAME + ".MyWorkflowConditionFactory",
                     getGeneratedModule().attributeValue("class"));
    }
    
    @Test
    public void moduleHasConditionClass() throws Exception
    {
        assertEquals(PACKAGE_NAME + ".MyWorkflowCondition", getGeneratedModule().selectSingleNode("condition-class").getText());
    }
}
