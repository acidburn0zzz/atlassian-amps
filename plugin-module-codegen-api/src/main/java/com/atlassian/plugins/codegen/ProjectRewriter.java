package com.atlassian.plugins.codegen;

/**
 * Interface for an object that knows how to apply some subset of changes from a
 * {@link PluginProjectChangeset} to the project.
 */
public interface ProjectRewriter
{
    static final String DEFAULT_I18N_NAME = "atlassian-plugin";

    void applyChanges(PluginProjectChangeset changes) throws Exception;
}
