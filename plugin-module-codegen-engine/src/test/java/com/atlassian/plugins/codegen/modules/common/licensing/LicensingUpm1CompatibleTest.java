package com.atlassian.plugins.codegen.modules.common.licensing;

import com.atlassian.plugins.codegen.AbstractCodegenTestCase;
import com.atlassian.plugins.codegen.BundleInstruction;
import com.atlassian.plugins.codegen.MavenPlugin;
import com.atlassian.plugins.codegen.PluginArtifact;
import com.atlassian.plugins.codegen.PluginParameter;
import com.atlassian.plugins.codegen.SourceFile;

import org.junit.Before;
import org.junit.Test;

import static com.atlassian.plugins.codegen.PluginParameter.pluginParameter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @since 3.8
 */
public class LicensingUpm1CompatibleTest extends AbstractCodegenTestCase<LicensingProperties>
{
    @Before
    public void setupProps()
    {
        setCreator(new LicensingUpm1CompatibleModuleCreator());
        setProps(new LicensingProperties());
    }

    @Test
    public void licensingPluginParamIsAdded() throws Exception
    {
        assertEquals(pluginParameter("atlassian-licensing-enabled", "true"), getChangesetForModule(PluginParameter.class).get(0));
    }
    
    @Test
    public void pluginInstallerComponentIsAdded() throws Exception
    {
        getComponentOfClass(LicensingUpm1CompatibleModuleCreator.PLUGIN_INSTALLER_CLASS);
    }
    
    @Test
    public void licenseStorageManagerComponentIsAdded() throws Exception
    {
        getComponentOfClass(LicensingUpm1CompatibleModuleCreator.LICENSE_STORAGE_MANAGER_CLASS);
    }
    
    @Test
    public void bundleInstructionsAreAdded() throws Exception
    {
        // won't verify all the individual bundle instructions, just make sure we have some
        assertFalse(getChangesetForModule(BundleInstruction.class).isEmpty());
    }

    @Test
    public void bundledArtifactsAreAdded() throws Exception
    {
        // won't verify all the individual bundled artifacts, just make sure we have some
        assertFalse(getChangesetForModule(PluginArtifact.class).isEmpty());
    }
    
    @Test
    public void mavenDependencyPluginIsAdded() throws Exception
    {
        MavenPlugin mp = getChangesetForModule(MavenPlugin.class).get(0);
        assertEquals("maven-dependency-plugin", mp.getGroupAndArtifactId().getCombinedId());
    }
    
    @Test
    public void pluginLicenseStorageLibDependencyIsAdded() throws Exception
    {
        getDependency("com.atlassian.upm", "plugin-license-storage-lib");
    }
    
    @Test
    public void servletModulesAreNotGeneratedByDefault() throws Exception
    {
        assertFalse(hasGeneratedModulesOfType("servlet"));
    }
    
    @Test
    public void classFilesAreNotGeneratedByDefault() throws Exception
    {
        assertTrue(getChangesetForModule(SourceFile.class).isEmpty());
    }
    
    @Test
    public void licenseServletModuleIsAddedIfExamplesAreDesired() throws Exception
    {
        props.setIncludeExamples(true);
        
        assertNotNull(getAllGeneratedModulesOfType("servlet").selectSingleNode("//servlet[@key='license-servlet']"));
    }

    @Test
    public void licenseServletModuleHasClass() throws Exception
    {
        props.setIncludeExamples(true);
        
        assertEquals(LicensingUpm1CompatibleModuleCreator.LICENSE_SERVLET_CLASS.getFullName(),
                     getAllGeneratedModulesOfType("servlet").selectSingleNode("//servlet[@key='license-servlet']/@class").getText());
    }

    @Test
    public void licenseServletModuleHasUrlPattern() throws Exception
    {
        props.setIncludeExamples(true);
        
        assertEquals(LicensingUpm1CompatibleModuleCreator.LICENSE_SERVLET_URL_PATTERN,
                     getAllGeneratedModulesOfType("servlet").selectSingleNode("//servlet[@key='license-servlet']/url-pattern").getText());
    }

    @Test
    public void helloWorldServletModuleIsAddedIfExamplesAreDesired() throws Exception
    {
        props.setIncludeExamples(true);
        
        assertNotNull(getAllGeneratedModulesOfType("servlet").selectSingleNode("//servlet[@key='hello-world-servlet']"));
    }
    
    @Test
    public void helloWorldServletModuleHasClass() throws Exception
    {
        props.setIncludeExamples(true);
        
        assertEquals(LicensingUpm1CompatibleModuleCreator.HELLO_WORLD_SERVLET_CLASS.getFullName(),
                     getAllGeneratedModulesOfType("servlet").selectSingleNode("//servlet[@key='hello-world-servlet']/@class").getText());
    }

    @Test
    public void helloWorldServletModuleHasUrlPattern() throws Exception
    {
        props.setIncludeExamples(true);
        
        assertEquals(LicensingUpm1CompatibleModuleCreator.HELLO_WORLD_SERVLET_URL_PATTERN,
                     getAllGeneratedModulesOfType("servlet").selectSingleNode("//servlet[@key='hello-world-servlet']/url-pattern").getText());
    }
}
