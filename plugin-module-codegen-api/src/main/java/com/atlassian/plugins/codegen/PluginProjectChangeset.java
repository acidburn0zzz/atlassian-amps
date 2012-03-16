package com.atlassian.plugins.codegen;

import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Describes changes that should be applied to the project.  These may include changes
 * to the POM, the plugin XML file, and any other files within the project.  Implementations
 * of {@link com.atlassian.plugins.codegen.modules.PluginModuleCreator} return an instance
 * of this class rather than performing the changes directly.
 * <p>
 * This class is immutable; all of its non-getter methods return new instances.
 */
public final class PluginProjectChangeset
{
    private final MavenProjectChangeset mavenProject;
    private final AmpsConfigChangeset ampsConfig;
    private final PluginXmlChangeset pluginXml;
    private final ProjectFilesChangeset projectFiles;
    
    public PluginProjectChangeset()
    {
        this(new MavenProjectChangeset(),
             new AmpsConfigChangeset(),
             new PluginXmlChangeset(),
             new ProjectFilesChangeset());
    }
    
    private PluginProjectChangeset(MavenProjectChangeset mavenProject,
                                   AmpsConfigChangeset ampsConfig,
                                   PluginXmlChangeset pluginXml,
                                   ProjectFilesChangeset projectFiles)
    {
        this.mavenProject = mavenProject;
        this.ampsConfig = ampsConfig;
        this.pluginXml = pluginXml;
        this.projectFiles = projectFiles;
    }
    
    /**
     * Returns the {@link BundleInstruction} objects for this changeset.
     */
    public ImmutableList<BundleInstruction> getBundleInstructions()
    {
        return ampsConfig.bundleInstructions;
    }

    /**
     * Returns the {@link PluginArtifact} items, if any, that should be added
     * to the &lt;bundledArtifacts&gt; list in the project's AMPS configuration.
     */
    public ImmutableList<PluginArtifact> getBundledArtifacts()
    {
        return ampsConfig.bundledArtifacts;
    }

    /**
     * Returns the {@link BundleInstruction} objects for this changeset.
     */
    public ImmutableList<ComponentDeclaration> getComponentDeclarations()
    {
        return pluginXml.componentDeclarations;
    }

    /**
     * Returns the {@link ComponentImport} objects for this changeset.
     */
    public ImmutableList<ComponentImport> getComponentImports()
    {
        return pluginXml.componentImports;
    }

    /**
     * Returns the {@link ArtifactDependency} objects for this changeset.
     */
    public ImmutableList<ArtifactDependency> getDependencies()
    {
        return mavenProject.dependencies;
    }
    
    /**
     * Returns the i18n properties, if any, that should be added to the project.
     */
    public ImmutableMap<String, String> getI18nProperties()
    {
        return projectFiles.i18nProperties;
    }

    /**
     * Returns the {@link MavenPlugin} objects for this changeset.
     */
    public ImmutableList<MavenPlugin> getMavenPlugins()
    {
        return mavenProject.mavenPlugins;
    }
    
    /**
     * Returns the {@link ModuleDescriptor} objects for this changeset.
     */
    public ImmutableList<ModuleDescriptor> getModuleDescriptors()
    {
        return pluginXml.moduleDescriptors;
    }

    /**
     * Returns the name-value pairs, if any, that should be added to the &lt;plugin-info&gt;
     * element in the plugin XML file.
     */
    public ImmutableMap<String, String> getPluginParameters()
    {
        return pluginXml.pluginParameters;
    }

    /**
     * Returns the {@link ResourceFile} objects for this changeset.
     */
    public ImmutableList<ResourceFile> getResourceFiles()
    {
        return projectFiles.resourceFiles;
    }

    /**
     * Returns the {@link SourceFile} objects for this changeset.
     */
    public ImmutableList<SourceFile> getSourceFiles()
    {
        return projectFiles.sourceFiles;
    }

    /**
     * Returns the union of this changeset with another.
     */
    public PluginProjectChangeset with(PluginProjectChangeset other)
    {
        return new PluginProjectChangeset(mavenProject.with(other.mavenProject),
                                          ampsConfig.with(other.ampsConfig),
                                          pluginXml.with(other.pluginXml),
                                          projectFiles.with(other.projectFiles));
    }
    
