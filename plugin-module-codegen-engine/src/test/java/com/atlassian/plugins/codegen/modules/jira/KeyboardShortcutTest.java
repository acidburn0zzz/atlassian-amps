package com.atlassian.plugins.codegen.modules.jira;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @since 3.6
 */
public class KeyboardShortcutTest extends AbstractModuleCreatorTestCase<KeyboardShortcutProperties>
{
    public KeyboardShortcutTest()
    {
        super("keyboard-shortcut", new KeyboardShortcutModuleCreator());
    }

    @Before
    public void setupProps() throws Exception
    {
        setProps(new KeyboardShortcutProperties("My Keyboard Shortcut"));
        props.setIncludeExamples(false);
        props.setOperationType("click");
        props.setOperationValue("do:something");
        props.setShortcut("m");
        props.setContext("issueaction");
    }

    @Test
    public void moduleHasShortcut() throws Exception
    {
        assertEquals("m", getGeneratedModule().selectSingleNode("shortcut").getText());
    }
    
    @Test
    public void moduleHasContext() throws Exception
    {
        assertEquals("issueaction", getGeneratedModule().selectSingleNode("context").getText());
    }

    @Test
    public void moduleHasOperationType() throws Exception
    {
        assertEquals("click", getGeneratedModule().selectSingleNode("operation/@type").getText());
    }

    @Test
    public void moduleHasOperationValue() throws Exception
    {
        assertEquals("do:something", getGeneratedModule().selectSingleNode("operation").getText());
    }

    @Test
    public void moduleHasDefaultOrder() throws Exception
    {
        assertEquals("10", getGeneratedModule().selectSingleNode("order").getText());
    }

    @Test
    public void moduleHasSpecifiedOrder() throws Exception
    {
        props.setOrder(1000);
        
        assertEquals("1000", getGeneratedModule().selectSingleNode("order").getText());
    }
}
