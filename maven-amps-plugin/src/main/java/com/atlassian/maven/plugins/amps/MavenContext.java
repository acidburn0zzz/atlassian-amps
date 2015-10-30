package com.atlassian.maven.plugins.amps;

import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.twdata.maven.mojoexecutor.MojoExecutor;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

public class MavenContext
{
    private final MavenProject project;
    private final List<MavenProject> reactor;
    private final MavenSession session;

    private final BuildPluginManager buildPluginManager;

    private final Log log;
    
    private String versionOverridesPath;
    
    private Properties versionOverrides;
    private Set<String> versionOverridesSet;

    public MavenContext(final MavenProject project, List<MavenProject> reactor, final MavenSession session,
            BuildPluginManager buildPluginManager,
            Log log)
    {
        this.project = project;
        this.reactor = reactor;
        this.session = session;

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
            
            if(null != versionOverridesSet)
            {
                if(null != project.getPluginManagement())
                {
                    Set<String> found = new HashSet<>();
                    for(Plugin plg : project.getPluginManagement().getPlugins())
                    {
                        if (versionOverridesSet.contains(plg.getArtifactId()))
                        {
                            versionOverrides.setProperty(plg.getArtifactId(), plg.getVersion());
                            found.add(plg.getArtifactId());
                        }
                    }
                    final Sets.SetView<String> diff = Sets.difference(versionOverridesSet, found);
                    if(!diff.isEmpty())
                    {
                        getLog().warn("Plugin artifactId(s) defined in 'versionOverrides' parameter but no associated entry found in <pluginManagement> section for " + diff.toString());
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
                this.buildPluginManager,
                this.log);
    }

    public ExecutionEnvironment getExecutionEnvironment()
    {
        return MojoExecutor.executionEnvironment(project, session, buildPluginManager);
    }

    public void setVersionOverridesPath(String versionOverridesPath)
    {
        this.versionOverridesPath = versionOverridesPath;
    }

    void setVersionOverrides(Set<String> versionOverrides)
    {
        this.versionOverridesSet = versionOverrides;
    }
}
