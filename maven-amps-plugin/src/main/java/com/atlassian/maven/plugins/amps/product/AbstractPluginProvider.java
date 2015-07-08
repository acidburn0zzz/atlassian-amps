package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public abstract class AbstractPluginProvider implements PluginProvider
{
    public final List<ProductArtifact> provide(Product product)
    {
        final List<ProductArtifact> artifacts = new ArrayList<ProductArtifact>();
        artifacts.addAll(product.getPluginArtifacts());

        if (product.getSalVersion() != null)
        {
            artifacts.addAll(getSalArtifacts(product.getSalVersion()));
        }

        if (product.getPdkVersion() != null)
        {
            artifacts.addAll(getPdkInstallArtifacts(product.getPdkVersion()));
        }

        if (product.getRestVersion() != null)
        {
            artifacts.addAll(getRestArtifacts(product.getRestVersion()));
        }

        if (product.getWebConsoleVersion() != null)
        {
            artifacts.addAll(getWebConsoleArtifacts(product.getWebConsoleVersion()));
        }

        if (product.isEnableFastdev() && product.getFastdevVersion() != null)
        {
            artifacts.addAll(getFastdevArtifacts(product.getFastdevVersion()));
        }

        if (product.isEnableDevToolbox() && product.getDevToolboxVersion() != null)
        {
            artifacts.addAll(getDevToolboxArtifacts(product.getDevToolboxVersion()));
        }

        if (product.isEnablePde() && product.getPdeVersion() != null) 
        {
            artifacts.addAll(getPdeArtifacts(product.getPdeVersion()));
        }

        return artifacts;
    }

    protected abstract Collection<ProductArtifact> getSalArtifacts(String salVersion);

    protected Collection<ProductArtifact> getPdkInstallArtifacts(String pdkInstallVersion)
    {
        return Collections.singletonList(new ProductArtifact("com.atlassian.pdkinstall", "pdkinstall-plugin", pdkInstallVersion));
    }

    protected Collection<ProductArtifact> getWebConsoleArtifacts(String webConsoleVersion)
    {
        return Arrays.asList(
                new ProductArtifact("org.apache.felix", "org.apache.felix.webconsole", webConsoleVersion),
                new ProductArtifact("org.apache.felix", "org.osgi.compendium", "1.2.0"),
                new ProductArtifact("com.atlassian.labs.httpservice", "httpservice-bridge", "0.6.2")
                );
    }

    protected Collection<ProductArtifact> getFastdevArtifacts(String fastdevVersion)
    {
        return Collections.singletonList(new ProductArtifact("com.atlassian.labs", "fastdev-plugin", fastdevVersion));
    }

    protected Collection<ProductArtifact> getDevToolboxArtifacts(String devToolboxVersion)
    {
        List<ProductArtifact> artifacts = new ArrayList();
        artifacts.add(new ProductArtifact("com.atlassian.devrel", "developer-toolbox-plugin", devToolboxVersion));
        artifacts.add(new ProductArtifact("com.atlassian.labs", "rest-api-browser", AmpsDefaults.DEFAULT_REST_API_BROWSER_VERSION));
        return artifacts;
    }

    protected Collection<ProductArtifact> getPdeArtifacts(String pdeVersion)
    {
        return Collections.singletonList(new ProductArtifact("com.atlassian.plugins", "plugin-data-editor", pdeVersion));
    }
    
    protected Collection<ProductArtifact> getRestArtifacts(String restVersion)
    {
        return Collections.singletonList(new ProductArtifact("com.atlassian.plugins.rest", "atlassian-rest-module", restVersion));
    }
}
