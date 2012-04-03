package com.atlassian.plugins.codegen.modules.common.web;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.BambooPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.ConfluencePluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.JiraPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.RefAppPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;

/**
 * @since 3.6
 */
@RefAppPluginModuleCreator
@JiraPluginModuleCreator
@ConfluencePluginModuleCreator
@BambooPluginModuleCreator
public class WebPanelModuleCreator extends AbstractPluginModuleCreator<WebPanelProperties>
{
    public static final String MODULE_NAME = "Web Panel";
    private static final String TEMPLATE_PREFIX = "templates/common/web/webpanel/";

    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "web-panel-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(WebPanelProperties props) throws Exception
    {
        return new PluginProjectChangeset()
            .with(MOCKITO_TEST)
            .with(createModule(props, PLUGIN_MODULE_TEMPLATE));
    }
    
    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
