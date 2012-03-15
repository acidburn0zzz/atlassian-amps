package com.atlassian.plugins.codegen.modules.common.licensing;

import com.atlassian.plugins.codegen.ComponentDeclaration;
import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.VersionId;
import com.atlassian.plugins.codegen.annotations.BambooPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.ConfluencePluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.CrowdPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.FeCruPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.JiraPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.RefAppPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.fugue.Option.some;
import static com.atlassian.plugins.codegen.ArtifactDependency.dependency;
import static com.atlassian.plugins.codegen.ArtifactDependency.Scope.PROVIDED;
import static com.atlassian.plugins.codegen.ClassId.fullyQualified;
import static com.atlassian.plugins.codegen.ComponentImport.componentImport;
import static com.atlassian.plugins.codegen.PluginParameter.pluginParameter;

/**
 * @since 3.7
 */
@RefAppPluginModuleCreator
@JiraPluginModuleCreator
@ConfluencePluginModuleCreator
@BambooPluginModuleCreator
@FeCruPluginModuleCreator
@CrowdPluginModuleCreator
public class LicensingUpm2ModuleCreator extends AbstractPluginModuleCreator<LicensingProperties>
{
    public static final String MODULE_NAME = "License Management (UPM 2 API)";

    public static final String LICENSE_CHECKER_DESCRIPTION = "Atlassian license management module";
    public static final String LICENSE_CHECKER_CLASS_TEMPLATE = "templates/common/licensing/LicenseChecker.java.vtl";
    
    public static final VersionId UPM_API_VERSION_PROPERTY = VersionId.versionProperty("upm.api.version", "2.0");
    
    @Override
    public PluginProjectChangeset createModule(LicensingProperties props) throws Exception
    {
        ComponentDeclaration licensingComponent = ComponentDeclaration.builder(props.getClassId(), props.getModuleKey())
            .description(some(LICENSE_CHECKER_DESCRIPTION))
            .build();
        return new PluginProjectChangeset()
            .with(dependency("com.atlassian.upm", "licensing-api", UPM_API_VERSION_PROPERTY, PROVIDED),
                  dependency("com.atlassian.upm", "upm-api", UPM_API_VERSION_PROPERTY, PROVIDED),
                  dependency("com.atlassian.plugins", "atlassian-plugins-core", "2.9.0", PROVIDED),
                  dependency("com.atlassian.sal", "sal-api", "2.6.0", PROVIDED))
            .with(pluginParameter("atlassian-licensing-enabled", "true"))
            .with(componentImport(fullyQualified("com.atlassian.upm.api.license.PluginLicenseManager")),
                  componentImport(fullyQualified("com.atlassian.upm.api.license.PluginLicenseEventRegistry")),
                  componentImport(fullyQualified("com.atlassian.plugin.PluginController")))
            .with(licensingComponent)
            .with(createClass(props, LICENSE_CHECKER_CLASS_TEMPLATE));
    }

    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
