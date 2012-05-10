package com.atlassian.plugins.codegen;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.atlassian.plugins.codegen.modules.PluginModuleCreator;
import com.atlassian.plugins.codegen.modules.PluginModuleCreatorRegistry;

/**
 * @since 3.6
 */
public class PluginModuleCreatorRegistryImpl implements PluginModuleCreatorRegistry
{

    private final Map<String, SortedMap<Class, PluginModuleCreator>> creatorRegistry;

    public PluginModuleCreatorRegistryImpl()
    {
        ModuleNameComparator comparator = new ModuleNameComparator();
        this.creatorRegistry = new HashMap<String, SortedMap<Class, PluginModuleCreator>>();
        creatorRegistry.put(PluginModuleCreatorRegistry.JIRA, new TreeMap<Class, PluginModuleCreator>(comparator));
        creatorRegistry.put(PluginModuleCreatorRegistry.BAMBOO, new TreeMap<Class, PluginModuleCreator>(comparator));
        creatorRegistry.put(PluginModuleCreatorRegistry.CONFLUENCE, new TreeMap<Class, PluginModuleCreator>(comparator));
        creatorRegistry.put(PluginModuleCreatorRegistry.CROWD, new TreeMap<Class, PluginModuleCreator>(comparator));
        creatorRegistry.put(PluginModuleCreatorRegistry.FECRU, new TreeMap<Class, PluginModuleCreator>(comparator));
        creatorRegistry.put(PluginModuleCreatorRegistry.STASH, new TreeMap<Class, PluginModuleCreator>(comparator));
        creatorRegistry.put(PluginModuleCreatorRegistry.REFAPP, new TreeMap<Class, PluginModuleCreator>(comparator));
    }

    @Override
    public void registerModuleCreator(String productId, PluginModuleCreator moduleCreator)
    {
        Map<Class, PluginModuleCreator> moduleMap = creatorRegistry.get(productId);
        if (null != moduleMap)
        {
            moduleMap.put(moduleCreator.getClass(), moduleCreator);
        }
    }

    @Override
    public <T extends PluginModuleCreator> T getModuleCreator(String productId, Class<T> creatorClass)
    {
        Map<Class, PluginModuleCreator> moduleMap = creatorRegistry.get(productId);
        T creator = null;
        if (null != moduleMap)
        {
            creator = creatorClass.cast(moduleMap.get(creatorClass));
        }

        return creator;
    }

    @Override
    public Map<Class, PluginModuleCreator> getModuleCreatorsForProduct(String productId)
    {
        SortedMap<Class, PluginModuleCreator> moduleMap = creatorRegistry.get(productId);
        if (null != moduleMap)
        {
            moduleMap = Collections.unmodifiableSortedMap(moduleMap);
        }

        return moduleMap;
    }

    private class ModuleNameComparator implements Comparator<Class>
    {
        @Override
        public int compare(Class class1, Class class2)
        {
            return class1.getSimpleName()
                    .compareTo(class2.getSimpleName());
        }
    }
}
