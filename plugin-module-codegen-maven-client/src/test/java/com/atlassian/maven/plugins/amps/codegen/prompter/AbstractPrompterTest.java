package com.atlassian.maven.plugins.amps.codegen.prompter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.atlassian.plugins.codegen.modules.PluginModuleLocation;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import static org.mockito.Mockito.mock;

/**
 * @since 3.6
 */
public abstract class AbstractPrompterTest
{
    @Rule
    public final TemporaryFolder tempDir = new TemporaryFolder();

    protected PluginModuleLocation moduleLocation;
    protected Prompter prompter;

    @Before
    public void setupDirs() throws Exception
    {
        File srcDir = tempDir.newFolder("src");
        File testDir = tempDir.newFolder("test-src");
        File resourcesDir = tempDir.newFolder("resources");
        File templateDir = tempDir.newFolder("resources", "templates");
        File pluginXml = tempDir.newFile("resources" + File.separator + "atlassian-plugin.xml");

        InputStream is = this.getClass().getResourceAsStream("/empty-plugin.xml");
        try (FileOutputStream pluginXmlStream = FileUtils.openOutputStream(pluginXml))
        {
            IOUtils.copy(is, pluginXmlStream);
        }

        moduleLocation = new PluginModuleLocation.Builder(srcDir)
                .resourcesDirectory(resourcesDir)
                .testDirectory(testDir)
                .templateDirectory(templateDir)
                .build();
        prompter = mock(Prompter.class);
    }
}
