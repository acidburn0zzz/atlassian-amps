package com.atlassian.plugins.codegen.modules.bitbucket.idx;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.BitbucketPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;

/**
 * @since 6.1.0
 */
@BitbucketPluginModuleCreator
public class BitbucketCommitIndexerModuleCreator extends AbstractPluginModuleCreator<BitbucketCommitIndexerProperties>
{

    public static final String MODULE_NAME = "Commit Indexer";

    private static final String TEMPLATE_PREFIX = "templates/bitbucket/idx/";

    private static final String CLASS_TEMPLATE = TEMPLATE_PREFIX + "CommitIndexer.java.vtl";
    private static final String TEST_TEMPLATE = TEMPLATE_PREFIX + "CommitIndexerTest.java.vtl";
    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "commit-indexer-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(BitbucketCommitIndexerProperties props) throws Exception
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
