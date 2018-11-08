package com.atlassian.maven.plugins.amps;

import com.atlassian.maven.plugins.amps.product.ProductHandler;
import com.atlassian.maven.plugins.amps.util.GoogleAmpsTracker;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Run the webapp
 */
@Mojo(name = "run", requiresDependencyResolution = ResolutionScope.TEST)
@Execute(phase = LifecyclePhase.PACKAGE)
public class RunMojo extends AbstractTestGroupsHandlerMojo
{
    @Parameter(property = "wait", defaultValue = "true")
    private boolean wait;

    /**
     * Whether or not to write properties used by the plugin to amps.properties.
     */
    @Parameter(property = "amps.properties", required = true, defaultValue = "false")
    protected boolean writePropertiesToFile;

    /**
     * When this property is set to {@literal true}, Mojo will be executed on the last project in the Reactor
     */
    @Parameter(property = "runLastProject", required = true, defaultValue = "false")
    protected boolean runLastProject;

    /**
     * When there is {@literal runProject} property set, Mojo will be executed on project with specified artifact's ID.
     * <p>Example:
     * <ul>
     *     <li><code>mvn amps:run -DrunProject=my-project</code></li>
     * </ul>
     * </p>
     */
    @Parameter(property = "runProject")
    protected String runProject;

    /**
     * The properties actually used by the mojo when running
     */
    protected final Map<String, String> properties = new HashMap<>();

    protected void doExecute() throws MojoExecutionException, MojoFailureException
    {
        if (!shouldExecute())
        {
            getLog().info("Skipping execution");
            return;
        }

        getUpdateChecker().check();

        getAmpsPluginVersionChecker().checkAmpsVersionInPom(getAmpsPluginVersion(),getMavenContext().getProject());

        trackFirstRunIfNeeded();
        
        getGoogleTracker().track(GoogleAmpsTracker.RUN);

        final List<ProductExecution> productExecutions = getProductExecutions();

        startProducts(productExecutions);
    }

