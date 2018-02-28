package com.atlassian.maven.plugins.amps;

import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import com.atlassian.maven.plugins.amps.util.GoogleAmpsTracker;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.locator.DefaultModelLocator;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;

/**
 * Run the webapp without a plugin project
 */
@Mojo(name = "run-standalone", requiresProject = false)
public class RunStandaloneMojo extends AbstractProductHandlerMojo {
    private final String GROUP_ID = "com.atlassian.amps";
    private final String ARTIFACT_ID = "standalone";

    @Component
    private ProjectBuilder projectBuilder;

    private Artifact getStandaloneArtifact() {
        final String version = getPluginInformation().getVersion();
        return artifactFactory.createProjectArtifact(GROUP_ID, ARTIFACT_ID, version);
    }

    protected String getAmpsGoal() {
        return "run";
    }

    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        getUpdateChecker().check();

        promptForEmailSubscriptionIfNeeded();

        trackFirstRunIfNeeded();

        getGoogleTracker().track(GoogleAmpsTracker.RUN_STANDALONE);

        try {
            MavenGoals goals;
            Xpp3Dom configuration;

            goals = createMavenGoals(projectBuilder);

            /* When we run with Maven 3 the configuration from the pom isn't automatically picked up
             * by the mojo executor. Grab it manually from pluginManagement.
             */
            PluginManagement mgmt = goals.getContextProject().getBuild().getPluginManagement();
            Plugin plugin = (Plugin) mgmt.getPluginsAsMap().get("com.atlassian.maven.plugins:maven-amps-plugin");

            configuration = (Xpp3Dom) plugin.getConfiguration();
            goals.executeAmpsRecursively(getPluginInformation().getVersion(), getAmpsGoal(), configuration);
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    protected MavenGoals createMavenGoals(ProjectBuilder projectBuilder)
            throws MojoExecutionException, MojoFailureException, ProjectBuildingException,
            IOException {
        // overall goal here is to create a new MavenContext / MavenGoals for the standalone project
        final MavenContext oldContext = getMavenContext();

        MavenSession oldSession = oldContext.getSession();

        // Different dir per version.
        // (If we use the same directory, we get support pain when the old version from an existing directory gets restarted
        //  instead of the version they specified on the commandline.)
        final Properties systemProperties = oldSession.getSystemProperties();

        // Defaults to refapp if not set
        final String product = getPropertyOrDefault(systemProperties, "product", "refapp");

        // Only the inner execution in com.atlassian.amps:standalone has the actual latest version that will be used if
        // they didn't specify a version number. Too hard to restructure; 'LATEST' is better than nothing.
        String productVersion = getPropertyOrDefault(systemProperties, "product.version", "LATEST");

        // If we're building FECRU, not using LATEST and have entered a short version name then get the full version name
        if (product.equals(ProductHandlerFactory.FECRU) && productVersion != "LATEST" && !Pattern.matches(".*[0-9]{14}$", productVersion)) {
            String fullProductVersionMessage = getFullVersion(productVersion)
                    .map((version) -> generateFullVersionMessage(productVersion, version))
                    .orElseGet(() -> generateNoVersionMessage(productVersion));
            getLog().error("=======================================================================");
            getLog().error(fullProductVersionMessage);
            getLog().error("=======================================================================");
            System.exit(1);
        }

        File base = new File("amps-standalone-" + product + "-" + productVersion).getAbsoluteFile();

        ProjectBuildingRequest pbr = oldSession.getProjectBuildingRequest();

        // hack #1 from before
        pbr.setRemoteRepositories(oldSession.getCurrentProject().getRemoteArtifactRepositories());
        pbr.setPluginArtifactRepositories(oldSession.getCurrentProject().getPluginArtifactRepositories());
        pbr.getSystemProperties().setProperty("project.basedir", base.getPath());

        ProjectBuildingResult result = projectBuilder.build(getStandaloneArtifact(), false, pbr);

        final List<MavenProject> newReactor = singletonList(result.getProject());

        MavenSession newSession = oldSession.clone();
        newSession.setProjects(newReactor);

        // Horrible hack #3 from before
        result.getProject().setFile(new DefaultModelLocator().locatePom(base));

        final MavenContext newContext = oldContext.with(
                result.getProject(),
                newReactor,
                newSession);

        return new MavenGoals(newContext);
    }

    private String generateNoVersionMessage(String productVersion) {
        String message = "There is no valid full version of " + productVersion + ". Please double check your input\n" +
        "\tThe full list of versions can be found at: " +
                "\n\thttps://packages.atlassian.com/content/repositories/atlassian-public/com/atlassian/fecru/amps-fecru/";
        return message;
    }

    private String generateFullVersionMessage(String productVersion, String version) {
        String message = "You entered: " + productVersion + " as your version, this is not a version." +
                "\n\tDid you mean?: " + version + "\n\tPlease re-run with the correct version (atlas-run-standalone --product " +
                "fecru -v " + version + ")";
        return message;
    }

    private Optional<String> getFullVersion(String versionInput) throws IOException {
        Pattern p = Pattern.compile(".*>(" + versionInput + "-[0-9]{14}).*");
        Matcher m;
        String correctVersion = null;
        URLConnection connection = new URL("https://packages.atlassian.com/content/repositories/atlassian-public/com/atlassian/fecru/amps-fecru/").openConnection();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
            String inputLine;
            // Read each line of the page and parse it to see the version number
            while ((inputLine = in.readLine()) != null) {
                m = p.matcher(inputLine);
                if (m.matches()) {
                    correctVersion = m.group(1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(correctVersion);
    }

    private String getPropertyOrDefault(Properties systemProperties, String property, String defaultValue) {
        String value = systemProperties.getProperty(property);
        return value == null ? defaultValue : value;
    }
}
