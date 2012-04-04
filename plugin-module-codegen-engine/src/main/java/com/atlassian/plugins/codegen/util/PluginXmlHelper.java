package com.atlassian.plugins.codegen.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import com.atlassian.fugue.Option;
import com.atlassian.plugins.codegen.modules.PluginModuleLocation;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Option.some;

/**
 * Provides useful read-only operations on atlassian-plugin.xml.
 */
public class PluginXmlHelper
{
    private final File xmlFile;
    private final Document document;
    private final PluginModuleLocation location;

    public PluginXmlHelper(PluginModuleLocation location) throws IOException, DocumentException
    {
        this(location, "atlassian-plugin.xml");
    }
    
    public PluginXmlHelper(PluginModuleLocation location, String fileName) throws IOException, DocumentException
    {
        this.location = location;
        this.xmlFile = new File(location.getResourcesDir(), fileName);

        final SAXReader reader = new SAXReader();
        reader.setMergeAdjacentText(true);
        reader.setStripWhitespaceText(false);
        this.document = reader.read(new FileInputStream(xmlFile));
    }
    
    public Document getDocument()
    {
        return document;
    }
    
    public File getXmlFile()
    {
        return xmlFile;
    }
    
    public String getPluginKey()
    {
        String key = document.getRootElement().attributeValue("key");
        if (key == null)
        {
            throw new IllegalStateException("atlassian-plugin element does not have required attribute: key");
        }
        return key.replace("${project.groupId}", location.getGroupId()).replace("${project.artifactId}", location.getArtifactId());
    }
    
    public String getDefaultI18nName()
    {
        return "i18n";
    }
    
    @SuppressWarnings("unchecked")
    public String getDefaultI18nLocation()
    {
        List<Element> i18nElements = (List<Element>) document.selectNodes("//resource[@type='i18n']");
        if (!i18nElements.isEmpty())
        {
            return i18nElements.get(0).attributeValue("location");
        }
        return getPluginKey();
    }
    
    @SuppressWarnings("unchecked")
    public static Option<Element> findElementByTypeAndAttribute(Element parent, String type, String attributeName, String attributeValue)
    {
        for (Element e : (List<Element>) parent.elements(type))
        {
            if (attributeValue.equals(e.attributeValue(attributeName)))
            {
                return some(e);
            }
        }
        return none();
    }
}
