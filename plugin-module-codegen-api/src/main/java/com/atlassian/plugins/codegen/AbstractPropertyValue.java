package com.atlassian.plugins.codegen;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base class for any change item that is a key-value pair.
 */
public abstract class AbstractPropertyValue
{
    private final String name;
    private final String value;
    
    protected AbstractPropertyValue(String name, String value)
    {
        this.name = checkNotNull(name, "name");
        this.value = checkNotNull(value, "value");
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getValue()
    {
        return value;
    }
    
    @Override
    public String toString()
    {
        return name + "=" + value;
    }
    
    @Override
    public boolean equals(Object other)
    {
        if (other.getClass() == this.getClass())
        {
            AbstractPropertyValue p = (AbstractPropertyValue) other;
            return name.equals(p.name) && value.equals(p.value);
        }
        return false;
    }
    
    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }
}
