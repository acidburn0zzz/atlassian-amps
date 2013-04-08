package com.atlassian.plugins.codegen.modules.confluence.blueprint;

import com.atlassian.plugins.codegen.modules.BasicNameModuleProperties;
import com.atlassian.plugins.codegen.modules.common.web.WebItemProperties;
import com.atlassian.plugins.codegen.modules.common.web.WebResourceProperties;

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

    public BlueprintProperties()
    {
        List<ContentTemplateProperties> contentTemplateKeys = newArrayList();
        put(CONTENT_TEMPLATES, contentTemplateKeys);
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
        setProperty(HOW_TO_USE_TEMPLATE, howToUseTemplate);
    }

    public String getHowToUseTemplate()
    {
        return getProperty(HOW_TO_USE_TEMPLATE);
    }
}