    /**
     * Returns a copy of this changeset with added {@link ArtifactDependency} instances.
     */
    public PluginProjectChangeset withDependencies(ArtifactDependency... dependencies)
    {
        return new PluginProjectChangeset(mavenProject.withDependencies(dependencies),
                                          this.ampsConfig,
                                          this.pluginXml,
                                          this.projectFiles);
    }
    
    /**
     * Returns a copy of this changeset with added {@link BundleInstruction} instances.
     */
    public PluginProjectChangeset withBundleInstructions(BundleInstruction... bundleInstructions)
    {
        return new PluginProjectChangeset(this.mavenProject,
                                          ampsConfig.withBundleInstructions(bundleInstructions),
                                          this.pluginXml,
                                          this.projectFiles);
    }

    /**
     * Returns a copy of this changeset with artifacts to add to the &lt;bundledArtifacts&gt; list in
     * AMPS configuration.
     */
    public PluginProjectChangeset withBundledArtifacts(PluginArtifact... artifacts)
    {
        return new PluginProjectChangeset(this.mavenProject,
                                          ampsConfig.withBundledArtifacts(artifacts),
                                          this.pluginXml,
                                          this.projectFiles);
    }
    
    /**
     * Returns a copy of this changeset with added {@link ComponentDeclaration} instances.
     */
    public PluginProjectChangeset withComponentDeclarations(ComponentDeclaration... componentDeclarations)
    {
        return new PluginProjectChangeset(this.mavenProject,
                                          this.ampsConfig,
                                          pluginXml.withComponentDeclarations(componentDeclarations),
                                          this.projectFiles);
    }

    /**
     * Returns a copy of this changeset with added {@link ComponentImport} instances.
     */
    public PluginProjectChangeset withComponentImports(ComponentImport... componentImports)
    {
        return new PluginProjectChangeset(this.mavenProject,
                                          this.ampsConfig,
                                          pluginXml.withComponentImports(componentImports),
                                          this.projectFiles);
    }

    /**
     * Returns a copy of this changeset with added i18n property values.
     */
    public PluginProjectChangeset withI18nProperties(Map<String, String> i18nProperties)
    {
        return new PluginProjectChangeset(this.mavenProject,
                                          this.ampsConfig,
                                          this.pluginXml,
                                          projectFiles.withI18nProperties(i18nProperties));
    }

    /**
     * Returns a copy of this changeset with added {@link MavenPlugin} instances.
     */
    public PluginProjectChangeset withMavenPlugins(MavenPlugin... mavenPlugins)
    {
        return new PluginProjectChangeset(mavenProject.withMavenPlugins(mavenPlugins),
                                          this.ampsConfig,
                                          this.pluginXml,
                                          this.projectFiles);
    }
    
    /**
     * Returns a copy of this changeset with added {@link ModuleDescriptor} instances.
     */
    public PluginProjectChangeset withModuleDescriptor(ModuleDescriptor moduleDescriptor)
    {
        return new PluginProjectChangeset(this.mavenProject,
                                          this.ampsConfig,
                                          pluginXml.withModuleDescriptor(moduleDescriptor),
                                          this.projectFiles);
    }

    /**
     * Returns a copy fo this changeset with added name-value pairs that should be included in the
     * &lt;plugin-info&gt; element in the plugin XML file.
     */
    public PluginProjectChangeset withPluginParameters(Map<String, String> parameters)
    {
        return new PluginProjectChangeset(this.mavenProject,
                                          this.ampsConfig,
                                          pluginXml.withPluginParameters(parameters),
                                          this.projectFiles);
    }
    
    /**
     * Returns a copy of this changeset with added {@link ResourceFile} instances.
     */
    public PluginProjectChangeset withResourceFile(ResourceFile resourceFile)
    {
        return new PluginProjectChangeset(this.mavenProject,
                                          this.ampsConfig,
                                          this.pluginXml,
                                          projectFiles.withResourceFile(resourceFile));
    }

