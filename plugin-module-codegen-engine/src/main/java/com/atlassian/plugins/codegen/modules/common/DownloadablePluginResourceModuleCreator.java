package com.atlassian.plugins.codegen.modules.common;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.BambooPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.ConfluencePluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.CrowdPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.FeCruPluginModuleCreator;
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
@CrowdPluginModuleCreator
@FeCruPluginModuleCreator
public class DownloadablePluginResourceModuleCreator extends AbstractPluginModuleCreator<DownloadablePluginResourceProperties>
{
    public static final String MODULE_NAME = "Downloadable Plugin Resource";
    private static final String TEMPLATE_PREFIX = "templates/common/";
    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "resource-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(DownloadablePluginResourceProperties props) throws Exception
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
