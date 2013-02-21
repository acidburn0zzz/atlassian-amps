package com.atlassian.maven.plugins.amps.codegen.prompter.jira;

import com.atlassian.maven.plugins.amps.codegen.annotations.ModuleCreatorClass;
import com.atlassian.maven.plugins.amps.codegen.prompter.common.keyboard.AbstractKeyboardShortcutPrompter;
import com.atlassian.plugins.codegen.modules.jira.keyboard.JiraKeyboardShortcutModuleCreator;
import com.atlassian.plugins.codegen.modules.jira.keyboard.JiraKeyboardShortcutProperties;
import org.codehaus.plexus.components.interactivity.Prompter;

@ModuleCreatorClass(JiraKeyboardShortcutModuleCreator.class)
public class JiraKeyboardShortcutPrompter extends AbstractKeyboardShortcutPrompter<JiraKeyboardShortcutProperties> {

    public JiraKeyboardShortcutPrompter(Prompter prompter) {
        super(prompter);
    }

    @Override
    protected JiraKeyboardShortcutProperties createProperties(String moduleName) {
        return new JiraKeyboardShortcutProperties(moduleName);
    }

}
