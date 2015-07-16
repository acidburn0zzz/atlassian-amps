package com.atlassian.plugins.codegen.modules.bitbucket.keyboard;

import com.atlassian.plugins.codegen.modules.common.keyboard.AbstractKeyboardShortcutProperties;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @since 6.1.0
 */
public class BitbucketKeyboardShortcutProperties extends AbstractKeyboardShortcutProperties
{

    private static final List<String> BITBUCKET_CONTEXTS = ImmutableList.of(
            "branch", "commit", "commits", "filebrowser", "sourceview");

    public BitbucketKeyboardShortcutProperties(String moduleName)
    {
        super(moduleName);
    }

    @Override
    protected List<String> getAdditionalContexts()
    {
        return BITBUCKET_CONTEXTS;
    }

}
