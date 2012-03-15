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
@FeCruPluginModuleCreator
@CrowdPluginModuleCreator
public class TemplateContextItemModuleCreator extends AbstractPluginModuleCreator<TemplateContextItemProperties>
{

    public static final String MODULE_NAME = "Template Context Item";
    private static final String TEMPLATE_PREFIX = "templates/common/";

    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "template-context-item-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(TemplateContextItemProperties props) throws Exception
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
