package com.atlassian.plugins.codegen.modules.common.servlet;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.*;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.plugins.codegen.modules.Dependencies.*;

/**
 * @since 3.6
 */
@RefAppPluginModuleCreator
@JiraPluginModuleCreator
@ConfluencePluginModuleCreator
@BambooPluginModuleCreator
@StashPluginModuleCreator
public class ServletFilterModuleCreator extends AbstractPluginModuleCreator<ServletFilterProperties>
{

    public static final String MODULE_NAME = "Servlet Filter";
    private static final String TEMPLATE_PREFIX = "templates/common/servlet/filter/";

    //stub
    private static final String CLASS_TEMPLATE = TEMPLATE_PREFIX + "ServletFilter.java.vtl";
    private static final String UNIT_TEST_TEMPLATE = TEMPLATE_PREFIX + "ServletFilterTest.java.vtl";
    private static final String FUNC_TEST_TEMPLATE = TEMPLATE_PREFIX + "ServletFilterFuncTest.java.vtl";

    //examples
    private static final String EXAMPLE_CLASS_TEMPLATE = TEMPLATE_PREFIX + "Example" + CLASS_TEMPLATE;

    private static final String PLUGIN_MODULE_TEMPLATE = "templates/common/servlet/filter/servlet-filter-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(ServletFilterProperties props) throws Exception
    {
        PluginProjectChangeset ret = new PluginProjectChangeset()
            .with(SERVLET_API,
                  SLF4J,
                  HTTPCLIENT_TEST,
                  MOCKITO_TEST)
            .with(createModule(props, PLUGIN_MODULE_TEMPLATE));

        if (props.includeExamples())
        {
            return ret.with(createClass(props, EXAMPLE_CLASS_TEMPLATE));
        }
        else
        {
            return ret.with(createClassAndTests(props, CLASS_TEMPLATE, UNIT_TEST_TEMPLATE, FUNC_TEST_TEMPLATE));
        }
    }
    
    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
