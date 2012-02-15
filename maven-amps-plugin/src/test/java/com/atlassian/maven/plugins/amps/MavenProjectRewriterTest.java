package com.atlassian.maven.plugins.amps;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.util.UUID;

import com.atlassian.plugins.codegen.ArtifactDependency;
import com.atlassian.plugins.codegen.ArtifactDependency.Scope;
import com.atlassian.plugins.codegen.BundleInstruction;
import com.atlassian.plugins.codegen.BundleInstruction.Category;
import com.atlassian.plugins.codegen.PluginProjectChangeset;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.atlassian.plugins.codegen.ArtifactDependency.dependency;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MavenProjectRewriterTest
{
    private static final String TEST_POM = "test-pom.xml";
    private static final String TEST_POM_WITH_CONFIG = "test-pom-with-config.xml";
    private static final String TEST_POM_WITH_INSTRUCTIONS = "test-pom-with-instructions.xml";
    private static final ArtifactDependency NEW_DEPENDENCY =
        dependency("com.atlassian.dogs", "cooper", "1.0", Scope.PROVIDED);
    private static final BundleInstruction NEW_IMPORT_PACKAGE =
        new BundleInstruction(Category.IMPORT, "com.atlassian.random", "2.0.1");
    private static final BundleInstruction NEW_PRIVATE_PACKAGE =
        new BundleInstruction(Category.PRIVATE, "com.atlassian.random", "2.0.1");
    
    private File tempDir;
    private File pom;
    private MavenProjectRewriter rewriter;
    
    @Before
    public void setup() throws Exception
    {
        final File sysTempDir = new File("target");
        String dirName = UUID.randomUUID().toString();
        tempDir = new File(sysTempDir, dirName);
        tempDir.mkdirs();
        
        pom = new File(tempDir, "pom.xml");
    }
    
    @After
    public void deleteTempDir() throws Exception
    {
        FileUtils.deleteDirectory(tempDir);
    }

    @Test
    public void dependencyIsAdded() throws Exception
    {
        setupProject(TEST_POM);
        Element xml = applyChanges(new PluginProjectChangeset().withDependencies(NEW_DEPENDENCY));
        
        assertEquals(3, xml.selectNodes("//m:dependencies/m:dependency").size());
    }

    @Test
    public void dependencyHasGroupId() throws Exception
    {
        setupProject(TEST_POM);
        Element xml = applyChanges(new PluginProjectChangeset().withDependencies(NEW_DEPENDENCY));
        
        
        assertEquals(NEW_DEPENDENCY.getGroupId(),
                     xml.selectSingleNode("//m:dependencies/m:dependency[3]/m:groupId").getText());
    }

    @Test
    public void dependencyHasArtifactId() throws Exception
    {
        setupProject(TEST_POM);
        Element xml = applyChanges(new PluginProjectChangeset().withDependencies(NEW_DEPENDENCY));
        
        assertEquals(NEW_DEPENDENCY.getArtifactId(),
                     xml.selectSingleNode("//m:dependencies/m:dependency[3]/m:artifactId").getText());
    }

    @Test
    public void dependencyHasVersion() throws Exception
    {
        setupProject(TEST_POM);
        Element xml = applyChanges(new PluginProjectChangeset().withDependencies(NEW_DEPENDENCY));
        
        assertEquals(NEW_DEPENDENCY.getVersion(),
                     xml.selectSingleNode("//m:dependencies/m:dependency[3]/m:version").getText());
    }

    @Test
    public void dependencyHasScope() throws Exception
    {
        setupProject(TEST_POM);
        Element xml = applyChanges(new PluginProjectChangeset().withDependencies(NEW_DEPENDENCY));
        
        assertEquals("provided",
                     xml.selectSingleNode("//m:dependencies/m:dependency[3]/m:scope").getText());
    }
    
    @Test
    public void duplicateDependencyIsNotAdded() throws Exception
    {
        ArtifactDependency existing = dependency("com.atlassian.plugins", "test-artifact-1", "1.0", Scope.PROVIDED);
        setupProject(TEST_POM);
        Element xml = applyChanges(new PluginProjectChangeset().withDependencies(existing));
        
        assertEquals(2, xml.selectNodes("//m:dependencies/m:dependency").size());
    }
    
    @Test
    public void configElementIsCreatedForBundleInstruction() throws Exception
    {
        setupProject(TEST_POM);
        Element xml = applyChanges(new PluginProjectChangeset().withBundleInstructions(NEW_IMPORT_PACKAGE));
        
        assertNotNull(xml.selectSingleNode("//m:build/m:plugins/m:plugin[1]/m:configuration"));
    }

    @Test
    public void instructionsElementIsCreatedWithinNewlyCreatedConfigElement() throws Exception
    {
        setupProject(TEST_POM);
        Element xml = applyChanges(new PluginProjectChangeset().withBundleInstructions(NEW_IMPORT_PACKAGE));
        
        assertNotNull(xml.selectSingleNode("//m:build/m:plugins/m:plugin[1]/m:configuration/m:instructions"));
    }

    @Test
    public void bundleInstructionIsAddedToNewInstructionsInNewConfig() throws Exception
    {
        setupProject(TEST_POM);
        Element xml = applyChanges(new PluginProjectChangeset().withBundleInstructions(NEW_IMPORT_PACKAGE));
        
        String instructions = xml.selectSingleNode("//m:build/m:plugins/m:plugin[1]/m:configuration/m:instructions/m:Import-Package").getText();
        assertEquals("com.atlassian.random;version=\"2.0.1\"", instructions.trim());
    }

    @Test
    public void instructionsElementIsCreatedWithinExistingConfigElement() throws Exception
    {
        setupProject(TEST_POM_WITH_CONFIG);
        Element xml = applyChanges(new PluginProjectChangeset().withBundleInstructions(NEW_IMPORT_PACKAGE));
        
        assertNotNull(xml.selectSingleNode("//m:build/m:plugins/m:plugin[1]/m:configuration/m:instructions"));
    }

    @Test
    public void bundleInstructionIsAddedToNewInstructionsInExistingConfig() throws Exception
    {
        setupProject(TEST_POM_WITH_CONFIG);
        Element xml = applyChanges(new PluginProjectChangeset().withBundleInstructions(NEW_IMPORT_PACKAGE));
        
        String instructions = xml.selectSingleNode("//m:build/m:plugins/m:plugin[1]/m:configuration/m:instructions/m:Import-Package").getText();
        assertEquals("com.atlassian.random;version=\"2.0.1\"", instructions.trim());
    }

    @Test
    public void bundleInstructionIsAddedToExistingCategory() throws Exception
    {
        setupProject(TEST_POM_WITH_INSTRUCTIONS);
        Element xml = applyChanges(new PluginProjectChangeset().withBundleInstructions(NEW_IMPORT_PACKAGE));
        
        String instructions = xml.selectSingleNode("//m:build/m:plugins/m:plugin[1]/m:configuration/m:instructions/m:Import-Package").getText();
        assertEquals(4, instructions.split(",").length);
    }

    @Test
    public void bundleInstructionIsInsertedInPackageOrderWithinExistingCategory() throws Exception
    {
        setupProject(TEST_POM_WITH_INSTRUCTIONS);
        Element xml = applyChanges(new PluginProjectChangeset().withBundleInstructions(NEW_IMPORT_PACKAGE));
        
        String instructions = xml.selectSingleNode("//m:build/m:plugins/m:plugin[1]/m:configuration/m:instructions/m:Import-Package").getText();
        assertEquals("com.atlassian.random;version=\"2.0.1\"", instructions.split(",")[2].trim());
    }

    @Test
    public void existingInstructionsArePreservedWhenAddingNewInstructionInCategory() throws Exception
    {
        setupProject(TEST_POM_WITH_INSTRUCTIONS);
        Element xml = applyChanges(new PluginProjectChangeset().withBundleInstructions(NEW_IMPORT_PACKAGE));
        
        String instructions = xml.selectSingleNode("//m:build/m:plugins/m:plugin[1]/m:configuration/m:instructions/m:Import-Package").getText();
        assertEquals("com.atlassian.plugin.*;version=\"${atlassian.plugins.version}\"", instructions.split(",")[0].trim());
    }

    @Test
    public void bundleInstructionIsAddedToNewCategoryInExistingInstructions() throws Exception
    {
        setupProject(TEST_POM_WITH_INSTRUCTIONS);
        Element xml = applyChanges(new PluginProjectChangeset().withBundleInstructions(NEW_PRIVATE_PACKAGE));
        
        String instructions = xml.selectSingleNode("//m:build/m:plugins/m:plugin[1]/m:configuration/m:instructions/m:Private-Package").getText();
        assertEquals("com.atlassian.random;version=\"2.0.1\"", instructions.trim());
    }

    protected void setupProject(String pomTemplateName) throws Exception
    {
        InputStream is = getClass().getClassLoader().getResourceAsStream(pomTemplateName);
        FileUtils.copyInputStreamToFile(is, pom);
        closeQuietly(is);
        
        Reader reader = new FileReader(pom);
        try
        {
            MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
            Model model = xpp3Reader.read(reader);
            rewriter = new MavenProjectRewriter(model, pom);
        }
        finally
        {
            closeQuietly(reader);
        }
    }
    
    protected Element applyChanges(PluginProjectChangeset changes) throws Exception
    {
        rewriter.applyChanges(changes);
        Document doc = DocumentHelper.parseText(FileUtils.readFileToString(pom));
        doc.getRootElement().addNamespace("m", "http://maven.apache.org/POM/4.0.0");
        return (Element) doc.selectSingleNode("//m:project");
    }
}
