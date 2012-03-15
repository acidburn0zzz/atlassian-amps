package com.atlassian.plugins.codegen;

import com.atlassian.fugue.Option;

import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Option.some;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes a line item in a bundle instruction element, such as &lt;Import-Package&gt;,
 * that should be added to the POM.
 */
public final class BundleInstruction implements PluginProjectChange
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
    private Option<String> version;
    
    public static BundleInstruction importPackage(String packageName, String version)
    {
        return new BundleInstruction(Category.IMPORT, packageName, some(version));
    }
    
    public static BundleInstruction dynamicImportPackage(String packageName, String version)
    {
        return new BundleInstruction(Category.DYNAMIC_IMPORT, packageName, some(version));
    }
    
    public static BundleInstruction privatePackage(String packageName)
    {
        return new BundleInstruction(Category.PRIVATE, packageName, none(String.class));
    }
    
    public BundleInstruction(Category category, String packageName, Option<String> version)
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

    public Option<String> getVersion()
    {
        return version;
    }
    
    @Override
    public String toString()
    {
        return "[" + category.getElementName() + ": " + packageName
            + (version.isDefined() ? (" " + version.get()) : "") + "]";
    }
}
