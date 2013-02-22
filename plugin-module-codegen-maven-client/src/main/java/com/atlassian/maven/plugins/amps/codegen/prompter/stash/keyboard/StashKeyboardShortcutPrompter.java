package com.atlassian.maven.plugins.amps.codegen.prompter.stash.keyboard;

import com.atlassian.maven.plugins.amps.codegen.annotations.ModuleCreatorClass;
import com.atlassian.maven.plugins.amps.codegen.prompter.common.keyboard.AbstractKeyboardShortcutPrompter;
import com.atlassian.plugins.codegen.modules.stash.keyboard.StashKeyboardShortcutModuleCreator;
import com.atlassian.plugins.codegen.modules.stash.keyboard.StashKeyboardShortcutProperties;
import org.codehaus.plexus.components.interactivity.Prompter;

@ModuleCreatorClass(StashKeyboardShortcutModuleCreator.class)
public class StashKeyboardShortcutPrompter extends AbstractKeyboardShortcutPrompter<StashKeyboardShortcutProperties>
{

    public StashKeyboardShortcutPrompter(Prompter prompter)
    {
        super(prompter);
    }

    @Override
    protected StashKeyboardShortcutProperties createProperties(String moduleName)
    {
        return new StashKeyboardShortcutProperties(moduleName);
    }

}
