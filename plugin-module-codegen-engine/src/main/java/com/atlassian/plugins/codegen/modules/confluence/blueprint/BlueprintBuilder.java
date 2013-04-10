package com.atlassian.plugins.codegen.modules.confluence.blueprint;

import com.atlassian.plugins.codegen.modules.common.Resource;
import com.atlassian.plugins.codegen.modules.common.web.WebItemProperties;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintPromptEntry.*;
import static com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintProperties.INDEX_KEY;
import static com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintProperties.WEB_ITEM_BLUEPRINT_KEY;
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
        String indexKey = (String) promptProps.get(BlueprintPromptEntry.INDEX_KEY);
        props.setProperty(INDEX_KEY, indexKey);

        stringer = new BlueprintStringer(indexKey);
        String blueprintModuleKey = stringer.makeBlueprintModuleKey();
        props.setModuleKey(blueprintModuleKey);

        props.setWebItem(makeWebItem(indexKey, blueprintModuleKey, promptProps));
        String blueprintName = (String) promptProps.get(WEB_ITEM_NAME);
        props.setModuleName(stringer.makeBlueprintModuleName(blueprintName));

        List<String> contentTemplateKeys = (List<String>) promptProps.get(CONTENT_TEMPLATE_KEYS);
        for (int i = 0; i < contentTemplateKeys.size(); i++)
        {
            String contentTemplateKey = contentTemplateKeys.get(i);
            ContentTemplateProperties contentTemplate = makeContentTemplate(contentTemplateKey, blueprintName, i);
            props.addContentTemplate(contentTemplate);
        }

        if ((Boolean)promptProps.get(HOW_TO_USE))
        {
            String howToUseTemplate = stringer.makeHowToUseTemplateRef(blueprintName);
            props.setHowToUseTemplate(howToUseTemplate);
        }

        return props;
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
        webItem.setModuleName((String) promptProps.get(WEB_ITEM_NAME));
        webItem.setDescription((String) promptProps.get(WEB_ITEM_DESC));
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
