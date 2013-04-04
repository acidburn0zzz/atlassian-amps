package com.atlassian.plugins.codegen.modules.common;

/**
 * @since 4.1.7
 */
public class ContextProvider
{
    private final String className;

    public ContextProvider(String className)
    {
        this.className = className;
    }

    public String getClassName()
    {
        return className;
    }
}
