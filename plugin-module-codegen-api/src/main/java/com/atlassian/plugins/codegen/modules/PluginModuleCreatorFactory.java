package com.atlassian.plugins.codegen.modules;

import java.util.Map;

/**
 * @since 3.6
 */
public interface PluginModuleCreatorFactory
{
    PluginModuleCreator getModuleCreator(String productId, Class creatorClass);

    Map<Class, PluginModuleCreator> getModuleCreatorsForProduct(String productId);
}
