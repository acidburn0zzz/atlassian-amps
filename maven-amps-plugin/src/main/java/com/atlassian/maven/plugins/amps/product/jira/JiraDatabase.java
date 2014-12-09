package com.atlassian.maven.plugins.amps.product.jira;

import com.atlassian.maven.plugins.amps.DataSource;

import org.apache.maven.model.Dependency;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public interface JiraDatabase
{
    /**
     *
     * @return
     */
    Xpp3Dom getPluginConfiguration();

    /**
     *
     * @return
     */
    Dependency getDependency();
}
