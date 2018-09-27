package com.atlassian.plugins.codegen.modules.common.servlet;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.BambooPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.BitbucketPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.ConfluencePluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.JiraPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.RefAppPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.plugins.codegen.modules.Dependencies.HTTPCLIENT_TEST;
import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;
import static com.atlassian.plugins.codegen.modules.Dependencies.SERVLET_API;
import static com.atlassian.plugins.codegen.modules.Dependencies.SLF4J;

/**
 * @since 3.6
 */
@RefAppPluginModuleCreator
@JiraPluginModuleCreator
@ConfluencePluginModuleCreator
@BambooPluginModuleCreator
@BitbucketPluginModuleCreator
public class ServletContextListenerModuleCreator extends AbstractPluginModuleCreator<ServletContextListenerProperties>
{

    public static final String MODULE_NAME = "Servlet Context Listener";
    private static final String TEMPLATE_PREFIX = "templates/common/servlet/listener/";

    //stub
    private static final String CLASS_TEMPLATE = TEMPLATE_PREFIX + "ServletContextListener.java.vtl";
    private static final String UNIT_TEST_TEMPLATE = TEMPLATE_PREFIX + "ServletContextListenerTest.java.vtl";

    //examples
    private static final String EXAMPLE_CLASS_TEMPLATE = TEMPLATE_PREFIX + "Example" + CLASS_TEMPLATE;

    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "servlet-context-listener-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(ServletContextListenerProperties props) throws Exception
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
            return ret.with(createClassAndTests(props, CLASS_TEMPLATE, UNIT_TEST_TEMPLATE));
            // context listener is too complex to func test without a known servlet
        }
    }
    
    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
