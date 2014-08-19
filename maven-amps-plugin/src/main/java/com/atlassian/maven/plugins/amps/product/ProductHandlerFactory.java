package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.product.studio.StudioBambooProductHandler;
import com.atlassian.maven.plugins.amps.product.studio.StudioConfluenceProductHandler;
import com.atlassian.maven.plugins.amps.product.studio.StudioCrowdProductHandler;
import com.atlassian.maven.plugins.amps.product.studio.StudioFeCruProductHandler;
import com.atlassian.maven.plugins.amps.product.studio.StudioJiraProductHandler;
import com.atlassian.maven.plugins.amps.product.studio.StudioProductHandler;

import java.util.Arrays;
import java.util.Collection;

import org.apache.maven.artifact.factory.ArtifactFactory;

public class ProductHandlerFactory
{
    public static final String REFAPP = "refapp";
    public static final String CONFLUENCE = "confluence";
    public static final String JIRA = "jira";
    public static final String JIRA_PLATFORM = "jira-platform";
    public static final String JIRA_SOFTWARE = "jira-software";
    public static final String SERVICEDESK = "servicedesk";
    public static final String BAMBOO = "bamboo";
    public static final String FECRU = "fecru";
    public static final String CROWD = "crowd";
    public static final String STASH = "stash";
    public static final String CTK_SERVER = "ctk-server";

    public static final String STUDIO = "studio";
    public static final String STUDIO_CONFLUENCE = "studio-confluence";
    public static final String STUDIO_JIRA = "studio-jira";
    public static final String STUDIO_BAMBOO = "studio-bamboo";
    public static final String STUDIO_FECRU = "studio-fecru";
    public static final String STUDIO_CROWD = "studio-crowd";



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
        else if (JIRA_PLATFORM.equals(id))
        {
            return new JiraPlatformProductHandler(context, goals,artifactFactory);
        }
        else if (JIRA_SOFTWARE.equals(id))
        {
            return new JiraSoftwareProductHandler(context, goals,artifactFactory);
        }
        else if (SERVICEDESK.equals(id))
        {
            return new JiraServiceDeskProductHandler(context, goals,artifactFactory);
        }
        else if (BAMBOO.equals(id))
        {
            return new BambooProductHandler(context, goals,artifactFactory);
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

        // The Studio product itself
        else if (STUDIO.equals(id))
        {
            return new StudioProductHandler(context, goals, artifactFactory);
        }

        // The Studio products (products which are part of)
        else if (STUDIO_CONFLUENCE.equals(id))
        {
            return new StudioConfluenceProductHandler(context, goals,artifactFactory);
        }
        else if (STUDIO_JIRA.equals(id))
        {
            return new StudioJiraProductHandler(context, goals,artifactFactory);
        }
        else if (STUDIO_BAMBOO.equals(id))
        {
            return new StudioBambooProductHandler(context, goals,artifactFactory);
        }
        else if (STUDIO_FECRU.equals(id))
        {
            return new StudioFeCruProductHandler(context, goals, artifactFactory);
        }
        else if (STUDIO_CROWD.equals(id))
        {
            return new StudioCrowdProductHandler(context, goals,artifactFactory);
        }


        throw new IllegalArgumentException("Unknown product id: '" + id + "' Valid values: "
            + Arrays.toString(getIds().toArray()));
    }

    public static Collection<String> getIds()
    {
        return Arrays.asList(REFAPP, CONFLUENCE, JIRA, JIRA_PLATFORM, JIRA_SOFTWARE, SERVICEDESK, BAMBOO, FECRU, CROWD, STASH, CTK_SERVER,
                STUDIO, STUDIO_CONFLUENCE, STUDIO_JIRA, STUDIO_BAMBOO, STUDIO_FECRU, STUDIO_CROWD);
    }
}