    protected void startProducts(List<ProductExecution> productExecutions) throws MojoExecutionException
    {
        if (wait)
        {
            getLog().debug("=======> ADDING SHUTDOWN HOOK\n");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                getLog().info("Running Shutdown Hook");
                try
                {
                    stopProducts(productExecutions);
                }
                catch (MojoExecutionException e)
                {
                    throw new RuntimeException("Unable to shut down products in shutdown hook", e);
                }
            }));
        }

        long globalStartTime = System.nanoTime();
        setParallelMode(productExecutions);
        List<StartupInformation> successMessages = Lists.newArrayList();
        for (ProductExecution productExecution : productExecutions)
        {
            final ProductHandler productHandler = productExecution.getProductHandler();
            final Product product = productExecution.getProduct();
            if (product.isInstallPlugin() == null)
            {
                product.setInstallPlugin(shouldInstallPlugin());
            }

            //add artifacts for test console
            if (shouldBuildTestPlugin())
            {
                List<ProductArtifact> plugins = product.getBundledArtifacts();
                plugins.addAll(getTestFrameworkPlugins());
            }

            // Leave a blank line and say what it's doing
            getLog().info("");
            if (StringUtils.isNotBlank(product.getOutput()))
            {
                getLog().info(String.format("Starting %s... (see log at %s)", product.getInstanceId(), product.getOutput()));
            }
            else
            {
                getLog().info(String.format("Starting %s...", product.getInstanceId()));
            }

            // Actually start the product
            long startTime = System.nanoTime();
            int actualHttpPort = productHandler.start(product);
            long durationSeconds = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime);

            // Log the success message
            StartupInformation message = new StartupInformation(product, "started successfully", actualHttpPort, durationSeconds);
            if (!parallel)
            {
                getLog().info(message.toString());
            }
            successMessages.add(message);

            if (writePropertiesToFile)
            {
                if (productExecutions.size() == 1)
                {
                    properties.put("http.port", String.valueOf(actualHttpPort));
                    properties.put("context.path", product.getContextPath());
                }

                properties.put("http." + product.getInstanceId() + ".port", String.valueOf(actualHttpPort));
                properties.put("context." + product.getInstanceId() + ".path", product.getContextPath());
                String baseUrl = MavenGoals.getBaseUrl(product, actualHttpPort);
                properties.put("baseurl." + product.getInstanceId(), baseUrl);
            }
        }

        if (writePropertiesToFile)
        {
            writePropertiesFile();
        }

        if (parallel)
        {
            waitForProducts(productExecutions, true);
        }
        long globalDurationSeconds = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - globalStartTime);

        // Give the messages once all applications are started
        if (successMessages.size() > 1 || parallel)
        {
            getLog().info("");
            getLog().info("=== Summary (total time " + globalDurationSeconds + "s):");
            // First show the log files
            for (StartupInformation message : successMessages)
            {
                if (StringUtils.isNotBlank(message.getOutput()))
                {
                    getLog().info("Log available at: " + message.getOutput());
                }
            }
            // Then show the applications
            for (StartupInformation message : successMessages)
            {
                getLog().info(message.toString());
            }
        }

        if (wait)
        {
            getLog().info("Type Ctrl-C to shutdown gracefully");
            try
            {
                // Handles Ctrl-D commands
                for (int r; (r=System.in.read()) != -1;)
                {
                }
            }
            catch (final Exception e)
            {
                // ignore
            }

            // We don't stop products when -Dwait=false, because some projects rely on the
            // application running after the end of the RunMojo goal. The Invoker tests
            // check this behaviour.
            stopProducts(productExecutions);
        }
    }

    /**
     * Only install a plugin if the installPlugin flag is true and the project is a jar. If the test plugin was built,
     * it will be installed as well.
     */
    private boolean shouldInstallPlugin()
    {
        Artifact artifact = getMavenContext().getProject().getArtifact();
        return installPlugin &&
                (artifact != null && !"pom".equalsIgnoreCase(artifact.getType()));
    }

    private void writePropertiesFile() throws MojoExecutionException
    {
        final Properties props = new Properties();

        for (Map.Entry<String, String> entry : properties.entrySet())
        {
            props.setProperty(entry.getKey(), entry.getValue());
        }

        final File ampsProperties = new File(getMavenContext().getProject().getBuild().getDirectory(), "amps.properties");
        try (OutputStream out = new FileOutputStream(ampsProperties))
        {
            props.store(out, "");
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Error writing " + ampsProperties.getAbsolutePath(), e);
        }
    }

    /**
     * Wraps information about the startup of a product
     */
    private static class StartupInformation
    {
        private int actualHttpPort;
        private long durationSeconds;
        private String event;
        private Product product;

        StartupInformation(final Product product, final String event, final int actualHttpPort, final long durationSeconds)
        {
            this.actualHttpPort = actualHttpPort;
            this.product = product;
            this.event = event;
            this.durationSeconds = durationSeconds;
        }

        @Override
        public String toString()
        {
            String message = String.format("%s %s in %ds", product.getInstanceId(), event
                    + (Boolean.FALSE.equals(product.getSynchronousStartup()) ? " (asynchronously)" : ""), durationSeconds);
            if (actualHttpPort != 0)
            {
                // product.getServer() replaced with localhost to make the link direct to the spun up product, may wish to use this again in the future
                message += " at " + product.getProtocol() + "://" + "localhost" + ":" + actualHttpPort + (product.getContextPath().equals("ROOT") ? "" : product.getContextPath());
            }
            return message;
        }

        /**
         * @return the output of the product
         */
        public String getOutput()
        {
            return product.getOutput();
        }
    }

    /**
     * <p>Determines whether Mojo should be executed. By default it won't affect execution but it can be influenced by
     * {@link com.atlassian.maven.plugins.amps.RunMojo#runLastProject} and {@link com.atlassian.maven.plugins.amps.RunMojo#runProject} properties</p>
     *
     * @see com.atlassian.maven.plugins.amps.RunMojo#runLastProject
     * @see com.atlassian.maven.plugins.amps.RunMojo#runProject
     *
     * @return <code>true</code> when this execution not should be skipped, <code>false</code> otherwise
     */
    protected boolean shouldExecute()
    {
        final MavenContext mavenContext = getMavenContext();
        final MavenProject currentProject = mavenContext.getProject();

        getLog().debug(String.format("Current project ID: %s, runLastProject=%b, runProject=%s", currentProject.getArtifactId(), runLastProject, runProject));

        //check explicit runProject setup
        if (StringUtils.isNotBlank(runProject))
        {
            return StringUtils.equalsIgnoreCase(runProject, currentProject.getArtifactId());
        }

        //otherwise respect runLastProject
        if (!runLastProject)
        {
            return true;
        }
        final List<MavenProject> reactor = mavenContext.getReactor();
        return reactor == null || Iterables.getLast(reactor, currentProject).equals(currentProject);
    }
}
