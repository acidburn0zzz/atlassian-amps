package com.atlassian.maven.plugins.amps;

/**
 * Provides Maven coordinates (@{code groupId}, {@code artifactId} and {@code version}) for an AMPS plugin,
 * as well as specifying which product the plugin is for..
 */
public class PluginInformation
{
    private final String artifactId;
    private final String groupId;
    private final String productId;
    private final String version;

    /**
     * @param artifactId the {@link #getArtifactId() artifactId} for the AMPS plugin
     * @param version    the {@link #getVersion() version} for the AMPS plugin
     * @param productId  the {@link #getProductId() product ID} for the AMPS plugin
     * @since 6.1.0
     */
    public PluginInformation(final String artifactId, final String version, final String productId)
    {
        this("com.atlassian.maven.plugins", artifactId, version, productId);
    }

    /**
     * @param groupId    the {@link #getGroupId() groupId} for the AMPS plugin
     * @param artifactId the {@link #getArtifactId() artifactId} for the AMPS plugin
     * @param version    the {@link #getVersion() version} for the AMPS plugin
     * @param productId  the {@link #getProductId() product ID} for the AMPS plugin
     * @since 6.1.0
     */
    public PluginInformation(final String groupId, final String artifactId,
                             final String version, final String productId)
    {
        this.artifactId = artifactId;
        this.groupId = groupId;
        this.productId = productId;
        this.version = version;
    }

    /**
     * @param productId the {@link #getProductId product ID} for the AMPS plugin, from which the
     *                  {@link #getGroupId() groupId} and {@link #getArtifactId() artifactId} will be inferred
     * @param version   the {@link #getVersion() version} for the AMPS plugin
     * @deprecated in 6.1.0. Use {@link #PluginInformation(String, String, String, String)} and provide an explicit
     *             {@link #getGroupId() grouId} and {@link #getArtifactId() artifactId} instead
     */
    @Deprecated
    public PluginInformation(final String productId, final String version)
    {
        this("maven-" + productId + "-plugin", version, productId);
    }

    /**
     * @return the {code artifactId} for the AMPS plugin
     * @since 6.1.0
     */
    public String getArtifactId()
    {
        return artifactId;
    }

    /**
     * @return the {@code groupId} for the AMPS plugin
     * @since 6.1.0
     */
    public String getGroupId()
    {
        return groupId;
    }

    /**
     * @return the {@link #getProductId() product ID} for the AMPS plugin
     */
    public String getId()
    {
        return productId;
    }

    /**
     * @return the product (JIRA, Confluence, etc.) for the AMPS plugin
     * @since 6.1.0
     */
    public String getProductId()
    {
        return productId;
    }

    /**
     * @return the {@code version} for the AMPS plugin
     */
    public String getVersion()
    {
        return version;
    }
}