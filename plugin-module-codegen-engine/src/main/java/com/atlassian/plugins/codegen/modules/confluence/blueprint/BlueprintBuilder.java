package com.atlassian.plugins.codegen.modules.confluence.blueprint;

import com.atlassian.plugins.codegen.modules.common.Resource;
import com.atlassian.plugins.codegen.modules.common.web.WebItemProperties;
import com.atlassian.plugins.codegen.modules.common.web.WebResourceProperties;
import com.atlassian.plugins.codegen.modules.common.web.WebResourceTransformation;
import com.atlassian.plugins.codegen.modules.common.web.WebResourceTransformer;
import com.google.common.collect.Lists;

import java.util.List;

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
 * @since 4.1.7
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

        WebItemProperties webItem = makeWebItem(pluginKey, blueprintModuleKey, promptProps);
        props.setWebItem(webItem);
        String blueprintName = (String) promptProps.get(WEB_ITEM_NAME_PROMPT);
        props.setModuleName(stringer.makeBlueprintModuleName(blueprintName));

        String indexTitleI18nKey = stringer.makeI18nKey("index.page.title");
        props.setIndexTitleI18nKey(indexTitleI18nKey);
        props.addI18nProperty(indexTitleI18nKey, blueprintName + "s");  // THIS IS NOT OPTIMISED

        List<String> contentTemplateKeys = (List<String>) promptProps.get(CONTENT_TEMPLATE_KEYS_PROMPT);
        for (int i = 0; i < contentTemplateKeys.size(); i++)
        {
            String contentTemplateKey = contentTemplateKeys.get(i);
            ContentTemplateProperties contentTemplate = makeContentTemplate(contentTemplateKey, blueprintName, i);
            props.addContentTemplate(contentTemplate);
        }

        // Reused for icon CSS, how-to-use and dialog-wizard.
        WebResourceProperties webResource = new WebResourceProperties();
        webResource.addContext("atl.general");
        webResource.addContext("atl.admin");
        webResource.addDependency("com.atlassian.confluence.plugins.confluence-create-content-plugin:resources");

        props.setWebResource(webResource);

        boolean hasHowToUse = (Boolean)promptProps.get(HOW_TO_USE_PROMPT);
        boolean hasDialogWizard = (Boolean)promptProps.get(DIALOG_WIZARD_PROMPT);

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
            webResource.setProperty(BlueprintProperties.WIZARD_FORM_FIELD_ID, indexKey + "-blueprint-page-title");
        }
        if (hasHowToUse || hasDialogWizard)
        {
            addSoyTemplateToWebResource(webResource, props.getPluginKey(), indexKey, soyPackage, hasHowToUse, hasDialogWizard);
        }

        return props;
    }

    private void addJsI18n(WebResourceProperties properties)
    {
        String titleLabel = stringer.makeI18nKey("wizard.page0.title.label");
        String titlePlace = stringer.makeI18nKey("wizard.page0.title.placeholder");
        String titleError = stringer.makeI18nKey("wizard.page0.title.error");
        String preRender  = stringer.makeI18nKey("wizard.page0.pre-render");
        String postRender = stringer.makeI18nKey("wizard.page0.post-render");

        properties.setProperty(WIZARD_FORM_FIELD_LABEL_I18N_KEY, titleLabel);
        properties.addI18nProperty(titleLabel, WIZARD_FORM_FIELD_LABEL_VALUE);

        properties.setProperty(WIZARD_FORM_FIELD_PLACEHOLDER_I18N_KEY, titlePlace);
        properties.addI18nProperty(titlePlace, WIZARD_FORM_FIELD_PLACEHOLDER_VALUE);

        properties.setProperty(WIZARD_FORM_FIELD_VALIDATION_ERROR_I18N_KEY, titleError);
        properties.addI18nProperty(titleError, WIZARD_FORM_FIELD_VALIDATION_ERROR_VALUE);

        properties.setProperty(WIZARD_FORM_FIELD_PRE_RENDER_TEXT_I18N_KEY, preRender);
        properties.addI18nProperty(preRender, WIZARD_FORM_FIELD_PRE_RENDER_TEXT_VALUE);

        properties.setProperty(WIZARD_FORM_FIELD_POST_RENDER_TEXT_I18N_KEY, postRender);
        properties.addI18nProperty(postRender, WIZARD_FORM_FIELD_POST_RENDER_TEXT_VALUE);
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
            String howToUseHeadingI18nKey = pluginKey + ".wizard.how-to-use.heading";
            String howToUseContentI18nKey = pluginKey + ".wizard.how-to-use.content";
            properties.put("HOW_TO_USE", true);
            properties.setProperty(HOW_TO_USE_HEADING_I18N_KEY, howToUseHeadingI18nKey);
            properties.setProperty(HOW_TO_USE_CONTENT_I18N_KEY, howToUseContentI18nKey);
            properties.addI18nProperty(howToUseHeadingI18nKey, HOW_TO_USE_HEADING_VALUE);
            properties.addI18nProperty(howToUseContentI18nKey, HOW_TO_USE_CONTENT_VALUE);
        }

        if (hasDialogWizard)
        {
            properties.put("DIALOG_WIZARD", true);
            properties.setProperty(INDEX_KEY, indexKey);
        }
    }

    private ContentTemplateProperties makeContentTemplate(String contentTemplateKey, String webItemName, int counter)
    {
        ContentTemplateProperties template = new ContentTemplateProperties(contentTemplateKey);

        template.setModuleName(stringer.makeContentTemplateName(webItemName, counter));
        template.setDescription("Contains Storage-format XML used by the " + webItemName + " Blueprint");
        template.setNameI18nKey(contentTemplateKey + ".name");
        template.setDescriptionI18nKey(contentTemplateKey + ".desc");
        template.setContentText(contentTemplateKey + ".content.text", CONTENT_I18N_DEFAULT_VALUE);

        Resource templateResource = new Resource();
        templateResource.setName("template");
        templateResource.setType("download");

        // TODO - should allow base path to be set (so xml dir can be relative)
        templateResource.setLocation("xml/" + contentTemplateKey + ".xml");
        template.addResource(templateResource);

        return template;
    }

    private WebItemProperties makeWebItem(String pluginKey, String blueprintModuleKey, BlueprintPromptEntries promptProps)
    {
        WebItemProperties webItem = new WebItemProperties();

        // TODO - i18n key should be prefixed with the plugin key for uniqueness.
        webItem.setModuleKey(blueprintModuleKey + "-web-item");
        webItem.setModuleName((String) promptProps.get(WEB_ITEM_NAME_PROMPT));
        webItem.setDescription((String) promptProps.get(WEB_ITEM_DESC_PROMPT));
        webItem.setNameI18nKey(pluginKey + ".blueprint.display.name");
        webItem.setDescriptionI18nKey(pluginKey + ".blueprint.display.desc");
        webItem.setSection("system.create.dialog/content");
        webItem.addParam(WEB_ITEM_BLUEPRINT_KEY, blueprintModuleKey);

        // TODO - REAALLY shouldn't be like this. data-uri CSS not resource!
        Resource webItemResource = new Resource();
        webItemResource.setName("icon");
        webItemResource.setType("download");
        webItemResource.setLocation("images/" + blueprintModuleKey + "-icon.png");
        List<Resource> resources = Lists.newArrayList(webItemResource);
        webItem.setResources(resources);

        return webItem;
    }
}
