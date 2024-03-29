package com.atlassian.plugins.codegen.modules.bitbucket.hook;

import com.atlassian.plugins.codegen.modules.BasicClassModuleProperties;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @since 6.1.0
 */
public class BitbucketRepositoryHookProperties extends BasicClassModuleProperties
{

    private static final String ICON = "ICON";
    private static final String FIELDS = "FIELDS";
    private static final String SOY_PACKAGE = "SOY_PACKAGE";

    private final String type;

    public BitbucketRepositoryHookProperties(String fqRequestClassName, String type)
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
        return (String) get(ICON);
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
        @SuppressWarnings("unchecked")
        List<String> fields = (List) get(FIELDS);
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
            if (type.equals(BitbucketRepositoryHookModuleCreator.TYPE_POST))
            {
                addField("url");
            }
            else if (type.equals(BitbucketRepositoryHookModuleCreator.TYPE_MERGE_CHECK))
            {
                addField("reviewers");
            }
        }
        else
        {
            remove(FIELDS);
        }
    }

    @Override
    public void setFullyQualifiedClassname(String fqName) {
        super.setFullyQualifiedClassname(fqName);
        put(SOY_PACKAGE, getClassId().getPackage() + "." + getClassId().getName().toLowerCase());
    }

    public String getSoyFile() {
        return getProperty(MODULE_KEY) + ".soy";
    }
}
