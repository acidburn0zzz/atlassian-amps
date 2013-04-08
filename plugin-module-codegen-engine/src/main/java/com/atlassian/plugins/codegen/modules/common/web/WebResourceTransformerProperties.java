package com.atlassian.plugins.codegen.modules.common.web;

import com.atlassian.plugins.codegen.modules.BasicClassModuleProperties;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @since 3.6
 */
public class WebResourceTransformerProperties extends BasicClassModuleProperties
{
    private List<String> functionsList;

    public WebResourceTransformerProperties()
    {
        this("My Web Resource Transformer");
        functionsList = newArrayList();
    }

    public WebResourceTransformerProperties(String fqClassName)
    {
        super(fqClassName);
    }

    public void addFunctions(String functions)
    {
        functionsList.add(functions);
    }

    public List<String> getFunctionsList()
    {
        return Collections.unmodifiableList(functionsList);
    }
}
