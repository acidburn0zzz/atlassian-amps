package com.atlassian.plugins.codegen.modules.common.servlet;


import com.atlassian.plugins.codegen.modules.BasicNameModuleProperties;

/**
 * @since 3.6
 */
public class ServletContextParameterProperties extends BasicNameModuleProperties
{
    public static final String PARAM_NAME = "PARAM_NAME";
    public static final String PARAM_VALUE = "PARAM_VALUE";

    public ServletContextParameterProperties()
    {
        this("MyServletContextParameter");
    }

    public ServletContextParameterProperties(String moduleName)
    {
        super(moduleName);
    }

    public void setParamName(String name)
    {
        setProperty(PARAM_NAME, name);
    }

    public void setParamValue(String value)
    {
        setProperty(PARAM_VALUE, value);
    }
}
