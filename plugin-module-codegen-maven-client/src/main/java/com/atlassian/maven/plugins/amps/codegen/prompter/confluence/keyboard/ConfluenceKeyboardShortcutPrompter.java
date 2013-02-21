package com.atlassian.maven.plugins.amps.codegen.prompter.confluence.keyboard;

import com.atlassian.maven.plugins.amps.codegen.annotations.ModuleCreatorClass;
import com.atlassian.maven.plugins.amps.codegen.prompter.common.keyboard.AbstractKeyboardShortcutPrompter;
import com.atlassian.plugins.codegen.modules.confluence.keyboard.ConfluenceKeyboardShortcutModuleCreator;
import com.atlassian.plugins.codegen.modules.confluence.keyboard.ConfluenceKeyboardShortcutProperties;
import org.codehaus.plexus.components.interactivity.Prompter;

@ModuleCreatorClass(ConfluenceKeyboardShortcutModuleCreator.class)
public class ConfluenceKeyboardShortcutPrompter extends AbstractKeyboardShortcutPrompter<ConfluenceKeyboardShortcutProperties> {

    public ConfluenceKeyboardShortcutPrompter(Prompter prompter) {
        super(prompter);
    }

    @Override
    protected ConfluenceKeyboardShortcutProperties createProperties(String moduleName) {
        return new ConfluenceKeyboardShortcutProperties(moduleName);
    }

}
