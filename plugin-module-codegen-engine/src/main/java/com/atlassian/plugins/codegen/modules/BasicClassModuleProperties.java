package com.atlassian.plugins.codegen.modules;

import com.atlassian.plugins.codegen.ClassId;
import com.atlassian.plugins.codegen.util.ClassnameUtil;

/**
 *
 */
public class BasicClassModuleProperties extends AbstractClassBasedModuleProperties
{   
    public BasicClassModuleProperties()
    {
        this("MyPluginModule");
    }

    protected BasicClassModuleProperties(BasicClassModuleProperties from, ClassId newClass)
    {
        super(from, newClass);
    }

    public BasicClassModuleProperties(String fqClassName)
    {
        super();

        setFullyQualifiedClassname(fqClassName);

        String classname = getProperty(CLASSNAME);
        setModuleName(ClassnameUtil.camelCaseToSpaced(classname));
        setModuleKey(ClassnameUtil.camelCaseToDashed(classname)
                .toLowerCase());
        setDescription("The " + getProperty(MODULE_NAME) + " Plugin");
        setNameI18nKey(getProperty(MODULE_KEY) + ".name");
        setDescriptionI18nKey(getProperty(MODULE_KEY) + ".description");
    }

    public ClassBasedModuleProperties withClass(ClassId newClass)
    {
        return new BasicClassModuleProperties(this, newClass);
    }
}
