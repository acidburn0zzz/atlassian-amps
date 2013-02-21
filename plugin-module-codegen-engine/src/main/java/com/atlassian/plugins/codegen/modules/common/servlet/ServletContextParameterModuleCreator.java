package com.atlassian.plugins.codegen.modules.common.servlet;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.*;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.plugins.codegen.modules.Dependencies.SERVLET_API;

/**
 * @since 3.6
 */
@RefAppPluginModuleCreator
@JiraPluginModuleCreator
@ConfluencePluginModuleCreator
@BambooPluginModuleCreator
@StashPluginModuleCreator
public class ServletContextParameterModuleCreator extends AbstractPluginModuleCreator<ServletContextParameterProperties>
{
    public static final String MODULE_NAME = "Servlet Context Parameter";
    private static final String TEMPLATE_PREFIX = "templates/common/servlet/parameter/";

    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "servlet-context-parameter-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(ServletContextParameterProperties props) throws Exception
    {
        return new PluginProjectChangeset()
            .with(SERVLET_API)
            .with(createModule(props, PLUGIN_MODULE_TEMPLATE));
    }

    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
