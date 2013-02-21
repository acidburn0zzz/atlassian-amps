package com.atlassian.plugins.codegen.modules.confluence.keyboard;

import com.atlassian.plugins.codegen.modules.common.keyboard.AbstractKeyboardShortcutProperties;
import com.google.common.collect.Lists;

import java.util.List;

public class ConfluenceKeyboardShortcutProperties extends AbstractKeyboardShortcutProperties {

    private static final List<String> CONFLUENCE_CONTEXTS = Lists.newArrayList("viewcontent", "viewinfo");

    public ConfluenceKeyboardShortcutProperties(String moduleName) {
        super(moduleName);
    }

    @Override
    protected List<String> getAdditionalContexts() {
        return CONFLUENCE_CONTEXTS;
    }

}
