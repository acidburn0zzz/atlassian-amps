package com.atlassian.plugins.codegen.modules;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * @since 3.6
 */
public abstract class AbstractPluginModuleProperties extends Properties implements PluginModuleProperties
{
    protected boolean includeExamples;
    protected Map<String, String> i18nProperties;

    protected AbstractPluginModuleProperties()
    {
        super();
        i18nProperties = Maps.newHashMap();
        includeExamples = false;
        setProductId("RefApp");
    }

    protected AbstractPluginModuleProperties(AbstractPluginModuleProperties from)
    {
        super();
        putAll(from);
        i18nProperties = new HashMap(from.i18nProperties);
        includeExamples = from.includeExamples;
    }

    @Override
    public void setProductId(String id)
    {
        setProperty(PRODUCT_ID, id);
    }

    @Override
    public String getProductId()
    {
        return getProperty(PRODUCT_ID);
    }

    @Override
    public void setIncludeExamples(boolean includeExamples)
    {
        this.includeExamples = includeExamples;
    }

    @Override
    public boolean includeExamples()
    {
        return includeExamples;
    }

    @Override
    public void addI18nProperty(String name, String value)
    {
        i18nProperties.put(name, value);
    }

    @Override
    public ImmutableMap<String, String> getI18nProperties()
    {
        return ImmutableMap.copyOf(i18nProperties);
    }

}
