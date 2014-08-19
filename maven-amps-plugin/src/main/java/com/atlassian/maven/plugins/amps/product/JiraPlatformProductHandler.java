package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.DataSource;
import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.util.ConfigFileUtils.Replacement;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.atlassian.maven.plugins.amps.product.ProductHandlerFactory.JIRA_PLATFORM;
import static com.atlassian.maven.plugins.amps.util.ConfigFileUtils.RegexReplacement;
import static com.atlassian.maven.plugins.amps.util.FileUtils.fixWindowsSlashes;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isBlank;

public class JiraPlatformProductHandler extends JiraProductHandler
{
      public JiraPlatformProductHandler(final MavenContext context, final MavenGoals goals, ArtifactFactory artifactFactory)
    {
        super(context, goals, artifactFactory);
    }

    public String getId()
    {
        return JIRA_PLATFORM;
    }

    @Override
    public ProductArtifact getArtifact()
    {
        return new ProductArtifact("com.atlassian.jira", "atlassian-jira-webapp-platform", "RELEASE");
    }

    @Override
    public ProductArtifact getTestResourcesArtifact()
    {
        return new ProductArtifact("com.atlassian.jira.plugins", "jira-platform-plugin-test-resources");
    }
}
