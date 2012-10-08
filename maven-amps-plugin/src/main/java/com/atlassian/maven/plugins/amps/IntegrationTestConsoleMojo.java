package com.atlassian.maven.plugins.amps;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.maven.plugins.amps.DebugMojo;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.ProductExecution;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import com.atlassian.maven.plugins.amps.util.GoogleAmpsTracker;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "integration-test-console", requiresDependencyResolution = ResolutionScope.TEST)
@Execute(phase = LifecyclePhase.PACKAGE)
public class IntegrationTestConsoleMojo extends DebugMojo
{
    private final List<ProductArtifact> testFrameworkPlugins = new ArrayList<ProductArtifact>()
    {{
            add(new ProductArtifact("org.junit","com.springsource.org.junit","4.10.0"));
            add(new ProductArtifact("com.atlassian.plugins","atlassian-plugins-osgi-testrunner-bundle","1.1-SNAPSHOT"));
        }};
    
    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException
    {
        getUpdateChecker().check();

        getAmpsPluginVersionChecker().checkAmpsVersionInPom(getSdkVersion(),getMavenContext().getProject());

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

            if (product.getJvmArgs() == null)
            {
                product.setJvmArgs(StringUtils.defaultString(jvmArgs));
            }

            product.setJvmArgs(product.getJvmArgs() + debugArgs);

            if(shouldBuildTestPlugin())
            {
                List<ProductArtifact> plugins = product.getBundledArtifacts();
                plugins.addAll(testFrameworkPlugins);

                List<ProductArtifact> libs = product.getLibArtifacts();
                libs.add(new ProductArtifact("org.junit","com.springsource.org.junit","4.10.0"));
            }

            if (writePropertiesToFile)
            {
                if (productExecutions.size() == 1)
                {
                    properties.put("debug.port", String.valueOf(port));
                }

                properties.put("debug." + product.getInstanceId() + ".port", String.valueOf(port));
            }

            if (ProductHandlerFactory.FECRU.equals(getDefaultProductId()) && debugNotSet()) {
                String message = "You must set the ATLAS_OPTS environment variable to the following string:'" + product.getJvmArgs() + "' when calling atlas-debug to enable Fisheye/Crucible debugging.";
                getLog().error(message);
                throw new MojoFailureException(message);
            }
        }

        startProducts(productExecutions);
    }
}
