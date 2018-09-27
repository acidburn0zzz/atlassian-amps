package com.atlassian.maven.plugins.ampsdispatcher;

import junit.framework.TestCase;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.project.MavenProject;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestAbstractAmpsDispatcherMojo extends TestCase
{

    private MavenProject project;
    private MavenSession session;
    private AbstractAmpsDispatcherMojo mojo;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        BuildPluginManager buildPluginManager = mock(BuildPluginManager.class);

        project = new MavenProject();
        session = mock(MavenSession.class);

        mojo = new AbstractAmpsDispatcherMojo(){};
        mojo.buildPluginManager = buildPluginManager;
        mojo.project = project;
        mojo.session = session;
    }

    public void testDetectAmpsProduct()
    {
        assertPlugin(true, "bitbucket-maven-plugin");
        assertPlugin(true, "refapp-maven-plugin");
        assertPlugin(true, "confluence-maven-plugin");
        assertPlugin(true, "jira-maven-plugin");
        assertPlugin(true, "confluence-maven-plugin");
        assertPlugin(true, "bamboo-maven-plugin");
        assertPlugin(true, "crowd-maven-plugin");
        assertPlugin(true, "fecru-maven-plugin");
        assertPlugin(false, "maven-refappsd-plugin");
        assertPlugin(false, "refappsd-maven-plugin");
        assertPlugin(false, "mas");
    }

    public void testDetermineGoal()
    {
        when(session.getGoals()).thenReturn(Arrays.asList("foo"));
        assertEquals("foo", mojo.determineGoal());

        when(session.getGoals()).thenReturn(Arrays.asList("foo:bar"));
        assertEquals("bar", mojo.determineGoal());

        when(session.getGoals()).thenReturn(Arrays.asList("foo:bar:baz"));
        assertEquals("baz", mojo.determineGoal());

        when(session.getGoals()).thenReturn(Arrays.asList("foo", "bar"));
        assertEquals("foo", mojo.determineGoal());
    }

    private void assertPlugin(boolean expected, String artifactId)
    {
        List<Plugin> buildPlugins = new ArrayList<Plugin>();
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
