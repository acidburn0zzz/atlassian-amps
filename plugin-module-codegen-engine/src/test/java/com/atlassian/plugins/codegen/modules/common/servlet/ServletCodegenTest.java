package com.atlassian.plugins.codegen.modules.common.servlet;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @since 3.6
 */
public class ServletCodegenTest extends AbstractModuleCreatorTestCase<ServletProperties>
{   
    public ServletCodegenTest()
    {
        super("servlet", new ServletModuleCreator());
    }

    @Before
    public void setupProps() throws Exception
    {
        setProps(new ServletProperties(PACKAGE_NAME + ".MyServlet"));
        props.setUrlPattern("/myservlet");
        props.setIncludeExamples(false);
        props.addInitParam("foo", "bar");
    }

    @Test
    public void classFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "MyServlet");
    }

    @Test
    public void unitTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(PACKAGE_NAME, "MyServletTest");
    }

    @Test
    public void functionalTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(FUNC_TEST_PACKAGE_NAME, "MyServletFuncTest");
    }

    @Test
    public void moduleHasDefaultKey() throws Exception
    {
        assertEquals("my-servlet", getGeneratedModule().attributeValue("key"));
    }

    @Test
    public void moduleHasClass() throws Exception
    {
        assertEquals(PACKAGE_NAME + ".MyServlet", getGeneratedModule().attributeValue("class"));
    }
    
    @Test
    public void moduleHasUrlPattern() throws Exception
    {
        assertEquals("/myservlet", getGeneratedModule().selectSingleNode("url-pattern").getText());
    }
    
    @Test
    public void moduleHasInitParamName() throws Exception
    {
        assertEquals("foo", getGeneratedModule().selectSingleNode("init-param/param-name").getText());
    }
    
    @Test
    public void moduleHasInitParamValue() throws Exception
    {
        assertEquals("bar", getGeneratedModule().selectSingleNode("init-param/param-value").getText());
    }
}
