package com.atlassian.plugins.codegen;

import static com.google.common.base.Preconditions.checkNotNull;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Describes a plugin module element that should be added to the plugin XML file.
 * This is provided as an arbitrary XML fragment string.
 */
public class ModuleDescriptor implements PluginProjectChange
{
    private final Element content;
    
    public static ModuleDescriptor moduleDescriptor(String content)
    {
        return new ModuleDescriptor(parseXml(content));
    }
    
    public static ModuleDescriptor moduleDescriptor(Element content)
    {
        return new ModuleDescriptor(content);
    }
    
    private ModuleDescriptor(Element content)
    {
        this.content = checkNotNull(content, "content");
    }

    public String getType()
    {
        return content.getName();
    }
    
    public Element getContent()
    {
        return content;
    }
    
    @Override
    public String toString()
    {
        return "[module: " + getType() + "]";
    }
    
    private static Element parseXml(String content)
    {
        try 
        {
            Document doc = DocumentHelper.parseText(content);
            Element root = doc.getRootElement();
            root.detach();
            return root;
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Invalid XML content for module descriptor", e);
        }
    }
}
