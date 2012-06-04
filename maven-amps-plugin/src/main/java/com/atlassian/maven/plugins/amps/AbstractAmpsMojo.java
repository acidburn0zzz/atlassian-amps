package com.atlassian.maven.plugins.amps;

import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
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
}
