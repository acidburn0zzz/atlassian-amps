package com.atlassian.maven.plugins.amps;

/**
 * Represents an addon product to be retrieved
 */
public class AddonProduct
{
    private String productId, version;

    public AddonProduct() {
    }

    public AddonProduct(final String productId) {
        this.productId = productId;
    }

    public AddonProduct(final String productId, final String version) {
        this.productId = productId;
        this.version = version;
    }

    public String getProductId()
    {
        return productId;
    }

    public void setProductId(final String productId)
    {
        this.productId = productId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    @Override
    public String toString()
    {
        return productId + ":" + version;
    }
}
