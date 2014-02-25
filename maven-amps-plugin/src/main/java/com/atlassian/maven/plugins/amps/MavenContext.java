package com.atlassian.maven.plugins.amps;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.twdata.maven.mojoexecutor.MojoExecutor;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

public class MavenContext
{
    private final MavenProject project;
    private final List<MavenProject> reactor;
    private final MavenSession session;

    /* Maven 2 */
    private final PluginManager pluginManager;

    /* Maven 3 */
    private final BuildPluginManager buildPluginManager;

    private final Log log;
    
    private String versionOverridesPath;
    
    private Properties versionOverrides;

    public MavenContext(final MavenProject project, List<MavenProject> reactor, final MavenSession session, final PluginManager pluginManager, Log log)
    {
        this(project, reactor, session, pluginManager, null, log);
    }

    public MavenContext(final MavenProject project, List<MavenProject> reactor, final MavenSession session,
            BuildPluginManager buildPluginManager,
            Log log)
    {
        this(project, reactor, session, null, buildPluginManager, log);
    }

    private MavenContext(final MavenProject project, List<MavenProject> reactor, final MavenSession session,
            final PluginManager pluginManager,
            BuildPluginManager buildPluginManager,
            Log log)
    {
        this.project = project;
        this.reactor = reactor;
        this.session = session;

        this.pluginManager = pluginManager;

        this.buildPluginManager = buildPluginManager;

        this.log = log;
    }

    public MavenProject getProject()
    {
        return project;
    }

    MavenSession getSession()
    {
        return session;
    }

    public Log getLog()
    {
        return log;
    }

    public Properties getVersionOverrides()
    {
        if(null == versionOverrides)
        {
            this.versionOverrides = new Properties();
            
            if(null != versionOverridesPath)
            {
                File overridesFile = new File(versionOverridesPath);
                
                if(overridesFile.exists() && overridesFile.canRead())
                {
                    try
                    {
                        versionOverrides.load(FileUtils.openInputStream(overridesFile));
                    }
                    catch (IOException e)
                    {
                        log.error("unable to load version overrides file as Properties: " + overridesFile.getAbsolutePath(), e);
                    }
                }
            }
        }
        
        return versionOverrides;
    }

    public List<MavenProject> getReactor()
    {
        return reactor;
    }

    public MavenContext with(final MavenProject project, List<MavenProject> reactor, final MavenSession session)
    {
        return new MavenContext(project, reactor, session,
                this.pluginManager, this.buildPluginManager,
                this.log);
    }

    public ExecutionEnvironment getExecutionEnvironment()
    {
        if (buildPluginManager != null)
        {
            /* Maven 3 */
            return MojoExecutor.executionEnvironment(project, session, buildPluginManager);
        }
        else
        {
            /* Maven 2 */
            return MojoExecutor.executionEnvironment(project, session, pluginManager);
        }
    }

    public void setVersionOverridesPath(String versionOverridesPath)
    {
        this.versionOverridesPath = versionOverridesPath;
    }
}
