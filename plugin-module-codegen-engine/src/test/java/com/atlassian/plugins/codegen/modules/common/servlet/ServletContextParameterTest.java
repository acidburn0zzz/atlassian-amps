package com.atlassian.plugins.codegen.modules.common.servlet;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @since 3.6
 */
public class ServletContextParameterTest extends AbstractModuleCreatorTestCase<ServletContextParameterProperties>
{
    public ServletContextParameterTest()
    {
        super("servlet-context-param", new ServletContextParameterModuleCreator());
    }

    @Before
    public void setupProps() throws Exception
    {
        setProps(new ServletContextParameterProperties("My Parameter"));
        props.setParamName("color");
        props.setParamValue("blue");
    }

    @Test
    public void moduleHasDefaultKey() throws Exception
    {
        assertEquals("my-parameter", getGeneratedModule().attributeValue("key"));
    }

    @Test
    public void moduleHasParamName() throws Exception
    {
        assertEquals("color", getGeneratedModule().selectSingleNode("param-name").getText());
    }

    @Test
    public void moduleHasParamValue() throws Exception
    {
        assertEquals("blue", getGeneratedModule().selectSingleNode("param-value").getText());
    }
}
