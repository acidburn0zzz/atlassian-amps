package com.atlassian.plugins.codegen.modules.common;

import com.atlassian.plugins.codegen.modules.PluginModuleProperties;

import java.util.List;

/**
 * For {@link PluginModuleProperties} that contain an ordered collection of {@link Resource}s.
 *
 * @since 4.1.7
 */
public interface ResourcedProperties extends PluginModuleProperties
{
    List<Resource> getResources();
    void setResources(List<Resource> resources);
    void addResource(Resource resource);
}
