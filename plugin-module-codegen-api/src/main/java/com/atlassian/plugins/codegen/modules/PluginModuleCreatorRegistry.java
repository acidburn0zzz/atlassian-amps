package com.atlassian.plugins.codegen.modules;

import java.util.Map;

/**
 * @since 3.6
 */
public interface PluginModuleCreatorRegistry
{

    String REFAPP = "refapp";
    String CONFLUENCE = "confluence";
    String JIRA = "jira";
    String BAMBOO = "bamboo";
    /**
     * @since 6.1.0
     */
    String BITBUCKET = "bitbucket";
    String FECRU = "fecru";
    String CROWD = "crowd";

    void registerModuleCreator(String productId, PluginModuleCreator moduleCreator);

    <T extends PluginModuleCreator> T getModuleCreator(String productId, Class<T> type);

    Map<Class, PluginModuleCreator> getModuleCreatorsForProduct(String productId);
}
