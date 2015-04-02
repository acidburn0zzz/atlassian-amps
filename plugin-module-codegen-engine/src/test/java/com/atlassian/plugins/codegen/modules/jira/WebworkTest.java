package com.atlassian.plugins.codegen.modules.jira;

import java.io.File;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @since 3.6
 */
public class WebworkTest extends AbstractModuleCreatorTestCase<WebworkProperties>
{
    protected ActionProperties action;
    protected View successView;
    protected ActionProperties action2;
    
    public WebworkTest()
    {
        super("webwork1", new WebworkModuleCreator());
    }
    
    @Before
    public void setupProps() throws Exception
    {
        setProps(new WebworkProperties("My Webwork"));

        action = new ActionProperties(PACKAGE_NAME + ".ActionOne");
        successView = new View("success", "templates/success.vm");
        action.addView(successView);
        
        props.addAction(action);

        action2 = new ActionProperties(PACKAGE_NAME + ".ActionTwo");
    }

    @Test
    public void singleActionClassFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "ActionOne");
    }

    @Test
    public void singleActionTestClassFileIsGenerated() throws Exception
    {
        getTestSourceFile(TEST_PACKAGE_NAME, "ActionOneTest");
    }

    @Test
    public void secondActionClassFileIsGenerated() throws Exception
    {
        props.addAction(action2);

        getSourceFile(PACKAGE_NAME, "ActionTwo");
    }

    @Test
    public void secondActionTestClassFileIsGenerated() throws Exception
    {
        props.addAction(action2);

        getTestSourceFile(TEST_PACKAGE_NAME, "ActionTwoTest");
    }

    @Test
    public void actionHasName() throws Exception
    {
        assertEquals(action.getClassId().getFullName(), getGeneratedModule().selectSingleNode("actions/action/@name").getText());
    }
    
    @Test
    public void actionHasAlias() throws Exception
    {
        assertEquals(action.getClassId().getName(), getGeneratedModule().selectSingleNode("actions/action/@alias").getText());
    }

    @Test
    public void actionHasView() throws Exception
    {
        assertNotNull(getGeneratedModule().selectSingleNode("actions/action/view"));
    }

    @Test
    public void viewHasName() throws Exception
    {
        assertEquals("success", getGeneratedModule().selectSingleNode("actions/action/view/@name").getText());
    }

    @Test
    public void viewHasTemplatePath() throws Exception
    {
        assertEquals("templates/success.vm", getGeneratedModule().selectSingleNode("actions/action/view").getText());
    }

    @Test
    public void viewFileIsGenerated() throws Exception
    {
        getResourceFile("templates"+ File.separator, "success.vm");
    }

    @Test
    public void secondViewIsAdded() throws Exception
    {
        View errorView = new View("error", "templates/error.vm");
        action.addView(errorView);
        
        assertEquals(2, getGeneratedModule().selectNodes("actions/action/view").size());
    }

    @Test
    public void secondViewFileIsGenerated() throws Exception
    {
        View errorView = new View("error", "templates/error.vm");
        action.addView(errorView);
        
        getResourceFile("templates"+ File.separator, "error.vm");
    }
}
