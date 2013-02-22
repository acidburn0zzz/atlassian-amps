package com.atlassian.plugins.codegen.modules.jira.keyboard;

import com.atlassian.plugins.codegen.modules.common.keyboard.AbstractKeyboardShortcutProperties;
import com.google.common.collect.Lists;

import java.util.List;

public class JiraKeyboardShortcutProperties extends AbstractKeyboardShortcutProperties
{

    private static final List<String> JIRA_CONTEXTS = Lists.newArrayList("issueaction", "issuenavigation");

    public JiraKeyboardShortcutProperties(String moduleName)
    {
        super(moduleName);
    }

    @Override
    protected List<String> getAdditionalContexts()
    {
        return JIRA_CONTEXTS;
    }

}
