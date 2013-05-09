package com.atlassian.plugins.codegen.modules.confluence.blueprint;

import com.atlassian.plugins.codegen.ArtifactDependency;
import com.atlassian.plugins.codegen.ComponentDeclaration;
import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.ConfluencePluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.BasicClassModuleProperties;
import com.atlassian.plugins.codegen.modules.ClassBasedModuleProperties;
import com.atlassian.plugins.codegen.modules.common.ContextProviderProperties;
import com.atlassian.plugins.codegen.modules.common.Resource;
import com.atlassian.plugins.codegen.modules.common.ResourcedProperties;
import com.atlassian.plugins.codegen.modules.common.web.WebItemModuleCreator;
import com.atlassian.plugins.codegen.modules.common.web.WebItemProperties;
import com.atlassian.plugins.codegen.modules.common.web.WebResourceModuleCreator;
import com.atlassian.plugins.codegen.modules.common.web.WebResourceProperties;

import static com.atlassian.plugins.codegen.ArtifactDependency.dependency;

/**
 * Creates a Confluence Blueprint module and any dependent modules.
 *
 * @since 4.1.8
 */
@ConfluencePluginModuleCreator
public class BlueprintModuleCreator extends AbstractPluginModuleCreator<BlueprintProperties>
{
    private static final String MODULE_NAME = "Blueprint";
    private static final String TEMPLATE_PREFIX = "templates/confluence/blueprint/";

    private static final String BLUEPRINT_MODULE_TEMPLATE = TEMPLATE_PREFIX + "plugin-module-blueprint.xml.vtl";
    private static final String CONTENT_TEMPLATE_MODULE_TEMPLATE = TEMPLATE_PREFIX + "plugin-module-content-template.xml.vtl";
    private static final String CONTENT_TEMPLATE_FILE_TEMPLATE = TEMPLATE_PREFIX + "resource-file-content-template.xml.vtl";
    private static final String INDEX_PAGE_CONTENT_TEMPLATE_FILE_TEMPLATE = TEMPLATE_PREFIX + "resource-file-index-page-content-template.xml.vtl";
    private static final String SOY_TEMPLATE_FILE_TEMPLATE = TEMPLATE_PREFIX + "resource-file-soy-template.soy.vtl";
    private static final String JS_TEMPLATE_FILE_TEMPLATE = TEMPLATE_PREFIX + "resource-file-dialog-wizard.js.vtl";
    private static final String CSS_TEMPLATE_FILE_TEMPLATE = TEMPLATE_PREFIX + "resource-file-blueprints.css.vtl";
    // Template name is "jva" not "java" only to avoid IDE headaches with imports.
    private static final String CONTEXT_PROVIDER_CLASS_TEMPLATE = TEMPLATE_PREFIX + "ContentTemplateContextProvider.jva.vtl";
    private static final String EVENT_LISTENER_CLASS_TEMPLATE = TEMPLATE_PREFIX + "BlueprintCreatedListener.jva.vtl";

    @Override
    public PluginProjectChangeset createModule(BlueprintProperties props) throws Exception
    {
        PluginProjectChangeset changeset = new PluginProjectChangeset().with(createModule(props, BLUEPRINT_MODULE_TEMPLATE));

        ArtifactDependency createContent = dependency("com.atlassian.confluence.plugins", "confluence-create-content-plugin", "1.5.17", ArtifactDependency.Scope.PROVIDED);
        changeset = changeset.with(createContent);

        for (ContentTemplateProperties contentTemplateProperties : props.getContentTemplates())
        {
            changeset = changeset.with(createModule(contentTemplateProperties, CONTENT_TEMPLATE_MODULE_TEMPLATE));

            changeset = addResourceFiles(changeset, contentTemplateProperties, CONTENT_TEMPLATE_FILE_TEMPLATE);

            ContextProviderProperties contextProviderProperties = contentTemplateProperties.getContextProvider();
            if (contextProviderProperties != null)
            {
                changeset = changeset.with(createClass(contextProviderProperties, CONTEXT_PROVIDER_CLASS_TEMPLATE));
            }
        }

        ContentTemplateProperties indexPageContentTemplate = props.getIndexPageContentTemplate();
        if (indexPageContentTemplate != null)
        {
            changeset = changeset.with(createModule(indexPageContentTemplate, CONTENT_TEMPLATE_MODULE_TEMPLATE));
            changeset = addResourceFiles(changeset, indexPageContentTemplate, INDEX_PAGE_CONTENT_TEMPLATE_FILE_TEMPLATE);
        }

        WebItemProperties webItem = props.getWebItem();
        if (webItem != null)  // Only ever expected to be null for testing.
        {
            changeset = changeset.with(createModule(webItem, WebItemModuleCreator.PLUGIN_MODULE_TEMPLATE));
        }

        WebResourceProperties webResource = props.getWebResource();
        changeset = changeset.with(createModule(webResource, WebResourceModuleCreator.PLUGIN_MODULE_TEMPLATE));
        changeset = addResourceFiles(changeset, webResource, null); // null means figure the template out...

        ComponentDeclaration eventListener = props.getEventListener();
        if (eventListener != null)
        {
            changeset = changeset.with(eventListener);   // adds the <component> element

            ClassBasedModuleProperties classProps = new BasicClassModuleProperties(eventListener.getClassId().getFullName());
            classProps.setProperty("PLUGIN_KEY", props.getPluginKey());
            changeset = changeset.with(createClass(classProps, EVENT_LISTENER_CLASS_TEMPLATE));
        }

        return changeset;
    }

    private PluginProjectChangeset addResourceFiles(PluginProjectChangeset changeset,
        ResourcedProperties properties, String givenTemplate) throws Exception
    {
        for (Resource resource : properties.getResources())
        {
            String filePath = resource.getLocation();
            int lastSlash = filePath.lastIndexOf('/');
            String path = filePath.substring(0, lastSlash);
            String filename = filePath.substring(lastSlash + 1);

            String resourceFileTemplate = getResourceTemplate(givenTemplate, filename);
            changeset = changeset.with(createResource(properties, path, filename, resourceFileTemplate));
        }
        return changeset;
    }

    // This method is pretty rough. Where are my String switches?!
    private String getResourceTemplate(String givenTemplate, String filename)
    {
        if (givenTemplate != null)
            return givenTemplate;

        if (filename.endsWith(".js"))
            return JS_TEMPLATE_FILE_TEMPLATE;

        if (filename.endsWith(".css"))
            return CSS_TEMPLATE_FILE_TEMPLATE;

        if (filename.endsWith(".soy"))
            return SOY_TEMPLATE_FILE_TEMPLATE;

        throw new UnsupportedOperationException("Can't render resource template for filename: " + filename);
    }

    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
