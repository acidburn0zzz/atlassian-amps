package com.atlassian.maven.plugins.amps;

import java.io.File;
import java.util.*;

import com.atlassian.maven.plugins.amps.product.AmpsDefaults;
import com.atlassian.maven.plugins.amps.product.ProductHandler;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * Run the integration tests against the webapp.
 */
@Mojo(name = "integration-test", requiresDependencyResolution = ResolutionScope.TEST)
public class IntegrationTestMojo extends AbstractTestGroupsHandlerMojo
{
    /**
     * Pattern for to use to find integration tests.  Only used if no test groups are defined.
     */
    @Parameter(property = "functional.test.pattern")
    private String functionalTestPattern = "it/**";

    /**
     * The directory containing generated test classes of the project being tested.
     */
    @Parameter(property = "project.build.testOutputDirectory", required = true)
    private File testClassesDirectory;

    /**
     * A comma separated list of test groups to run.  If not specified, all
     * test groups are run.
     */
    @Parameter(property = "testGroups")
    private String configuredTestGroupsToRun;

    /**
     * Whether the reference application will not be started or not
     */
    @Parameter(property = "no.webapp", defaultValue = "false")
    private boolean noWebapp = false;

    @Component
    private ArtifactHandlerManager artifactHandlerManager;

    @Parameter(property = "maven.test.skip", defaultValue = "false")
    private boolean testsSkip = false;

    @Parameter(property = "skipTests", defaultValue = "false")
    private boolean skipTests = false;

    /**
     * Skip the integration tests along with any product startups
     */
    @Parameter(property = "skipITs", defaultValue = "false")
    private boolean skipITs = false;

    /**
     * port for debugging
     */
    @Parameter(property = "jvm.debug.port", defaultValue = "0")
    protected int jvmDebugPort;

    /**
     * Suspend when debugging
     */
    @Parameter(property = "jvm.debug.suspend")
    protected boolean jvmDebugSuspend = false;

    /**
     * Denotes test category as defined by surefire/failsafe notion of groups. In JUnit4, this affects tests annotated
     * with {@link org.junit.experimental.categories.Category @Category} annotation.
     */
    @Parameter
    protected String category;
    
    /**
     * By default this goal performs both failsafe:integration-test and failsafe:verify goals. Set this to true if you want
     * to run failsafe:verify at a later lifecycle stage (verify) to do cleanup in post-integration-test phase.
     * Please note that you will have to set the execution(s) for failsafe:verify yourself in the pom.xml file
     * including the 'reportsDirectory' configuration for the verify goal.
     */
    @Parameter(property = "skip.IT.verification")
    protected boolean skipITVerification = false;

    protected void doExecute() throws MojoExecutionException
    {
        final MavenProject project = getMavenContext().getProject();

        // workaround for MNG-1682/MNG-2426: force maven to install artifact using the "jar" handler
        project.getArtifact().setArtifactHandler(artifactHandlerManager.getArtifactHandler("jar"));

        if (!new File(testClassesDirectory, "it").exists())
        {
            getLog().info("No integration tests found");
            return;
        }

        if (skipTests || testsSkip || skipITs)
        {
            getLog().info("Integration tests skipped");
            return;
        }

        final MavenGoals goals = getMavenGoals();
        final String pluginJar = targetDirectory.getAbsolutePath() + "/" + finalName + ".jar";

        final Set<String> configuredTestGroupIds = getTestGroupIds();
        if (configuredTestGroupIds.isEmpty())
        {
            runTestsForTestGroup(NO_TEST_GROUP, goals, pluginJar, copy(systemPropertyVariables));
        }
        else if (configuredTestGroupsToRun != null)
        {
            String[] testGroupIdsToRun = configuredTestGroupsToRun.split(",");

            // now run the tests
            for (String testGroupId : testGroupIdsToRun)
            {
                if (!configuredTestGroupIds.contains(testGroupId))
                {
                    getLog().warn("Test group " + testGroupId + " does not exist");
                }
                else
                {
                    runTestsForTestGroup(testGroupId, goals, pluginJar, copy(systemPropertyVariables));
                }
            }
        }
        else
        {
            for (String testGroupId : configuredTestGroupIds)
            {
                runTestsForTestGroup(testGroupId, goals, pluginJar, copy(systemPropertyVariables));
            }
        }
    }

