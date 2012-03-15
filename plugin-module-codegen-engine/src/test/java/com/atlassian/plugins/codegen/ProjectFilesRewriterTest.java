package com.atlassian.plugins.codegen;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.UUID;

import com.atlassian.plugins.codegen.modules.PluginModuleLocation;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.atlassian.plugins.codegen.ClassId.fullyQualified;
import static com.atlassian.plugins.codegen.I18nString.i18nString;
import static com.atlassian.plugins.codegen.SourceFile.sourceFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProjectFilesRewriterTest
{
    public static final ClassId CLASS = fullyQualified("com.atlassian.test.MyClass");
    public static final String CONTENT = "this is some amazing content";
    
    protected File tempDir;
    protected File srcDir;
    protected File testDir;
    protected File resourcesDir;

    protected PluginModuleLocation moduleLocation;
    protected ProjectFilesRewriter rewriter;
    
    @Before
    public void setup() throws Exception
    {
        final File sysTempDir = new File("target");
        String dirName = UUID.randomUUID().toString();
        tempDir = new File(sysTempDir, dirName);
        srcDir = new File(tempDir, "src");
        testDir = new File(tempDir, "test-src");
        resourcesDir = new File(tempDir, "resources");

        tempDir.mkdirs();
        srcDir.mkdirs();
        resourcesDir.mkdirs();
        
        moduleLocation = new PluginModuleLocation.Builder(srcDir)
            .resourcesDirectory(resourcesDir)
            .testDirectory(testDir)
            .build();
        
        rewriter = new ProjectFilesRewriter(moduleLocation);
    }
    
    @After
    public void deleteTempDir() throws Exception
    {
        FileUtils.deleteDirectory(tempDir);
    }
    
    @Test
    public void sourceFileIsCreated() throws Exception
    {
        PluginProjectChangeset changes = new PluginProjectChangeset()
            .with(sourceFile(CLASS, SourceFile.SourceGroup.MAIN, CONTENT));
        rewriter.applyChanges(changes);
        
        assertTrue(new File(srcDir, "com/atlassian/test/MyClass.java").exists());
    }
    
    @Test
    public void sourceFileHasContent() throws Exception
    {
        PluginProjectChangeset changes = new PluginProjectChangeset()
            .with(sourceFile(CLASS, SourceFile.SourceGroup.MAIN, CONTENT));
        rewriter.applyChanges(changes);
        
        assertEquals(CONTENT, FileUtils.readFileToString(new File(srcDir, "com/atlassian/test/MyClass.java")));
    }
    
    @Test
    public void testSourceFileIsCreated() throws Exception
    {
        PluginProjectChangeset changes = new PluginProjectChangeset()
            .with(sourceFile(CLASS, SourceFile.SourceGroup.TESTS, CONTENT));
        rewriter.applyChanges(changes);
        
        assertTrue(new File(testDir, "com/atlassian/test/MyClass.java").exists());
    }
    
    @Test
    public void testSourceFileHasContent() throws Exception
    {
        PluginProjectChangeset changes = new PluginProjectChangeset()
            .with(sourceFile(CLASS, SourceFile.SourceGroup.TESTS, CONTENT));
        rewriter.applyChanges(changes);
        
        assertEquals(CONTENT, FileUtils.readFileToString(new File(testDir, "com/atlassian/test/MyClass.java")));
    }
    
    @Test
    public void resourceFileIsCreated() throws Exception
    {
        PluginProjectChangeset changes = new PluginProjectChangeset()
            .with(ResourceFile.resourceFile("templates/test", "template.vm", CONTENT));
        rewriter.applyChanges(changes);
        
        assertTrue(new File(resourcesDir, "templates/test/template.vm").exists());        
    }
    
    @Test
    public void resourceFileHasContent() throws Exception
    {
        PluginProjectChangeset changes = new PluginProjectChangeset()
            .with(ResourceFile.resourceFile("templates/test", "template.vm", CONTENT));
        rewriter.applyChanges(changes);
        
        assertEquals(CONTENT, FileUtils.readFileToString(new File(resourcesDir, "templates/test/template.vm")));
    }
    
    @Test
    public void i18nPropertyFileIsCreated() throws Exception
    {
        PluginProjectChangeset changes = new PluginProjectChangeset()
            .with(i18nString("foo", "bar"));
        rewriter.applyChanges(changes);
        
        assertTrue(new File(resourcesDir, ProjectRewriter.DEFAULT_I18N_NAME + ".properties").exists());        
    }
    
    @Test
    public void i18nPropertyFileHasPropertyValue() throws Exception
    {
        PluginProjectChangeset changes = new PluginProjectChangeset()
            .with(i18nString("foo", "bar"));
        rewriter.applyChanges(changes);
        
        Properties props = new Properties();
        props.load(new FileInputStream(new File(resourcesDir, ProjectRewriter.DEFAULT_I18N_NAME + ".properties")));
        assertEquals("bar", props.getProperty("foo"));
    }
}
