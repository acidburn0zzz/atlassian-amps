package com.atlassian.maven.plugins.amps;

import com.google.common.collect.ImmutableSet;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.locator.DefaultModelLocator;
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

    @Component
    private ArtifactFactory artifactFactory;

    @Parameter( property = "productPackage", required = true)
    private String productPackage;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getUpdateChecker().check();

        if (!SUPPORTED_PRODUCTS.contains(productPackage))
        {
            throw new IllegalArgumentException("Unknown productPackage: '" + productPackage + "' Valid values: " + SUPPORTED_PRODUCTS);
        }

        if (productPackage.equals(JIRA_SOFTWARE))
        {

            File pomFile = new File(POM_FILENAME);
            if(pomFile.isFile())
            {
                this.getLog().info("You already have a jira-run-pom.xml in the current directory. If you want to update, remove the file.");
            }
            else
            {
                getMavenGoals().saveArtifactToCurrentDirectory(
                        "com.atlassian.plugins",
                        "atlassian-connect-jira-software-runner",
                        "LATEST",
                        "pom",
                        POM_FILENAME);
            }

            try {
                MavenGoals goals = createMavenGoals(new File(POM_FILENAME));
                Plugin plugin = getPluginFromProjectDefinition(goals.getContextProject(), "com.atlassian.maven.plugins", "maven-jira-plugin");

                executeMojo(plugin,
                        goal("run"),
                        (Xpp3Dom) plugin.getConfiguration(),
                        getMavenContext().getExecutionEnvironment());

            } catch (ProjectBuildingException e) {
                throw new IllegalStateException("Couldn't run jira:run from downloaded pom.");
            }
        }
    }

    private MavenGoals createMavenGoals(File pomFile) throws ProjectBuildingException {
        final MavenContext oldContext = getMavenContext();
        MavenSession oldSession = oldContext.getSession();

        ProjectBuildingRequest pbr = oldSession.getProjectBuildingRequest();

        pbr.getSystemProperties().setProperty("project.basedir", pomFile.getPath());

        ProjectBuildingResult result = projectBuilder.build(getPomArtifact("com.atlassian.plugins", "atlassian-connect-jira-software-runner", "LATEST"), false, pbr);

        final List<MavenProject> newReactor = singletonList(result.getProject());

        MavenSession newSession = oldSession.clone();
        newSession.setProjects(newReactor);

        // Horrible hack #3 from before
        result.getProject().setFile(new DefaultModelLocator().locatePom(pomFile));

        final MavenContext newContext = oldContext.with(
                result.getProject(),
                newReactor,
                newSession);

        return new MavenGoals(newContext);
    }

    private Artifact getPomArtifact(final String groupId, final String artifactId, final String version)
    {
        return artifactFactory.createProjectArtifact(groupId, artifactId, version, "pom");
    }

    private Plugin getPluginFromProjectDefinition(MavenProject project, final String groupId, final String artifactId) {
        List<Plugin> plugins = project.getBuild().getPlugins();
        return plugins.stream().filter(new Predicate<Plugin>() {
            @Override
            public boolean test(Plugin plugin) {
                return plugin.getArtifactId().equals(artifactId) && plugin.getGroupId().equals(groupId);
            }
        }).findFirst().get();
    }
}
