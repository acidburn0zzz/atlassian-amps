package com.atlassian.maven.plugins.amps.product;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.util.ConfigFileUtils;

import org.apache.maven.plugin.MojoExecutionException;

public interface ProductHandler
{
    /**
     * @return a convenient string to identify this application, especially for the context path.
     * No further restriction is defined on this ID.
     */
    String getId();

    /**
     * Extracts the product and its home, prepares them and starts the product.
     * @return the port on which the product is accessible
     */
    int start(Product ctx) throws MojoExecutionException;

    void stop(Product ctx) throws MojoExecutionException;

    int getDefaultHttpPort();

    String getDefaultContextPath();

    String getDefaultContainerId();

    String getDefaultContainerId(final Product product) throws MojoExecutionException;

    /**
     * Return the directory to snapshot when we want to restore
     * the state of the instance.
     *
     * Most often, equivalent to the home directory.
     */
    File getSnapshotDirectory(Product product);

    File getHomeDirectory(Product product);

    File getBaseDirectory(Product product);

    /**
     * Lists parameters which must be replaced in the configuration files of the home directory.
     * <p/>
     * Replacements returned by this method are guaranteed to be reversed when creating the home zip.
     *
     * @return a mutable list of replacements
     */
    List<ConfigFileUtils.Replacement> getReplacements(Product product);

    /**
     * List the configuration files. Used when doing a snapshot to reopen on another
     * machine, with different port, context path, path, instanceId
     * <p/>
     * Files returned by this method are guaranteed to be reversed when creating the home zip.
     *
     * @param snapshotCopyDir A snapshot equivalent to the home in most cases. It is a copy of the folder: {@link #getSnapshotDirectory(Product)}
     *
     * @return a mutable list of files
     */
    List<File> getConfigFiles(Product product, File snapshotCopyDir);

    /**
     * Snapshots the home directory. The goal is that the state is totally restored if we restart the application
     * with this minimal snapshot.
     * <p/>
     * It must call {@link #cleanupProductHomeForZip(Product, File)} to clean up the snapshot.
     *
     *
     * @param homeDirectory The path to the previous run's home directory.
     * @param targetZip     The path to the final zip file.
     * @param product       The product
     *
     * @since 3.1-m3
     */
    public void createHomeZip(final File homeDirectory, final File targetZip, final Product product) throws MojoExecutionException;

    /**
     * Prepares the home directory to be zipped.
     * <ul>
     * <li>Removes all unnecessary files</li>
     * <li>Perform product-specific clean-up</li>
     * <ul>
     * @param product the product details
     * @param homeDirectory an image of the home directory. This is not the current home, so you're free to remove files and parametrise them.
     */
    public void cleanupProductHomeForZip(Product product, File homeDirectory) throws MojoExecutionException, IOException;

    /**
     * Get the default port for HTTP for each product.
     * @since 5.0.4
     */
    int getDefaultHttpsPort();
}
