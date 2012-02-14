package com.atlassian.plugins.codegen;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Simple wrapper for a fully qualified class name.
 */
public final class ClassId
{
    private final String packageName;
    private final String className;
    
    public static ClassId packageAndClass(String packageName, String className)
    {
        return new ClassId(packageName, className); 
    }
    
    public static ClassId fullyQualified(String fullyQualifiedClassName)
    {
        checkNotNull(fullyQualifiedClassName);
        int lastDotPos = fullyQualifiedClassName.lastIndexOf(".");
        if (lastDotPos > 0)
        {
            return new ClassId(fullyQualifiedClassName.substring(0, lastDotPos),
                               fullyQualifiedClassName.substring(lastDotPos + 1));
        }
        else
        {
            return new ClassId("", fullyQualifiedClassName);
        }
    }
    
    private ClassId(String packageName, String className)
    {
        this.packageName = checkNotNull(packageName, "packageName");
        this.className = checkNotNull(className, "className");
    }
    
    /**
     * Returns a copy of this instance with the package name changed and the class name unchanged.
     */
    public ClassId packageName(String packageName)
    {
        return new ClassId(packageName, this.className);
    }
    
    /**
     * Returns a copy of this instance with a prefix added to the package name.
     */
    public ClassId packageNamePrefix(String prefix)
    {
        return new ClassId(packageName.equals("") ? prefix : (prefix + "." + packageName), className);
    }
    
    /**
     * Returns a copy of this instance with the class name changed and the package name unchanged.
     */
    public ClassId className(String className)
    {
        return new ClassId(this.packageName, className);
    }

    /**
     * Returns a copy of this instance with a suffix added to the class name.
     */
    public ClassId classNameSuffix(String suffix)
    {
        return new ClassId(this.packageName, this.className + suffix);
    }
    
    public String getPackage()
    {
        return packageName;
    }
    
    public String getName()
    {
        return className;
    }

    public String getFullName()
    {
        return packageName.equals("") ? className : (packageName + "." + className);
    }
    
    @Override
    public String toString()
    {
        return getFullName();
    }
    
    @Override
    public boolean equals(Object other)
    {
        if (other instanceof ClassId)
        {
            ClassId c = (ClassId) other;
            return className.equals(c.className) && packageName.equals(c.packageName);
        }
        return false;
    }
    
    @Override
    public int hashCode()
    {
        return getFullName().hashCode();
    }
}
