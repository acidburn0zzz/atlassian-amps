package com.atlassian.maven.plugins.ampsdispatcher;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestAbstractAmpsDispatcherMojo
{
    private MavenProject project;
    private MavenSession session;
    private AbstractAmpsDispatcherMojo mojo;

    @Before
    public void setUp()
    {
        BuildPluginManager buildPluginManager = mock(BuildPluginManager.class);

        project = new MavenProject();
        session = mock(MavenSession.class);

        mojo = new AbstractAmpsDispatcherMojo(){};
        mojo.buildPluginManager = buildPluginManager;
        mojo.project = project;
        mojo.session = session;
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
        when(session.getGoals()).thenReturn(Collections.singletonList("foo"));
        assertEquals("foo", mojo.determineGoal());

        when(session.getGoals()).thenReturn(Collections.singletonList("foo:bar"));
        assertEquals("bar", mojo.determineGoal());

        when(session.getGoals()).thenReturn(Collections.singletonList("foo:bar:baz"));
        assertEquals("baz", mojo.determineGoal());

        when(session.getGoals()).thenReturn(Arrays.asList("foo", "bar"));
        assertEquals("foo", mojo.determineGoal());
    }

    private void assertPlugin(boolean expected, String artifactId)
    {
        List<Plugin> buildPlugins = new ArrayList<>();
        Plugin plugin = new Plugin();
        plugin.setGroupId("com.atlassian.maven.plugins");
        plugin.setArtifactId(artifactId);
        buildPlugins.add(plugin);
        Build build = new Build();
        build.setPlugins(buildPlugins);
        project.setBuild(build);
        assertEquals(expected, artifactId.equals(mojo.detectAmpsProduct()));
    }
}
