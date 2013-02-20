package com.atlassian.plugins.codegen.modules.stash.ssh;

import com.atlassian.plugins.codegen.ClassId;
import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.StashPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;

@StashPluginModuleCreator
public class SshScmRequestHandlerModuleCreator extends AbstractPluginModuleCreator<SshScmRequestHandlerProperties> {

    public static final String MODULE_NAME = "SSH Request Handler";

    private static final String TEMPLATE_PREFIX = "templates/stash/ssh/";

    private static final String REQUEST_TEMPLATE = TEMPLATE_PREFIX + "SshScmRequest.java.vtl";
    private static final String REQUEST_TEST_TEMPLATE = TEMPLATE_PREFIX + "SshScmRequestTest.java.vtl";
    private static final String HANDLER_TEMPLATE = TEMPLATE_PREFIX + "SshScmRequestHandler.java.vtl";
    private static final String HANDLER_TEST_TEMPLATE = TEMPLATE_PREFIX + "SshScmRequestHandlerTest.java.vtl";
    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "ssh-request-handler-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(SshScmRequestHandlerProperties props) throws Exception {
        ClassId requestTestClass = testClassFor(props.getRequestClassId());
        return new PluginProjectChangeset()
                .with(MOCKITO_TEST)
                .with(createModule(props, PLUGIN_MODULE_TEMPLATE))
                .with(createClass(props, props.getRequestClassId(), REQUEST_TEMPLATE))
                .with(createTestClass(props.withClass(requestTestClass), requestTestClass, REQUEST_TEST_TEMPLATE))
                .with(createClassAndTests(props, HANDLER_TEMPLATE, HANDLER_TEST_TEMPLATE));
    }

    @Override
    public String getModuleName() {
        return MODULE_NAME;
    }

}
