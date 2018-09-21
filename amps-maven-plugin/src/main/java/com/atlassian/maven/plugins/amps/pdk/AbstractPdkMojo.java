package com.atlassian.maven.plugins.amps.pdk;

import com.atlassian.maven.plugins.amps.AbstractProductHandlerAwareMojo;
import com.atlassian.maven.plugins.amps.product.ProductHandler;

import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 */
public abstract class AbstractPdkMojo extends AbstractProductHandlerAwareMojo
{
    /**
     *
     */
    @Parameter(property = "atlassian.plugin.key")
    protected String pluginKey;

    /**
     *
     */
    @Parameter(property = "project.groupId")
    protected String groupId;

    /**
     *
     */
    @Parameter(property = "project.artifactId")
    protected String artifactId;

    /**
     * HTTP port for the servlet containers
     */
    @Parameter(property = "http.port")
    protected int httpPort;

    /**
     * Application context path
     */
    @Parameter(property = "context.path")
    protected String contextPath;

    /**
     * Username of user that will install the plugin
     */
    @Parameter(property = "username", defaultValue = "admin")
    protected String username;

    /**
     * Password of user that will install the plugin
     */
    @Parameter(property = "password", defaultValue = "admin")
    protected String password;

    /**
     * Application server
     */
    @Parameter(property = "server", defaultValue = "localhost")
    protected String server;

    protected void ensurePluginKeyExists()
    {
        if (pluginKey == null)
        {
            pluginKey = groupId + "." + artifactId;
        }
    }

    protected int getHttpPort(final ProductHandler handler)
    {
        return httpPort == 0 ? handler.getDefaultHttpPort() : httpPort;
    }

    protected String getContextPath(final ProductHandler handler)
    {
        return contextPath == null ? "/" + handler.getId() : contextPath;
    }
}
