package com.atlassian.plugins.codegen.modules.common.component;

import com.atlassian.plugins.codegen.ComponentDeclaration;
import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.BambooPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.ConfluencePluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.CrowdPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.FeCruPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.JiraPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.RefAppPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.StashPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.fugue.Option.option;
import static com.atlassian.fugue.Option.some;
import static com.atlassian.plugins.codegen.ComponentDeclaration.Visibility.PRIVATE;
import static com.atlassian.plugins.codegen.ComponentDeclaration.Visibility.PUBLIC;
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
public class ComponentModuleCreator extends AbstractPluginModuleCreator<ComponentProperties>
{
    public static final String MODULE_NAME = "Component";
    private static final String TEMPLATE_PREFIX = "templates/common/component/";

    //stub
    private static final String CLASS_TEMPLATE = TEMPLATE_PREFIX + "Component.java.vtl";
    private static final String INTERFACE_TEMPLATE = TEMPLATE_PREFIX + "ComponentInterface.java.vtl";

    //examples
    private static final String EXAMPLE_CLASS_TEMPLATE = TEMPLATE_PREFIX + "Example" + CLASS_TEMPLATE;

    @Override
    public PluginProjectChangeset createModule(ComponentProperties props) throws Exception
    {
        ComponentDeclaration.Builder component = ComponentDeclaration.builder(props.getClassId(), props.getModuleKey())
            .name(option(props.getModuleName()))
            .nameI18nKey(option(props.getNameI18nKey()))
            .description(option(props.getDescription()))
            .descriptionI18nKey(option(props.getDescriptionI18nKey()))
            .visibility(props.isPublic() ? PUBLIC : PRIVATE);
        if (props.generateInterface())
        {
            component.interfaceId(some(props.getInterfaceId()));
        }
        if (props.getServiceProps() != null)
        {
            component.serviceProperties(props.getServiceProps());
        }

        PluginProjectChangeset ret = new PluginProjectChangeset()
            .with(MOCKITO_TEST)
            .with(component.build());
        
        if (props.includeExamples())
        {
            return ret.with(createClass(props, EXAMPLE_CLASS_TEMPLATE));
        }
        else
        {
            if (props.generateClass())
            {
                ret = ret.with(createClassAndTests(props, CLASS_TEMPLATE, GENERIC_TEST_TEMPLATE, GENERIC_TEST_TEMPLATE));
            }
            if (props.generateInterface())
            {
                ret = ret.with(createClass(props, props.getInterfaceId(), INTERFACE_TEMPLATE));
            }
            return ret;
        }
    }

    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
