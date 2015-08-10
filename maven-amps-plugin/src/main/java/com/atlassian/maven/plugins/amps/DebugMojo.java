package com.atlassian.maven.plugins.amps;

import com.atlassian.maven.plugins.amps.util.GoogleAmpsTracker;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.util.List;

/**
 * Debug the webapp
 */
@Mojo(name = "debug", requiresDependencyResolution = ResolutionScope.TEST)
@Execute(phase = LifecyclePhase.PACKAGE)
public class DebugMojo extends RunMojo
{
    /**
     * port for debugging
     */
    @Parameter(property = "jvm.debug.port", defaultValue = "5005")
    protected int jvmDebugPort;

    /**
     * Suspend when debugging
     */
    @Parameter(property = "jvm.debug.suspend")
    protected boolean jvmDebugSuspend = false;


    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException
    {
        if (!shouldExecute())
        {
            getLog().info("Skipping execution");
            return;
        }

        getUpdateChecker().check();

        getAmpsPluginVersionChecker().checkAmpsVersionInPom(getAmpsPluginVersion(),getMavenContext().getProject());

        promptForEmailSubscriptionIfNeeded();
        
        trackFirstRunIfNeeded();
        getGoogleTracker().track(GoogleAmpsTracker.DEBUG);

        final List<ProductExecution> productExecutions = getProductExecutions();
        setParallelMode(productExecutions);

        int counter = 0;
        for (ProductExecution productExecution : productExecutions)
        {
            final Product product = productExecution.getProduct();

            if (product.getJvmDebugPort() == 0)
            {
                product.setJvmDebugPort(jvmDebugPort + counter++);
            }
            final int port = product.getJvmDebugPort();

            String debugArgs = " -Xdebug -Xrunjdwp:transport=dt_socket,address=" +
                               String.valueOf(port) + ",suspend=" + (jvmDebugSuspend ? "y" : "n") + ",server=y ";

            if (StringUtils.stripToNull(product.getJvmArgs()) == null)
            {
                product.setJvmArgs(StringUtils.defaultString(jvmArgs));
            }

            product.setDebugArgs(debugArgs);

            if (writePropertiesToFile)
            {
                if (productExecutions.size() == 1)
                {
                    properties.put("debug.port", String.valueOf(port));
                }

                properties.put("debug." + product.getInstanceId() + ".port", String.valueOf(port));
            }
        }

        startProducts(productExecutions);
    }
}
