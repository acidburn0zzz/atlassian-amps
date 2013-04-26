package com.atlassian.plugins.codegen.modules.confluence.blueprint;

import java.util.HashMap;

/**
 * Contains all of the data from the Prompter phase using for creation of Confluence Blueprint modules.
 *
 * @since 1.6
 */
public class BlueprintPromptEntries extends HashMap<BlueprintPromptEntry, Object>
{
    private final String pluginKey;
    private final String defaultBasePackage;

    public BlueprintPromptEntries(String pluginKey, String defaultBasePackage)
    {
        this.pluginKey = pluginKey;
        this.defaultBasePackage = defaultBasePackage;
    }

    public BlueprintPromptEntries(String pluginKey)
    {
        this(pluginKey, pluginKey);
    }

    public String getDefaultBasePackage()
    {
        return defaultBasePackage;
    }

    public String getPluginKey()
    {
        return pluginKey;
    }
}
