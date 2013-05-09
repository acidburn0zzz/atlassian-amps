package com.atlassian.plugins.codegen.modules.confluence.blueprint;

import com.atlassian.plugins.codegen.ClassId;
import com.atlassian.plugins.codegen.ComponentDeclaration;
import com.atlassian.plugins.codegen.modules.common.ContextProviderProperties;
import com.atlassian.plugins.codegen.modules.common.Resource;
import com.atlassian.plugins.codegen.modules.common.web.WebItemProperties;
import com.atlassian.plugins.codegen.modules.common.web.WebResourceProperties;
import com.atlassian.plugins.codegen.modules.common.web.WebResourceTransformation;
import com.atlassian.plugins.codegen.modules.common.web.WebResourceTransformer;
import com.google.common.collect.Lists;

import java.util.List;

import static com.atlassian.fugue.Option.some;
import static com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintI18nProperty.*;
import static com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintPromptEntry.*;
import static com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintProperties.*;
import static com.atlassian.plugins.codegen.modules.confluence.blueprint.ContentTemplateProperties.CONTENT_I18N_DEFAULT_VALUE;

/**
 * Different to the {@link BlueprintModuleCreator}, the generator takes simple objects provided by the prompter
 * and creates complex {@link BlueprintProperties}.
 *
 * This class is needed for Confluence Blueprints because unlike other SDK module-creators, the Blueprint creator
 * adds multiple modules and converts a small amount of user input into a large data structure.
 *
 * @since 4.1.8
 */
public class BlueprintBuilder
{
    private final BlueprintPromptEntries promptProps;
    private final BlueprintProperties props;
    private BlueprintStringer stringer;

    public BlueprintBuilder(BlueprintPromptEntries promptProps)
    {
        this.promptProps = promptProps;
        props = new BlueprintProperties();
    }

