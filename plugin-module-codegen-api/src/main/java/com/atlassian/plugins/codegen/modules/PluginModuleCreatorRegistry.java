package com.atlassian.plugins.codegen.modules;

import java.util.Map;

/**
 * @since 3.6
 */
public interface PluginModuleCreatorRegistry
{

    public static final String REFAPP = "refapp";
    public static final String CONFLUENCE = "confluence";
    public static final String JIRA = "jira";
    public static final String BAMBOO = "bamboo";
    public static final String FECRU = "fecru";
    public static final String CROWD = "crowd";
    public static final String STASH = "stash";

    void registerModuleCreator(String productId, PluginModuleCreator moduleCreator);

    <T extends PluginModuleCreator> T getModuleCreator(String productId, Class<T> type);

    Map<Class, PluginModuleCreator> getModuleCreatorsForProduct(String productId);
}
