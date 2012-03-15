package com.atlassian.plugins.codegen;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * Describes an key-value pair that should be added to the plugin project's I18n strings file.
 */
public class I18nString extends AbstractPropertyValue implements PluginProjectChange
{
    public static I18nString i18nString(String name, String value)
    {
        return new I18nString(name, value);
    }

    public static Iterable<I18nString> i18nStrings(Map<String, String> map)
    {
        return Iterables.<Map.Entry<String, String>, I18nString>transform(map.entrySet(), new Function<Map.Entry<String, String>, I18nString>()
        {
            public I18nString apply(Map.Entry<String, String> entry)
            {
                return new I18nString(entry.getKey(), entry.getValue());
            }
        });
    }
    
    private I18nString(String name, String value)
    {
        super(name, value);
    }
    
    @Override
    public String toString()
    {
        return "[i18n: " + super.toString() + "]";
    }
}
