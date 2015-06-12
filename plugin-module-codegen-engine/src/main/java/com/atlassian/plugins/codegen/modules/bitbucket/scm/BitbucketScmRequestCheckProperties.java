package com.atlassian.plugins.codegen.modules.bitbucket.scm;

import com.atlassian.plugins.codegen.modules.BasicClassModuleProperties;

/**
 * @since 6.1.0
 */
public class BitbucketScmRequestCheckProperties extends BasicClassModuleProperties
{

    public static final String WEIGHT = "WEIGHT";

    public BitbucketScmRequestCheckProperties(String fqClassName)
    {
        super(fqClassName);
        setWeight(150); // default
    }

    public void setWeight(int weight)
    {
        setProperty(WEIGHT, Integer.toString(weight));
    }

}
