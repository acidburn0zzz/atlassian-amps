package com.atlassian.plugins.codegen.modules.common.keyboard;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;

/**
 * @since 3.6
 */
public abstract class AbstractKeyboardShortcutModuleCreator<T extends AbstractKeyboardShortcutProperties> extends AbstractPluginModuleCreator<T>
{
    public static final String MODULE_NAME = "Keyboard Shortcut";
    private static final String TEMPLATE_PREFIX = "templates/common/keyboard/";

    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "keyboard-shortcut-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(AbstractKeyboardShortcutProperties props) throws Exception
    {
        return new PluginProjectChangeset()
            .with(MOCKITO_TEST)
            .with(createModule(props, PLUGIN_MODULE_TEMPLATE));
    }

    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
