package com.atlassian.maven.plugins.amps.util;

import org.codehaus.plexus.components.interactivity.PrompterException;

/**
 * @since version
 */
public interface AmpsCreatePluginPrompter
{
    public CreatePluginProperties prompt() throws PrompterException;
}
