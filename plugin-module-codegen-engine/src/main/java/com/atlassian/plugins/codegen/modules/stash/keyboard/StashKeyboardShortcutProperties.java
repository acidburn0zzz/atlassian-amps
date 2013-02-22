package com.atlassian.plugins.codegen.modules.stash.keyboard;

import com.atlassian.plugins.codegen.modules.common.keyboard.AbstractKeyboardShortcutProperties;
import com.google.common.collect.Lists;

import java.util.List;

public class StashKeyboardShortcutProperties extends AbstractKeyboardShortcutProperties
{

    private static final List<String> STASH_CONTEXTS = Lists.newArrayList("branch", "changeset", "commits",
            "filebrowser", "sourceview");

    public StashKeyboardShortcutProperties(String moduleName)
    {
        super(moduleName);
    }

    @Override
    protected List<String> getAdditionalContexts()
    {
        return STASH_CONTEXTS;
    }

}
