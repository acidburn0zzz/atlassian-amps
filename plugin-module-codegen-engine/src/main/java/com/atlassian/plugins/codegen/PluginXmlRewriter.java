package com.atlassian.plugins.codegen;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
        
            if (changes.hasItems(I18nString.class))
            {
                pluginXmlHelper.addI18nResource(DEFAULT_I18N_NAME);
            }
            
            for (PluginParameter pluginParam : changes.getItems(PluginParameter.class))
            {
                pluginXmlHelper.addPluginInfoParam(pluginParam.getName(), pluginParam.getValue());
            }
            
            for (ComponentImport componentImport : changes.getItems(ComponentImport.class))
            {
                pluginXmlHelper.addComponentImport(componentImport);
            }
            
            for (ComponentDeclaration component : changes.getItems(ComponentDeclaration.class))
            {
                pluginXmlHelper.addComponentDeclaration(component);
            }
            
            for (ModuleDescriptor moduleDescriptor : changes.getItems(ModuleDescriptor.class))
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
