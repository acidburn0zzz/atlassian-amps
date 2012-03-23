package com.atlassian.plugins.codegen.modules.common.licensing;

import com.atlassian.plugins.codegen.ArtifactDependency;
import com.atlassian.plugins.codegen.BundleInstruction;
import com.atlassian.plugins.codegen.ClassId;
import com.atlassian.plugins.codegen.ComponentDeclaration;
import com.atlassian.plugins.codegen.ComponentImport;
import com.atlassian.plugins.codegen.PluginArtifact;
import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.VersionId;
import com.atlassian.plugins.codegen.annotations.BambooPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.ConfluencePluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.CrowdPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.FeCruPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.JiraPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.RefAppPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.BasicClassModuleProperties;
import com.atlassian.plugins.codegen.modules.common.servlet.ServletModuleCreator;
import com.atlassian.plugins.codegen.modules.common.servlet.ServletProperties;

import static com.atlassian.fugue.Option.some;
import static com.atlassian.plugins.codegen.ArtifactDependency.dependency;
import static com.atlassian.plugins.codegen.ArtifactDependency.Scope.COMPILE;
import static com.atlassian.plugins.codegen.ArtifactDependency.Scope.PROVIDED;
import static com.atlassian.plugins.codegen.ArtifactId.artifactId;
import static com.atlassian.plugins.codegen.BundleInstruction.dynamicImportPackage;
import static com.atlassian.plugins.codegen.BundleInstruction.privatePackage;
import static com.atlassian.plugins.codegen.ClassId.fullyQualified;
import static com.atlassian.plugins.codegen.ComponentDeclaration.componentDeclaration;
import static com.atlassian.plugins.codegen.ComponentImport.componentImport;
import static com.atlassian.plugins.codegen.MavenPlugin.mavenPlugin;
import static com.atlassian.plugins.codegen.PluginArtifact.pluginArtifact;
import static com.atlassian.plugins.codegen.PluginArtifact.ArtifactType.BUNDLED_ARTIFACT;
import static com.atlassian.plugins.codegen.PluginParameter.pluginParameter;
import static com.atlassian.plugins.codegen.ResourceFile.resourceFile;
import static com.atlassian.plugins.codegen.VersionId.noVersion;
import static com.atlassian.plugins.codegen.VersionId.version;
import static com.atlassian.plugins.codegen.VersionId.versionProperty;

/**
 * @since 3.9
 */
@RefAppPluginModuleCreator
@JiraPluginModuleCreator
@ConfluencePluginModuleCreator
@BambooPluginModuleCreator
@FeCruPluginModuleCreator
@CrowdPluginModuleCreator
public class LicensingUpm1CompatibleModuleCreator extends AbstractPluginModuleCreator<LicensingProperties>
{
    public static final String MODULE_NAME = "Licensing API Support";

    public static final String LICENSE_SERVLET_TEMPLATE = "templates/common/licensing/LicenseServlet.java.vtl";
    public static final String HELLO_WORLD_SERVLET_TEMPLATE = "templates/common/licensing/HelloWorldServlet.java.vtl";
    public static final String LICENSE_SERVLET_VELOCITY_TEMPLATE = "templates/common/licensing/license-admin.vm";
    public static final String PROPERTIES_TEMPLATE = "templates/common/licensing/license-admin.properties.vtl";
    
    public static final ClassId LICENSE_STORAGE_MANAGER_CLASS = fullyQualified("com.atlassian.upm.license.storage.lib.ThirdPartyPluginLicenseStorageManagerImpl");
    public static final ClassId PLUGIN_INSTALLER_CLASS = fullyQualified("com.atlassian.upm.license.storage.lib.PluginLicenseStoragePluginInstaller");
    public static final ClassId MARKETPLACE_URI_FACTORY_CLASS = fullyQualified("com.atlassian.upm.license.storage.lib.AtlassianMarketplaceUriFactoryImpl");
    
