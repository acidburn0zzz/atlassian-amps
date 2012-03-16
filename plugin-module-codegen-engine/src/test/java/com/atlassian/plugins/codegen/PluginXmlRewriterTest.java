package com.atlassian.plugins.codegen;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

import com.atlassian.plugins.codegen.ComponentDeclaration.Visibility;
import com.atlassian.plugins.codegen.modules.PluginModuleLocation;

import com.google.common.collect.ImmutableMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.junit.Before;
import org.junit.Test;

import static com.atlassian.fugue.Option.some;
import static com.atlassian.plugins.codegen.ClassId.fullyQualified;
import static com.atlassian.plugins.codegen.I18nString.i18nString;
import static com.atlassian.plugins.codegen.ModuleDescriptor.moduleDescriptor;
import static com.atlassian.plugins.codegen.PluginParameter.pluginParameter;
import static com.atlassian.plugins.codegen.PluginProjectChangeset.changeset;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class PluginXmlRewriterTest
{
    protected static final String CLASS = "com.atlassian.test.MyClass";
    protected static final String INTERFACE = "com.atlassian.test.MyInterface";
    protected static final ComponentImport IMPORT = ComponentImport.componentImport(fullyQualified(INTERFACE));
     
    protected File tempDir;
    protected File resourcesDir;
    protected File pluginXml;
    protected ComponentDeclaration.Builder componentBuilder = ComponentDeclaration.builder(fullyQualified(CLASS), "my-key");

    protected PluginXmlRewriter rewriter;
    
    @Before
    public void setup() throws Exception
    {
        final File sysTempDir = new File("target");
        String dirName = UUID.randomUUID().toString();
        tempDir = new File(sysTempDir, dirName);
        resourcesDir = new File(tempDir, "resources");
        pluginXml = new File(resourcesDir, "atlassian-plugin.xml");

        tempDir.mkdirs();
        resourcesDir.mkdirs();

        InputStream is = this.getClass().getResourceAsStream("/empty-plugin.xml");
        IOUtils.copy(is, FileUtils.openOutputStream(pluginXml));
        
        rewriter = new PluginXmlRewriter(pluginXml);
    }
    
    @Test
    public void canConstructXmlRewriterFromLocation() throws Exception
    {
        PluginModuleLocation location = new PluginModuleLocation.Builder(new File(tempDir, "src"))
            .resourcesDirectory(resourcesDir)
            .testDirectory(new File(tempDir, "test-src"))
            .build();
        rewriter = new PluginXmlRewriter(location);
        
        // make an arbitrary change
        long oldFileSize = FileUtils.sizeOf(pluginXml);
        rewriter.applyChanges(addPluginParam());
        
        // verify that it found the XML file in its expected location and modified it
        assertTrue(FileUtils.sizeOf(pluginXml) != oldFileSize);
    }
    
    @Test
    public void i18nResourceIsNotAddedByDefault() throws Exception
    {
        Document xml = applyChanges(addPluginParam());
        
        assertEquals(0, xml.selectNodes("//resource").size());
    }

    @Test
    public void i18nResourceIsAddedIfI18nPropertiesArePresent() throws Exception
    {
        Document xml = applyChanges(addI18nProperty());
        
        assertNotNull(xml.selectSingleNode("//resource"));
    }

    @Test
    public void i18nResourceHasType() throws Exception
    {
        Document xml = applyChanges(addI18nProperty());
        
        assertEquals("i18n", xml.selectSingleNode("//resource/@type").getText());
    }

    @Test
    public void i18nResourceHasName() throws Exception
    {
        Document xml = applyChanges(addI18nProperty());
        
        assertEquals("i18n", xml.selectSingleNode("//resource/@name").getText());
    }

    @Test
    public void i18nResourceHasLocation() throws Exception
    {
        Document xml = applyChanges(addI18nProperty());
        
        assertEquals(ProjectRewriter.DEFAULT_I18N_NAME, xml.selectSingleNode("//resource/@location").getText());
    }

    @Test
    public void pluginParamIsAdded() throws Exception
    {
        Document xml = applyChanges(addPluginParam());
        
        assertNotNull(xml.selectSingleNode("//plugin-info/param"));
    }
    
    @Test
    public void pluginParamHasName() throws Exception
    {
        Document xml = applyChanges(addPluginParam());
        
        assertEquals("foo", xml.selectSingleNode("//plugin-info/param/@name").getText());
    }

    @Test
    public void pluginParamHasValue() throws Exception
    {
        Document xml = applyChanges(addPluginParam());
        
        assertEquals("bar", xml.selectSingleNode("//plugin-info/param").getText());
    }

    @Test
    public void secondPluginParamIsAdded() throws Exception
    {
        applyChanges(addPluginParam());
        Document xml = applyChanges(new PluginProjectChangeset().with(pluginParameter("second", "thing")));
        
        assertEquals(2, xml.selectNodes("//plugin-info/param").size());
    }

    @Test
    public void secondPluginParamHasValue() throws Exception
    {
        applyChanges(addPluginParam());
        Document xml = applyChanges(new PluginProjectChangeset().with(pluginParameter("second", "thing")));
        
        assertEquals("thing", xml.selectSingleNode("//plugin-info/param[@name='second']").getText());
    }

    @Test
    public void pluginParamCannotBeOverwritten() throws Exception
    {
        applyChanges(addPluginParam());
        Document xml = applyChanges(new PluginProjectChangeset().with(pluginParameter("foo", "baz")));
        
        assertEquals("bar", xml.selectSingleNode("//plugin-info/param").getText());
    }
    
    @Test
    public void componentImportIsAdded() throws Exception
    {
        Document xml = applyChanges(changeset().with(IMPORT));
        
        assertNotNull(xml.selectSingleNode("//component-import"));
    }

    @Test
    public void componentImportHasInterface() throws Exception
    {
        Document xml = applyChanges(changeset().with(IMPORT));
        
        assertEquals(INTERFACE, xml.selectSingleNode("//component-import/@interface").getText());
    }

    @Test
    public void componentImportHasDefaultKey() throws Exception
    {
        Document xml = applyChanges(changeset().with(IMPORT));
        
        assertEquals("myInterface", xml.selectSingleNode("//component-import/@key").getText());
    }
    
    @Test
    public void componentImportHasSpecifiedKey() throws Exception
    {
        Document xml = applyChanges(changeset().with(IMPORT.key(some("new-key"))));
        
        assertEquals("new-key", xml.selectSingleNode("//component-import/@key").getText());
    }
    
    @Test
    public void componentImportHasNoFilterByDefault() throws Exception
    {
        Document xml = applyChanges(changeset().with(IMPORT));
        
        assertNull(xml.selectSingleNode("//component-import/@filter"));
    }
    
    @Test
    public void componentImportHasSpecifiedFilter() throws Exception
    {
        Document xml = applyChanges(changeset().with(IMPORT.filter(some("my-filter"))));
        
        assertEquals("my-filter", xml.selectSingleNode("//component-import/@filter").getText());
    }
    
    @Test
    public void componentIsAdded() throws Exception
    {
        Document xml = applyChanges(changeset().with(componentBuilder.build()));
        
        assertNotNull(xml.selectSingleNode("//component"));
    }

    @Test
    public void componentHasKey() throws Exception
    {
        Document xml = applyChanges(changeset().with(componentBuilder.build()));
        
        assertEquals("my-key", xml.selectSingleNode("//component/@key").getText());
    }

    @Test
    public void componentHasClass() throws Exception
    {
        Document xml = applyChanges(changeset().with(componentBuilder.build()));
        
        assertEquals(CLASS, xml.selectSingleNode("//component/@class").getText());
    }

    @Test
    public void componentHasNoNameByDefault() throws Exception
    {
        Document xml = applyChanges(changeset().with(componentBuilder.build()));
        
        assertNull(xml.selectSingleNode("//component/@name"));
    }

    @Test
    public void componentHasSpecifiedName() throws Exception
    {
        componentBuilder.name(some("my-name"));
        Document xml = applyChanges(changeset().with(componentBuilder.build()));
        
        assertEquals("my-name", xml.selectSingleNode("//component/@name").getText());
    }

    @Test
    public void componentHasNoNameI18nKeyByDefault() throws Exception
    {
        Document xml = applyChanges(changeset().with(componentBuilder.build()));
        
        assertNull(xml.selectSingleNode("//component/@i18n-name-key"));
    }

    @Test
    public void componentHasSpecifiedNameI18nKey() throws Exception
    {
        componentBuilder.nameI18nKey(some("name-key"));
        Document xml = applyChanges(changeset().with(componentBuilder.build()));
        
        assertEquals("name-key", xml.selectSingleNode("//component/@i18n-name-key").getText());
    }

    @Test
    public void componentIsPrivateByDefault() throws Exception
    {
        Document xml = applyChanges(changeset().with(componentBuilder.build()));
        
        assertNull(xml.selectSingleNode("//component/@public"));
    }

    @Test
    public void componentIsPublicIfSpecified() throws Exception
    {
        componentBuilder.visibility(Visibility.PUBLIC);
        Document xml = applyChanges(changeset().with(componentBuilder.build()));
        
        assertEquals("true", xml.selectSingleNode("//component/@public").getText());
    }

    @Test
    public void componentHasNoAliasByDefault() throws Exception
    {
        Document xml = applyChanges(changeset().with(componentBuilder.build()));
        
        assertNull(xml.selectSingleNode("//component/@alias"));
    }

    @Test
    public void componentHasSpecifiedAlias() throws Exception
    {
        componentBuilder.alias(some("my-alias"));
        Document xml = applyChanges(changeset().with(componentBuilder.build()));
        
        assertEquals("my-alias", xml.selectSingleNode("//component/@alias").getText());
    }

    @Test
    public void componentHasNoDescriptionByDefault() throws Exception
    {
        Document xml = applyChanges(changeset().with(componentBuilder.build()));
        
        assertNull(xml.selectSingleNode("//component/description"));
    }

    @Test
    public void componentHasSpecifiedDescription() throws Exception
    {
        componentBuilder.description(some("desc"));
        Document xml = applyChanges(changeset().with(componentBuilder.build()));
        
        assertEquals("desc", xml.selectSingleNode("//component/description").getText());
    }

    @Test
    public void componentHasNoDescriptionI18nKeyByDefault() throws Exception
    {
        componentBuilder.description(some("desc"));
        Document xml = applyChanges(changeset().with(componentBuilder.build()));
        
        assertNull(xml.selectSingleNode("//component/description/@key"));
    }

    @Test
    public void componentHasSpecifiedDescriptionI18nKey() throws Exception
    {
        componentBuilder.description(some("desc")).descriptionI18nKey(some("desc-key"));
        Document xml = applyChanges(changeset().with(componentBuilder.build()));
        
        assertEquals("desc-key", xml.selectSingleNode("//component/description/@key").getText());
    }

    @Test
    public void componentHasNoInterfaceByDefault() throws Exception
    {
        Document xml = applyChanges(changeset().with(componentBuilder.build()));
        
        assertNull(xml.selectSingleNode("//component/interface"));
    }

    @Test
    public void componentHasSpecifiedInterface() throws Exception
    {
        componentBuilder.interfaceId(some(fullyQualified(INTERFACE)));
        Document xml = applyChanges(changeset().with(componentBuilder.build()));
        
        assertEquals(INTERFACE, xml.selectSingleNode("//component/interface").getText());
    }

    @Test
    public void componentHasNoServicePropertiesByDefault() throws Exception
    {
        Document xml = applyChanges(changeset().with(componentBuilder.build()));
        
        assertNull(xml.selectSingleNode("//component/service-properties"));
    }

    @Test
    public void componentHasSpecifiedServiceProperties() throws Exception
    {
        componentBuilder.serviceProperties(ImmutableMap.of("foo", "bar"));
        Document xml = applyChanges(changeset().with(componentBuilder.build()));
        
        assertEquals("bar", xml.selectSingleNode("//component/service-properties/entry[@key='foo']/@value").getText());
    }

    @Test
    public void moduleDescriptorIsAdded() throws Exception
    {
        String module = "<my-module>has some content</my-module>";
        Document xml = applyChanges(changeset().with(moduleDescriptor(module)));
        
        assertEquals("has some content", xml.selectSingleNode("//my-module").getText());
    }
    
    @Test
    public void moduleDescriptorIsPlacedAfterOtherModuleDescriptorsOfSameElementName() throws Exception
    {
        String module1 = "<foo-module type=\"test\">has some content</foo-module>";
        String module2 = "<bar-module type=\"test\">has some content</bar-module>";
        applyChanges(changeset().with(moduleDescriptor(module1), moduleDescriptor(module2)));
        
        String module3 = "<foo-module type=\"test\">another one</foo-module>";
        Document xml = applyChanges(changeset().with(moduleDescriptor(module3)));
        
        assertEquals("bar-module", ((Node) xml.selectNodes("//*[@type='test']").get(1)).getName());
    }
    
    protected PluginProjectChangeset addPluginParam()
    {
        return new PluginProjectChangeset().with(pluginParameter("foo", "bar"));
    }
    
    protected PluginProjectChangeset addI18nProperty()
    {
        return new PluginProjectChangeset().with(i18nString("foo", "bar"));
    }
    
    protected Document applyChanges(PluginProjectChangeset changes) throws Exception
    {
        rewriter.applyChanges(changes);
        return DocumentHelper.parseText(FileUtils.readFileToString(pluginXml));
    }
}
