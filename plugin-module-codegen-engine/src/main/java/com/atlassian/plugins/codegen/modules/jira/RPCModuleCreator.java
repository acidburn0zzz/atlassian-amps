package com.atlassian.plugins.codegen.modules.jira;

import com.atlassian.plugins.codegen.ComponentDeclaration;
import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.JiraPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.plugins.codegen.modules.Dependencies.HTTPCLIENT_TEST;
import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;
import static com.atlassian.plugins.codegen.modules.Dependencies.SLF4J;
import static io.atlassian.fugue.Option.option;
import static io.atlassian.fugue.Option.some;

/**
 * @since 3.6
 */
@JiraPluginModuleCreator
public class RPCModuleCreator extends AbstractPluginModuleCreator<RPCProperties>
{
    public static final String MODULE_NAME = "RPC Endpoint Plugin";
    private static final String TEMPLATE_PREFIX = "templates/jira/rpc/";

    //stub
    private static final String CLASS_TEMPLATE = TEMPLATE_PREFIX + "RPCService.java.vtl";
    private static final String INTERFACE_TEMPLATE = TEMPLATE_PREFIX + "RPCServiceInterface.java.vtl";
    private static final String UNIT_TEST_TEMPLATE = "templates/generic/GenericTest.java.vtl";

    //examples
    private static final String EXAMPLE_CLASS_TEMPLATE = TEMPLATE_PREFIX + "Example" + CLASS_TEMPLATE;

    private static final String SOAP_PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "soap-rpc-plugin.xml.vtl";
    private static final String XML_PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "xml-rpc-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(RPCProperties props) throws Exception
    {
        String moduleKey = props.getModuleKey() + "-component";
        String description = "Component For " + props.getModuleName();
        String name = props.getModuleName() + " Component";
        String nameI18nKey = props.getNameI18nKey() + ".component";
        
        PluginProjectChangeset ret = new PluginProjectChangeset()
            .with(HTTPCLIENT_TEST,
                  SLF4J,
                  MOCKITO_TEST)
            .with(createModule(props, props.isSoap() ? SOAP_PLUGIN_MODULE_TEMPLATE : XML_PLUGIN_MODULE_TEMPLATE));

        if (props.includeExamples())
        {
            ret = ret.with(createClass(props, EXAMPLE_CLASS_TEMPLATE));
        }
        else
        {
            ret = ret.with(createClassAndTests(props, CLASS_TEMPLATE, UNIT_TEST_TEMPLATE))
                .with(createClass(props, props.getInterfaceId(), INTERFACE_TEMPLATE));
        }
        
        ComponentDeclaration component = ComponentDeclaration.builder(props.getClassId(), moduleKey)
            .interfaceId(option(props.getInterfaceId()))
            .description(some(description))
            .name(some(name))
            .nameI18nKey(some(nameI18nKey))
            .build();
        return ret.with(component);
    }

    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
