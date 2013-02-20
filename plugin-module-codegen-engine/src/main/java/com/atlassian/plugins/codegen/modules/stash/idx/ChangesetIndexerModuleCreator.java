package com.atlassian.plugins.codegen.modules.stash.idx;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.StashPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;

@StashPluginModuleCreator
public class ChangesetIndexerModuleCreator extends AbstractPluginModuleCreator<ChangesetIndexerProperties> {

    public static final String MODULE_NAME = "Changeset Indexer";

    private static final String TEMPLATE_PREFIX = "templates/stash/idx/";

    private static final String CLASS_TEMPLATE = TEMPLATE_PREFIX + "ChangesetIndexer.java.vtl";
    private static final String TEST_TEMPLATE = TEMPLATE_PREFIX + "ChangesetIndexerTest.java.vtl";
    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "changeset-indexer-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(ChangesetIndexerProperties props) throws Exception {
        return new PluginProjectChangeset()
                .with(MOCKITO_TEST)
                .with(createModule(props, PLUGIN_MODULE_TEMPLATE))
                .with(createClassAndTests(props, CLASS_TEMPLATE, TEST_TEMPLATE));
    }

    @Override
    public String getModuleName() {
        return MODULE_NAME;
    }

}
