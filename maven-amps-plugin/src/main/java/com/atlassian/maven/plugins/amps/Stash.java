package com.atlassian.maven.plugins.amps;

import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;


public class Stash extends Product
{
    {
        id = ProductHandlerFactory.STASH;
    }

    @Override
    public String toString()
    {
        return "Stash " + id + " [instanceId=" + instanceId + ", localhost:" + httpPort + contextPath + "]";
    }

}
