package com.atlassian.maven.plugins.amps;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.maven.plugins.amps.util.AmpsPluginVersionChecker;
import com.atlassian.maven.plugins.amps.util.MojoExecutorWrapper;
import com.atlassian.maven.plugins.amps.util.ProjectUtils;
import com.atlassian.maven.plugins.amps.util.UpdateChecker;
import com.atlassian.maven.plugins.updater.LocalSdk;
import com.atlassian.maven.plugins.updater.SdkResource;

import java.util.Set;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

public abstract class AbstractAmpsMojo extends AbstractMojo
{
    /**
     * The Maven Project Object
     */
    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject project;

    /**
     * The list of modules being built, the reactor
     */
    @Parameter(property = "reactorProjects", required = true, readonly = true)
    private List<MavenProject> reactor;

    /**
     * The Maven Session Object
     */
    @Parameter(property = "session", required = true, readonly = true)
    private MavenSession session;

    @Component
    private BuildPluginManager buildPluginManager;

    /**
     * The current Maven plugin artifact id
     */
    @Parameter(property = "plugin.artifactId", required = true, readonly = true)
    private String pluginArtifactId;

    /**
     * The current Maven plugin version
     */
    @Parameter(property = "plugin.version", required = true, readonly = true)
    private String pluginVersion;

    /**
     * the maven context
     */
    private MavenContext mavenContext;

    /**
     * the maven goals
     */
    private MavenGoals mavenGoals;

    /**
     * Component for checking for SDK updates.
     */
    @Component
    private SdkResource sdkResource;

    /**
     * Component that provides information about the currently installed SDK.
     */
    @Component
    private LocalSdk localSdk;

    @Component
    private UpdateChecker updateChecker;

    /**
     * Flag to force a check for a new SDK regardless of the last time such a check was made.
     */
    @Parameter(property = "force.update.check", defaultValue = "false")
    private boolean forceUpdateCheck;

	/**
     * Flag to skip checking amps version in pom
     */
    @Parameter(property = "skip.amps.pom.check", defaultValue = "false")
    private boolean skipAmpsPomCheck;

    /**
     * Flag to use google closure instead of YUI for js compilation
     */
    @Parameter(property = "closure.js.compiler", defaultValue = "true")
    protected boolean closureJsCompiler;

    /**
     * If true, will skip files ending in -min.js or .min.js
     */
    @Parameter(property = "js.compiler.skip.minified", defaultValue = "false")
    protected boolean skipMinifiedJs;
    
    @Component
    private AmpsPluginVersionChecker ampsPluginVersionChecker;

    /**
     * Flag to skip all prompting so automated builds don't hang
     */
    @Parameter(property = "skipAllPrompts", defaultValue = "false")
    private boolean skipAllPrompts;

    /**
     * List of artifacts to exclude when copying test bundle dependencies
     */
    @Parameter
    protected List<ProductArtifact> testBundleExcludes = new ArrayList<ProductArtifact>();
    
    /**
     * Whether the test plugin should be built or not.  If not specified, it detects an atlassian-plugin.xml in the
     * test classes directory and builds if exists.
     */
    @Parameter(property = "buildTestPlugin", defaultValue = "false")
    private boolean buildTestPlugin;

    /**
     * Whether the test plugin should exclude test scoped dependencies.
     * This should always be false for running "wired" integration tests
     */
    @Parameter(property = "excludeAllTestDependencies", defaultValue = "false")
    protected boolean excludeAllTestDependencies;

    @Parameter(property = "offline", defaultValue = "${settings.offline}")
    protected boolean offline;

    /**
     * The path to a properties file to override internal plugin versions.
     * The props file should be in the following format:
     * artifactId=version
     * @deprecated doesn't work well with multimodule builds, use versionOverrides
     * e.g.
     * maven-deploy-plugin=2.5
     */
    @Deprecated
    @Parameter(property = "version.override.path")
    private String versionOverridesPath;


    /**
     * Set of plugin artifactId (as used by amps internally) to override internal
     * hardcoded versions with those provided
     * by the effective pom's pluginManagement section.
     * @since 6.2.0
     */
    @Parameter(property = "version.override.set")
    private Set<String> versionOverrides;

    /**
     * Project source files encoding. Along with explicit definition in the plugin
     * configuration property inherits value from the build section (for maven 3.x)
     * <pre>
     * &#60;project&#62;
     *   ...
     *   &#60;build&#62;
     *      ...
     *      &#60;sourceEncoding&#62;UTF-8&#60;/sourceEncoding&#62;
     *      ....
     *   &#60;/build&#62;
     *   ...
     *  &#60;/project&#62;
     * </pre>
     * 
     * For maven 2.x value inherits from the global property 'project.build.sourceEncoding':
     * <pre>
     * &#60;project&#62;
     *  ...
     *   &#60;properties&#62;
     *       &#60;project.build.sourceEncoding&#62;UTF-8&#60;/project.build.sourceEncoding&#62;
     *       ...
     *   &#60;/properties&#62;
     *   ...
     *  &#60;/project&#62;
     * </pre>
     */
    @Parameter(property = "encoding", defaultValue = "${project.build.sourceEncoding}")
    protected String encoding;

    @Component
    protected MojoExecutorWrapper mojoExecutorWrapper;

    protected MavenContext getMavenContext()
    {
        if (mavenContext == null)
        {
            mavenContext = new MavenContext(project, reactor, session, buildPluginManager, getLog());
        }
        
        mavenContext.setVersionOverridesPath(this.versionOverridesPath);
        mavenContext.setVersionOverrides(this.versionOverrides);
        return mavenContext;
    }

    protected MavenGoals getMavenGoals()
    {
        if (mavenGoals == null)
        {
            mavenGoals = new MavenGoals(getMavenContext(), mojoExecutorWrapper);
        }
        return mavenGoals;
    }

    protected PluginInformation getPluginInformation()
    {
        final String artifactId = pluginArtifactId == null ? "amps-maven-plugin" : pluginArtifactId;

        return PluginInformation.fromArtifactId(artifactId, pluginVersion);
    }

    protected UpdateChecker getUpdateChecker() throws MojoExecutionException
    {
        updateChecker.setCurrentVersion(getSDKVersion());
        updateChecker.setForceCheck(forceUpdateCheck);
        
        boolean skipCheck = (shouldSkipPrompts() || offline);
        
        updateChecker.setSkipCheck(skipCheck);
        
        return updateChecker;
    }

	protected AmpsPluginVersionChecker getAmpsPluginVersionChecker()
    {
        ampsPluginVersionChecker.skipPomCheck(skipAmpsPomCheck);
        
        if(shouldSkipPrompts())
        {
            ampsPluginVersionChecker.skipPomCheck(true);
        }
        return ampsPluginVersionChecker;
    }

    protected String getAmpsPluginVersion()
    {
        final String ampsPluginVersion = System.getenv("AMPS_PLUGIN_VERSION");
        return ampsPluginVersion != null ? ampsPluginVersion : getPluginInformation().getVersion();
    }

    protected String getSDKVersion()
    {
        final String sdkVersion = System.getenv("ATLAS_VERSION");
        return sdkVersion != null ? sdkVersion : getPluginInformation().getVersion();
    }
    
    protected boolean shouldBuildTestPlugin()
    {
        boolean shouldBuild = false;
        if(buildTestPlugin)
        {
           shouldBuild = true;
        }
        else if (ProjectUtils.shouldDeployTestJar(getMavenContext()))
        {
            shouldBuild = true;
        }
        
        return shouldBuild;
    }
    
    protected boolean shouldSkipPrompts()
    {
        return skipAllPrompts;
    }
}
