package com.atlassian.plugins.codegen;

/**
 * Interface for an object that knows how to apply some subset of changes from a
 * {@link PluginProjectChangeset} to the project.
 */
public interface ProjectRewriter
{
    void applyChanges(PluginProjectChangeset changes) throws Exception;
}
