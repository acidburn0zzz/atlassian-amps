package com.atlassian.plugins.codegen;

import com.atlassian.plugins.codegen.modules.PluginModuleLocation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProjectHelper extends ExternalResource
{
    static final String GROUP_ID = "myGroupId";
    static final String ARTIFACT_ID = "myArtifactId";
    static final String PLUGIN_KEY = "myGroupId.myArtifactId";

    public PluginModuleLocation location;
    public File pluginXml;
    public File srcDir;
    public File testDir;
    public File resourcesDir;

    private final TemporaryFolder tempDir;
    
    public ProjectHelper()
    {
        tempDir = new TemporaryFolder();
    }

    public void usePluginXml(String path) throws Exception
    {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
             OutputStream os = FileUtils.openOutputStream(pluginXml))
        {
            IOUtils.copy(is, os);
        }
    }

    @Override
    protected void after()
    {
        tempDir.delete();
    }

    @Override
    protected void before() throws IOException
    {
        tempDir.create();

        srcDir = tempDir.newFolder("src");
        testDir = tempDir.newFolder("test");
        resourcesDir = tempDir.newFolder("resources");
        pluginXml = new File(resourcesDir, "atlassian-plugin.xml");

        location = new PluginModuleLocation.Builder(srcDir)
                .testDirectory(testDir)
                .resourcesDirectory(resourcesDir)
                .groupAndArtifactId(GROUP_ID, ARTIFACT_ID)
                .build();
    }
}