    /**
     * Returns a copy of this changeset with added {@link SourceFile} instances.
     */
    public PluginProjectChangeset withSourceFile(SourceFile sourceFile)
    {
        return new PluginProjectChangeset(this.mavenProject,
                                          this.ampsConfig,
                                          this.pluginXml,
                                          projectFiles.withSourceFile(sourceFile));
    }
    
    private static Map<String, String> addMaps(Map<String, String> first, Map<String, String> second)
    {
        Map<String, String> ret = newHashMap(first);
        ret.putAll(second);
        return ret;
    }
    
    private static class MavenProjectChangeset
    {
        private final ImmutableList<ArtifactDependency> dependencies;
        private final ImmutableList<MavenPlugin> mavenPlugins;
        
        MavenProjectChangeset()
        {
            this(ImmutableList.<ArtifactDependency>of(),
                 ImmutableList.<MavenPlugin>of());
        }
        
        MavenProjectChangeset(Iterable<ArtifactDependency> dependencies,
                              Iterable<MavenPlugin> mavenPlugins)
        {
            this.dependencies = ImmutableList.copyOf(dependencies);
            this.mavenPlugins = ImmutableList.copyOf(mavenPlugins);
        }
        
        MavenProjectChangeset with(MavenProjectChangeset other)
        {
            return new MavenProjectChangeset(concat(this.dependencies, other.dependencies),
                                             concat(this.mavenPlugins, other.mavenPlugins));
        }
        
        MavenProjectChangeset withDependencies(ArtifactDependency... dependencies)
        {
            return new MavenProjectChangeset(concat(this.dependencies, ImmutableList.copyOf(dependencies)),
                                             this.mavenPlugins);
        }
        
        MavenProjectChangeset withMavenPlugins(MavenPlugin... mavenPlugins)
        {
            return new MavenProjectChangeset(this.dependencies,
                                             concat(this.mavenPlugins, ImmutableList.copyOf(mavenPlugins)));
        }
    }

    private static class AmpsConfigChangeset
    {
        private final ImmutableList<BundleInstruction> bundleInstructions;
        private final ImmutableList<PluginArtifact> bundledArtifacts;
        
        AmpsConfigChangeset()
        {
            this(ImmutableList.<BundleInstruction>of(),
                 ImmutableList.<PluginArtifact>of());
        }
        
        AmpsConfigChangeset(Iterable<BundleInstruction> bundleInstructions,
                            Iterable<PluginArtifact> bundledArtifacts)
        {
            this.bundleInstructions = ImmutableList.copyOf(bundleInstructions);
            this.bundledArtifacts = ImmutableList.copyOf(bundledArtifacts);
        }
        
        AmpsConfigChangeset with(AmpsConfigChangeset other)
        {
            return new AmpsConfigChangeset(concat(this.bundleInstructions, other.bundleInstructions),
                                           concat(this.bundledArtifacts, other.bundledArtifacts));
        }
        
        AmpsConfigChangeset withBundleInstructions(BundleInstruction... bundleInstructions)
        {
            return new AmpsConfigChangeset(concat(this.bundleInstructions, ImmutableList.copyOf(bundleInstructions)),
                                           this.bundledArtifacts);
        }
        
        AmpsConfigChangeset withBundledArtifacts(PluginArtifact... artifacts)
        {
            return new AmpsConfigChangeset(this.bundleInstructions,
                                           concat(this.bundledArtifacts, ImmutableList.copyOf(artifacts)));
        }
    }
    
    private static class PluginXmlChangeset
    {
        final ImmutableList<ComponentDeclaration> componentDeclarations;
        final ImmutableList<ComponentImport> componentImports;
        final ImmutableList<ModuleDescriptor> moduleDescriptors;
        final ImmutableMap<String, String> pluginParameters;
        
        PluginXmlChangeset()
        {
            this(ImmutableList.<ComponentDeclaration>of(),
                 ImmutableList.<ComponentImport>of(),
                 ImmutableList.<ModuleDescriptor>of(),
                 ImmutableMap.<String, String>of());
        }
        
