package com.atlassian.plugins.codegen.modules.confluence.blueprint;

import com.atlassian.plugins.codegen.modules.common.Resource;
import com.atlassian.plugins.codegen.modules.common.web.WebItemProperties;
import com.atlassian.plugins.codegen.modules.common.web.WebResourceProperties;
import com.atlassian.plugins.codegen.modules.common.web.WebResourceTransformation;
import com.atlassian.plugins.codegen.modules.common.web.WebResourceTransformerProperties;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    private final Map<BlueprintPromptEntry, Object> promptProps;
    private final BlueprintProperties props;
    private BlueprintStringer stringer;

    public BlueprintBuilder(Map<BlueprintPromptEntry, Object> promptProps)
    {
        this.promptProps = Collections.unmodifiableMap(promptProps);
        props = new BlueprintProperties();
    }

    @SuppressWarnings("unchecked")
    public BlueprintProperties build()
    {
        String indexKey = (String) promptProps.get(BlueprintPromptEntry.INDEX_KEY_PROMPT);
        props.setProperty(INDEX_KEY, indexKey);

        stringer = new BlueprintStringer(indexKey);
        String blueprintModuleKey = stringer.makeBlueprintModuleKey();
        props.setModuleKey(blueprintModuleKey);

        props.setWebItem(makeWebItem(indexKey, blueprintModuleKey, promptProps));
        String blueprintName = (String) promptProps.get(WEB_ITEM_NAME_PROMPT);
        props.setModuleName(stringer.makeBlueprintModuleName(blueprintName));

        List<String> contentTemplateKeys = (List<String>) promptProps.get(CONTENT_TEMPLATE_KEYS_PROMPT);
        for (int i = 0; i < contentTemplateKeys.size(); i++)
        {
            String contentTemplateKey = contentTemplateKeys.get(i);
            ContentTemplateProperties contentTemplate = makeContentTemplate(contentTemplateKey, blueprintName, i);
            props.addContentTemplate(contentTemplate);
        }

        // Reused for icon CSS, how-to-use and dialog-wizard.
        WebResourceProperties webResource = new WebResourceProperties();
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
            DialogPageProperties wizardPage = new DialogPageProperties(indexKey, 0, soyPackage);
            wizard.setDialogPages(Lists.newArrayList(wizardPage));
            props.setDialogWizard(wizard);
        }
        if (hasHowToUse || hasDialogWizard)
        {
            addSoyTemplateToWebResource(webResource, indexKey, soyPackage, hasHowToUse, hasDialogWizard);
        }

        return props;
    }

    private void addSoyTemplateToWebResource(WebResourceProperties properties, String indexKey, String soyPackage,
        boolean hasHowToUse, boolean hasDialogWizard)
    {
        // Soy transformer
        WebResourceTransformation transformation = new WebResourceTransformation("soy");
        WebResourceTransformerProperties transformer = new WebResourceTransformerProperties();
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
            String howToUseHeadingI18nKey = indexKey + "-blueprint.wizard.how-to-use.heading";
            String howToUseContentI18nKey = indexKey + "-blueprint.wizard.how-to-use.content";
            properties.put("HOW_TO_USE", true);
            properties.setProperty(HOW_TO_USE_HEADING_I18N_KEY, howToUseHeadingI18nKey);
            properties.setProperty(HOW_TO_USE_CONTENT_I18N_KEY, howToUseContentI18nKey);
            properties.addI18nProperty(howToUseHeadingI18nKey, HOW_TO_USE_HEADING_VALUE);
            properties.addI18nProperty(howToUseContentI18nKey, HOW_TO_USE_CONTENT_VALUE);
        }

        if (hasDialogWizard)
        {
            String wizardPageFieldLabelI18nKey = indexKey + "-blueprint.wizard.page0.title.label";
            String wizardPageFieldPlaceholderI18nKey = indexKey + "-blueprint.wizard.page0.title.placeholder";
            properties.put("DIALOG_WIZARD", true);
            properties.setProperty(INDEX_KEY, indexKey);
            properties.setProperty(WIZARD_FORM_FIELD_LABEL_I18N_KEY, wizardPageFieldLabelI18nKey);
            properties.setProperty(WIZARD_FORM_FIELD_PLACEHOLDER_I18N_KEY, wizardPageFieldPlaceholderI18nKey);
            properties.addI18nProperty(wizardPageFieldLabelI18nKey, WIZARD_FORM_FIELD_LABEL_VALUE);
            properties.addI18nProperty(wizardPageFieldPlaceholderI18nKey, WIZARD_FORM_FIELD_PLACEHOLDER_VALUE);
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

    private WebItemProperties makeWebItem(String indexKey, String blueprintModuleKey, Map<BlueprintPromptEntry, Object> promptProps)
    {
        WebItemProperties webItem = new WebItemProperties();

        // TODO - i18n key should be prefixed with the plugin key for uniqueness.
        webItem.setModuleKey(blueprintModuleKey + "-web-item");
        webItem.setModuleName((String) promptProps.get(WEB_ITEM_NAME_PROMPT));
        webItem.setDescription((String) promptProps.get(WEB_ITEM_DESC_PROMPT));
        webItem.setNameI18nKey(indexKey + "-blueprint.display.name");
        webItem.setDescriptionI18nKey(indexKey + "-blueprint.display.desc");
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
