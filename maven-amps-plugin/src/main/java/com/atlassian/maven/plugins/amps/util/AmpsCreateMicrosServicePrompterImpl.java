package com.atlassian.maven.plugins.amps.util;

import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

/**
 * @since version
 */
public class AmpsCreateMicrosServicePrompterImpl implements AmpsCreatePluginPrompter
{
    public static final String DEFAULT_VERSION = "1.0.0-SNAPSHOT";
    public static final String DEFAULT_GROUP_ID = "com.atlassian.micros";
    private Prompter prompter;

    @Override
    public CreateMicrosProperties prompt() throws PrompterException
    {
        CreateMicrosProperties props = null;

        String name = prompter.prompt("Micros Service name: ");
        String desc = prompter.prompt("Micros Service description: ");
        String organization = prompter.prompt("Micros Service organization: ");

        String groupId = prompter.prompt("Define value for groupId: ", DEFAULT_GROUP_ID);
        String artifactId = prompter.prompt("Define value for artifactId: ", name);
        String version = prompter.prompt("Define value for version: ", DEFAULT_VERSION);
        String thePackageDefault = artifactId.indexOf(groupId) > 0 ? artifactId : (groupId + "." + artifactId);
        String thePackage = prompter.prompt("Define value for package: ", thePackageDefault);

        String sourceUrlDefault = "https://bitbucket.org/atlassian/" + name + ".git";
        String sourceUrl = prompter.prompt("Define value for source url: ", sourceUrlDefault);
        String ownerEmail = prompter.prompt("Define value for owner email: ");
        String notificationEmail = prompter.prompt("Define value for notification email: ", ownerEmail);

        StringBuilder query = new StringBuilder("Confirm properties configuration:\n");
        query.append("name: ").append(name).append("\n")
                .append("description: ").append(desc).append("\n")
                .append("organization: ").append(organization).append("\n")
                .append("groupId: ").append(groupId).append("\n")
                .append("artifactId: ").append(artifactId).append("\n")
                .append("version: ").append(version).append("\n")
                .append("package: ").append(thePackage).append("\n")
                .append("sourceUrl: ").append(sourceUrl).append("\n")
                .append("ownerEmail: ").append(ownerEmail).append("\n")
                .append("notificationEmail: ").append(notificationEmail).append("\n");

        String confirmed = prompter.prompt(query.toString(), "Y");
        if ("Y".equalsIgnoreCase(confirmed))
        {
            props = new CreateMicrosProperties(name, desc, organization, groupId, artifactId, version, thePackage,
                    sourceUrl, ownerEmail, notificationEmail);
        }

        return props;
    }


}
