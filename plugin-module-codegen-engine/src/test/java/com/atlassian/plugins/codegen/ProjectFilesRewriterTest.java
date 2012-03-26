package com.atlassian.plugins.codegen;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

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
    protected static final ClassId CLASS = fullyQualified("com.atlassian.test.MyClass");
    protected static final String CONTENT = "this is some amazing content";
    
    protected ProjectHelper helper;
    protected ProjectFilesRewriter rewriter;
    
    @Before
    public void setup() throws Exception
    {
        helper = new ProjectHelper();        
        usePluginXml("empty-plugin.xml");
    }
    
    private void usePluginXml(String path) throws Exception
    {
        helper.usePluginXml(path);
        rewriter = new ProjectFilesRewriter(helper.location);
    }

    @After
    public void deleteTempDir() throws Exception
    {
        helper.destroy();
    }
    
    @Test
    public void sourceFileIsCreated() throws Exception
    {
        PluginProjectChangeset changes = new PluginProjectChangeset()
            .with(sourceFile(CLASS, SourceFile.SourceGroup.MAIN, CONTENT));
        rewriter.applyChanges(changes);
        
        assertTrue(new File(helper.srcDir, "com/atlassian/test/MyClass.java").exists());
    }
    
    @Test
    public void sourceFileHasContent() throws Exception
    {
        PluginProjectChangeset changes = new PluginProjectChangeset()
            .with(sourceFile(CLASS, SourceFile.SourceGroup.MAIN, CONTENT));
        rewriter.applyChanges(changes);
        
        assertEquals(CONTENT, FileUtils.readFileToString(new File(helper.srcDir, "com/atlassian/test/MyClass.java")));
    }
    
    @Test
    public void testSourceFileIsCreated() throws Exception
    {
        PluginProjectChangeset changes = new PluginProjectChangeset()
            .with(sourceFile(CLASS, SourceFile.SourceGroup.TESTS, CONTENT));
        rewriter.applyChanges(changes);
        
        assertTrue(new File(helper.testDir, "com/atlassian/test/MyClass.java").exists());
    }
    
    @Test
    public void testSourceFileHasContent() throws Exception
    {
        PluginProjectChangeset changes = new PluginProjectChangeset()
            .with(sourceFile(CLASS, SourceFile.SourceGroup.TESTS, CONTENT));
        rewriter.applyChanges(changes);
        
        assertEquals(CONTENT, FileUtils.readFileToString(new File(helper.testDir, "com/atlassian/test/MyClass.java")));
    }
    
    @Test
    public void resourceFileIsCreated() throws Exception
    {
        PluginProjectChangeset changes = new PluginProjectChangeset()
            .with(ResourceFile.resourceFile("templates/test", "template.vm", CONTENT));
        rewriter.applyChanges(changes);
        
        assertTrue(new File(helper.resourcesDir, "templates/test/template.vm").exists());        
    }
    
    @Test
    public void resourceFileHasContent() throws Exception
    {
        PluginProjectChangeset changes = new PluginProjectChangeset()
            .with(ResourceFile.resourceFile("templates/test", "template.vm", CONTENT));
        rewriter.applyChanges(changes);
        
        assertEquals(CONTENT, FileUtils.readFileToString(new File(helper.resourcesDir, "templates/test/template.vm")));
    }
    
    @Test
    public void i18nPropertyFileIsCreatedWithDefaultLocation() throws Exception
    {
        PluginProjectChangeset changes = new PluginProjectChangeset()
            .with(i18nString("foo", "bar"));
        rewriter.applyChanges(changes);
        
        assertTrue(new File(helper.resourcesDir, ProjectHelper.GROUP_ID + File.separator
                            + ProjectHelper.ARTIFACT_ID + ".properties").exists());        
    }
    
    @Test
    public void i18nPropertyFileHasPropertyValue() throws Exception
    {
        PluginProjectChangeset changes = new PluginProjectChangeset()
            .with(i18nString("foo", "bar"));
        rewriter.applyChanges(changes);
        
        Properties props = new Properties();
        props.load(new FileInputStream(new File(helper.resourcesDir, ProjectHelper.GROUP_ID + File.separator
                                                + ProjectHelper.ARTIFACT_ID + ".properties")));
        assertEquals("bar", props.getProperty("foo"));
    }
    
    @Test
    public void i18nPropertyFileIsCreatedWithCustomLocation() throws Exception
    {
        usePluginXml("plugin-with-same-i18n-name.xml");

        PluginProjectChangeset changes = new PluginProjectChangeset()
        .with(i18nString("foo", "bar"));
        rewriter.applyChanges(changes);
    
        assertTrue(new File(helper.resourcesDir, "nonstandard-location.properties").exists());
    }
}
