package com.atlassian.plugins.codegen.modules.confluence.blueprint;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.ConfluencePluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.common.web.WebItemProperties;

/**
 * Creates a Confluence Blueprint module and any dependent modules.
 *
 * @since 4.1.7
 */
@ConfluencePluginModuleCreator
public class BlueprintModuleCreator extends AbstractPluginModuleCreator<BlueprintProperties>
{
    private static final String MODULE_NAME = "Blueprint";
    private static final String TEMPLATE_PREFIX = "templates/confluence/blueprint/";

    private static final String BLUEPRINT_MODULE_TEMPLATE = TEMPLATE_PREFIX + "blueprint-plugin.xml.vtl";
    private static final String CONTENT_TEMPLATE_MODULE_TEMPLATE = TEMPLATE_PREFIX + "content-template-plugin.xml.vtl";

    // TODO - not copy this. dT
    private static final String WEB_TEMPLATE_PREFIX = "templates/common/web/webitem/";
    private static final String WEB_ITEM_MODULE_TEMPLATE = WEB_TEMPLATE_PREFIX + "web-item-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(BlueprintProperties props) throws Exception
    {
        PluginProjectChangeset changeset = new PluginProjectChangeset().with(createModule(props, BLUEPRINT_MODULE_TEMPLATE));

        for (ContentTemplateProperties contentTemplateProperties : props.getContentTemplates())
        {
            changeset = changeset.with(createModule(contentTemplateProperties, CONTENT_TEMPLATE_MODULE_TEMPLATE));
        }

        WebItemProperties webItem = props.getWebItem();
        if (webItem != null)
        {
            // Only ever expected to be null for testing.
            changeset = changeset.with(createModule(webItem, WEB_ITEM_MODULE_TEMPLATE));
        }

        return changeset;
    }

    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
