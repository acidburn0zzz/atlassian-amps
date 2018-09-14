package com.atlassian.plugins.codegen;

import io.atlassian.fugue.Option;

import com.google.common.collect.ImmutableList;

import static com.atlassian.plugins.codegen.ClassId.fullyQualified;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.atlassian.fugue.Option.none;

/**
 * Describes a &lt;component-import&gt; element that should be added to the plugin XML file.
 */
public final class ComponentImport implements PluginProjectChange
{
    private final ClassId interfaceClass;
    private final Option<String> key;
    private final Option<String> filter;
    private final ImmutableList<ClassId> alternateInterfaces;
    
    public static ComponentImport componentImport(ClassId interfaceClass)
    {
        return new ComponentImport(interfaceClass, none(String.class), none(String.class), ImmutableList.<ClassId>of()); 
    }

    public static ComponentImport componentImport(String fullyQualifiedInterfaceName)
    {
        return new ComponentImport(fullyQualified(fullyQualifiedInterfaceName), none(String.class), none(String.class), ImmutableList.<ClassId>of()); 
    }
    
    private ComponentImport(ClassId interfaceClass, Option<String> key, Option<String> filter, ImmutableList<ClassId> alternateInterfaces)
    {
        this.interfaceClass = checkNotNull(interfaceClass, "interfaceClass");
        this.key = checkNotNull(key, "key");
        this.filter = checkNotNull(filter, "filter");
        this.alternateInterfaces = checkNotNull(alternateInterfaces, "alternateInterfaces");
    }
    
    public ComponentImport key(Option<String> key)
    {
        return new ComponentImport(this.interfaceClass, key, this.filter, this.alternateInterfaces);
    }

    public ComponentImport filter(Option<String> filter)
    {
        return new ComponentImport(this.interfaceClass, this.key, filter, this.alternateInterfaces);
    }

    public ComponentImport alternateInterfaces(ClassId... interfaceClasses)
    {
        return new ComponentImport(this.interfaceClass, this.key, this.filter, ImmutableList.copyOf(interfaceClasses));
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
    
    public Iterable<ClassId> getAlternateInterfaces()
    {
        return alternateInterfaces;
    }

    @Override
    public String toString()
    {
        return "[component-import: " + interfaceClass + "]";
    }
}
