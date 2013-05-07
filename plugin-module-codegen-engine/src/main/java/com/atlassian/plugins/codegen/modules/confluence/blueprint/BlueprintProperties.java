package com.atlassian.plugins.codegen.modules.confluence.blueprint;

import com.atlassian.plugins.codegen.ComponentDeclaration;
import com.atlassian.plugins.codegen.modules.BasicNameModuleProperties;
import com.atlassian.plugins.codegen.modules.common.web.WebItemProperties;
import com.atlassian.plugins.codegen.modules.common.web.WebResourceProperties;
import com.google.common.collect.ImmutableMap;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Holds properties for a Confluence Blueprint.
 *
 * @since 4.1.7
 */
public class BlueprintProperties extends BasicNameModuleProperties
{
    public static final String INDEX_KEY = "INDEX_KEY";
    public static final String WEB_ITEM = "WEB_ITEM";
    private static final String WEB_RESOURCE = "WEB_RESOURCE";
    public static final String WEB_ITEM_BLUEPRINT_KEY = "blueprintKey";
    public static final String CONTENT_TEMPLATES = "CONTENT_TEMPLATES";
    private static final String HOW_TO_USE_TEMPLATE = "HOW_TO_USE_TEMPLATE";
    private static final String CREATE_RESULT = "CREATE_RESULT";
    private static final String INDEX_PAGE_TEMPLATE = "INDEX_PAGE_TEMPLATE";
    public static final String DIALOG_WIZARD = "DIALOG_WIZARD";
    public static final String INDEX_TITLE_I18N_KEY = "INDEX_TITLE_I18N_KEY";

    public static final String SOY_PACKAGE = "SOY_PACKAGE";
    public static final String HOW_TO_USE_HEADING_I18N_KEY = "HOW_TO_USE_HEADING_I18N_KEY";
    public static final String HOW_TO_USE_CONTENT_I18N_KEY = "HOW_TO_USE_CONTENT_I18N_KEY";
    public static final String WIZARD_FORM_FIELD_LABEL_I18N_KEY = "WIZARD_FORM_FIELD_LABEL_I18N_KEY";
    public static final String WIZARD_FORM_FIELD_PLACEHOLDER_I18N_KEY = "WIZARD_FORM_FIELD_PLACEHOLDER_I18N_KEY";
    public static final String HOW_TO_USE_HEADING_VALUE = "Welcome to my Blueprint";
    public static final String HOW_TO_USE_CONTENT_VALUE = "This blueprint can be used to create a special page.";
    public static final String WIZARD_FORM_FIELD_LABEL_VALUE = "Page title";
    public static final String WIZARD_FORM_FIELD_PLACEHOLDER_VALUE = "Add a title for your new page";

    public static final String PLUGIN_KEY = "PLUGIN_KEY";
    public static final String WEB_ITEM_KEY = "WEB_ITEM_KEY";
    public static final String EVENT_LISTENER = "EVENT_LISTENER";
    public static final String WIZARD_FORM_FIELD_ID = "WIZARD_FORM_FIELD_ID";
    public static final String WIZARD_FORM_FIELD_PRE_RENDER_TEXT_I18N_KEY = "WIZARD_FORM_FIELD_PRE_RENDER_TEXT_I18N_KEY";
    public static final String WIZARD_FORM_FIELD_POST_RENDER_TEXT_I18N_KEY = "WIZARD_FORM_FIELD_POST_RENDER_TEXT_I18N_KEY";
    public static final String WIZARD_FORM_FIELD_VALIDATION_ERROR_I18N_KEY = "WIZARD_FORM_FIELD_VALIDATION_ERROR_I18N_KEY";
    public static final String WIZARD_FORM_FIELD_PRE_RENDER_TEXT_VALUE = "This text comes from the pre-render hook in the Wizard JavaScript";
    public static final String WIZARD_FORM_FIELD_POST_RENDER_TEXT_VALUE = "This text comes from the post-render hook in the Wizard JavaScript";
    public static final String WIZARD_FORM_FIELD_VALIDATION_ERROR_VALUE = "You must enter a title";
    public static final String CREATE_RESULT_VIEW = "view";
    public static final String INDEX_TEMPLATE_DEFAULT_KEY = "custom-index-page-template";

    public BlueprintProperties()
    {
        List<ContentTemplateProperties> contentTemplateKeys = newArrayList();
        put(CONTENT_TEMPLATES, contentTemplateKeys);
    }

    public void setPluginKey(String pluginKey)
    {
        setProperty(PLUGIN_KEY, pluginKey);
    }

    public String getPluginKey()
    {
        return getProperty(PLUGIN_KEY);
    }

    public void addContentTemplate(ContentTemplateProperties templateProps)
    {
        getContentTemplates().add(templateProps);
    }

    public List<ContentTemplateProperties> getContentTemplates()
    {
        return (List<ContentTemplateProperties>) get(CONTENT_TEMPLATES);
    }

    public void setWebItem(WebItemProperties webItem)
    {
        put(WEB_ITEM, webItem);
    }

    public WebItemProperties getWebItem()
    {
        return (WebItemProperties) get(WEB_ITEM);
    }

    public void setWebResource(WebResourceProperties webResource)
    {
        put(WEB_RESOURCE, webResource);
    }

    public WebResourceProperties getWebResource()
    {
        return (WebResourceProperties) get(WEB_RESOURCE);
    }

    public void setIndexKey(String indexKey)
    {
        setProperty(INDEX_KEY, indexKey);
    }

    public String getIndexKey()
    {
        return getProperty(INDEX_KEY);
    }

    public void setHowToUseTemplate(String howToUseTemplate)
    {
        put(HOW_TO_USE_TEMPLATE, howToUseTemplate);
    }

    public String getHowToUseTemplate()
    {
        return (String) get(HOW_TO_USE_TEMPLATE);
    }

    public void setCreateResult(String createResult)
    {
        setProperty(CREATE_RESULT, createResult);
    }

    public String getCreateResult()
    {
        return getProperty(CREATE_RESULT);
    }

    public void setDialogWizard(DialogWizardProperties props)
    {
        put(DIALOG_WIZARD, props);
    }

    public DialogWizardProperties getDialogWizard()
    {
        return (DialogWizardProperties) get(DIALOG_WIZARD);
    }

    public void setIndexTitleI18nKey(String key)
    {
        setProperty(INDEX_TITLE_I18N_KEY, key);
    }

    public String getIndexTitleI18nKey()
    {
        return getProperty(INDEX_TITLE_I18N_KEY);
    }

    public void setEventListener(ComponentDeclaration component)
    {
        put(EVENT_LISTENER, component);
    }

    public ComponentDeclaration getEventListener()
    {
        return (ComponentDeclaration) get(EVENT_LISTENER);
    }

    @Override
    public ImmutableMap<String, String> getI18nProperties()
    {
        ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
        mapBuilder.putAll(super.getI18nProperties());
        if (getDialogWizard() != null)
        {
            mapBuilder.putAll(getDialogWizard().getI18nProperties());
        }
        return mapBuilder.build();
    }

    public ContentTemplateProperties getIndexPageContentTemplate()
    {
        return (ContentTemplateProperties) get(INDEX_PAGE_TEMPLATE);
    }

    public void setIndexPageContentTemplate(ContentTemplateProperties indexPageTemplate)
    {
        put(INDEX_PAGE_TEMPLATE, indexPageTemplate);
    }
}
