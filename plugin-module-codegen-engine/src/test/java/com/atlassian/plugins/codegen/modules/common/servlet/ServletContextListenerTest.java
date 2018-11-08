package com.atlassian.plugins.codegen.modules.common.servlet;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @since 3.6
 */
public class ServletContextListenerTest extends AbstractModuleCreatorTestCase<ServletContextListenerProperties>
{
    public ServletContextListenerTest()
    {
        super("servlet-context-listener", new ServletContextListenerModuleCreator());
    }
    
    @Before
    public void setupProps() throws Exception
    {
        setProps(new ServletContextListenerProperties(PACKAGE_NAME + ".MyServletContextListener"));
        props.setIncludeExamples(false);
    }

    @Test
    public void classFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "MyServletContextListener");
    }

    @Test
    public void unitTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(TEST_PACKAGE_NAME, "MyServletContextListenerTest");
    }

    @Test
    public void moduleHasDefaultKey() throws Exception
    {
        assertEquals("my-servlet-context-listener",
                     getGeneratedModule().attributeValue("key"));
    }
    
    @Test
    public void moduleHasClass() throws Exception
    {
        assertEquals(PACKAGE_NAME + ".MyServletContextListener", getGeneratedModule().attributeValue("class"));
    }
}
