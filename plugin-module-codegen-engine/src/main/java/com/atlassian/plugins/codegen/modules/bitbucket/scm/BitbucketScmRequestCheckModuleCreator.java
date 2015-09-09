package com.atlassian.plugins.codegen.modules.bitbucket.scm;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.BitbucketPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;

/**
 * @since 6.1.0
 */
@BitbucketPluginModuleCreator
public class BitbucketScmRequestCheckModuleCreator
        extends AbstractPluginModuleCreator<BitbucketScmRequestCheckProperties>
{

    public static final String MODULE_NAME = "SCM Request Check";

    private static final String TEMPLATE_PREFIX = "templates/bitbucket/scm/";

    private static final String CLASS_TEMPLATE = TEMPLATE_PREFIX + "ScmRequestCheck.java.vtl";
    private static final String TEST_TEMPLATE = TEMPLATE_PREFIX + "ScmRequestCheckTest.java.vtl";
    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "scm-request-check-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(BitbucketScmRequestCheckProperties props) throws Exception
    {
        return new PluginProjectChangeset()
                .with(MOCKITO_TEST)
                .with(createModule(props, PLUGIN_MODULE_TEMPLATE))
                .with(createClassAndTests(props, CLASS_TEMPLATE, TEST_TEMPLATE));
    }

    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }

}
