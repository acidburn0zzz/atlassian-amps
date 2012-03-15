package com.atlassian.plugins.codegen.modules.common.web;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.Dependencies;

/**
 * @since 3.6
 */

public class SimpleWebModuleCreator extends AbstractPluginModuleCreator<SimpleWebProperties>
{

    public static final String MODULE_NAME = "SimpleWebModule";
    private static final String TEMPLATE_PREFIX = "templates/common/web/";

    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "simple-web-module-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(SimpleWebProperties props) throws Exception
    {
        return new PluginProjectChangeset()
            .with(Dependencies.MOCKITO_TEST)
            .with(createModule(props, PLUGIN_MODULE_TEMPLATE));
    }


    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
