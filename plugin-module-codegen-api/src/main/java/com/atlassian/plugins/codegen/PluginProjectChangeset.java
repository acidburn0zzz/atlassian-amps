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
    private final ImmutableList<ArtifactDependency> dependencies;
    private final ImmutableList<BundleInstruction> bundleInstructions;
    private final ImmutableList<ComponentDeclaration> componentDeclarations;
    private final ImmutableList<ComponentImport> componentImports;
    private final ImmutableMap<String, String> i18nProperties;
    private final ImmutableList<ModuleDescriptor> moduleDescriptors;
    private final ImmutableMap<String, String> pluginParameters;
    private final ImmutableList<ResourceFile> resourceFiles;
    private final ImmutableList<SourceFile> sourceFiles;
    
    public PluginProjectChangeset()
    {
        this(ImmutableList.<ArtifactDependency>of(),
             ImmutableList.<BundleInstruction>of(),
             ImmutableList.<ComponentDeclaration>of(),
             ImmutableList.<ComponentImport>of(),
             ImmutableMap.<String, String>of(),
             ImmutableList.<ModuleDescriptor>of(),
             ImmutableMap.<String, String>of(),
             ImmutableList.<ResourceFile>of(),
             ImmutableList.<SourceFile>of());
    }
    
    private PluginProjectChangeset(Iterable<ArtifactDependency> dependencies,
                                   Iterable<BundleInstruction> bundleInstructions,
                                   Iterable<ComponentDeclaration> componentDeclarations,
                                   Iterable<ComponentImport> componentImports,
                                   Map<String, String> i18nProperties,
                                   Iterable<ModuleDescriptor> moduleDescriptors,
                                   Map<String, String> pluginParameters,
                                   Iterable<ResourceFile> resourceFiles,
                                   Iterable<SourceFile> sourceFiles)
    {
        this.dependencies = ImmutableList.copyOf(dependencies);
        this.bundleInstructions = ImmutableList.copyOf(bundleInstructions);
        this.componentDeclarations = ImmutableList.copyOf(componentDeclarations);
        this.componentImports = ImmutableList.copyOf(componentImports);
        this.i18nProperties = ImmutableMap.copyOf(i18nProperties);
        this.moduleDescriptors = ImmutableList.copyOf(moduleDescriptors);
        this.pluginParameters = ImmutableMap.copyOf(pluginParameters);
        this.resourceFiles = ImmutableList.copyOf(resourceFiles);
        this.sourceFiles = ImmutableList.copyOf(sourceFiles);
    }
    
    /**
     * Returns the {@link BundleInstruction} objects for this changeset.
     */
    public ImmutableList<BundleInstruction> getBundleInstructions()
    {
        return bundleInstructions;
    }

    /**
     * Returns the {@link BundleInstruction} objects for this changeset.
     */
    public ImmutableList<ComponentDeclaration> getComponentDeclarations()
    {
        return componentDeclarations;
    }

    /**
     * Returns the {@link ComponentImport} objects for this changeset.
     */
    public ImmutableList<ComponentImport> getComponentImports()
    {
        return componentImports;
    }

    /**
     * Returns the {@link ArtifactDependency} objects for this changeset.
     */
    public ImmutableList<ArtifactDependency> getDependencies()
    {
        return dependencies;
    }
    
    /**
     * Returns the i18n properties, if any, that should be added to the project.
     */
    public ImmutableMap<String, String> getI18nProperties()
    {
        return i18nProperties;
    }

    /**
     * Returns the {@link ModuleDescriptor} objects for this changeset.
     */
    public ImmutableList<ModuleDescriptor> getModuleDescriptors()
    {
        return moduleDescriptors;
    }

    /**
     * Returns the name-value pairs, if any, that should be added to the &lt;plugin-info&gt;
     * element in the plugin XML file.
     */
    public ImmutableMap<String, String> getPluginParameters()
    {
        return pluginParameters;
    }

    /**
     * Returns the {@link ResourceFile} objects for this changeset.
     */
    public ImmutableList<ResourceFile> getResourceFiles()
    {
        return resourceFiles;
    }

    /**
     * Returns the {@link SourceFile} objects for this changeset.
     */
    public ImmutableList<SourceFile> getSourceFiles()
    {
        return sourceFiles;
    }

    /**
     * Returns the union of this changeset with another.
     */
    public PluginProjectChangeset with(PluginProjectChangeset other)
    {
        return new PluginProjectChangeset(concat(this.dependencies, other.dependencies),
                                          concat(this.bundleInstructions, other.bundleInstructions),
                                          concat(this.componentDeclarations, other.componentDeclarations),
                                          concat(this.componentImports, other.componentImports),
                                          addMaps(this.i18nProperties, other.i18nProperties),
                                          concat(this.moduleDescriptors, other.moduleDescriptors),
                                          addMaps(this.pluginParameters, other.pluginParameters),
                                          concat(this.resourceFiles, other.resourceFiles),
                                          concat(this.sourceFiles, other.sourceFiles));
    }
    
    /**
     * Returns a copy of this changeset with added {@link ArtifactDependency} instances.
     */
    public PluginProjectChangeset withDependencies(ArtifactDependency... dependencies)
    {
        return new PluginProjectChangeset(concat(this.dependencies, ImmutableList.copyOf(dependencies)),
                                          this.bundleInstructions,
                                          this.componentDeclarations,
                                          this.componentImports,
                                          this.i18nProperties,
                                          this.moduleDescriptors,
                                          this.pluginParameters,
                                          this.resourceFiles,
                                          this.sourceFiles);
    }
    
    /**
     * Returns a copy of this changeset with added {@link BundleInstruction} instances.
     */
    public PluginProjectChangeset withBundleInstructions(BundleInstruction... bundleInstructions)
    {
        return new PluginProjectChangeset(this.dependencies,
                                          concat(this.bundleInstructions, ImmutableList.copyOf(bundleInstructions)),
                                          this.componentDeclarations,
                                          this.componentImports,
                                          this.i18nProperties,
                                          this.moduleDescriptors,
                                          this.pluginParameters,
                                          this.resourceFiles,
                                          this.sourceFiles);
    }
    
    /**
     * Returns a copy of this changeset with added {@link ComponentDeclaration} instances.
     */
    public PluginProjectChangeset withComponentDeclarations(ComponentDeclaration... componentDeclarations)
    {
        return new PluginProjectChangeset(this.dependencies,
                                          this.bundleInstructions,
                                          concat(this.componentDeclarations, ImmutableList.copyOf(componentDeclarations)),
                                          this.componentImports,
                                          this.i18nProperties,
                                          this.moduleDescriptors,
                                          this.pluginParameters,
                                          this.resourceFiles,
                                          this.sourceFiles);
    }

    /**
     * Returns a copy of this changeset with added {@link ComponentImport} instances.
     */
    public PluginProjectChangeset withComponentImports(ComponentImport... componentImports)
    {
        return new PluginProjectChangeset(this.dependencies,
                                          this.bundleInstructions,
                                          this.componentDeclarations,
                                          concat(this.componentImports, ImmutableList.copyOf(componentImports)),
                                          this.i18nProperties,
                                          this.moduleDescriptors,
                                          this.pluginParameters,
                                          this.resourceFiles,
                                          this.sourceFiles);
    }

    /**
     * Returns a copy of this changeset with added i18n property values.
     */
    public PluginProjectChangeset withI18nProperties(Map<String, String> i18nProperties)
    {
        return new PluginProjectChangeset(this.dependencies,
                                          this.bundleInstructions,
                                          this.componentDeclarations,
                                          this.componentImports,
                                          addMaps(this.i18nProperties, i18nProperties),
                                          this.moduleDescriptors,
                                          this.pluginParameters,
                                          this.resourceFiles,
                                          this.sourceFiles);
    }

    /**
     * Returns a copy of this changeset with added {@link ModuleDescriptor} instances.
     */
    public PluginProjectChangeset withModuleDescriptor(ModuleDescriptor moduleDescriptor)
    {
        return new PluginProjectChangeset(this.dependencies,
                                          this.bundleInstructions,
                                          this.componentDeclarations,
                                          this.componentImports,
                                          this.i18nProperties,
                                          concat(this.moduleDescriptors, ImmutableList.of(moduleDescriptor)),
                                          this.pluginParameters,
                                          this.resourceFiles,
                                          this.sourceFiles);
    }

    /**
     * Returns a copy fo this changeset with added name-value pairs that should be included in the
     * &lt;plugin-info&gt; element in the plugin XML file.
     */
    public PluginProjectChangeset withPluginParameters(Map<String, String> parameters)
    {
        return new PluginProjectChangeset(this.dependencies,
                                          this.bundleInstructions,
                                          this.componentDeclarations,
                                          this.componentImports,
                                          this.i18nProperties,
                                          this.moduleDescriptors,
                                          addMaps(this.pluginParameters, parameters),
                                          this.resourceFiles,
                                          this.sourceFiles);
    }
    
    /**
     * Returns a copy of this changeset with added {@link ResourceFile} instances.
     */
    public PluginProjectChangeset withResourceFile(ResourceFile resourceFile)
    {
        return new PluginProjectChangeset(this.dependencies,
                                          this.bundleInstructions,
                                          this.componentDeclarations,
                                          this.componentImports,
                                          this.i18nProperties,
                                          this.moduleDescriptors,
                                          this.pluginParameters,
                                          concat(this.resourceFiles, ImmutableList.of(resourceFile)),
                                          this.sourceFiles);
    }

    /**
     * Returns a copy of this changeset with added {@link SourceFile} instances.
     */
    public PluginProjectChangeset withSourceFile(SourceFile sourceFile)
    {
        return new PluginProjectChangeset(this.dependencies,
                                          this.bundleInstructions,
                                          this.componentDeclarations,
                                          this.componentImports,
                                          this.i18nProperties,
                                          this.moduleDescriptors,
                                          this.pluginParameters,
                                          this.resourceFiles,
                                          concat(this.sourceFiles, ImmutableList.of(sourceFile)));
    }
    
    private Map<String, String> addMaps(Map<String, String> first, Map<String, String> second)
    {
        Map<String, String> ret = newHashMap(first);
        ret.putAll(second);
        return ret;
    }
}
