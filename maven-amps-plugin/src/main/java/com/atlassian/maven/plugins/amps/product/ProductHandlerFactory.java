package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import org.apache.maven.artifact.factory.ArtifactFactory;

public class ProductHandlerFactory
{
    public static final String REFAPP = "refapp";
    public static final String CONFLUENCE = "confluence";
    public static final String JIRA = "jira";
    public static final String BAMBOO = "bamboo";
    /**
     * @since 6.1.0
     */
    public static final String BITBUCKET = "bitbucket";
    public static final String FECRU = "fecru";
    public static final String CROWD = "crowd";
    public static final String STASH = "stash";
    public static final String CTK_SERVER = "ctk-server";


    private static final List<String> PRODUCT_IDS = ImmutableList.of(
            REFAPP, CONFLUENCE, JIRA, BAMBOO, BITBUCKET, FECRU, CROWD, STASH, CTK_SERVER);

    public static ProductHandler create(String id, MavenContext context, MavenGoals goals, ArtifactFactory artifactFactory)
    {
        if (REFAPP.equals(id))
        {
            return new RefappProductHandler(context, goals,artifactFactory);
        }
        else if (CONFLUENCE.equals(id))
        {
            return new ConfluenceProductHandler(context, goals,artifactFactory);
        }
        else if (JIRA.equals(id))
        {
            return new JiraProductHandler(context, goals,artifactFactory);
        }
        else if (BAMBOO.equals(id))
        {
            return new BambooProductHandler(context, goals,artifactFactory);
        }
        else if (BITBUCKET.equals(id))
        {
            return new BitbucketProductHandler(context, goals, artifactFactory);
        }
        else if (FECRU.equals(id))
        {
            return new FeCruProductHandler(context, goals, artifactFactory);
        }
        else if (CROWD.equals(id))
        {
            return new CrowdProductHandler(context, goals,artifactFactory);
        }
        else if (STASH.equals(id))
        {
            return new StashProductHandler(context, goals,artifactFactory);
        }
        else if (CTK_SERVER.equals(id))
        {
            return new CtkServerProductHandler(context, goals);
        }

        throw new IllegalArgumentException("Unknown product ID: '" + id + "' Valid values: " + getIds());
    }

    public static Collection<String> getIds()
    {
        return PRODUCT_IDS;
    }
}