    public static final String LICENSE_SERVLET_NAME_I18N_KEY = "license-servlet.name";
    public static final String LICENSE_SERVLET_DESCRIPTION_I18N_KEY = "license-servlet.description";
    
    public static final String HELLO_WORLD_SERVLET_CLASS_NAME = "LicenseHelloWorldServlet";
    public static final String HELLO_WORLD_SERVLET_NAME_I18N_KEY = "license-hello-world-servlet.name";
    public static final String HELLO_WORLD_SERVLET_DESCRIPTION_I18N_KEY = "license-hello-world-servlet.description";
    
    public static final String LICENSE_API_VERSION = "2.1";
    public static final VersionId LICENSE_API_VERSION_PROPERTY = versionProperty("upm.license.compatibility.version", LICENSE_API_VERSION);

    public static final String LICENSE_API_IMPORT_VERSION = "2.0.1";
    
    public static final ArtifactDependency[] DEPENDENCIES = {
        dependency("com.atlassian.upm", "plugin-license-storage-lib", LICENSE_API_VERSION_PROPERTY, COMPILE),
        dependency("com.atlassian.upm", "plugin-license-storage-plugin", LICENSE_API_VERSION_PROPERTY, PROVIDED),
        dependency("com.atlassian.upm", "licensing-api", LICENSE_API_VERSION_PROPERTY, PROVIDED),
        dependency("com.atlassian.upm", "upm-api", LICENSE_API_VERSION_PROPERTY, PROVIDED),
        dependency("com.atlassian.sal", "sal-api", "2.4.0", PROVIDED),
        dependency("com.atlassian.templaterenderer", "atlassian-template-renderer-api", versionProperty("atlassian.templaterenderer.version", "1.0.5"), PROVIDED),
        dependency("org.springframework.osgi", "spring-osgi-core", "1.1.3", PROVIDED),
        dependency("commons-lang", "commons-lang", "2.4", PROVIDED)
    };
    
    public static final PluginArtifact[] BUNDLED_ARTIFACTS = {
        pluginArtifact(BUNDLED_ARTIFACT, artifactId("com.atlassian.upm", "atlassian-universal-plugin-manager-plugin"), version("1.6.3")),
        pluginArtifact(BUNDLED_ARTIFACT, artifactId("com.atlassian.upm", "plugin-license-storage-plugin"), LICENSE_API_VERSION_PROPERTY)
    };
    
    public static final BundleInstruction[] BUNDLE_INSTRUCTIONS = {
        privatePackage("com.atlassian.upm.license.storage.lib*"),
        dynamicImportPackage("com.atlassian.upm.api.license", LICENSE_API_IMPORT_VERSION),
        dynamicImportPackage("com.atlassian.upm.api.license.entity", LICENSE_API_IMPORT_VERSION),
        dynamicImportPackage("com.atlassian.upm.api.util", LICENSE_API_IMPORT_VERSION),
        dynamicImportPackage("com.atlassian.upm.license.storage.plugin", LICENSE_API_IMPORT_VERSION)
    };
    
    public static final ComponentImport[] COMPONENT_IMPORTS = {
        componentImport(fullyQualified("com.atlassian.plugin.PluginAccessor")),
        componentImport(fullyQualified("com.atlassian.plugin.PluginController")),
        componentImport(fullyQualified("com.atlassian.sal.api.transaction.TransactionTemplate")).key(some("txTemplate")),
        componentImport(fullyQualified("com.atlassian.sal.api.ApplicationProperties")),
        componentImport(fullyQualified("com.atlassian.templaterenderer.TemplateRenderer")),
        componentImport(fullyQualified("com.atlassian.sal.api.pluginsettings.PluginSettingsFactory")),
        componentImport(fullyQualified("com.atlassian.sal.api.auth.LoginUriProvider")),
        componentImport(fullyQualified("com.atlassian.sal.api.user.UserManager")),
        componentImport(fullyQualified("com.atlassian.sal.api.message.I18nResolver"))
    };
    
