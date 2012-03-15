package com.atlassian.plugins.codegen.modules.common.licensing;

import com.atlassian.plugins.codegen.AbstractCodegenTestCase;
import com.atlassian.plugins.codegen.ClassId;
import com.atlassian.plugins.codegen.PluginParameter;

import org.junit.Before;
import org.junit.Test;

import static com.atlassian.plugins.codegen.PluginParameter.pluginParameter;

import static com.atlassian.plugins.codegen.ClassId.fullyQualified;
import static org.junit.Assert.assertEquals;

/**
 * @since 3.8
 */
public class LicensingUpm2Test extends AbstractCodegenTestCase<LicensingProperties>
{
    private static final ClassId LICENSE_CHECKER_CLASS = ClassId.packageAndClass(PACKAGE_NAME, "MyLicenseChecker");
    
    @Before
    public void setupProps()
    {
        setCreator(new LicensingUpm2ModuleCreator());
        setProps(new LicensingProperties(LICENSE_CHECKER_CLASS.getFullName()));
    }

    @Test
    public void licensingPluginParamIsAdded() throws Exception
    {
        assertEquals(pluginParameter("atlassian-licensing-enabled", "true"), getChangesetForModule(PluginParameter.class).get(0));
    }
    
    @Test
    public void licenseCheckerComponentIsAdded() throws Exception
    {
        getComponentOfClass(LICENSE_CHECKER_CLASS);
    }
    
    @Test
    public void licenseCheckerClassIsCreated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, LICENSE_CHECKER_CLASS.getName());
    }
    
    @Test
    public void pluginLicenseManagerImportIsAdded() throws Exception
    {
        getComponentImportOfInterface(fullyQualified("com.atlassian.upm.api.license.PluginLicenseManager"));
    }
    
    @Test
    public void pluginLicenseEventRegistryImportIsAdded() throws Exception
    {
        getComponentImportOfInterface(fullyQualified("com.atlassian.upm.api.license.PluginLicenseEventRegistry"));
    }
    
    @Test
    public void pluginControllerImportIsAdded() throws Exception
    {
        getComponentImportOfInterface(fullyQualified("com.atlassian.plugin.PluginController"));
    }
    
    @Test
    public void upmApiDependencyIsAdded() throws Exception
    {
        getDependency("com.atlassian.upm", "upm-api");
    }
    
    @Test
    public void licensingApiDependencyIsAdded() throws Exception
    {
        getDependency("com.atlassian.upm", "licensing-api");
    }
    
    @Test
    public void pluginsDependencyIsAdded() throws Exception
    {
        getDependency("com.atlassian.plugins", "atlassian-plugins-core");
    }
    
    @Test
    public void salDependencyIsAdded() throws Exception
    {
        getDependency("com.atlassian.sal", "sal-api");
    }
}
