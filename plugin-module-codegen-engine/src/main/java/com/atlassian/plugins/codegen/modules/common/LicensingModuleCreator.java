package com.atlassian.plugins.codegen.modules.common;

import com.atlassian.plugins.codegen.ComponentDeclaration;
import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.BambooPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.ConfluencePluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.CrowdPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.FeCruPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.JiraPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.RefAppPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import com.google.common.collect.ImmutableMap;

import static com.atlassian.fugue.Option.some;
import static com.atlassian.plugins.codegen.ArtifactDependency.dependency;
import static com.atlassian.plugins.codegen.ArtifactDependency.Scope.PROVIDED;
import static com.atlassian.plugins.codegen.ClassId.fullyQualified;
import static com.atlassian.plugins.codegen.ComponentImport.componentImport;

/**
 * @since 3.7
 */
@RefAppPluginModuleCreator
@JiraPluginModuleCreator
@ConfluencePluginModuleCreator
@BambooPluginModuleCreator
@FeCruPluginModuleCreator
@CrowdPluginModuleCreator
public class LicensingModuleCreator extends AbstractPluginModuleCreator<LicensingProperties>
{
    public static final String MODULE_NAME = "Atlassian License Management";

    public static final String LICENSE_CHECKER_DESCRIPTION = "Atlassian license management module";
    public static final String LICENSE_CHECKER_CLASS_TEMPLATE = "templates/common/licensing/LicenseChecker.java.vtl";
    
    @Override
    public PluginProjectChangeset createModule(LicensingProperties props) throws Exception
    {
        ComponentDeclaration licensingComponent = ComponentDeclaration.builder(props.getClassId(), props.getModuleKey())
            .description(some(LICENSE_CHECKER_DESCRIPTION))
            .build();
        return new PluginProjectChangeset()
            .withDependencies(dependency("com.atlassian.upm", "licensing-api", "2.0", PROVIDED),
                                      dependency("com.atlassian.upm", "upm-api", "2.0", PROVIDED),
                                      dependency("com.atlassian.plugins", "atlassian-plugins-core", "2.9.0", PROVIDED),
                                      dependency("com.atlassian.sal", "sal-api", "2.6.0", PROVIDED))
            .withPluginParameters(ImmutableMap.of("atlassian-licensing-enabled", "true"))
            .withComponentImports(componentImport(fullyQualified("com.atlassian.upm.api.license.PluginLicenseManager")),
                                  componentImport(fullyQualified("com.atlassian.upm.api.license.PluginLicenseEventRegistry")),
                                  componentImport(fullyQualified("com.atlassian.plugin.PluginController")))
            .withComponentDeclarations(licensingComponent)
            .with(createClass(props, LICENSE_CHECKER_CLASS_TEMPLATE));
    }

    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
