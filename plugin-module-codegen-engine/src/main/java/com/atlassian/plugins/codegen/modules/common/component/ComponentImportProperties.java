package com.atlassian.plugins.codegen.modules.common.component;

import com.atlassian.plugins.codegen.modules.ClassWithInterfaceProperties;

import org.apache.commons.lang3.StringUtils;

/**
 * @since 3.6
 */
public class ComponentImportProperties extends ClassWithInterfaceProperties
{
    public static final String FILTER = "FILTER";
    public static final String FQ_INTERFACE = "FQ_INTERFACE";

    public ComponentImportProperties()
    {
        this("MyComponentImportProperties");
    }

    public ComponentImportProperties(String fqClassName)
    {
        if (StringUtils.isNotBlank(fqClassName))
        {
            setFullyQualifiedInterface(fqClassName);
            setModuleKey(StringUtils.uncapitalize(getInterfaceId().getName()));
        }

        setFilter("");
    }

    public void setFilter(String filter)
    {
        setProperty(FILTER, filter);
    }

    public String getFilter()
    {
        return getProperty(FILTER);
    }
}
