package com.atlassian.plugins.codegen;

import com.atlassian.fugue.Option;

import static com.atlassian.fugue.Option.none;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes a &lt;component-import&gt; element that should be added to the plugin XML file.
 */
public final class ComponentImport
{
    private ClassId interfaceClass;
    private Option<String> key;
    private Option<String> filter;
    
    public static ComponentImport componentImport(ClassId interfaceClass)
    {
        return new ComponentImport(interfaceClass, none(String.class), none(String.class)); 
    }
    
    private ComponentImport(ClassId interfaceClass, Option<String> key, Option<String> filter)
    {
        this.interfaceClass = checkNotNull(interfaceClass, "interfaceClass");
        this.key = checkNotNull(key, "key");
        this.filter = checkNotNull(filter, "filter");
    }
    
    public ComponentImport key(Option<String> key)
    {
        return new ComponentImport(this.interfaceClass, key, this.filter);
    }

    public ComponentImport filter(Option<String> filter)
    {
        return new ComponentImport(this.interfaceClass, this.key, filter);
    }

    public ClassId getInterfaceClass()
    {
        return interfaceClass;
    }

    public Option<String> getKey()
    {
        return key;
    }
    
    public Option<String> getFilter()
    {
        return filter;
    }
}
