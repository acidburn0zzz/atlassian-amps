package com.atlassian.maven.plugins.amps;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides Maven coordinates (@{code groupId}, {@code artifactId} and {@code version}) for an AMPS plugin,
 * as well as specifying which product the plugin is for..
 */
public class PluginInformation
{
    /**
     * Pattern for matching old-style product plugins, like {@code maven-jira-plugin}, and extracting the product ID.
     *
     * @since 6.1.0
     */
    private static final Pattern MAVEN_PRODUCT_PLUGIN = Pattern.compile("maven-(.*)-plugin");
    /**
     * Pattern for matching new-style product plugins, like {@code bitbucket-maven-plugin}, and extracting the product
     * ID.
     *
     * @since 6.1.0
     */
    private static final Pattern PRODUCT_MAVEN_PLUGIN = Pattern.compile("(.*)-maven-plugin");

    private final String artifactId;
    private final String groupId;
    private final String productId;
    private final String version;

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
     * @deprecated in 6.1.0. Use {@link #PluginInformation(String, String, String, String)} or
     *             {@link #fromArtifactId(String, String)} instead and provide the full
     *             {@link #getArtifactId() artifactId}
     */
    @Deprecated
    public PluginInformation(final String productId, final String version)
    {
        this("maven-" + productId + "-plugin", version, productId);
    }

    /**
     * @param artifactId the {@link #getArtifactId() artifactId} for the AMPS plugin
     * @param version    the {@link #getVersion() version} for the AMPS plugin
     * @param productId  the {@link #getProductId() product ID} for the AMPS plugin
     * @since 6.1.0
     */
    private PluginInformation(final String artifactId, final String version, final String productId)
    {
        this("com.atlassian.maven.plugins", artifactId, version, productId);
    }

    /**
     * Extracts the product ID from the provided {@code artifactId}, if it matches a known format. Artifact IDs can
     * be in the following forms:
     * <ul>
     *     <li>{@link #MAVEN_PRODUCT_PLUGIN maven-$productId-plugin}</li>
     *     <li>{@link #PRODUCT_MAVEN_PLUGIN $productId-maven-plugin}</li>
     * </ul>
     * If the artifact ID doesn't match, it is returned unchanged.
     *
     * @param artifactId the artifact ID to extract the product ID from
     * @return the extracted product ID, or the provided artifact ID if it didn't match any known format
     * @since 6.1.0
     */
    public static String extractProductId(final String artifactId)
    {
        // Check first for an old-style artifactId, because it's still the most common style
        Matcher matcher = MAVEN_PRODUCT_PLUGIN.matcher(artifactId);
        if (!matcher.find()) // find() instead of matches() because that's what replaceAll uses
        {
            // If the artifactId doesn't match the old style, try the new style
            matcher = PRODUCT_MAVEN_PLUGIN.matcher(artifactId);
        }
        return matcher.replaceAll("$1");
    }

    /**
     * {@link #extractProductId(String) Extracts the product ID} from the provided {@code artifactId} and
     * creates a new {@code PluginInformation} instance with the artifact ID, version and extracted product ID.
     *
     * @param artifactId the {@link #getArtifactId() artifactId} for the AMPS plugin
     * @param version    the {@link #getVersion() version} for the AMPS plugin
     * @return plugin and product information for the provided {@code artifactId}
     * @since 6.1.0
     */
    public static PluginInformation fromArtifactId(final String artifactId, final String version)
    {
        return new PluginInformation(artifactId, version, extractProductId(artifactId));
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