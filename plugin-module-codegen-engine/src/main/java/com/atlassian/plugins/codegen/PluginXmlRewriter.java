package com.atlassian.plugins.codegen;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import com.atlassian.plugins.codegen.modules.PluginModuleLocation;
import com.atlassian.plugins.codegen.util.PluginXmlHelper;

import org.apache.commons.io.FileUtils;
import org.dom4j.DocumentException;

/**
 * Applies the subset of changes from a {@link PluginProjectChangeset} that affect the
 * {@code atlassian-plugin.xml} file.
 */
public class PluginXmlRewriter implements ProjectRewriter
{
    private final File xmlFile;
    
    public PluginXmlRewriter(File xmlFile)
    {
        this.xmlFile = xmlFile;
    }
    
    public PluginXmlRewriter(PluginModuleLocation location)
    {
        this(new File(location.getResourcesDir(), "atlassian-plugin.xml"));
    }
    
    @Override
    public void applyChanges(PluginProjectChangeset changes) throws IOException
    {
        FileInputStream in = new FileInputStream(xmlFile);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        applyPluginXmlChanges(in, changes, out);
        in.close();
        out.close();
        FileUtils.writeByteArrayToFile(xmlFile, out.toByteArray());
    }
    
    public void applyPluginXmlChanges(InputStream in, PluginProjectChangeset changes, OutputStream out) throws IOException
    {
        try
            {
            PluginXmlHelper pluginXmlHelper = new PluginXmlHelper(in);
        
            if (!changes.getI18nProperties().isEmpty())
            {
                pluginXmlHelper.addI18nResource(DEFAULT_I18N_NAME);
            }
            
            for (Map.Entry<String, String> pluginParam : changes.getPluginParameters().entrySet())
            {
                pluginXmlHelper.addPluginInfoParam(pluginParam.getKey(), pluginParam.getValue());
            }
            
            for (ComponentImport componentImport : changes.getComponentImports())
            {
                pluginXmlHelper.addComponentImport(componentImport);
            }
            
            for (ComponentDeclaration component : changes.getComponentDeclarations())
            {
                pluginXmlHelper.addComponentDeclaration(component);
            }
            
            for (ModuleDescriptor moduleDescriptor : changes.getModuleDescriptors())
            {
                pluginXmlHelper.addModuleAsLastChild(moduleDescriptor.getContent());
            }
            
            pluginXmlHelper.savePluginXml(out);
        }
        catch (DocumentException e)
        {
            throw new IOException(e);
        }
    }
}
