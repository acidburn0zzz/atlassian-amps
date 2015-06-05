package com.atlassian.maven.plugins.amps.util;

import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

import java.util.regex.Pattern;

/**
 * @since version
 */
public class AmpsCreateMicrosServicePrompterImpl implements AmpsCreatePluginPrompter
{
    public static final String DEFAULT_VERSION = "1.0.0-SNAPSHOT";
    public static final String DEFAULT_GROUP_ID = "io.atlassian.micros";
    private Prompter prompter;

    @Override
    public CreatePluginProperties prompt() throws PrompterException
    {
        CreateMicrosProperties props = null;

        String name = promptRegexAndMaxLength("Micros Service name: ", "EasyMicros", "^[A-Za-z\\d]*$", 100);
        String desc = promptMinLength("Micros Service description: ", null, 1);
        String organization = promptRegex("Micros Service organization: ", "RD:Engineering Services", "^[^&]+$");

        String groupId = prompter.prompt("Define value for groupId: ", DEFAULT_GROUP_ID);
        String artifactId = prompter.prompt("Define value for artifactId: ", name);
        String version = prompter.prompt("Define value for version: ", DEFAULT_VERSION);
//        String thePackageDefault = artifactId.indexOf(groupId) > 0 ? artifactId : (groupId + "." + artifactId);
        String thePackage = prompter.prompt("Define value for package: ", groupId);
        String sourceUrlDefault = "https://bitbucket.org/atlassian/" + name + ".git";
        String sourceUrl = prompter.prompt("Define value for source url: ", sourceUrlDefault);
        String ownerEmail = promptRegex("Define value for owner email: ", "saas@atlassian.com", ".+@atlassian\\.com$");
        String notificationEmail = promptRegex("Define value for notification email: ", ownerEmail, ".+@atlassian\\.com$");

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

    private String promptMinLength(String message, String defaultValue, int minLength) throws PrompterException
    {
        String value = defaultValue == null ? prompter.prompt(message) : prompter.prompt(message, defaultValue);
        if (value.length() <= minLength)
        {
            value = promptMinLength(message, defaultValue, minLength);
        }
        return value;
    }

    private String promptMaxLength(String message, String defaultValue, int maxLength) throws PrompterException
    {
        String value = defaultValue == null ? prompter.prompt(message) : prompter.prompt(message, defaultValue);
        if (value.length() > maxLength)
        {
            value = promptMaxLength(message, defaultValue, maxLength);
        }
        return value;
    }

    private String promptRegex(String message, String defaultValue, String regex) throws PrompterException
    {
        String value = defaultValue == null ? prompter.prompt(message) : prompter.prompt(message, defaultValue);
        final Pattern p = Pattern.compile(regex);
        if (!p.matcher(value).matches())
        {
            value = promptRegex(message, defaultValue, regex);
        }
        return value;
    }

    private String promptRegexAndMaxLength(String message, String defaultValue, String pattern, int maxLength) throws PrompterException
    {
        String value = defaultValue == null ? prompter.prompt(message) : prompter.prompt(message, defaultValue);
        final Pattern p = Pattern.compile(pattern);
        if (value.length() > maxLength || !p.matcher(value).matches())
        {
            value = promptRegexAndMaxLength(message, defaultValue, pattern, maxLength);
        }
        return value;
    }

    public static void main(String[] args)
    {
        String value = "saas@atlassian.com";
        final Pattern p = Pattern.compile(".+@atlassian\\.com$");
        System.out.println(p.matcher(value).matches());
    }

}
