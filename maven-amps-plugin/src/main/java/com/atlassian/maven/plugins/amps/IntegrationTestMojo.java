package com.atlassian.maven.plugins.amps;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.atlassian.maven.plugins.amps.product.ProductHandler;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * Run the integration tests against the webapp
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

        //ids.addAll(ProductHandlerFactory.getIds());
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

        List<ProductExecution> executionsDeclaredInPom = getTestGroupProductExecutions(testGroupId);
        List<ProductExecution> productExecutions = includeStudioDependentProducts(executionsDeclaredInPom, goals);
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

                    if (product.getJvmArgs() == null)
                    {
                        product.setJvmArgs(StringUtils.defaultString(jvmArgs));
                    }

                    product.setJvmArgs(product.getJvmArgs() + debugArgs);
                }
                
                actualHttpPort = productHandler.start(product);
            }

            if (productExecutions.size() == 1)
            {
                systemProperties.put("http.port", String.valueOf(actualHttpPort));
                systemProperties.put("context.path", product.getContextPath());
            }

            String baseUrl = MavenGoals.getBaseUrl(product, actualHttpPort);
            // hard coded system properties...
            systemProperties.put("http." + product.getInstanceId() + ".port", String.valueOf(actualHttpPort));
            systemProperties.put("context." + product.getInstanceId() + ".path", product.getContextPath());
            systemProperties.put("http." + product.getInstanceId() + ".url", MavenGoals.getBaseUrl(product, actualHttpPort));

            systemProperties.put("baseurl." + product.getInstanceId(), baseUrl);
            systemProperties.put("plugin.jar", pluginJar);

            // yes, this means you only get one base url if multiple products, but that is what selenium would expect
            if (!systemProperties.containsKey("baseurl"))
            {
                systemProperties.put("baseurl", baseUrl);
            }

            systemProperties.put("homedir." + product.getInstanceId(), productHandler.getHomeDirectory(product).getAbsolutePath());
            if (!systemProperties.containsKey("homedir"))
            {
                systemProperties.put("homedir", productHandler.getHomeDirectory(product).getAbsolutePath());
            }

            systemProperties.putAll(getProductFunctionalTestProperties(product));
        }
        systemProperties.put("testGroup", testGroupId);
        systemProperties.putAll(getTestGroupSystemProperties(testGroupId));

        if (parallel)
        {
            waitForProducts(productExecutions, true);
        }

        // Actually run the tests
        goals.runTests("group-" + testGroupId, getClassifier(testGroupId), includes, excludes, systemProperties, targetDirectory);

        // Shut all products down
        if (!noWebapp)
        {
            stopProducts(productExecutions);
        }
    }

    /**
     * Returns the classifier of the test group. Unless specified, this is "tomcat6x", the default container.
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
                    return DEFAULT_CONTAINER;
                }
            }
        }
        return DEFAULT_CONTAINER;
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
                    return group.getIncludes();
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