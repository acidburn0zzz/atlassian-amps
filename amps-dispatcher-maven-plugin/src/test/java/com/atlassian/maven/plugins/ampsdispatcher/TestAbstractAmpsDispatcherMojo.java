package com.atlassian.maven.plugins.ampsdispatcher;

import com.google.common.collect.ImmutableList;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class TestAbstractAmpsDispatcherMojo
{
    private MavenProject project;
    private AbstractAmpsDispatcherMojo mojo;

    @Before
    public void setUp()
    {
        BuildPluginManager buildPluginManager = mock(BuildPluginManager.class);

        project = new MavenProject();

        mojo = new AbstractAmpsDispatcherMojo(){};
        mojo.buildPluginManager = buildPluginManager;
        mojo.project = project;
        mojo.session = mock(MavenSession.class);
    }

    @Test
    public void testDetectAmpsProduct()
    {
        assertPlugin(true, "bamboo-maven-plugin");
        assertPlugin(true, "bitbucket-maven-plugin");
        assertPlugin(true, "confluence-maven-plugin");
        assertPlugin(true, "crowd-maven-plugin");
        assertPlugin(true, "fecru-maven-plugin");
        assertPlugin(true, "jira-maven-plugin");
        assertPlugin(true, "refapp-maven-plugin");
        assertPlugin(false, "maven-refappsd-plugin");
        assertPlugin(false, "refappsd-maven-plugin");
        assertPlugin(false, "mas");
    }

    @Test
    public void testDetermineGoal()
    {
        assertEquals("foo", AbstractAmpsDispatcherMojo.determineGoal("foo"));
        assertEquals("bar", AbstractAmpsDispatcherMojo.determineGoal("foo:bar"));
        assertEquals("baz", AbstractAmpsDispatcherMojo.determineGoal("foo:bar:baz"));
    }

    private void assertPlugin(boolean expected, String artifactId)
    {
        Plugin plugin = new Plugin();
        plugin.setGroupId("com.atlassian.maven.plugins");
        plugin.setArtifactId(artifactId);

        Build build = new Build();
        build.setPlugins(ImmutableList.of(plugin));

        project.setBuild(build);

        Optional<Plugin> found = mojo.findProductPlugin();
        assertEquals(expected ? of(plugin) : empty(), found);
    }
}
