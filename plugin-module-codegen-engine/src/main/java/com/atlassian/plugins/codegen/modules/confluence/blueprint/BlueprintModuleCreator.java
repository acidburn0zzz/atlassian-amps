package com.atlassian.plugins.codegen.modules.confluence.blueprint;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.ConfluencePluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.common.Resource;
import com.atlassian.plugins.codegen.modules.common.web.WebItemModuleCreator;
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
    private static final String CONTENT_TEMPLATE_FILE_TEMPLATE = TEMPLATE_PREFIX + "content-template-file.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(BlueprintProperties props) throws Exception
    {
        PluginProjectChangeset changeset = new PluginProjectChangeset().with(createModule(props, BLUEPRINT_MODULE_TEMPLATE));

        // pull in CC maven dependency
//        .with(MOCKITO_TEST)

        for (ContentTemplateProperties contentTemplateProperties : props.getContentTemplates())
        {
            changeset = changeset.with(createModule(contentTemplateProperties, CONTENT_TEMPLATE_MODULE_TEMPLATE));

            // TODO - this feels like common code... dT
            for (Resource resource : contentTemplateProperties.getResources())
            {
                String filePath = resource.getLocation();
                int lastSlash = filePath.lastIndexOf('/');
                String path = filePath.substring(0, lastSlash);
                String filename = filePath.substring(lastSlash + 1);
                changeset = changeset.with(createResource(contentTemplateProperties, path, filename, CONTENT_TEMPLATE_FILE_TEMPLATE));
            }
        }

        WebItemProperties webItem = props.getWebItem();
        if (webItem != null)  // Only ever expected to be null for testing.
        {
            changeset = changeset.with(createModule(webItem, WebItemModuleCreator.PLUGIN_MODULE_TEMPLATE));
        }

        return changeset;
    }

    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
