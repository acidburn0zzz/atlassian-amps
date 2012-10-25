package com.atlassian.plugins.codegen.modules.jira;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @since 3.6
 */
public class JqlFunctionTest extends AbstractModuleCreatorTestCase<JqlFunctionProperties>
{
    public JqlFunctionTest()
    {
        super("jql-function", new JqlFunctionModuleCreator());
    }

    @Before
    public void setupProps() throws Exception
    {
        setProps(new JqlFunctionProperties(PACKAGE_NAME + ".MyJqlFunction"));
        props.setIncludeExamples(false);
    }

    @Test
    public void classFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "MyJqlFunction");
    }

    @Test
    public void unitTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(TEST_PACKAGE_NAME, "MyJqlFunctionTest");
    }
    
    @Test
    public void moduleHasDefaultKey() throws Exception
    {
        assertEquals("my-jql-function",
                     getGeneratedModule().attributeValue("key"));
    }
    
    @Test
    public void moduleHasClass() throws Exception
    {
        assertEquals(PACKAGE_NAME + ".MyJqlFunction",
                     getGeneratedModule().attributeValue("class"));
    }
}