    @SuppressWarnings("unchecked")
    public BlueprintProperties build()
    {
        String pluginKey = promptProps.getPluginKey();
        props.setPluginKey(pluginKey);

        String indexKey = (String) promptProps.get(BlueprintPromptEntry.INDEX_KEY_PROMPT);
        props.setProperty(INDEX_KEY, indexKey);

        stringer = new BlueprintStringer(indexKey, props.getPluginKey());
        String blueprintModuleKey = stringer.makeBlueprintModuleKey();
        props.setModuleKey(blueprintModuleKey);

        WebItemProperties webItem = makeWebItem(pluginKey, blueprintModuleKey, indexKey, promptProps);
        props.setWebItem(webItem);
        String blueprintName = (String) promptProps.get(WEB_ITEM_NAME_PROMPT);
        props.setModuleName(stringer.makeBlueprintModuleName(blueprintName));

        String indexTitleI18nKey = stringer.makeI18nKey("index.page.title");
        props.setIndexTitleI18nKey(indexTitleI18nKey);
        props.addI18nProperty(indexTitleI18nKey, blueprintName + "s");  // THIS IS NOT OPTIMISED

        List<String> contentTemplateKeys = (List<String>) promptProps.get(CONTENT_TEMPLATE_KEYS_PROMPT);
        ContextProviderProperties contextProvider = null;
        String packageName = cleanupPackage(pluginKey);
        if ((Boolean) promptProps.get(CONTEXT_PROVIDER_PROMPT))
        {
            contextProvider = new ContextProviderProperties(packageName + ".ContentTemplateContextProvider");
        }

        boolean hasDialogWizard = (Boolean)promptProps.get(DIALOG_WIZARD_PROMPT);
        boolean skipEditor = (Boolean) promptProps.get(SKIP_PAGE_EDITOR_PROMPT);
        String placeholderI18nKey = null;
        String mentionPlaceholderI18nKey = null;
        if (!skipEditor)
        {
            placeholderI18nKey = CONTENT_TEMPLATE_PLACEHOLDER.getI18nKey(stringer);
            mentionPlaceholderI18nKey = CONTENT_TEMPLATE_MENTION_PLACEHOLDER.getI18nKey(stringer);
            props.addI18nProperty(placeholderI18nKey, CONTENT_TEMPLATE_PLACEHOLDER.getI18nValue());
            props.addI18nProperty(mentionPlaceholderI18nKey, CONTENT_TEMPLATE_MENTION_PLACEHOLDER.getI18nValue());
        }

        for (int i = 0; i < contentTemplateKeys.size(); i++)
        {
            String contentTemplateKey = contentTemplateKeys.get(i);
            String moduleName = stringer.makeContentTemplateName(blueprintName, i);
            String description = "Contains Storage-format XML used by the " + blueprintName + " Blueprint";
            ContentTemplateProperties contentTemplate = makeContentTemplate(contentTemplateKey, moduleName, contextProvider,
                pluginKey, CONTENT_I18N_DEFAULT_VALUE, description);

            contentTemplate.setProperty(DIALOG_WIZARD, String.valueOf(hasDialogWizard));
            if (!skipEditor)
            {
                contentTemplate.setProperty(CONTENT_TEMPLATE_PLACEHOLDER.getPropertyKey(), placeholderI18nKey);
                contentTemplate.setProperty(CONTENT_TEMPLATE_MENTION_PLACEHOLDER.getPropertyKey(), mentionPlaceholderI18nKey);
            }

            props.addContentTemplate(contentTemplate);
        }

        // Reused for icon CSS, how-to-use and dialog-wizard.
        WebResourceProperties webResource = new WebResourceProperties();
        webResource.addContext("atl.general");
        webResource.addContext("atl.admin");
        webResource.addDependency("com.atlassian.confluence.plugins.confluence-create-content-plugin:resources");

        Resource cssResource = new Resource();
        cssResource.setType("download");
        cssResource.setName("blueprints.css");
        cssResource.setLocation("css/blueprints.css");
        webResource.addResource(cssResource);
        webResource.setProperty("INDEX_KEY", indexKey);

        props.setWebResource(webResource);

        boolean hasHowToUse = (Boolean)promptProps.get(HOW_TO_USE_PROMPT);

        String soyPackage = stringer.makeSoyTemplatePackage(blueprintName);

        if (hasHowToUse)
        {
            props.setHowToUseTemplate(soyPackage + ".howToUse");
        }
        if (hasDialogWizard)
        {
            DialogWizardProperties wizard = new DialogWizardProperties();
            wizard.setModuleKey(indexKey + "-wizard");
            DialogPageProperties wizardPage = new DialogPageProperties(0, soyPackage, stringer);
            wizard.setDialogPages(Lists.newArrayList(wizardPage));
            props.setDialogWizard(wizard);
            addJsToWebResource(webResource);
            addJsI18n(webResource);
            webResource.setProperty(BlueprintProperties.PLUGIN_KEY, pluginKey);
            webResource.setProperty(BlueprintProperties.WEB_ITEM_KEY, webItem.getModuleKey());
            webResource.setProperty(BlueprintProperties.WIZARD_FORM_TITLE_FIELD_ID, indexKey + "-blueprint-page-title");
        }
        if (hasHowToUse || hasDialogWizard)
        {
            addSoyTemplateToWebResource(webResource, props.getPluginKey(), indexKey, soyPackage, hasHowToUse, hasDialogWizard);
        }

        if ((Boolean)promptProps.get(EVENT_LISTENER_PROMPT))
        {
            ClassId classId = ClassId.packageAndClass(packageName, "BlueprintCreatedListener");
            ComponentDeclaration component = ComponentDeclaration.builder(classId, "blueprint-created-listener")
                .name(some("Blueprint Created Event Listener"))
                .build();
            props.setEventListener(component);
        }

        if (skipEditor)
        {
            props.setCreateResult(BlueprintProperties.CREATE_RESULT_VIEW);
        }

        if ((Boolean)promptProps.get(INDEX_PAGE_TEMPLATE_PROMPT))
        {
            String contentTemplateKey = INDEX_TEMPLATE_DEFAULT_KEY;
            String moduleName = "Custom Index Page Content Template";
            String description = "Contains Storage-format XML used by the " + blueprintName + " Blueprint's Index page";
            ContentTemplateProperties contentTemplate = makeContentTemplate(contentTemplateKey, moduleName, contextProvider,
                pluginKey, ContentTemplateProperties.INDEX_TEMPLATE_CONTENT_VALUE, description);
            props.setIndexPageContentTemplate(contentTemplate);
        }

        return props;
    }

    private String cleanupPackage(String pluginKey)
    {
        return pluginKey.replaceAll("[^a-z\\.]", "_");
    }

