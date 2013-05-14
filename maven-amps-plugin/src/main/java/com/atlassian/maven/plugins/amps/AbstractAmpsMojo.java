package com.atlassian.maven.plugins.amps;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.maven.plugins.amps.util.*;
import com.atlassian.maven.plugins.updater.LocalSdk;
import com.atlassian.maven.plugins.updater.SdkResource;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

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

    /**
     * The Maven PluginManager Object
     */
    @Component
    private PluginManager pluginManager;

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
    @Parameter(property = "closure.js.compiler", defaultValue = "false")
    protected boolean closureJsCompiler;
    
    @Component
    private AmpsPluginVersionChecker ampsPluginVersionChecker;

    @Component
    private AmpsEmailSubscriber ampsEmailSubscriber;

    /**
     * Flag to skip all prompting so automated builds don't hang
     */
    @Parameter(property = "skipAllPrompts", defaultValue = "false")
    private boolean skipAllPrompts;

    /**
     *  this is just a marker so IDE's don't complain when this property is present.
     *  Fastdev parses this out of the pom manually
     */
    @Parameter(property = "useFastdevCli", defaultValue = "true")
    private boolean useFastdevCli;

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
    
    protected MavenContext getMavenContext()
    {
        if (mavenContext == null)
        {
            try
            {
                Object buildPluginManager = (BuildPluginManager) session.lookup("org.apache.maven.plugin.BuildPluginManager");

                /* Maven 3 */
                mavenContext = new MavenContext(project, reactor, session, (BuildPluginManager) buildPluginManager, getLog());
            }
            catch (ComponentLookupException e)
            {
                /* Maven 2 */
                mavenContext = new MavenContext(project, reactor, session, pluginManager, getLog());
            }
        }
        return mavenContext;
    }

    protected MavenGoals getMavenGoals()
    {
        if (mavenGoals == null)
        {
            mavenGoals = new MavenGoals(getMavenContext());
        }
        return mavenGoals;
    }

    protected PluginInformation getPluginInformation()
    {
        if (pluginArtifactId == null)
        {
            return new PluginInformation("amps", "");
        }
        final String productId = pluginArtifactId.replaceAll("maven-(.*)-plugin", "$1");
        return new PluginInformation(productId, pluginVersion);
    }

    protected UpdateChecker getUpdateChecker() throws MojoExecutionException
    {
        updateChecker.setCurrentVersion(getSdkVersion());
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

    protected AmpsEmailSubscriber getAmpsEmailSubscriber()
    {
        return ampsEmailSubscriber;
    }

    protected String getSdkVersion()
    {
        String sdkVersion = System.getenv("ATLAS_VERSION");
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
