package com.atlassian.plugins.codegen.modules.stash.scm;

import com.atlassian.plugins.codegen.modules.BasicClassModuleProperties;

public class ScmRequestCheckProperties extends BasicClassModuleProperties
{

    public static final String WEIGHT = "WEIGHT";

    public ScmRequestCheckProperties(String fqClassName)
    {
        super(fqClassName);
        setWeight(150); // default
    }

    public void setWeight(int weight)
    {
        setProperty(WEIGHT, Integer.toString(weight));
    }

}
