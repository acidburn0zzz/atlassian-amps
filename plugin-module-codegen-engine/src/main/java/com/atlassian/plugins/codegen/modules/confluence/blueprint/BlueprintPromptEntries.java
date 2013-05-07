package com.atlassian.plugins.codegen.modules.confluence.blueprint;

import java.util.HashMap;

import static com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintPromptEntry.*;

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

        // Adding these false values avoids a lot of null-checks later on.
        put(SKIP_PAGE_EDITOR_PROMPT, false);
        put(HOW_TO_USE_PROMPT, false);
        put(DIALOG_WIZARD_PROMPT, false);
        put(CONTEXT_PROVIDER_PROMPT, false);
        put(EVENT_LISTENER_PROMPT, false);
        put(INDEX_PAGE_TEMPLATE_PROMPT, false);
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
