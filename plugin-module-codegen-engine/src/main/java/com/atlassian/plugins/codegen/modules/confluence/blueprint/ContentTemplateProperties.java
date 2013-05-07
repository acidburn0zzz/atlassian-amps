package com.atlassian.plugins.codegen.modules.confluence.blueprint;

import com.atlassian.fugue.Pair;
import com.atlassian.plugins.codegen.modules.AbstractNameBasedModuleProperties;
import com.atlassian.plugins.codegen.modules.common.ContextProviderProperties;
import com.atlassian.plugins.codegen.modules.common.Resource;
import com.atlassian.plugins.codegen.modules.common.ResourcedProperties;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Holds properties for the <content-template> module descriptor used in Blueprint creation.
 *
 * @since 4.1.7
 */
public class ContentTemplateProperties extends AbstractNameBasedModuleProperties implements ResourcedProperties
{
    public static final String LOCATION = "LOCATION";
    public static final String RESOURCES = "RESOURCES";
    public static final String CONTEXT_PROVIDER = "CONTEXT_PROVIDER";
    public static final String CONTENT_I18N_KEY = "CONTENT_I18N_KEY";
    public static final String CONTENT_I18N_VALUE = "CONTENT_I18N_VALUE";
    public static final String CONTENT_I18N_DEFAULT_VALUE = "This text will replace the at:i18n placeholder in the content template XML.";
    public static final String INDEX_TEMPLATE_CONTENT_VALUE =
        "This index page has used instead of the default one, by setting the index-template-key attribute of the blueprint config element.";

    public ContentTemplateProperties(String moduleKey)
    {
        super();
        setModuleKey(moduleKey);
    }

    public void addResource(Resource resource)
    {
        List<Resource> resources = getResources();
        if (resources == null)
        {
            resources = Lists.newArrayList();
            put(RESOURCES, resources);
        }
        resources.add(resource);
    }

    public void setResources(List<Resource> resources)
    {
        put(RESOURCES, resources);
    }

    public List<Resource> getResources()
    {
        return (List<Resource>) get(RESOURCES);
    }

    public void setContextProvider(ContextProviderProperties provider)
    {
        put(CONTEXT_PROVIDER, provider);
    }

    public ContextProviderProperties getContextProvider()
    {
        return (ContextProviderProperties) get(CONTEXT_PROVIDER);
    }

    /**
     * Sets the i18n key and value for the at:i18n placeholder in the template file XML.
     */
    public void setContentText(String i18nKey, String value)
    {
        put(CONTENT_I18N_KEY, i18nKey);
        put(CONTENT_I18N_VALUE, value);
    }

    @Override
    public ImmutableMap<String, String> getI18nProperties()
    {
        return ImmutableMap.<String, String>builder()
            .putAll(super.getI18nProperties())
            .put(getProperty(CONTENT_I18N_KEY), getProperty(CONTENT_I18N_VALUE))
            .build();
    }

    public Pair getContentText()
    {
        return new Pair(getProperty(CONTENT_I18N_KEY), getProperty(CONTENT_I18N_VALUE));
    }
}
