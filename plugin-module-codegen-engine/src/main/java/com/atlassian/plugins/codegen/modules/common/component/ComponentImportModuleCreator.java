package com.atlassian.plugins.codegen.modules.common.component;

import com.atlassian.plugins.codegen.ComponentImport;
import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.*;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.fugue.Option.option;
import static com.atlassian.plugins.codegen.ComponentImport.componentImport;
import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;
import static com.atlassian.plugins.codegen.modules.Dependencies.SLF4J;

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
public class ComponentImportModuleCreator extends AbstractPluginModuleCreator<ComponentImportProperties>
{
    public static final String MODULE_NAME = "Component Import";

    @Override
    public PluginProjectChangeset createModule(ComponentImportProperties props) throws Exception
    {
        ComponentImport componentImport = componentImport(props.getInterfaceId())
            .key(option(props.getModuleKey()))
            .filter(option(props.getFilter()));
        
        return new PluginProjectChangeset()
            .with(SLF4J,
                  MOCKITO_TEST)
            .with(componentImport);
    }

    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
