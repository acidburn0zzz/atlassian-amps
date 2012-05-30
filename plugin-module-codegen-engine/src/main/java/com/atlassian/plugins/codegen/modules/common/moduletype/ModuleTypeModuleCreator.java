package com.atlassian.plugins.codegen.modules.common.moduletype;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.BambooPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.ConfluencePluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.CrowdPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.FeCruPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.JiraPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.RefAppPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.StashPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;

/**
 * @since 3.6
 */
@RefAppPluginModuleCreator
@JiraPluginModuleCreator
@ConfluencePluginModuleCreator
@BambooPluginModuleCreator
@FeCruPluginModuleCreator
@CrowdPluginModuleCreator
@StashPluginModuleCreator
public class ModuleTypeModuleCreator extends AbstractPluginModuleCreator<ModuleTypeProperties>
{
    public static final String MODULE_NAME = "Module Type";
    private static final String TEMPLATE_PREFIX = "templates/common/moduletype/";

    //stub
    private static final String CLASS_TEMPLATE = TEMPLATE_PREFIX + "ModuleType.java.vtl";
    private static final String INTERFACE_TEMPLATE = TEMPLATE_PREFIX + "ModuleTypeInterface.java.vtl";

    //examples
    private static final String EXAMPLE_CLASS_TEMPLATE = TEMPLATE_PREFIX + "Example" + CLASS_TEMPLATE;

    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "module-type-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(ModuleTypeProperties props) throws Exception
    {
        PluginProjectChangeset ret = new PluginProjectChangeset()
            .with(MOCKITO_TEST)
            .with(createModule(props, PLUGIN_MODULE_TEMPLATE));

        
        if (props.includeExamples())
        {
            return ret.with(createClass(props, EXAMPLE_CLASS_TEMPLATE));
        }
        else
        {
            return ret.with(createClass(props, props.getInterfaceId(), INTERFACE_TEMPLATE))
                .with(createClassAndTests(props, CLASS_TEMPLATE, GENERIC_TEST_TEMPLATE, GENERIC_TEST_TEMPLATE));
        }
    }

    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
