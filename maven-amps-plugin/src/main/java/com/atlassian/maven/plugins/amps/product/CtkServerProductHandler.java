package com.atlassian.maven.plugins.amps.product;

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

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.util.ConfigFileUtils;
import com.atlassian.maven.plugins.amps.util.ProjectUtils;
import com.atlassian.maven.plugins.amps.util.ant.AntJavaExecutorThread;
import com.atlassian.maven.plugins.amps.util.ant.JavaTaskFactory;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.tools.ant.taskdefs.Java;

import static com.atlassian.maven.plugins.amps.util.ZipUtils.unzip;
import static com.atlassian.maven.plugins.amps.util.ant.JavaTaskFactory.output;

/**
 * The Atlassian Federated API CTK Server.
 */
public class CtkServerProductHandler implements ProductHandler
{
    private static final String CTK_SERVER_ARTIFACT_MATCHER = "federated-api-ctk-server-.*\\.jar";

    private final MavenContext context;
    private final MavenGoals goals;
    private final JavaTaskFactory javaTaskFactory;
    private final Log log;

    public CtkServerProductHandler(final MavenContext context, final MavenGoals goals)
    {
        this.context = context;
        this.goals = goals;
        this.javaTaskFactory = new JavaTaskFactory(context.getLog());
        this.log = context.getLog();
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
    public int getDefaultHttpsPort()
    {
        return 8448;
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
    public File getSnapshotDirectory(final Product product)
    {
        return getBaseDirectory(product);
    }

    /**
     * @param product the current product configuration
     * @return the server is stateless, so no separate home directory
     */
    @Override
    public File getHomeDirectory(final Product product)
    {
        return getBaseDirectory(product);
    }

    /**
     * The server itself is stateless, so multiple running instances of the same version share the same base directory.
     * @param product the current product configuration
     * @return the directory containing the CTK server jar files and its dependencies (for the configured product
     * version).
     */
    @Override
    public File getBaseDirectory(final Product product)
    {
        return ProjectUtils.createDirectory(new File(context.getProject().getBuild().getDirectory(), "ctk-server-" + product.getVersion()));
    }

    @Override
    public List<ConfigFileUtils.Replacement> getReplacements(final Product product)
    {
        return Collections.emptyList();
    }

    @Override
    public List<File> getConfigFiles(final Product product, final File snapshotCopyDir)
    {
        return Collections.emptyList();
    }

    @Override
    public void createHomeZip(final File homeDirectory, final File targetZip, final Product product) throws MojoExecutionException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cleanupProductHomeForZip(final Product product, final File homeDirectory) throws MojoExecutionException, IOException
    {
        throw new UnsupportedOperationException();
    }

    private void unpackContainer(final Product product) throws MojoExecutionException
    {
        final File baseDirectory = getBaseDirectory(product);
        final String[] directoryContents = baseDirectory.list();
        if (directoryContents == null || directoryContents.length == 0)
        {
            final File serverDistributionArtifactFile = copyServerArtifactToOutputDirectory(product);
            unpackServerArtifact(serverDistributionArtifactFile, baseDirectory);
            deleteServerArtifact(serverDistributionArtifactFile);
        }
        else
        {
            log.debug("CTK Server " + product.getVersion() + " already unpacked.");
        }
    }

    private File copyServerArtifactToOutputDirectory(final Product product) throws MojoExecutionException
    {
        final File buildDirectory = new File(context.getProject().getBuild().getDirectory());
        final ProductArtifact serverDistributionArtifact = getServerDistributionArtifact(product);
        final String filename = String.format("%s-%s.%s", serverDistributionArtifact.getArtifactId(), serverDistributionArtifact.getVersion(), serverDistributionArtifact.getType());
        return goals.copyZip(buildDirectory, serverDistributionArtifact, filename);
    }

    private void unpackServerArtifact(final File serverDistributionArtifactFile, final File serverDirectory) throws MojoExecutionException
    {
        try
        {
            unzip(serverDistributionArtifactFile, serverDirectory.getPath(), 0);
        }
        catch (final IOException ex)
        {
            throw new MojoExecutionException("Unable to extract CTK server distribution: "  + serverDistributionArtifactFile, ex);
        }
    }

    private void deleteServerArtifact(final File serverDistributionArtifactFile)
    {
        log.debug("Deleting CTK server distribution artifact: " + serverDistributionArtifactFile.getPath());
        if(!serverDistributionArtifactFile.delete())
        {
            log.warn("Failed to delete CTK server distribution artifact: " + serverDistributionArtifactFile.getPath());
        }
    }

    private void startContainer(final Product product)
    {
        final Map<String,String> systemProperties = getSystemProperties(product);
        final Java java = javaTaskFactory.newJavaTask(output(product.getOutput()).systemProperties(systemProperties).
                jvmArgs(product.getJvmArgs() + product.getDebugArgs()));
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
                return name.matches(CTK_SERVER_ARTIFACT_MATCHER);
            }
        });
        if (files.length == 0)
        {
            throw new IllegalStateException("CTK server jar file not found in: " + baseDirectory + " (expected file to match: " + CTK_SERVER_ARTIFACT_MATCHER + ")");
        }
        else if (files.length == 1)
        {
            return files[0];
        }
        else
        {
            throw new IllegalStateException("Found too CTK server jar files, expected only one: " + Arrays.toString(files));
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
        final URL url = new URL(product.getProtocol(), product.getServer(), product.getHttpPort(), product.getContextPath());
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
