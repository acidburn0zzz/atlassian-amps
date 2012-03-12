package com.atlassian.plugins.codegen.modules;

import com.atlassian.plugins.codegen.PluginProjectChangeset;

/**
 * Implementors of this class do the actual code generation for a specific plugin module.
 */
public interface PluginModuleCreator<T extends PluginModuleProperties>
{
    String getModuleName();

    /**
     * Returns a {@link PluginProjectChangeset} that describes (but does not perform) all of
     * the changes required to add this module.
     */
    PluginProjectChangeset createModule(T props) throws Exception;
}
