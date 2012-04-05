package com.atlassian.plugins.codegen;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.atlassian.plugins.codegen.modules.PluginModuleLocation;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import static org.apache.commons.io.FileUtils.deleteDirectory;

public class ProjectHelper
{
    public static final String GROUP_ID = "myGroupId";
    public static final String ARTIFACT_ID = "myArtifactId";
    public static final String PLUGIN_KEY = "myGroupId.myArtifactId";

    public PluginModuleLocation location;
    public File pluginXml;
    public File tempDir;
    public File srcDir;
    public File testDir;
    public File resourcesDir;
    
    public ProjectHelper() throws Exception
    {
        final File buildRoot = new File("target");
        String dirName = UUID.randomUUID().toString();
        tempDir = new File(buildRoot, dirName);
        srcDir = new File(tempDir, "src");
        testDir = new File(tempDir, "test");
        resourcesDir = new File(tempDir, "resources");
        pluginXml = new File(resourcesDir, "atlassian-plugin.xml");
        tempDir.mkdirs();
        srcDir.mkdirs();
        testDir.mkdirs();
        resourcesDir.mkdirs();
        location = new PluginModuleLocation.Builder(srcDir)
            .testDirectory(testDir)
            .resourcesDirectory(resourcesDir)
            .groupAndArtifactId(GROUP_ID, ARTIFACT_ID)
            .build();
    }
    
    public void usePluginXml(String path) throws Exception
    {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
        OutputStream os = FileUtils.openOutputStream(pluginXml);
        IOUtils.copy(is, os);
        is.close();
        os.close();
    }
    
    public void destroy() throws Exception
    {
        deleteDirectory(tempDir);
    }
}
