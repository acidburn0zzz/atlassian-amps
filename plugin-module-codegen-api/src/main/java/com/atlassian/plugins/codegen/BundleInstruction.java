package com.atlassian.plugins.codegen;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes a line item in a bundle instruction element, such as &lt;Import-Package&gt;,
 * that should be added to the POM.
 */
public final class BundleInstruction
{
    public enum Category
    {
        IMPORT("Import-Package"),
        PRIVATE("Private-Package"),
        DYNAMIC_IMPORT("DynamicImport-Package");
        
        private String elementName;
        
        private Category(String elementName)
        {
            this.elementName = elementName;
        }
        
        public String getElementName()
        {
            return elementName;
        }
    }
    
    private Category category;
    private String packageName;
    private String version;
    
    public BundleInstruction(Category category, String packageName, String version)
    {
        this.category = checkNotNull(category, "category");
        this.packageName = checkNotNull(packageName, "packageName");
        this.version = checkNotNull(version, "version");
    }
    
    public Category getCategory()
    {
        return category;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public String getVersion()
    {
        return version;
    }
}
