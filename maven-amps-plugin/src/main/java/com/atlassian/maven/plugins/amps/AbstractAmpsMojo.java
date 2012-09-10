package com.atlassian.maven.plugins.amps;

import java.util.List;

import com.atlassian.maven.plugins.amps.util.AmpsEmailSubscriber;
import com.atlassian.maven.plugins.amps.util.AmpsPluginVersionChecker;
import com.atlassian.maven.plugins.amps.util.ProjectUtils;
import com.atlassian.maven.plugins.amps.util.UpdateChecker;
import com.atlassian.maven.plugins.updater.SdkResource;
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
     * Flag to force a check for a new SDK regardless of the last time such a check was made.
     */
    @Parameter(property = "force.update.check", defaultValue = "false")
    private boolean forceUpdateCheck;

    private UpdateChecker updateChecker;

	/**
     * Flag to skip checking amps version in pom
     */
    @Parameter(property = "skip.amps.pom.check", defaultValue = "false")
    private boolean skipAmpsPomCheck;

    @Component
    private AmpsPluginVersionChecker ampsPluginVersionChecker;

    @Component
    private AmpsEmailSubscriber ampsEmailSubscriber;
    
    /**
     * Whether the test plugin should be built or not.  If not specified, it detects an atlassian-plugin.xml in the
     * test classes directory and builds if exists.
     */
    @Parameter
    private Boolean buildTestPlugin;

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
        if (updateChecker == null)
        {
            updateChecker = new UpdateChecker(getSdkVersion(), getLog(),
                    sdkResource, forceUpdateCheck);
        }

        return updateChecker;
    }

	protected AmpsPluginVersionChecker getAmpsPluginVersionChecker()
    {
        ampsPluginVersionChecker.skipPomCheck(skipAmpsPomCheck);
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
        if (buildTestPlugin != null)
        {
            if (buildTestPlugin.booleanValue())
            {
                shouldBuild = true;
            }
        }
        else if (ProjectUtils.shouldDeployTestJar(getMavenContext()))
        {
            shouldBuild = true;
        }
        
        return shouldBuild;
    }
}