    private Map<String,Object> copy(Map<String,Object> systemPropertyVariables)
    {
        return new HashMap<String,Object>(systemPropertyVariables);
    }

    /**
     * Returns product-specific properties to pass to the container during
     * integration testing. Default implementation does nothing.
     * @param product the {@code Product} object to use
     * @return a {@code Map} of properties to add to the system properties passed
     * to the container
     */
    protected Map<String, String> getProductFunctionalTestProperties(Product product)
    {
        return Collections.emptyMap();
    }

    private Set<String> getTestGroupIds() throws MojoExecutionException
    {
        Set<String> ids = new HashSet<String>();

        for (TestGroup group : getTestGroups())
        {
            ids.add(group.getId());
        }

        return ids;
    }

    private void runTestsForTestGroup(String testGroupId, MavenGoals goals, String pluginJar, Map<String,Object> systemProperties) throws MojoExecutionException
    {
        List<String> includes = getIncludesForTestGroup(testGroupId);
        List<String> excludes = getExcludesForTestGroup(testGroupId);

        List<ProductExecution> productExecutions = getTestGroupProductExecutions(testGroupId);
        setParallelMode(productExecutions);

        int counter = 0;
        // Install the plugin in each product and start it
        for (ProductExecution productExecution : productExecutions)
        {
            ProductHandler productHandler = productExecution.getProductHandler();
            Product product = productExecution.getProduct();
            if (product.isInstallPlugin() == null)
            {
                product.setInstallPlugin(installPlugin);
            }
            
            if (shouldBuildTestPlugin())
            {
                List<ProductArtifact> plugins = product.getBundledArtifacts();
                plugins.addAll(getTestFrameworkPlugins());
            }

            int actualHttpPort = 0;
            if (!noWebapp)
            {
                if(jvmDebugPort > 0)
                {
                    if (product.getJvmDebugPort() == 0)
                    {
                        product.setJvmDebugPort(jvmDebugPort + counter++);
                    }
                    final int debugPort = product.getJvmDebugPort();
                    String debugArgs = " -Xdebug -Xrunjdwp:transport=dt_socket,address=" +
                            String.valueOf(debugPort) + ",suspend=" + (jvmDebugSuspend ? "y" : "n") + ",server=y ";
    
                    if (StringUtils.stripToNull(product.getJvmArgs()) == null)
                    {
                        product.setJvmArgs(StringUtils.defaultString(jvmArgs));
                    }
    
                    product.setDebugArgs(debugArgs);
                }
                
                actualHttpPort = productHandler.start(product);
            }

            if (productExecutions.size() == 1)
            {
                putIfNotOverridden(systemProperties, "http.port", String.valueOf(actualHttpPort));
                putIfNotOverridden(systemProperties, "context.path", product.getContextPath());
            }

            String baseUrl = MavenGoals.getBaseUrl(product, actualHttpPort);
            // hard coded system properties...
            putIfNotOverridden(systemProperties, "http." + product.getInstanceId() + ".port", String.valueOf(actualHttpPort));
            putIfNotOverridden(systemProperties, "context." + product.getInstanceId() + ".path", product.getContextPath());
            putIfNotOverridden(systemProperties, "http." + product.getInstanceId() + ".url", MavenGoals.getBaseUrl(product, actualHttpPort));
            putIfNotOverridden(systemProperties, "http." + product.getInstanceId() + ".protocol", product.getProtocol());


            putIfNotOverridden(systemProperties, "baseurl." + product.getInstanceId(), baseUrl);
            putIfNotOverridden(systemProperties, "plugin.jar", pluginJar);

            // yes, this means you only get one base url if multiple products, but that is what selenium would expect
            putIfNotOverridden(systemProperties, "baseurl", baseUrl);

            putIfNotOverridden(systemProperties, "homedir." + product.getInstanceId(), productHandler.getHomeDirectory(product).getAbsolutePath());
            putIfNotOverridden(systemProperties, "homedir", productHandler.getHomeDirectory(product).getAbsolutePath());
            putIfNotOverridden(systemProperties, "product." + product.getInstanceId() + ".id", product.getId());
            putIfNotOverridden(systemProperties, "product." + product.getVersion() + ".version", product.getVersion());

            systemProperties.putAll(getProductFunctionalTestProperties(product));
        }
        putIfNotOverridden(systemProperties, "testGroup", testGroupId);
        systemProperties.putAll(getTestGroupSystemProperties(testGroupId));

        if (!noWebapp)
        {
            waitForProducts(productExecutions, true);
        }

        MojoExecutionException thrown = null;
        try
        {
            // Actually run the tests.
            goals.runIntegrationTests("group-" + testGroupId, getClassifier(testGroupId), includes, excludes, systemProperties, targetDirectory, category, skipITVerification);
        }
        catch (MojoExecutionException e)
        {
            // If any tests fail an exception will be thrown. We need to catch that and hold onto it, because
            // even if tests fail any running products still need to be stopped
            thrown = e;
        }
        finally
        {
            if (!noWebapp)
            {
                try
                {
                    // Shut all products down.
                    stopProducts(productExecutions);
                }
                catch (MojoExecutionException e)
                {
                    if (thrown == null)
                    {
                        // If no exception was thrown during the tests, propagate the failure to stop
                        thrown = e;
                    }
                    else
                    {
                        // Otherwise, suppress the stop failure and focus on the test failure
                        thrown.addSuppressed(e);
                    }
                }
            }
        }

        if (thrown != null)
        {
            // If tests failed, or if any products could not be stopped, propagate the exception
            throw thrown;
        }
    }

