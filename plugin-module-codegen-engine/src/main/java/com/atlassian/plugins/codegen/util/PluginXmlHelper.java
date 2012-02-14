package com.atlassian.plugins.codegen.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import com.atlassian.plugins.codegen.ClassId;
import com.atlassian.plugins.codegen.ComponentDeclaration;
import com.atlassian.plugins.codegen.ComponentImport;

import org.apache.commons.lang.Validate;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import static org.apache.commons.io.IOUtils.closeQuietly;

/**
 *
 */
public class PluginXmlHelper
{
    private final Document document;

    public PluginXmlHelper(File pluginXml) throws DocumentException, IOException
    {
        this(new FileInputStream(pluginXml));
    }

    public PluginXmlHelper(InputStream input) throws DocumentException, IOException
    {
        Validate.notNull(input);

        this.document = createDocument(input);
    }
    
    public void addModuleAsLastChild(String fragment) throws DocumentException
    {
        Document fragDoc = DocumentHelper.parseText(fragment);
        Element pluginRoot = document.getRootElement();
        pluginRoot.add(fragDoc.getRootElement());
    }

    public void addI18nResource(String name) throws DocumentException, IOException
    {
        String xpath = "//resource[@type='i18n' and @location='" + name + "']";
        Node resourceNode = document.selectSingleNode(xpath);

        if (null == resourceNode)
        {
            Element pluginRoot = document.getRootElement();
            Document fragDoc = DocumentHelper.parseText("<resource type=\"i18n\" name=\"i18n\" location=\"" + name + "\" />");
            pluginRoot.add(fragDoc.getRootElement());
        }
    }

    public void addPluginInfoParam(String name, String value)
    {
        Element pluginInfo = (Element) document.selectSingleNode("//plugin-info");
        if (pluginInfo == null)
        {
            pluginInfo = document.addElement("plugin-info");
        }
        pluginInfo.addElement("param").addAttribute("name", name).setText(value);
    }
    
    public void addComponentImport(ComponentImport componentImport) throws DocumentException
    {
        String key = componentImport.getKey().getOrElse(createKeyFromClass(componentImport.getInterfaceClass()));
        Element element = createModule("component-import");
        element.addAttribute("key", key);
        element.addAttribute("interface", componentImport.getInterfaceClass().getFullName());
        for (String filter : componentImport.getFilter())
        {
            element.addAttribute("filter", filter);
        }
    }
    
    public void addComponentDeclaration(ComponentDeclaration component) throws DocumentException
    {
        Element element = createModule("component");
        element.addAttribute("key", component.getKey());
        element.addAttribute("class", component.getClassId().getFullName());
        for (String name : component.getName())
        {
            element.addAttribute("name", name);
        }
        for (String nameI18nKey : component.getNameI18nKey())
        {
            element.addAttribute("i18n-name-key", nameI18nKey);
        }
        if (component.getVisibility() == ComponentDeclaration.Visibility.PUBLIC)
        {
            element.addAttribute("public", "true");
        }
        for (String alias : component.getAlias())
        {
            element.addAttribute("alias", alias);
        }
        for (String description : component.getDescription())
        {
            Element eDesc = element.addElement("description");
            eDesc.setText(description);
            for (String descI18nKey : component.getDescriptionI18nKey())
            {
                eDesc.addAttribute("key", descI18nKey);
            }
        }
        for (ClassId interfaceId : component.getInterfaceId())
        {
            element.addElement("interface").setText(interfaceId.getFullName());
        }
        if (!component.getServiceProperties().isEmpty())
        {
            Element eProps = element.addElement("service-properties");
            for (Map.Entry<String, String> entry : component.getServiceProperties().entrySet())
            {
                Element eEntry = eProps.addElement("entry");
                eEntry.addAttribute("key", entry.getKey());
                eEntry.addAttribute("value", entry.getValue());
            }
        }
    }
    
    protected String createKeyFromClass(ClassId classId)
    {
        return lowercaseFirst(classId.getName());
    }
    
    protected String lowercaseFirst(String input)
    {
        return input.equals("") ? input : (input.substring(0, 1).toLowerCase() + input.substring(1));
    }
    
    public String getPluginXmlAsString()
    {
        return document.asXML();
    }

    protected Document getDocument()
    {
        return document;
    }

    protected Document createDocument(final InputStream source) throws DocumentException, IOException
    {
        final SAXReader reader = new SAXReader();
        reader.setMergeAdjacentText(true);
        reader.setStripWhitespaceText(false);
        return reader.read(source);
    }
    
    protected Element createModule(String type)
    {
        Element newElement = document.getRootElement().addElement(type);
        List existingModules = document.getRootElement().elements(type);
        if (!existingModules.isEmpty())
        {
            newElement.detach();
            existingModules.add(newElement);
        }
        return newElement;
    }
    
    public void savePluginXml(File file) throws IOException
    {
        FileOutputStream out = new FileOutputStream(file);
        try
        {
            savePluginXml(out);
        }
        finally
        {
            closeQuietly(out);
        }
    }
    
    public void savePluginXml(OutputStream out) throws IOException
    {
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(new OutputStreamWriter(out), format);
        try
        {
            writer.write(document);
        }
        finally
        {
            writer.close();
        }
    }
}
