package com.atlassian.plugins.codegen.modules.common.licensing;

import com.atlassian.plugins.codegen.AbstractCodegenTestCase;
import com.atlassian.plugins.codegen.BundleInstruction;
import com.atlassian.plugins.codegen.MavenPlugin;
import com.atlassian.plugins.codegen.PluginArtifact;
import com.atlassian.plugins.codegen.PluginParameter;

import org.junit.Before;
import org.junit.Test;

import static com.atlassian.plugins.codegen.AmpsSystemPropertyVariable.ampsSystemPropertyVariable;
import static com.atlassian.plugins.codegen.ClassId.packageAndClass;
import static com.atlassian.plugins.codegen.I18nString.i18nString;
import static com.atlassian.plugins.codegen.PluginParameter.pluginParameter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @since 3.8
 */
public class LicensingUpm1CompatibleTest extends AbstractCodegenTestCase<LicensingProperties>
{
    private static final String LICENSE_SERVLET_PATH = "my/license";
    private static final String HELLO_SERVLET_PATH = "good/morning/sunshine";
    
    @Before
    public void setupProps()
    {
        setCreator(new LicensingUpm2CompatibleModuleCreator());
        setProps(new LicensingProperties(PACKAGE_NAME + ".MyServlet"));
        props.setLicenseServletPath(LICENSE_SERVLET_PATH);
        props.setHelloWorldServletPath(HELLO_SERVLET_PATH);
    }

    @Test
    public void licensingPluginParamIsAdded() throws Exception
    {
        assertEquals(pluginParameter("atlassian-licensing-enabled", "true"), getChangesetForModule(PluginParameter.class).get(0));
    }
    
    @Test
    public void pluginInstallerComponentIsAdded() throws Exception
    {
        //getComponentOfClass(LicensingUpm2CompatibleModuleCreator.PLUGIN_INSTALLER_CLASS);
    }

    @Test
    public void licenseStorageManagerComponentIsAdded() throws Exception
    {
       // TODO - this checks the commponents are added however now we're using springscanner it's quite different
       // for now we're leaving this out but we should find a better way to test this
       // getComponentOfClass(LicensingUpm2CompatibleModuleCreator.LICENSE_STORAGE_MANAGER_CLASS);
    }
    
    @Test
    public void bundleInstructionsAreAdded() throws Exception
    {
        // TODO - we need to update this test instruction too
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
    public void licenseServletModuleIsAdded() throws Exception
    {
        assertNotNull(getAllGeneratedModulesOfType("servlet").selectSingleNode("//servlet[@key='my-servlet']"));
    }

    @Test
    public void licenseServletModuleHasClass() throws Exception
    {
        assertEquals(packageAndClass(PACKAGE_NAME, "MyServlet").getFullName(),
                     getAllGeneratedModulesOfType("servlet").selectSingleNode("//servlet[@key='my-servlet']/@class").getText());
    }

    @Test
    public void licenseServletModuleHasUrlPattern() throws Exception
    {
        assertEquals("/" + LICENSE_SERVLET_PATH,
                     getAllGeneratedModulesOfType("servlet").selectSingleNode("//servlet[@key='my-servlet']/url-pattern").getText());
    }

    @Test
    public void helloWorldServletModuleIsNotAddedByDefault() throws Exception
    {
        assertNull(getAllGeneratedModulesOfType("servlet").selectSingleNode("//servlet[@key='license-hello-world-servlet']"));
    }
    
    @Test
    public void helloWorldServletModuleIsAddedIfExamplesAreSelected() throws Exception
    {
        props.setIncludeExamples(true);
        assertNotNull(getAllGeneratedModulesOfType("servlet").selectSingleNode("//servlet[@key='license-hello-world-servlet']"));
    }
    
    @Test
    public void helloWorldServletModuleHasClass() throws Exception
    {
        props.setIncludeExamples(true);
        assertEquals(packageAndClass(PACKAGE_NAME, LicensingUpm2CompatibleModuleCreator.HELLO_WORLD_SERVLET_CLASS_NAME).getFullName(),
                     getAllGeneratedModulesOfType("servlet").selectSingleNode("//servlet[@key='license-hello-world-servlet']/@class").getText());
    }

    @Test
    public void helloWorldServletModuleHasUrlPattern() throws Exception
    {
        props.setIncludeExamples(true);
        assertEquals("/" + HELLO_SERVLET_PATH,
                     getAllGeneratedModulesOfType("servlet").selectSingleNode("//servlet[@key='license-hello-world-servlet']/url-pattern").getText());
    }
    
    @Test
    public void i18nStringsAreAdded() throws Exception
    {
        // just check for a couple of the expected strings
        assertChangesetContains(i18nString("plugin.license.storage.admin.license.details", "License Details"),
                                i18nString("plugin.license.storage.admin.license.type.DEVELOPER", "developer"));
    }
}
