package com.atlassian.plugins.codegen.modules.jira;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.JiraPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;

/**
 * @since 3.6
 */
@JiraPluginModuleCreator
public class KeyboardShortcutModuleCreator extends AbstractPluginModuleCreator<KeyboardShortcutProperties>
{
    public static final String MODULE_NAME = "Keyboard Shortcut";
    private static final String TEMPLATE_PREFIX = "templates/jira/keyboard/";

    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "keyboard-shortcut-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(KeyboardShortcutProperties props) throws Exception
    {
        return new PluginProjectChangeset()
            .withDependencies(MOCKITO_TEST)
            .with(createModule(props, PLUGIN_MODULE_TEMPLATE));
    }

    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