    /**
     * Adds the property to the map if such property is not overridden in system properties passed to
     * maven executing this mojo.
     * @param map the properties map
     * @param key the key to be added
     * @param value the value to be set. Will be overridden by an existing system property, if such exits.
     */
    private void putIfNotOverridden(Map<String, Object> map, String key, Object value)
    {
        if (!map.containsKey(key))
        {
            if (System.getProperties().containsKey(key))
            {
                map.put(key, System.getProperty(key));
            }
            else
            {
                map.put(key, value);
            }
        }
    }

    /**
     * Returns the classifier of the test group. Unless specified, this is "tomcat85x", the default container.
     */
    private String getClassifier(String testGroupId)
    {
        for (TestGroup group : getTestGroups())
        {
            if (group.getId().equals(testGroupId))
            {
                if (group.getClassifier() != null)
                {
                    return group.getClassifier();
                }
                else
                {
                    return AmpsDefaults.DEFAULT_CONTAINER;
                }
            }
        }
        return AmpsDefaults.DEFAULT_CONTAINER;
    }


    private Map<String, String> getTestGroupSystemProperties(String testGroupId)
    {
        if (NO_TEST_GROUP.equals(testGroupId))
        {
            return Collections.emptyMap();
        }

        for (TestGroup group : getTestGroups())
        {
            if (group.getId().equals(testGroupId))
            {
                return group.getSystemProperties();
            }
        }
        return Collections.emptyMap();
    }

    private List<String> getIncludesForTestGroup(String testGroupId)
    {
        if (NO_TEST_GROUP.equals(testGroupId))
        {
            return Collections.singletonList(functionalTestPattern);
        }
        else
        {
            for (TestGroup group : getTestGroups())
            {
                if (group.getId().equals(testGroupId))
                {
                    List<String> groupIncludes =  group.getIncludes();
                    if(groupIncludes.isEmpty())
                    {
                        return Collections.singletonList(functionalTestPattern);
                    }
                    else
                    {
                        return groupIncludes;
                    }
                }
            }
        }
        return Collections.singletonList(functionalTestPattern);
    }

    private List<String> getExcludesForTestGroup(String testGroupId)
    {
        if (NO_TEST_GROUP.equals(testGroupId))
        {
            return Collections.emptyList();
        }
        else
        {
            for (TestGroup group : getTestGroups())
            {
                if (group.getId().equals(testGroupId))
                {
                    return group.getExcludes();
                }
            }
        }
        return Collections.emptyList();
    }
}