    private void addJsI18n(WebResourceProperties properties)
    {
        addI18n(properties, WIZARD_FORM_TITLE_FIELD_LABEL);
        addI18n(properties, WIZARD_FORM_TITLE_FIELD_PLACEHOLDER);
        addI18n(properties, WIZARD_FORM_TITLE_FIELD_ERROR);
        addI18n(properties, WIZARD_FORM_JSVAR_FIELD_LABEL);
        addI18n(properties, WIZARD_FORM_JSVAR_FIELD_PLACEHOLDER);
        addI18n(properties, WIZARD_FORM_PRE_RENDER_TEXT);
        addI18n(properties, WIZARD_FORM_POST_RENDER_TEXT);
        addI18n(properties, WIZARD_FORM_FIELD_REQUIRED);
    }

    private void addI18n(WebResourceProperties properties, BlueprintI18nProperty i18n)
    {
        String i18nKey = i18n.getI18nKey(stringer);
        properties.setProperty(i18n.getPropertyKey(), i18nKey);
        properties.addI18nProperty(i18nKey, i18n.getI18nValue());
    }

    private void addJsToWebResource(WebResourceProperties properties)
    {
        // JS transformer
        WebResourceTransformation transformation = new WebResourceTransformation("js");
        WebResourceTransformer transformer = new WebResourceTransformer();
        transformer.setModuleKey("jsI18n");
        transformation.addTransformer(transformer);
        properties.addTransformation(transformation);

        Resource soyResource = new Resource();
        soyResource.setType("download");
        soyResource.setName("dialog-wizard.js");
        soyResource.setLocation("js/dialog-wizard.js");
        properties.addResource(soyResource);
    }

    private void addSoyTemplateToWebResource(WebResourceProperties properties, String pluginKey, String indexKey,
        String soyPackage,
        boolean hasHowToUse, boolean hasDialogWizard)
    {
        // Soy transformer
        WebResourceTransformation transformation = new WebResourceTransformation("soy");
        WebResourceTransformer transformer = new WebResourceTransformer();
        transformer.setModuleKey("soyTransformer");
        transformer.addFunctions("com.atlassian.confluence.plugins.soy:soy-core-functions");
        transformation.addTransformer(transformer);
        properties.addTransformation(transformation);

        Resource soyResource = new Resource();
        soyResource.setType("download");
        soyResource.setName("templates-soy.js");
        soyResource.setLocation("soy/my-templates.soy");
        properties.addResource(soyResource);

        properties.setProperty(BlueprintProperties.SOY_PACKAGE, soyPackage);

        if (hasHowToUse)
        {
            properties.put("HOW_TO_USE", true);
            addI18n(properties, HOW_TO_USE_HEADING);
            addI18n(properties, HOW_TO_USE_CONTENT);
        }

        if (hasDialogWizard)
        {
            properties.put(DIALOG_WIZARD, true);
            properties.setProperty(INDEX_KEY, indexKey);
        }
    }

    private ContentTemplateProperties makeContentTemplate(String contentTemplateKey,
        String moduleName, final ContextProviderProperties contextProvider, String pluginKey,
        final String contentTextValue, final String description)
    {
        ContentTemplateProperties template = new ContentTemplateProperties(contentTemplateKey);

        template.setModuleName(moduleName);
        template.setDescription(description);
        template.setNameI18nKey(pluginKey + "." + contentTemplateKey + ".name");
        template.setDescriptionI18nKey(pluginKey + "." + contentTemplateKey + ".desc");
        template.setContentText(pluginKey + "." + contentTemplateKey + ".content.text", contentTextValue);

        if (contextProvider != null)
        {
            template.setContextProvider(contextProvider);
        }

        Resource templateResource = new Resource();
        templateResource.setName("template");
        templateResource.setType("download");

        templateResource.setLocation("xml/" + contentTemplateKey + ".xml");
        template.addResource(templateResource);

        return template;
    }

    private WebItemProperties makeWebItem(String pluginKey, String blueprintModuleKey, String indexKey, BlueprintPromptEntries promptProps)
    {
        WebItemProperties webItem = new WebItemProperties();

        webItem.setModuleKey(blueprintModuleKey + "-web-item");
        webItem.setModuleName((String) promptProps.get(WEB_ITEM_NAME_PROMPT));
        webItem.setDescription((String) promptProps.get(WEB_ITEM_DESC_PROMPT));
        webItem.setNameI18nKey(pluginKey + ".blueprint.display.name");
        webItem.setDescriptionI18nKey(pluginKey + ".blueprint.display.desc");
        webItem.setSection("system.create.dialog/content");
        webItem.addParam(WEB_ITEM_BLUEPRINT_KEY, blueprintModuleKey);
        webItem.setStyleClass("icon-" + indexKey + "-blueprint large");

        return webItem;
    }
}
