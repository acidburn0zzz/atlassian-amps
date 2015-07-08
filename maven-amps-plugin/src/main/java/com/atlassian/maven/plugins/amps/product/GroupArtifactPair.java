package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.ProductArtifact;

/**
 * Represents an immutable pair of groupId and artifactId values
 * that can be converted into ProductArtifact by providing actual version value.
 */
final class GroupArtifactPair
{
    private final String groupId;
    private final String artifactId;

    GroupArtifactPair(final String groupId, final String artifactId)
    {

        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    ProductArtifact createProductArtifactWithVersion(final String version)
    {
        return new ProductArtifact(groupId, artifactId, version);
    }
}
