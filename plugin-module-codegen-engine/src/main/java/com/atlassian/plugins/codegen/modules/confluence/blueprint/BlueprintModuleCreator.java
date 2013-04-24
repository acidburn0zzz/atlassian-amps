package com.atlassian.plugins.codegen.modules.confluence.blueprint;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.ConfluencePluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.common.Resource;
import com.atlassian.plugins.codegen.modules.common.ResourcedProperties;
import com.atlassian.plugins.codegen.modules.common.web.WebItemModuleCreator;
import com.atlassian.plugins.codegen.modules.common.web.WebItemProperties;
import com.atlassian.plugins.codegen.modules.common.web.WebResourceModuleCreator;
import com.atlassian.plugins.codegen.modules.common.web.WebResourceProperties;

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

    private static final String BLUEPRINT_MODULE_TEMPLATE = TEMPLATE_PREFIX + "plugin-module-blueprint.xml.vtl";
    private static final String CONTENT_TEMPLATE_MODULE_TEMPLATE = TEMPLATE_PREFIX + "plugin-module-content-template.xml.vtl";
    private static final String CONTENT_TEMPLATE_FILE_TEMPLATE = TEMPLATE_PREFIX + "resource-file-content-template.xml.vtl";
    private static final String SOY_TEMPLATE_FILE_TEMPLATE = TEMPLATE_PREFIX + "resource-file-soy-template.soy.vtl";

    @Override
    public PluginProjectChangeset createModule(BlueprintProperties props) throws Exception
    {
        PluginProjectChangeset changeset = new PluginProjectChangeset().with(createModule(props, BLUEPRINT_MODULE_TEMPLATE));

        // pull in CC maven dependency
//        .with(MOCKITO_TEST)

        for (ContentTemplateProperties contentTemplateProperties : props.getContentTemplates())
        {
            changeset = changeset.with(createModule(contentTemplateProperties, CONTENT_TEMPLATE_MODULE_TEMPLATE));

            changeset = addResourceFiles(changeset, contentTemplateProperties, CONTENT_TEMPLATE_FILE_TEMPLATE);
        }

        WebItemProperties webItem = props.getWebItem();
        if (webItem != null)  // Only ever expected to be null for testing.
        {
            changeset = changeset.with(createModule(webItem, WebItemModuleCreator.PLUGIN_MODULE_TEMPLATE));
        }

        WebResourceProperties webResource = props.getWebResource();
        changeset = changeset.with(createModule(webResource, WebResourceModuleCreator.PLUGIN_MODULE_TEMPLATE));

        // TODO - only needs creating if the props specify it.
        changeset = addResourceFiles(changeset, webResource, SOY_TEMPLATE_FILE_TEMPLATE);

        return changeset;
    }

    private PluginProjectChangeset addResourceFiles(PluginProjectChangeset changeset,
        ResourcedProperties properties, String resourceFileTemplate) throws Exception
    {
        for (Resource resource : properties.getResources())
        {
            String filePath = resource.getLocation();
            int lastSlash = filePath.lastIndexOf('/');
            String path = filePath.substring(0, lastSlash);
            String filename = filePath.substring(lastSlash + 1);
            changeset = changeset.with(createResource(properties, path, filename, resourceFileTemplate));
        }
        return changeset;
    }

    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
