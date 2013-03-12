package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.util.ConfigFileUtils;
import com.atlassian.maven.plugins.amps.util.ProjectUtils;
import com.atlassian.maven.plugins.amps.util.ant.AntJavaExecutorThread;
import com.atlassian.maven.plugins.amps.util.ant.JavaTaskFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.tools.ant.taskdefs.Java;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.atlassian.maven.plugins.amps.util.ant.JavaTaskFactory.output;

/**
 * The Atlassian Federated API CTK Server.
 */
public class CtkServerProductHandler implements ProductHandler
{
    private final MavenContext context;
    private final MavenGoals goals;
    private final JavaTaskFactory javaTaskFactory;

    public CtkServerProductHandler(final MavenContext context, final MavenGoals goals)
    {
        this.context = context;
        this.goals = goals;
        this.javaTaskFactory = new JavaTaskFactory(context.getLog());
    }

    public String getId()
    {
        return ProductHandlerFactory.CTK_SERVER;
    }

    @Override
    public int getDefaultHttpPort()
    {
        return 8990;
    }

    @Override
    public String getDefaultContextPath()
    {
        return "/";
    }

    @Override
    public int start(final Product product) throws MojoExecutionException
    {
        unpackContainer(product);
        startContainer(product);
        return product.getHttpPort();
    }

    @Override
    public void stop(final Product product) throws MojoExecutionException
    {
        try
        {
            stopContainer(product);
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Failed to send stop command to CTK server", e);
        }
    }

    @Override
    public String getDefaultContainerId()
    {
        return "ctk-server";
    }

    @Override
    public File getSnapshotDirectory(Product product)
    {
        return getBaseDirectory(product);
    }

    @Override
    public File getHomeDirectory(Product product)
    {
        return getBaseDirectory(product);
    }

    @Override
    public File getBaseDirectory(Product product)
    {
        return ProjectUtils.createDirectory(new File(context.getProject().getBuild().getDirectory(), product.getInstanceId()));
    }

    @Override
    public List<ConfigFileUtils.Replacement> getReplacements(Product product)
    {
        return Collections.emptyList();
    }

    @Override
    public List<File> getConfigFiles(Product product, File snapshotCopyDir)
    {
        return Collections.emptyList();
    }

    @Override
    public void createHomeZip(File homeDirectory, File targetZip, Product product) throws MojoExecutionException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cleanupProductHomeForZip(Product product, File homeDirectory) throws MojoExecutionException, IOException
    {
        throw new UnsupportedOperationException();
    }

    private void unpackContainer(final Product product) throws MojoExecutionException
    {
        final ProductArtifact serverDistributionArtifact = getServerDistributionArtifact(product);
        final String outputDirectory = getBaseDirectory(product).getAbsolutePath();
        goals.unpackProductArtifact(serverDistributionArtifact, outputDirectory);
    }

    private void startContainer(final Product product)
    {
        final Map<String,String> systemProperties = getSystemProperties(product);
        final Java java = javaTaskFactory.newJavaTask(output(product.getOutput()).systemProperties(systemProperties).jvmArgs(product.getJvmArgs()));
        java.setDir(getBaseDirectory(product));
        java.setJar(findServerJar(product));
        java.createArg().setValue("--host");
        java.createArg().setValue(product.getServer());
        java.createArg().setValue("--port");
        java.createArg().setValue(Integer.toString(product.getHttpPort()));

        final AntJavaExecutorThread javaThread = new AntJavaExecutorThread(java);
        javaThread.start();
    }

    private ProductArtifact getServerDistributionArtifact(final Product ctx)
    {
        return new ProductArtifact("com.atlassian.federation", "federated-api-ctk-server-distribution", ctx.getVersion(), "zip");
    }

    private Map<String, String> getSystemProperties(final Product product)
    {
        final Map<String, String> map = new HashMap<String, String>();
        for (Map.Entry<String, Object> entry : product.getSystemPropertyVariables().entrySet())
        {
            map.put(entry.getKey(), (String) entry.getValue());
        }
        return map;
    }

    private File findServerJar(final Product product)
    {
        final File baseDirectory = getBaseDirectory(product);
        final File[] files = baseDirectory.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(final File dir, final String name)
            {
                return name.matches("federated-api-ctk-server-.*\\.jar");
            }
        });
        if (files.length == 0)
        {
            throw new IllegalStateException("Jar file containing the CTK server could not be found in: " + baseDirectory);
        }
        else if (files.length == 1)
        {
            return files[0];
        }
        else
        {
            throw new IllegalStateException("Found more than one jar file name like the CTK server: " + Arrays.toString(files));
        }
    }

    /**
     * Send a <code>DELETE</code> request to the server to indicate it should stop serving content and shut down.
     * @param product the current product
     * @throws IOException
     * @throws MojoExecutionException
     */
    private void stopContainer(final Product product) throws IOException, MojoExecutionException
    {
        final URL url = new URL("http", product.getServer(), product.getHttpPort(), product.getContextPath());
        final HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setConnectTimeout(product.getShutdownTimeout());
        httpConnection.setRequestMethod("DELETE");

        final int responseCode = httpConnection.getResponseCode();
        if (responseCode != 200)
        {
            throw new MojoExecutionException("CTK server didn't understand stop command; received HTTP response code: " + responseCode);
        }
    }
}
