package com.atlassian.maven.plugins.amps;

import com.google.common.collect.ImmutableSet;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.Collections.singletonList;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;

@Mojo(name = "run-cloud", requiresProject = false)
public class RunCloudMojo extends AbstractAmpsMojo
{
    public static final String JIRA_SOFTWARE = "jira-software";
    private static final String POM_FILENAME = "jira-run-pom.xml";

    private static final Set<String> SUPPORTED_PRODUCTS = ImmutableSet.of(
            JIRA_SOFTWARE);

    @Component
    private ProjectBuilder projectBuilder;

    @Parameter( property = "application", required = true)
    private String application;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (!SUPPORTED_PRODUCTS.contains(application))
        {
            throw new IllegalArgumentException("Unknown application: '" + application + "' Valid values: " + SUPPORTED_PRODUCTS);
        }

        if (application.equals(JIRA_SOFTWARE)) {

            File basedir = getMavenContext().getExecutionEnvironment().getMavenProject().getBasedir();
            File pomFile = new File(basedir, POM_FILENAME);
            if (pomFile.isFile()) {
                this.getLog().info("You already have a jira-run-pom.xml in the current directory. If you want to update, remove the file.");
            } else {
                getMavenGoals().saveArtifactToCurrentDirectory(
                        "com.atlassian.plugins",
                        "atlassian-connect-jira-software-runner",
                        "RELEASE",
                        "pom",
                        POM_FILENAME);
            }

            try {
                File downloadedPom = new File(basedir, POM_FILENAME);
                if (!downloadedPom.isFile()) {
                    throw new IllegalStateException("The downloaded file does not exist" + downloadedPom.getAbsolutePath());
                }
                MavenContext mavenContext = createMavenContext(downloadedPom);
                MavenGoals goals = new MavenGoals(mavenContext);

                Plugin plugin = getPluginFromProjectDefinition(goals.getContextProject(), "com.atlassian.maven.plugins", "maven-jira-plugin");

                executeMojo(plugin,
                        goal("run"),
                        (Xpp3Dom) plugin.getConfiguration(),
                        mavenContext.getExecutionEnvironment());

            } catch (ProjectBuildingException e) {
                throw new IllegalStateException("Couldn't run jira:run from downloaded pom.");
            }
        }
    }

    private MavenContext createMavenContext(File pomFile) throws ProjectBuildingException
    {
        final MavenContext oldContext = getMavenContext();
        MavenSession oldSession = oldContext.getSession();

        ProjectBuildingRequest pbr = oldSession.getProjectBuildingRequest();

        pbr.getSystemProperties().setProperty("project.basedir", pomFile.getAbsoluteFile().getParent());

        ProjectBuildingResult result = projectBuilder.build(pomFile, pbr);

        final List<MavenProject> newReactor = singletonList(result.getProject());

        MavenSession newSession = oldSession.clone();
        newSession.setProjects(newReactor);

        return oldContext.with(
                result.getProject(),
                newReactor,
                newSession);
    }

    private Plugin getPluginFromProjectDefinition(MavenProject project, final String groupId, final String artifactId)
    {
        List<Plugin> plugins = project.getBuild().getPlugins();
        return plugins.stream().filter(new Predicate<Plugin>()
        {
            @Override
            public boolean test(Plugin plugin)
            {
                return plugin.getArtifactId().equals(artifactId) && plugin.getGroupId().equals(groupId);
            }
        }).findFirst().get();
    }
}
