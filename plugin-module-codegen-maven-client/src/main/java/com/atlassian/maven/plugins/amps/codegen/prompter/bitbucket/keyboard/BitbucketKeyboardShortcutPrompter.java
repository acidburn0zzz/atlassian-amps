package com.atlassian.maven.plugins.amps.codegen.prompter.bitbucket.keyboard;

import com.atlassian.maven.plugins.amps.codegen.annotations.ModuleCreatorClass;
import com.atlassian.maven.plugins.amps.codegen.prompter.common.keyboard.AbstractKeyboardShortcutPrompter;
import com.atlassian.plugins.codegen.modules.bitbucket.keyboard.BitbucketKeyboardShortcutModuleCreator;
import com.atlassian.plugins.codegen.modules.bitbucket.keyboard.BitbucketKeyboardShortcutProperties;
import org.codehaus.plexus.components.interactivity.Prompter;

/**
 * @since 6.1.0
 */
@ModuleCreatorClass(BitbucketKeyboardShortcutModuleCreator.class)
public class BitbucketKeyboardShortcutPrompter extends AbstractKeyboardShortcutPrompter<BitbucketKeyboardShortcutProperties>
{

    public BitbucketKeyboardShortcutPrompter(Prompter prompter)
    {
        super(prompter);
    }

    @Override
    protected BitbucketKeyboardShortcutProperties createProperties(String moduleName)
    {
        return new BitbucketKeyboardShortcutProperties(moduleName);
    }

}
