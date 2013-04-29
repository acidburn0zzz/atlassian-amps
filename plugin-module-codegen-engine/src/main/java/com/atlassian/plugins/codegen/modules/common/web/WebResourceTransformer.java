package com.atlassian.plugins.codegen.modules.common.web;

import com.atlassian.plugins.codegen.modules.BasicClassModuleProperties;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @since 3.6
 */
public class WebResourceTransformer extends BasicClassModuleProperties
{
    private List<String> functionsList;

    public WebResourceTransformer()
    {
        this("My Web Resource Transformer");
        functionsList = newArrayList();
    }

    public WebResourceTransformer(String fqClassName)
    {
        super(fqClassName);
    }

    public void addFunctions(String functions)
    {
        functionsList.add(functions);
    }

    public List<String> getFunctions()
    {
        return Collections.unmodifiableList(functionsList);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        WebResourceTransformer that = (WebResourceTransformer) o;

        if (!functionsList.equals(that.functionsList)) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + functionsList.hashCode();
        return result;
    }
}
