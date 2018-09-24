package com.atlassian.plugins.codegen;

import com.atlassian.fugue.Option;

import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Option.some;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Simple wrapper for an artifact version string.  This can also be a Maven property name, in which
 * case the version string is used as the default value for the property.
 */
public final class VersionId
{
    private final Option<String> version;
    private final Option<String> propertyName;
    
    public static VersionId version(String version)
    {
        return new VersionId(some(version), none(String.class)); 
    }
    
    public static VersionId versionProperty(String propertyName, String defaultVersion)
    {
        return new VersionId(some(defaultVersion), some(propertyName));
    }

    public static VersionId noVersion()
    {
        return new VersionId(none(String.class), none(String.class));
    }
    
    private VersionId(Option<String> version, Option<String> propertyName)
    {
        this.version = checkNotNull(version, "version");
        this.propertyName = checkNotNull(propertyName, "propertyName");
    }
    
    public Option<String> getVersion()
    {
        return version;
    }
    
    public Option<String> getPropertyName()
    {
        return propertyName;
    }

    public boolean isDefined()
    {
        return version.isDefined() || propertyName.isDefined();
    }
    
    /**
     * Returns the version string, unless a property name was specified, in which case it returns
     * "${propertyName}".  Returns none() if neither was specified.
     */
    public Option<String> getVersionOrPropertyPlaceholder()
    {
        for (String p : propertyName)
        {
            return some(placeholder(p));
        }
        return version;
    }
    
    @Override
    public String toString()
    {
        for (String p : propertyName)
        {
            for (String v : version)
            {
                return placeholder(p) + " (" + v + ")";
            }
            return placeholder(p);
        }
        for (String v : version)
        {
            return v;
        }
        return "?";
    }
    
    private String placeholder(String p)
    {
        return "${" + p + "}";
    }
    
    @Override
    public boolean equals(Object other)
    {
        if (other instanceof VersionId)
        {
            VersionId v = (VersionId) other;
            return version.equals(v.version) && propertyName.equals(v.propertyName);
        }
        return false;
    }
    
    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }
}
