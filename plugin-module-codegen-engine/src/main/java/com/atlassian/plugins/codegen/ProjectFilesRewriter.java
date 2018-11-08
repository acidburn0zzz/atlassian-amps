package com.atlassian.plugins.codegen;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import com.atlassian.plugins.codegen.modules.PluginModuleLocation;
import com.atlassian.plugins.codegen.util.FileUtil;
import com.atlassian.plugins.codegen.util.PluginXmlHelper;

import com.google.common.io.Files;

import org.apache.commons.io.FileUtils;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Applies the changes from a {@link PluginProjectChangeset} that involve creating
 * source or resource files.
 */
public class ProjectFilesRewriter implements ProjectRewriter
{
    private PluginModuleLocation location;
    
    public ProjectFilesRewriter(PluginModuleLocation location)
    {
        this.location = checkNotNull(location, "location");
    }

    @Override
    public void applyChanges(PluginProjectChangeset changes) throws Exception
    {
        // We use a PluginXmlHelper to read some information from atlassian-plugin.xml if needed
        PluginXmlHelper xmlHelper = new PluginXmlHelper(location);
        
        for (SourceFile sourceFile : changes.getItems(SourceFile.class))
        {
            File baseDir = sourceFile.getSourceGroup() == SourceFile.SourceGroup.TESTS ?
                location.getTestDirectory() : location.getSourceDirectory();
            File newFile = FileUtil.dotDelimitedFilePath(baseDir, sourceFile.getClassId().getFullName(), ".java");
            Files.createParentDirs(newFile);
            FileUtils.writeStringToFile(newFile, sourceFile.getContent(), StandardCharsets.UTF_8);
        }
        for (ResourceFile resourceFile : changes.getItems(ResourceFile.class))
        {
            File resourceDir = location.getResourcesDir();
            if (!resourceFile.getRelativePath().equals(""))
            {
                resourceDir = new File(resourceDir, resourceFile.getRelativePath());
            }
            File newFile = new File(resourceDir, resourceFile.getName());
            Files.createParentDirs(newFile);
            FileUtils.writeByteArrayToFile(newFile, resourceFile.getContent());
        }
        if (changes.hasItems(I18nString.class))
        {
            addI18nStrings(FileUtil.dotDelimitedFilePath(location.getResourcesDir(), xmlHelper.getDefaultI18nLocation(), ".properties"),
                changes.getItems(I18nString.class));
        }
    }

    private void addI18nStrings(File file, Iterable<I18nString> items) throws IOException
    {
        Files.createParentDirs(file);
        String oldContent = file.exists() ? FileUtils.readFileToString(file, StandardCharsets.UTF_8) : "";
        Properties oldProps = new Properties();
        oldProps.load(new StringReader(oldContent));
        StringBuilder newContent = new StringBuilder(oldContent);
        boolean modified = false;
        for (I18nString item : items)
        {
            if (!oldProps.containsKey(item.getName()))
            {
                if (!modified)
                {
                    newContent.append("\n");
                }
                modified = true;
                newContent.append(item.getName()).append("=").append(item.getValue()).append("\n");
                oldProps.put(item.getName(), item.getValue());
            }
        }
        if (modified)
        {
            FileUtils.writeStringToFile(file, newContent.toString(), StandardCharsets.UTF_8);
        }
    }
}
