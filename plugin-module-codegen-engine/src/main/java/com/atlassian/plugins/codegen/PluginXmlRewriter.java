package com.atlassian.plugins.codegen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import com.atlassian.plugins.codegen.modules.PluginModuleLocation;
import com.atlassian.plugins.codegen.util.PluginXmlHelper;

import com.google.common.collect.ImmutableList;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import static com.google.common.collect.Iterables.concat;

/**
 * Applies the subset of changes from a {@link PluginProjectChangeset} that affect the
 * {@code atlassian-plugin.xml} file.
 */
public class PluginXmlRewriter implements ProjectRewriter
{
    private final PluginXmlHelper xmlHelper;
    private final Document document;
    
    public PluginXmlRewriter(PluginModuleLocation location) throws IOException, DocumentException
    {
        this.xmlHelper = new PluginXmlHelper(location);
        this.document = xmlHelper.getDocument();
    }

    public PluginXmlRewriter(File pluginXmlFile) throws IOException, DocumentException
    {
        this.xmlHelper = new PluginXmlHelper(pluginXmlFile);
        this.document = xmlHelper.getDocument();
    }
    
    @Override
    public void applyChanges(PluginProjectChangeset changes) throws IOException
    {
        boolean modified = false;
        
        try
        {
            if (changes.hasItems(I18nString.class))
            {
                modified |= addI18nResource(xmlHelper.getDefaultI18nName(), xmlHelper.getDefaultI18nLocation());
            }
            
            for (PluginParameter pluginParam : changes.getItems(PluginParameter.class))
            {
                modified |= addPluginInfoParam(pluginParam.getName(), pluginParam.getValue());
            }
            
            for (ComponentImport componentImport : changes.getItems(ComponentImport.class))
            {
                modified |= addComponentImport(componentImport);
            }
            
            for (ComponentDeclaration component : changes.getItems(ComponentDeclaration.class))
            {
                modified |= addComponentDeclaration(component);
            }
            
            for (ModuleDescriptor moduleDescriptor : changes.getItems(ModuleDescriptor.class))
            {
                modified |= addModuleAsLastChild(moduleDescriptor.getContent());
            }
        }
        catch (DocumentException e)
        {
            throw new IOException(e);
        }

        if (modified)
        {
            OutputFormat format = OutputFormat.createPrettyPrint();
            FileOutputStream fos = new FileOutputStream(xmlHelper.getXmlFile());
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            XMLWriter writer = new XMLWriter(osw, format);
            try
            {
                writer.write(document);
            }
            finally
            {
                writer.close();
                IOUtils.closeQuietly(osw);
                IOUtils.closeQuietly(fos);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private Element createModule(String type)
    {
        Element newElement = document.getRootElement().addElement(type);
        List<Element> existingModules = (List<Element>) document.getRootElement().elements(type);
        if (!existingModules.isEmpty())
        {
            newElement.detach();
            existingModules.add(newElement);
        }
        return newElement;
    }
    
    private boolean addModuleAsLastChild(Element module)
    {
        String key = module.attributeValue("key");
        if ((key == null) || !PluginXmlHelper.findElementByTypeAndAttribute(document.getRootElement(), module.getName(), "key", key).isDefined())
        {
            Element pluginRoot = document.getRootElement();
            pluginRoot.add(module);
            return true;
        }
        return false;
    }

    private boolean addI18nResource(String name, String location) throws DocumentException, IOException
    {
        String xpath = "//resource[@type='i18n' and (@name = '" + name + "' or @location='" + location + "')]";
        Node resourceNode = document.selectSingleNode(xpath);

        if (resourceNode == null)
        {
            Element resource = document.getRootElement().addElement("resource");
            resource.addAttribute("type", "i18n");
            resource.addAttribute("name", name);
            resource.addAttribute("location", location);
            return true;
        }
        
        return false;
    }

    private boolean addPluginInfoParam(String name, String value)
    {
        Element pluginInfo = (Element) document.selectSingleNode("//plugin-info");
        if (pluginInfo == null)
        {
            pluginInfo = document.addElement("plugin-info");
        }
        if (!PluginXmlHelper.findElementByTypeAndAttribute(pluginInfo, "param", "name", name).isDefined())
        {
            pluginInfo.addElement("param").addAttribute("name", name).setText(value);
            return true;
        }
        return false;
    }
    
    private boolean addComponentImport(ComponentImport componentImport) throws DocumentException
    {
        String key = componentImport.getKey().getOrElse(createKeyFromClass(componentImport.getInterfaceClass()));
        for (ClassId interfaceId : concat(ImmutableList.of(componentImport.getInterfaceClass()), componentImport.getAlternateInterfaces()))
        {
            if ((document.getRootElement().selectNodes("//component-import[@interface='" + interfaceId.getFullName() + "']").size() > 0)
                || (document.getRootElement().selectNodes("//component-import/interface[text()='" + interfaceId.getFullName() + "']").size() > 0))
            {
                return false;
            }
        }
        if (PluginXmlHelper.findElementByTypeAndAttribute(document.getRootElement(), "component-import", "key", key).isDefined())
        {
            return false;
        }
        
        Element element = createModule("component-import");
        element.addAttribute("key", key);
        element.addAttribute("interface", componentImport.getInterfaceClass().getFullName());
        for (String filter : componentImport.getFilter())
        {
            element.addAttribute("filter", filter);
        }
        return true;
    }
    
    private boolean addComponentDeclaration(ComponentDeclaration component) throws DocumentException
    {
        if (!PluginXmlHelper.findElementByTypeAndAttribute(document.getRootElement(), "component", "key", component.getKey()).isDefined())
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
            for (String application : component.getApplication())
            {
                if(StringUtils.isNotBlank(application))
                {
                    element.addAttribute("application", application);
                }
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
            return true;
        }
        return false;
    }
    
    private String createKeyFromClass(ClassId classId)
    {
        return lowercaseFirst(classId.getName());
    }
    
    private String lowercaseFirst(String input)
    {
        return input.equals("") ? input : (input.substring(0, 1).toLowerCase() + input.substring(1));
    }   
}
