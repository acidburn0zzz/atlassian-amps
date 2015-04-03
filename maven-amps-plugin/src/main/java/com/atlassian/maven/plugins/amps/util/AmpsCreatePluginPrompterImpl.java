package com.atlassian.maven.plugins.amps.util;

import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

/**
 * @since version
 */
public class AmpsCreatePluginPrompterImpl implements AmpsCreatePluginPrompter
{
    public static final String DEFAULT_VERSION = "1.0.0-SNAPSHOT";
    private Prompter prompter;
    
    @Override
    public CreatePluginProperties prompt() throws PrompterException
    {
        CreatePluginProperties props = null;
        
        String groupId = prompter.prompt("Define value for groupId: ");
        String artifactId = prompter.prompt("Define value for artifactId: ");
        String version = prompter.prompt("Define value for version: ", DEFAULT_VERSION);
        String thePackage = prompter.prompt("Define value for package: ",groupId);

        StringBuilder query = new StringBuilder( "Confirm properties configuration:\n" );
        query.append("groupId: ").append(groupId).append("\n")
                .append("artifactId: ").append(artifactId).append("\n")
                .append("version: ").append(version).append("\n")
                .append("package: ").append(thePackage).append("\n");
        
        String confirmed = prompter.prompt(query.toString(),"Y");
        if("Y".equalsIgnoreCase(confirmed))
        {
            props = new CreatePluginProperties(groupId,artifactId,version,thePackage);
        }
        
        return props;
    }

    
}
