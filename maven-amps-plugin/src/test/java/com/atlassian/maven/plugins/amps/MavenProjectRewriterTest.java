package com.atlassian.maven.plugins.amps;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.atlassian.maven.plugins.amps.XmlMatchers.XmlWrapper;
import com.atlassian.plugins.codegen.ArtifactDependency;
import com.atlassian.plugins.codegen.ArtifactDependency.Scope;
import com.atlassian.plugins.codegen.ArtifactId;
import com.atlassian.plugins.codegen.BundleInstruction;
import com.atlassian.plugins.codegen.MavenPlugin;
import com.atlassian.plugins.codegen.PluginProjectChangeset;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.hasItem;

import static org.hamcrest.Matchers.allOf;

import static com.atlassian.maven.plugins.amps.XmlMatchers.node;
import static com.atlassian.maven.plugins.amps.XmlMatchers.nodeCount;
import static com.atlassian.maven.plugins.amps.XmlMatchers.nodeText;
import static com.atlassian.maven.plugins.amps.XmlMatchers.nodeTextEquals;
import static com.atlassian.maven.plugins.amps.XmlMatchers.nodes;
import static com.atlassian.plugins.codegen.AmpsSystemPropertyVariable.ampsSystemPropertyVariable;
import static com.atlassian.plugins.codegen.ArtifactDependency.dependency;
import static com.atlassian.plugins.codegen.ArtifactId.artifactId;
import static com.atlassian.plugins.codegen.BundleInstruction.importPackage;
import static com.atlassian.plugins.codegen.BundleInstruction.privatePackage;
import static com.atlassian.plugins.codegen.MavenPlugin.mavenPlugin;
import static com.atlassian.plugins.codegen.PluginArtifact.pluginArtifact;
import static com.atlassian.plugins.codegen.PluginArtifact.ArtifactType.BUNDLED_ARTIFACT;
import static com.atlassian.plugins.codegen.PluginArtifact.ArtifactType.PLUGIN_ARTIFACT;
import static com.atlassian.plugins.codegen.VersionId.noVersion;
import static com.atlassian.plugins.codegen.VersionId.version;
import static com.atlassian.plugins.codegen.VersionId.versionProperty;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class MavenProjectRewriterTest
{
    private static final ArtifactId CUSTOM_ARTIFACT = artifactId("com.atlassian.dogs", "cooper");
    private static final ArtifactId CUSTOM_ARTIFACT2 = artifactId("com.atlassian.dogs", "sailor");
    private static final ArtifactId MAVEN_ARTIFACT = artifactId("maven-dependency-plugin");
    
    private static final String TEST_POM = "test-pom.xml";
    private static final String TEST_POM_WITH_CONFIG = "test-pom-with-config.xml";
    private static final String TEST_POM_WITH_INSTRUCTIONS = "test-pom-with-instructions.xml";
    private static final String TEST_POM_WITH_MAVEN_PLUGIN = "test-pom-with-maven-plugin.xml";

    private static final ArtifactDependency NEW_DEPENDENCY =
        dependency(CUSTOM_ARTIFACT, "1.0", Scope.PROVIDED);

    private static final int INITIAL_MAVEN_PLUGIN_COUNT = 1;  // see test-pom.xml
    private static final String NEW_MAVEN_PLUGIN_CONFIG =
        "<executions><execution>" +
            "<id>EXECUTION_ID</id>" +
            "<phase>PHASE</phase>" +
            "<goals><goal>GOAL1</goal><goal>GOAL2</goal></goals>" +
            "<configuration>" +
                "<param1>value1</param1>" +
            "</configuration>" +
        "</execution></executions>";
    private static final String MAVEN_PLUGIN_CONFIG_WITH_CONFLICTING_EXECUTION =
        "<executions><execution>" +
            "<id>copy-storage-plugin</id>" +
            "<phase>WRONG</phase>" +
            "<goals><goal>NO</goal></goals>" +
        "</execution></executions>";

    private static final MavenPlugin NEW_MAVEN_PLUGIN_NO_VERSION =
        mavenPlugin(MAVEN_ARTIFACT, noVersion(), NEW_MAVEN_PLUGIN_CONFIG);
    private static final MavenPlugin NEW_MAVEN_PLUGIN_WITH_VERSION =
        mavenPlugin(MAVEN_ARTIFACT, version("1.0"), NEW_MAVEN_PLUGIN_CONFIG);
    private static final MavenPlugin NEW_MAVEN_PLUGIN_WITH_GROUP_ID =
        mavenPlugin(CUSTOM_ARTIFACT, version("1.0"), NEW_MAVEN_PLUGIN_CONFIG);

    private static final com.atlassian.plugins.codegen.PluginArtifact NEW_BUNDLED_ARTIFACT =
        pluginArtifact(BUNDLED_ARTIFACT, CUSTOM_ARTIFACT, noVersion());
    private static final com.atlassian.plugins.codegen.PluginArtifact NEW_BUNDLED_ARTIFACT_WITH_VERSION =
        pluginArtifact(BUNDLED_ARTIFACT, CUSTOM_ARTIFACT, version("1.0"));
    private static final com.atlassian.plugins.codegen.PluginArtifact NEW_BUNDLED_ARTIFACT_WITH_VERSION_PROPERTY =
        pluginArtifact(BUNDLED_ARTIFACT, CUSTOM_ARTIFACT, versionProperty("dog.version", "1.0"));
    private static final com.atlassian.plugins.codegen.PluginArtifact NEW_PLUGIN_ARTIFACT =
        pluginArtifact(PLUGIN_ARTIFACT, CUSTOM_ARTIFACT, noVersion());
    
    private static final BundleInstruction NEW_IMPORT_PACKAGE =
        importPackage("com.atlassian.random", "2.0.1");
    private static final BundleInstruction DUPLICATE_IMPORT_PACKAGE =
        importPackage("com.atlassian.plugins.rest.common*", "1.0.5");
    private static final BundleInstruction NEW_PRIVATE_PACKAGE =
        privatePackage("com.atlassian.random");
    
    private static final PluginProjectChangeset changeset = new PluginProjectChangeset();
    
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
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_DEPENDENCY)),
                   nodes("//dependencies/dependency", nodeCount(3)));
    }

    @Test
    public void dependencyHasGroupId() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_DEPENDENCY)),
                   node("//dependencies/dependency[3]/groupId", nodeTextEquals(CUSTOM_ARTIFACT.getGroupId().get())));
    }

    @Test
    public void dependencyHasArtifactId() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_DEPENDENCY)),
                   node("//dependencies/dependency[3]/artifactId", nodeTextEquals(CUSTOM_ARTIFACT.getArtifactId())));
    }

    @Test
    public void dependencyHasVersion() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_DEPENDENCY)),
                   node("//dependencies/dependency[3]/version", nodeTextEquals(NEW_DEPENDENCY.getVersionId().toString())));
    }

    @Test
    public void dependencyHasScope() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_DEPENDENCY)),
                   node("//dependencies/dependency[3]/scope", nodeTextEquals("provided")));
    }
    
    @Test
    public void duplicateDependencyIsNotAdded() throws Exception
    {
        ArtifactDependency existing = dependency("com.atlassian.plugins", "test-artifact-1", "1.0", Scope.PROVIDED);
        
        assertThat(applyChanges(TEST_POM, changeset.with(existing)),
                   nodes("//dependencies/dependency", nodeCount(2)));
    }
    
    @Test
    public void dependencyWithVersionPropertyUsesPropertyNameForVersion() throws Exception
    {
        ArtifactDependency dependency = dependency(CUSTOM_ARTIFACT, versionProperty("dog.version", "1.0"), Scope.PROVIDED);
        
        assertThat(applyChanges(TEST_POM, changeset.with(dependency)),
                   node("//dependencies/dependency[3]/version", nodeTextEquals("${dog.version}")));
    }

    @Test
    public void dependencyWithVersionPropertyAddsPropertyNameAndValueToProperties() throws Exception
    {
        ArtifactDependency dependency = dependency(CUSTOM_ARTIFACT, versionProperty("dog.version", "1.0"), Scope.PROVIDED);
        
        assertThat(applyChanges(TEST_POM, changeset.with(dependency)),
                   node("//properties/dog.version", nodeTextEquals("1.0")));
    }

    @Test
    public void dependencyWithVersionPropertyDoesNotOverwriteExistingProperty() throws Exception
    {
        ArtifactDependency dependency = dependency(CUSTOM_ARTIFACT, versionProperty("dog.version", "1.0"), Scope.PROVIDED);
        ArtifactDependency dependency2 = dependency(CUSTOM_ARTIFACT2, versionProperty("dog.version", "3.5"), Scope.PROVIDED);
        
        assertThat(applyChanges(TEST_POM, changeset.with(dependency, dependency2)),
                   node("//properties/dog.version", nodeTextEquals("1.0")));
    }
    
    @Test
    public void mavenPluginIsAdded() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_MAVEN_PLUGIN_NO_VERSION)),
                   nodes("//build/plugins/plugin", nodeCount(INITIAL_MAVEN_PLUGIN_COUNT + 1)));
    }
    
    @Test
    public void mavenPluginHasArtifactId() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_MAVEN_PLUGIN_NO_VERSION)),
                   node("//build/plugins/plugin[2]/artifactId", nodeTextEquals(MAVEN_ARTIFACT.getArtifactId())));
    }
    
    @Test
    public void mavenPluginHasExecutionId() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_MAVEN_PLUGIN_NO_VERSION)),
                   node("//build/plugins/plugin[2]/executions/execution/id", nodeTextEquals("EXECUTION_ID")));
    }

    @Test
    public void mavenPluginHasExecutionPhase() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_MAVEN_PLUGIN_NO_VERSION)),
                   node("//build/plugins/plugin[2]/executions/execution/phase", nodeTextEquals("PHASE")));
    }

    @Test
    public void mavenPluginHasExecutionGoal1() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_MAVEN_PLUGIN_NO_VERSION)),
                   node("//build/plugins/plugin[2]/executions/execution/goals/goal[1]", nodeTextEquals("GOAL1")));
    }

    @Test
    public void mavenPluginHasExecutionGoal2() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_MAVEN_PLUGIN_NO_VERSION)),
                   node("//build/plugins/plugin[2]/executions/execution/goals/goal[1]", nodeTextEquals("GOAL1")));
    }

    @Test
    public void mavenPluginHasExecutionConfig() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_MAVEN_PLUGIN_NO_VERSION)),
                   node("//build/plugins/plugin[2]/executions/execution/configuration/param1", nodeTextEquals("value1")));
    }

    @Test
    public void mavenPluginWithNoGroupIdHasNoGroupId() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_MAVEN_PLUGIN_NO_VERSION)),
                   node("//build/plugins/plugin[2]/groupId", nullValue()));
    }

    @Test
    public void mavenPluginWithGroupIdHasGroupId() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_MAVEN_PLUGIN_WITH_GROUP_ID)),
                   node("//build/plugins/plugin[2]/groupId", nodeTextEquals(CUSTOM_ARTIFACT.getGroupId().get())));
    }

    @Test
    public void mavenPluginWithNoVersionHasNoVersion() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_MAVEN_PLUGIN_NO_VERSION)),
                   node("//build/plugins/plugin[2]/version", nullValue()));
    }

    @Test
    public void mavenPluginWithVersionHasVersion() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_MAVEN_PLUGIN_WITH_VERSION)),
                   node("//build/plugins/plugin[2]/version", nodeTextEquals(NEW_MAVEN_PLUGIN_WITH_VERSION.getVersionId().toString())));
    }

    @Test
    public void mavenPluginExecutionIsAddedToExistingPlugin() throws Exception
    {
        assertThat(applyChanges(TEST_POM_WITH_MAVEN_PLUGIN, changeset.with(NEW_MAVEN_PLUGIN_NO_VERSION)),
                   node("//build/plugins/plugin[2]/executions/execution[2]/id", nodeTextEquals("EXECUTION_ID")));
    }

    @Test
    public void mavenPluginExecutionWithDuplicateIdIsNotAdded() throws Exception
    {
        MavenPlugin pluginWithConflictingExecutionConfig =
            mavenPlugin(MAVEN_ARTIFACT, noVersion(), MAVEN_PLUGIN_CONFIG_WITH_CONFLICTING_EXECUTION);

        assertThat(applyChanges(TEST_POM_WITH_MAVEN_PLUGIN, changeset.with(pluginWithConflictingExecutionConfig)),
                   nodes("//build/plugins/plugin[2]/executions/execution", nodeCount(1)));
    }

    @Test
    public void mavenPluginExecutionWithDuplicateIdDoesNotOverwriteExistingConfig() throws Exception
    {
        MavenPlugin pluginWithConflictingExecutionConfig =
            mavenPlugin(MAVEN_ARTIFACT, noVersion(), MAVEN_PLUGIN_CONFIG_WITH_CONFLICTING_EXECUTION);

        assertThat(applyChanges(TEST_POM_WITH_MAVEN_PLUGIN, changeset.with(pluginWithConflictingExecutionConfig)),
                   node("//build/plugins/plugin[2]/executions/execution/phase", nodeTextEquals("process-resources")));
    }

    @Test
    public void configElementIsCreatedForBundleInstruction() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_IMPORT_PACKAGE)),
                   node("//build/plugins/plugin[1]/configuration", notNullValue()));
    }

    @Test
    public void instructionsElementIsCreatedWithinNewlyCreatedConfigElement() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_IMPORT_PACKAGE)),
                   node("//build/plugins/plugin[1]/configuration/instructions", notNullValue()));
    }

    @Test
    public void bundleInstructionIsAddedToNewInstructionsInNewConfig() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_IMPORT_PACKAGE)),
                   node("//build/plugins/plugin[1]/configuration/instructions/Import-Package",
                        nodeTextEquals("com.atlassian.random;version=\"2.0.1\"")));
    }

    @Test
    public void instructionsElementIsCreatedWithinExistingConfigElement() throws Exception
    {
        assertThat(applyChanges(TEST_POM_WITH_CONFIG, changeset.with(NEW_IMPORT_PACKAGE)),
                   node("//build/plugins/plugin[1]/configuration/instructions", notNullValue()));
    }

    @Test
    public void bundleInstructionIsAddedToNewInstructionsInExistingConfig() throws Exception
    {
        assertThat(applyChanges(TEST_POM_WITH_CONFIG, changeset.with(NEW_IMPORT_PACKAGE)),
                   node("//build/plugins/plugin[1]/configuration/instructions/Import-Package",
                        nodeTextEquals("com.atlassian.random;version=\"2.0.1\"")));
    }

    @Test
    public void bundleInstructionIsAddedToExistingCategory() throws Exception
    {
        assertThat(applyChanges(TEST_POM_WITH_INSTRUCTIONS, changeset.with(NEW_IMPORT_PACKAGE)),
                   node("//build/plugins/plugin[1]/configuration/instructions/Import-Package",
                        nodeText(delimitedList(",", Matchers.<String>iterableWithSize(4)))));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void bundleInstructionIsInsertedInPackageOrderWithinExistingCategory() throws Exception
    {
        assertThat(applyChanges(TEST_POM_WITH_INSTRUCTIONS, changeset.with(NEW_IMPORT_PACKAGE)),
                   node("//build/plugins/plugin[1]/configuration/instructions/Import-Package",
                        nodeText(delimitedList(",",
                                               Matchers.<String>hasItems(any(String.class), 
                                                   equalTo("com.atlassian.random;version=\"2.0.1\""),
                                                   any(String.class), any(String.class))))));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void existingInstructionsArePreservedWhenAddingNewInstructionInCategory() throws Exception
    {
        assertThat(applyChanges(TEST_POM_WITH_INSTRUCTIONS, changeset.with(NEW_IMPORT_PACKAGE)),
                   node("//build/plugins/plugin[1]/configuration/instructions/Import-Package",
                        nodeText(delimitedList(",",
                                               Matchers.<String>hasItems(equalTo("com.atlassian.plugin.*;version=\"${atlassian.plugins.version}\""),
                                                   any(String.class), any(String.class), any(String.class))))));
    }

    @Test
    public void bundleInstructionIsNotInsertedIfPackageIsAlreadyPresentInCategory() throws Exception
    {
        assertThat(applyChanges(TEST_POM_WITH_INSTRUCTIONS, changeset.with(DUPLICATE_IMPORT_PACKAGE)),
                   node("//build/plugins/plugin[1]/configuration/instructions/Import-Package",
                        nodeText(delimitedList(",",
                                               Matchers.<String>iterableWithSize(3)))));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void bundleInstructionDoesNotOverwriteInstructionForSamePackage() throws Exception
    {
        assertThat(applyChanges(TEST_POM_WITH_INSTRUCTIONS, changeset.with(DUPLICATE_IMPORT_PACKAGE)),
                   node("//build/plugins/plugin[1]/configuration/instructions/Import-Package",
                        nodeText(delimitedList(",",
                                               Matchers.<String>hasItems(equalTo("com.atlassian.plugins.rest.common*;version=\"1.0.5\";resolution:=optional"))))));
    }

    @Test
    public void bundleInstructionIsAddedToNewCategoryInExistingInstructions() throws Exception
    {
        assertThat(applyChanges(TEST_POM_WITH_INSTRUCTIONS, changeset.with(NEW_PRIVATE_PACKAGE)),
                   node("//build/plugins/plugin[1]/configuration/instructions/Private-Package", nodeTextEquals("com.atlassian.random")));
    }

    @Test
    public void configElementIsCreatedForBundledArtifact() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_BUNDLED_ARTIFACT)),
                   node("//build/plugins/plugin[1]/configuration", notNullValue()));
    }

    @Test
    public void bundledArtifactHasGroupId() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_BUNDLED_ARTIFACT)),
                   node("//build/plugins/plugin[1]/configuration/bundledArtifacts/bundledArtifact/groupId",
                        nodeTextEquals(CUSTOM_ARTIFACT.getGroupId().get())));
    }

    @Test
    public void bundledArtifactHasArtifactId() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_BUNDLED_ARTIFACT)),
                   node("//build/plugins/plugin[1]/configuration/bundledArtifacts/bundledArtifact/artifactId",
                        nodeTextEquals(CUSTOM_ARTIFACT.getArtifactId())));
    }

    @Test
    public void pluginArtifactHasArtifactId() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_PLUGIN_ARTIFACT)),
                   node("//build/plugins/plugin[1]/configuration/pluginArtifacts/pluginArtifact/artifactId",
                        nodeTextEquals(CUSTOM_ARTIFACT.getArtifactId())));
    }

    @Test
    public void bundledArtifactWithNoVersionHasNoVersion() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_BUNDLED_ARTIFACT)),
                   node("//build/plugins/plugin[1]/configuration/bundledArtifacts/bundledArtifact/version",
                        nullValue()));
    }
    
    @Test
    public void bundledArtifactWithVersionHasVersion() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_BUNDLED_ARTIFACT_WITH_VERSION)),
                   node("//build/plugins/plugin[1]/configuration/bundledArtifacts/bundledArtifact/version",
                        nodeTextEquals("1.0")));
    }

    @Test
    public void bundledArtifactWithVersionPropertyUsesPropertyName() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_BUNDLED_ARTIFACT_WITH_VERSION_PROPERTY)),
                   node("//build/plugins/plugin[1]/configuration/bundledArtifacts/bundledArtifact/version",
                        nodeTextEquals("${dog.version}")));
    }

    @Test
    public void bundledArtifactWithVersionPropertyAddsPropertyNameAndValueToProperties() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(NEW_BUNDLED_ARTIFACT_WITH_VERSION_PROPERTY)),
                   node("//properties/dog.version", nodeTextEquals("1.0")));
    }

    @Test
    public void existingBundledArtifactIsNotAddedAgain() throws Exception
    {
        assertThat(applyChanges(TEST_POM_WITH_CONFIG, changeset.with(NEW_BUNDLED_ARTIFACT)),
                   nodes("//build/plugins/plugin[1]/configuration/bundledArtifacts/bundledArtifact", nodeCount(1)));
    }

    @Test
    public void configElementIsCreatedForAmpsSystemProperty() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(ampsSystemPropertyVariable("newVariable", "bar"))),
                   node("//build/plugins/plugin[1]/configuration", notNullValue()));
    }
    
    @Test
    public void variablesElementIsCreatedForAmpsSystemProperty() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(ampsSystemPropertyVariable("newVariable", "bar"))),
                   node("//build/plugins/plugin[1]/configuration/systemPropertyVariables", notNullValue()));
    }
    
    @Test
    public void ampsSystemPropertyHasNameAndValue() throws Exception
    {
        assertThat(applyChanges(TEST_POM, changeset.with(ampsSystemPropertyVariable("newVariable", "bar"))),
                   node("//build/plugins/plugin[1]/configuration/systemPropertyVariables/newVariable", nodeTextEquals("bar")));
    }

    @Test
    public void ampsSystemPropertyIsAddedToList() throws Exception
    {
        assertThat(applyChanges(TEST_POM_WITH_CONFIG, changeset.with(ampsSystemPropertyVariable("newVariable", "bar"))),
                   node("//build/plugins/plugin[1]/configuration/systemPropertyVariables/newVariable", nodeTextEquals("bar")));
    }
    
    @Test
    public void ampsSystemPropertyDoesNotOverwriteVariableWithDifferentName() throws Exception
    {
        assertThat(applyChanges(TEST_POM_WITH_CONFIG, changeset.with(ampsSystemPropertyVariable("newVariable", "bar"))),
                   node("//build/plugins/plugin[1]/configuration/systemPropertyVariables/existingVariable", nodeTextEquals("foo")));
    }
    
    @Test
    public void ampsSystemPropertyDoesNotOverwriteVariableWithSameName() throws Exception
    {
        assertThat(applyChanges(TEST_POM_WITH_CONFIG, changeset.with(ampsSystemPropertyVariable("existingVariable", "bar"))),
                   node("//build/plugins/plugin[1]/configuration/systemPropertyVariables/existingVariable", nodeTextEquals("foo")));
    }
    
    protected XmlWrapper applyChanges(String pomTemplateName, PluginProjectChangeset changes) throws Exception
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

        rewriter.applyChanges(changes);
        
        return XmlMatchers.xml(FileUtils.readFileToString(pom), "project");
    }
    

    public static Matcher<String> delimitedList(final String delimiter, final Matcher<Iterable<String>> listMatcher)
    {
        return new TypeSafeDiagnosingMatcher<String>()
        {
            protected boolean matchesSafely(String s, Description mismatchDescription)
            {
                String[] parts = s.split(delimiter);
                List<String> trimmed = new ArrayList<String>(parts.length);
                for (String p : parts)
                {
                    trimmed.add(p.trim());
                }
                if (!listMatcher.matches(trimmed))
                {
                    listMatcher.describeMismatch(trimmed, mismatchDescription);
                    return false;
                }
                return true;
            }

            public void describeTo(Description description)
            {
                description.appendText("list delimited by '" + delimiter + "' ");
                listMatcher.describeTo(description);
            }
        };
    }
}