        PluginXmlChangeset(Iterable<ComponentDeclaration> componentDeclarations,
                           Iterable<ComponentImport> componentImports,
                           Iterable<ModuleDescriptor> moduleDescriptors,
                           Map<String, String> pluginParameters)
        {
            this.componentDeclarations = ImmutableList.copyOf(componentDeclarations);
            this.componentImports = ImmutableList.copyOf(componentImports);
            this.moduleDescriptors = ImmutableList.copyOf(moduleDescriptors);
            this.pluginParameters = ImmutableMap.copyOf(pluginParameters);
        }

        PluginXmlChangeset with(PluginXmlChangeset other)
        {
            return new PluginXmlChangeset(concat(this.componentDeclarations, other.componentDeclarations),
                                          concat(this.componentImports, other.componentImports),
                                          concat(this.moduleDescriptors, other.moduleDescriptors),
                                          addMaps(this.pluginParameters, other.pluginParameters));
        }
        
        PluginXmlChangeset withComponentDeclarations(ComponentDeclaration... componentDeclarations)
        {
            return new PluginXmlChangeset(concat(this.componentDeclarations, ImmutableList.copyOf(componentDeclarations)),
                                          this.componentImports,
                                          this.moduleDescriptors,
                                          this.pluginParameters);
        }

        PluginXmlChangeset withComponentImports(ComponentImport... componentImports)
        {
            return new PluginXmlChangeset(this.componentDeclarations,
                                          concat(this.componentImports, ImmutableList.copyOf(componentImports)),
                                          this.moduleDescriptors,
                                          this.pluginParameters);
        }
        
        PluginXmlChangeset withModuleDescriptor(ModuleDescriptor moduleDescriptor)
        {
            return new PluginXmlChangeset(this.componentDeclarations,
                                          this.componentImports,
                                          concat(this.moduleDescriptors, ImmutableList.of(moduleDescriptor)),
                                          this.pluginParameters);
        }

        PluginXmlChangeset withPluginParameters(Map<String, String> parameters)
        {
            return new PluginXmlChangeset(this.componentDeclarations,
                                          this.componentImports,
                                          this.moduleDescriptors,
                                          addMaps(this.pluginParameters, parameters));
        }
    }
    
    private static class ProjectFilesChangeset
    {
        final ImmutableMap<String, String> i18nProperties;
        final ImmutableList<ResourceFile> resourceFiles;
        final ImmutableList<SourceFile> sourceFiles;

        ProjectFilesChangeset()
        {
            this(ImmutableMap.<String, String>of(),
                 ImmutableList.<ResourceFile>of(),
                 ImmutableList.<SourceFile>of());
        }
        
        ProjectFilesChangeset(Map<String, String> i18nProperties,
                              Iterable<ResourceFile> resourceFiles,
                              Iterable<SourceFile> sourceFiles)
        {
            this.i18nProperties = ImmutableMap.copyOf(i18nProperties);
            this.resourceFiles = ImmutableList.copyOf(resourceFiles);
            this.sourceFiles = ImmutableList.copyOf(sourceFiles);
        }
        
        ProjectFilesChangeset with(ProjectFilesChangeset other)
        {
            return new ProjectFilesChangeset(addMaps(this.i18nProperties, other.i18nProperties),
                                             concat(this.resourceFiles, other.resourceFiles),
                                             concat(this.sourceFiles, other.sourceFiles));
        }
        
        ProjectFilesChangeset withI18nProperties(Map<String, String> i18nProperties)
        {
            return new ProjectFilesChangeset(addMaps(this.i18nProperties, i18nProperties),
                                             this.resourceFiles,
                                             this.sourceFiles);
        }
        
        ProjectFilesChangeset withResourceFile(ResourceFile resourceFile)
        {
            return new ProjectFilesChangeset(this.i18nProperties,
                                             concat(this.resourceFiles, ImmutableList.of(resourceFile)),
                                             this.sourceFiles);
        }
        
        ProjectFilesChangeset withSourceFile(SourceFile sourceFile)
        {
            return new ProjectFilesChangeset(this.i18nProperties,
                                             this.resourceFiles,
                                             concat(this.sourceFiles, ImmutableList.of(sourceFile)));
        }
    }
}
