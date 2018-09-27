package com.atlassian.plugins.codegen.modules.common.component;

import com.atlassian.plugins.codegen.ComponentImport;
import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.BambooPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.BitbucketPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.ConfluencePluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.CrowdPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.FeCruPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.JiraPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.RefAppPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.plugins.codegen.ComponentImport.componentImport;
import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;
import static com.atlassian.plugins.codegen.modules.Dependencies.SLF4J;
import static io.atlassian.fugue.Option.option;

/**
 * @since 3.6
 */
@RefAppPluginModuleCreator
@JiraPluginModuleCreator
@ConfluencePluginModuleCreator
@BambooPluginModuleCreator
@BitbucketPluginModuleCreator
@FeCruPluginModuleCreator
@CrowdPluginModuleCreator
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
