package com.atlassian.plugins.codegen;

/**
 * Interface for {@link PluginProjectChange} objects that should not be logged individually.
 */
public interface SummarizeAsGroup
{
    String getGroupName();
}
