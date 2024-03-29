package com.atlassian.plugins.codegen.modules;

import java.util.Enumeration;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * @since 3.6
 */
public interface PluginModuleProperties extends Map<Object, Object>
{

    String PRODUCT_ID = "PRODUCT_ID";

    Enumeration<?> propertyNames();

    Object setProperty(java.lang.String s, java.lang.String s1);

    String getProperty(java.lang.String s);

    void setIncludeExamples(boolean includeExamples);

    boolean includeExamples();

    void setProductId(String id);

    String getProductId();

    void addI18nProperty(String name, String value);

    ImmutableMap<String, String> getI18nProperties();
}
