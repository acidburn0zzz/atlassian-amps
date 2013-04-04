package com.atlassian.plugins.codegen.modules.confluence.blueprint;

import com.atlassian.plugins.codegen.modules.AbstractNameBasedModuleProperties;
import com.atlassian.plugins.codegen.modules.common.ContextProvider;
import com.atlassian.plugins.codegen.modules.common.Resource;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Holds properties for the <content-template> module descriptor used in Blueprint creation.
 *
 * @since 4.1.7
 */
public class ContentTemplateProperties extends AbstractNameBasedModuleProperties
{
    public static final String LOCATION = "LOCATION";
    public static final String RESOURCES = "RESOURCES";
    public static final String CONTEXT_PROVIDER = "CONTEXT_PROVIDER";

    public ContentTemplateProperties(String moduleKey)
    {
        super();
        setModuleKey(moduleKey);
    }

    public void addResource(Resource resource)
    {
        List<Resource> resources = getResources();
        if (resources == null)
        {
            resources = Lists.newArrayList();
            put(RESOURCES, resources);
        }
        resources.add(resource);
    }

    public void setResources(List<Resource> resources)
    {
        put(RESOURCES, resources);
    }

    public List<Resource> getResources()
    {
        return (List<Resource>) get(RESOURCES);
    }

    public void setContextProvider(ContextProvider provider)
    {
        put(CONTEXT_PROVIDER, provider);
    }
}
