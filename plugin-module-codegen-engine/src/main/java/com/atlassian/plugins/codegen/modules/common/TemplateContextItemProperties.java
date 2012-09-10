package com.atlassian.plugins.codegen.modules.common;

import com.atlassian.plugins.codegen.ClassId;
import com.atlassian.plugins.codegen.modules.AbstractClassBasedModuleProperties;

/**
 * @since 3.6
 */
public class TemplateContextItemProperties extends AbstractClassBasedModuleProperties
{
    public static final String CONTEXT_KEY = "CONTEXT_KEY";
    public static final String COMPONENT_REF = "COMPONENT_REF";
    public static final String GLOBAL = "GLOBAL";

    public TemplateContextItemProperties()
    {
        this("My Template Context Item");
    }

    protected TemplateContextItemProperties(TemplateContextItemProperties from, ClassId newClass)
    {
        super(from, newClass);
    }

    public TemplateContextItemProperties withClass(ClassId newClass)
    {
        return new TemplateContextItemProperties(this, newClass);
    }

    public TemplateContextItemProperties(String moduleName)
    {
        super();
        setModuleNameAndKey(moduleName);
        setGlobal(false);
    }

    public TemplateContextItemProperties(String moduleName, String contextKey)
    {
        this(moduleName);
        setContextKey(contextKey);
    }

    public void setContextKey(String key)
    {
        setProperty(CONTEXT_KEY, key);
    }

    public String getContextKey()
    {
        return getProperty(CONTEXT_KEY);
    }

    public void setComponentRef(String ref)
    {
        setProperty(COMPONENT_REF, ref);
    }

    public String getComponentRef()
    {
        return getProperty(COMPONENT_REF);
    }

    public void setGlobal(boolean global)
    {
        setProperty(GLOBAL, Boolean.toString(global));
    }

    public boolean isGlobal()
    {
        return Boolean.valueOf(getProperty(GLOBAL));
    }
}
