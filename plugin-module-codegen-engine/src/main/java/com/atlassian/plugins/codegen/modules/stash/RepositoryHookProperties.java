package com.atlassian.plugins.codegen.modules.stash;

import com.atlassian.plugins.codegen.modules.BasicClassModuleProperties;
import com.google.common.collect.Lists;

import java.util.List;

public class RepositoryHookProperties extends BasicClassModuleProperties
{
    private static final String ICON = "ICON";
    private static final String FIELDS = "FIELDS";

    private final String type;
    private String icon;

    public RepositoryHookProperties(String fqRequestClassName, String type)
    {
        super(fqRequestClassName);
        this.type = type;
        setConfigured(true);
        setIcon(true);
    }

    public String getType()
    {
        return type;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(boolean icon)
    {
        if (icon)
        {
            put(ICON, "icon-example.png");
        }
        else
        {
            remove(ICON);
        }
    }

    public boolean ifConfigured()
    {
        return get(FIELDS) != null;
    }

    public void addField(String fieldName)
    {
        List<String> fields = (List<String>) get(FIELDS);
        if (fields == null)
        {
            put(FIELDS, fields = Lists.newArrayList());
        }
        fields.add(fieldName);
    }

    public void setConfigured(boolean configured)
    {
        if (configured)
        {
            if (type.equals(RepositoryHookModuleCreator.TYPE_POST))
            {
                addField("url");
            }
            else if (type.equals(RepositoryHookModuleCreator.TYPE_MERGE_CHECK))
            {
                addField("reviewers");
            }
        }
        else
        {
            remove(FIELDS);
        }
    }
}