    public static final ComponentDeclaration[] COMPONENTS = {
        componentDeclaration(LICENSE_STORAGE_MANAGER_CLASS, "thirdPartyPluginLicenseStorageManager"),
        componentDeclaration(PLUGIN_INSTALLER_CLASS, "pluginLicenseStoragePluginInstaller"),
        componentDeclaration(MARKETPLACE_URI_FACTORY_CLASS, "atlassianMarketplaceUriFactory")
    };
    
    public static final String MAVEN_DEPENDENCY_PLUGIN_ID = "maven-dependency-plugin";
    public static final String MAVEN_DEPENDENCY_PLUGIN_CONFIG = "<executions>" +
            "<execution>" +
                "<id>copy-storage-plugin</id>" +
                "<phase>process-resources</phase>" +
                "<goals><goal>copy-dependencies</goal></goals>" +
                "<configuration>" +
                    "<outputDirectory>${project.build.outputDirectory}</outputDirectory>" +
                    "<includeArtifactIds>plugin-license-storage-plugin</includeArtifactIds>" +
                    "<stripVersion>true</stripVersion>" +
                "</configuration>" +
            "</execution>" +
        "</executions>";
    
    @Override
    public PluginProjectChangeset createModule(LicensingProperties props) throws Exception
    {
        ClassId licenseServletClass = props.getClassId();
        ServletProperties licenseServletProps = new ServletProperties(licenseServletClass.getFullName());
        licenseServletProps.setUrlPattern("/" + props.getLicenseServletPath());
        licenseServletProps.setNameI18nKey(LICENSE_SERVLET_NAME_I18N_KEY);
        licenseServletProps.setDescriptionI18nKey(LICENSE_SERVLET_DESCRIPTION_I18N_KEY);
        licenseServletProps.setCreateClass(false);
        BasicClassModuleProperties licenseServletClassProps = new BasicClassModuleProperties(licenseServletClass.getFullName());
        licenseServletClassProps.putAll(props);
        PluginProjectChangeset licenseServlet = new ServletModuleCreator().createModule(licenseServletProps)
            .with(createClass(licenseServletClassProps, LICENSE_SERVLET_TEMPLATE));
        
        PluginProjectChangeset ret = new PluginProjectChangeset()
            .with(DEPENDENCIES)
            .with(BUNDLE_INSTRUCTIONS)
            .with(BUNDLED_ARTIFACTS)
            .with(mavenPlugin(artifactId(MAVEN_DEPENDENCY_PLUGIN_ID), noVersion(), MAVEN_DEPENDENCY_PLUGIN_CONFIG))
            .with(pluginParameter("atlassian-licensing-enabled", "true"))
            .with(COMPONENT_IMPORTS)
            .with(COMPONENTS)
            .with(licenseServlet)
            .with(resourceFile("", "license-admin.vm", fromFile(LICENSE_SERVLET_VELOCITY_TEMPLATE)))
            .with(createI18nStrings(props, PROPERTIES_TEMPLATE));
        
        if (props.includeExamples())
        {
            ClassId helloWorldServletClass = props.getClassId().className(HELLO_WORLD_SERVLET_CLASS_NAME);
            ServletProperties helloWorldServletProps = new ServletProperties(helloWorldServletClass.getFullName());
            helloWorldServletProps.setUrlPattern("/" + props.getHelloWorldServletPath());
            helloWorldServletProps.setCreateClass(false);
            BasicClassModuleProperties helloWorldServletClassProps = new BasicClassModuleProperties(helloWorldServletClass.getFullName());
            helloWorldServletClassProps.putAll(props);
            PluginProjectChangeset helloWorldServlet = new ServletModuleCreator().createModule(helloWorldServletProps)
                .with(createClass(helloWorldServletClassProps, HELLO_WORLD_SERVLET_TEMPLATE));

            ret = ret.with(helloWorldServlet);
        }
        
        return ret;
    }

    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
