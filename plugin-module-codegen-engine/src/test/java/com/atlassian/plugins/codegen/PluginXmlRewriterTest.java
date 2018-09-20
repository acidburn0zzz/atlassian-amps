package com.atlassian.plugins.codegen;

import com.atlassian.plugins.codegen.ComponentDeclaration.Visibility;
import com.atlassian.plugins.codegen.XmlMatchers.XmlWrapper;

import com.google.common.collect.ImmutableMap;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.atlassian.plugins.codegen.ClassId.fullyQualified;
import static com.atlassian.plugins.codegen.I18nString.i18nString;
import static com.atlassian.plugins.codegen.ModuleDescriptor.moduleDescriptor;
import static com.atlassian.plugins.codegen.PluginParameter.pluginParameter;
import static com.atlassian.plugins.codegen.PluginProjectChangeset.changeset;
import static com.atlassian.plugins.codegen.XmlMatchers.node;
import static com.atlassian.plugins.codegen.XmlMatchers.nodeCount;
import static com.atlassian.plugins.codegen.XmlMatchers.nodeName;
import static com.atlassian.plugins.codegen.XmlMatchers.nodeTextEquals;
import static com.atlassian.plugins.codegen.XmlMatchers.nodes;
import static io.atlassian.fugue.Option.some;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class PluginXmlRewriterTest
{
    protected static final String CLASS = "com.atlassian.test.MyClass";
    protected static final String INTERFACE = "com.atlassian.test.MyInterface";
    protected static final String INTERFACE2 = "com.atlassian.test.MyInterface2";
    protected static final ComponentImport IMPORT = ComponentImport.componentImport(fullyQualified(INTERFACE));
    protected static final ComponentImport IMPORT2 = ComponentImport.componentImport(fullyQualified(INTERFACE2));
    
    protected ComponentDeclaration.Builder componentBuilder = ComponentDeclaration.builder(fullyQualified(CLASS), "my-key");

    protected ProjectHelper helper;
    protected PluginXmlRewriter rewriter;
    
    @Before
    public void setup() throws Exception
    {
        helper = new ProjectHelper();
        usePluginXml("empty-plugin.xml");
    }
    
    private void usePluginXml(String path) throws Exception
    {
        helper.usePluginXml(path);
        rewriter = new PluginXmlRewriter(helper.location);
    }
    
    @After
    public void deleteTempDir() throws Exception
    {
        helper.destroy();
    }
    
    @Test
    public void i18nResourceIsNotAddedByDefault() throws Exception
    {
        assertThat(applyChanges(addPluginParam()),
                   nodes("//resource[@type='i18n']", nodeCount(0)));
    }

    @Test
    public void i18nResourceIsAddedIfI18nPropertiesArePresent() throws Exception
    {
        assertThat(applyChanges(addI18nProperty()),
                   nodes("//resource[@type='i18n']", nodeCount(1)));
    }

    @Test
    public void i18nResourceHasDefaultName() throws Exception
    {
        assertThat(applyChanges(addI18nProperty()),
                   node("//resource[@type='i18n']/@name", nodeTextEquals("i18n")));
    }

    @Test
    public void i18nResourceHasDefaultLocation() throws Exception
    {
        assertThat(applyChanges(addI18nProperty()),
                   node("//resource[@type='i18n']/@location", nodeTextEquals(ProjectHelper.PLUGIN_KEY)));
    }

    @Test
    public void i18nResourceWithSameNameIsNotAdded() throws Exception
    {
        usePluginXml("plugin-with-same-i18n-name.xml");
        
        assertThat(applyChanges(addI18nProperty()),
                   nodes("//resource[@type='i18n']", nodeCount(1)));
    }
    
    @Test
    public void i18nResourceWithSameNameIsNotOverwritten() throws Exception
    {
        usePluginXml("plugin-with-same-i18n-name.xml");

        assertThat(applyChanges(addI18nProperty()),
                   node("//resource[@type='i18n']/@location", nodeTextEquals("nonstandard-location")));
    }

    @Test
    public void i18nResourceWithSameLocationIsNotAdded() throws Exception
    {
        usePluginXml("plugin-with-same-i18n-location.xml");
        
        assertThat(applyChanges(addI18nProperty()),
                   nodes("//resource[@type='i18n']", nodeCount(1)));
    }
    
    @Test
    public void i18nResourceWithSameLocationIsNotOverwritten() throws Exception
    {
        usePluginXml("plugin-with-same-i18n-location.xml");

        assertThat(applyChanges(addI18nProperty()),
                   node("//resource[@type='i18n']/@name", nodeTextEquals("nonstandard-name")));
    }
    
    @Test
    public void pluginParamIsAdded() throws Exception
    {
        assertThat(applyChanges(addPluginParam()),
                   nodes("//plugin-info/param", nodeCount(1)));
    }
    
    @Test
    public void pluginParamHasName() throws Exception
    {
        assertThat(applyChanges(addPluginParam()),
                   node("//plugin-info/param/@name", nodeTextEquals("foo")));
    }

    @Test
    public void pluginParamHasValue() throws Exception
    {
        assertThat(applyChanges(addPluginParam()),
                   node("//plugin-info/param", nodeTextEquals("bar")));
    }

    @Test
    public void secondPluginParamIsAdded() throws Exception
    {
        applyChanges(addPluginParam());

        assertThat(applyChanges(changeset().with(pluginParameter("second", "thing"))),
                   nodes("//plugin-info/param", nodeCount(2)));
    }

    @Test
    public void secondPluginParamHasValue() throws Exception
    {
        applyChanges(addPluginParam());

        assertThat(applyChanges(changeset().with(pluginParameter("second", "thing"))),
                   node("//plugin-info/param[@name='second']", nodeTextEquals("thing")));
    }

    @Test
    public void duplicatePluginParamIsNotAdded() throws Exception
    {
        applyChanges(addPluginParam());

        assertThat(applyChanges(changeset().with(pluginParameter("foo", "baz"))),
                   nodes("//plugin-info/param", nodeCount(1)));
    }

    @Test
    public void pluginParamCannotBeOverwritten() throws Exception
    {
        applyChanges(addPluginParam());

        assertThat(applyChanges(changeset().with(pluginParameter("second", "thing"))),
                   node("//plugin-info/param", nodeTextEquals("bar")));
    }
    
    @Test
    public void componentImportIsAdded() throws Exception
    {
        assertThat(applyChanges(changeset().with(IMPORT)),
                   node("//component-import", notNullValue()));
    }

    @Test
    public void componentImportHasInterface() throws Exception
    {
        assertThat(applyChanges(changeset().with(IMPORT)),
                   node("//component-import/@interface", nodeTextEquals(INTERFACE)));
    }

    @Test
    public void componentImportHasDefaultKey() throws Exception
    {
        assertThat(applyChanges(changeset().with(IMPORT)),
                   node("//component-import/@key", nodeTextEquals("myInterface")));
    }
    
    @Test
    public void componentImportHasSpecifiedKey() throws Exception
    {
        assertThat(applyChanges(changeset().with(IMPORT.key(some("new-key")))),
                   node("//component-import/@key", nodeTextEquals("new-key")));
    }
    
    @Test
    public void componentImportHasNoFilterByDefault() throws Exception
    {
        assertThat(applyChanges(changeset().with(IMPORT)),
                   node("//component-import/@filter", nullValue()));
    }
    
    @Test
    public void componentImportHasSpecifiedFilter() throws Exception
    {
        assertThat(applyChanges(changeset().with(IMPORT.filter(some("my-filter")))),
                   node("//component-import/@filter", nodeTextEquals("my-filter")));
    }
    
    @Test
    public void duplicateComponentImportKeyCannotBeAdded() throws Exception
    {
        applyChanges(changeset().with(IMPORT.key(some("my-key"))));

        assertThat(applyChanges(changeset().with(IMPORT2.key(some("my-key")))),
                   nodes("//component-import", nodeCount(1)));
    }

    @Test
    public void componentImportWithSameKeyIsNotOverwritten() throws Exception
    {
        applyChanges(changeset().with(IMPORT.key(some("my-key"))));

        assertThat(applyChanges(changeset().with(IMPORT2.key(some("my-key")))),
                   node("//component-import/@interface", nodeTextEquals(INTERFACE)));
    }
    
    @Test
    public void componentWithSameInterfaceCannotBeAdded() throws Exception
    {
        applyChanges(changeset().with(IMPORT.key(some("key1"))));

        assertThat(applyChanges(changeset().with(IMPORT.key(some("key2")))),
                   nodes("//component-import", nodeCount(1)));
    }

    @Test
    public void componentWithSameInterfaceInSubElementCannotBeAdded() throws Exception
    {
        usePluginXml("plugin-with-import-interface-element.xml");

        assertThat(applyChanges(changeset().with(IMPORT.key(some("key2")))),
                   nodes("//component-import", nodeCount(1)));
    }
    
    @Test
    public void componentCannotBeAddedIfAlternateInterfaceIsAlreadyUsed() throws Exception
    {
        applyChanges(changeset().with(IMPORT.key(some("key1"))));

        assertThat(applyChanges(changeset().with(IMPORT2.alternateInterfaces(fullyQualified(INTERFACE)).key(some("key2")))),
                   nodes("//component-import", nodeCount(1)));
    }
    
    @Test
    public void componentIsAdded() throws Exception
    {
        assertThat(applyChanges(changeset().with(componentBuilder.build())),
                   node("//component", notNullValue()));
    }

    @Test
    public void componentHasKey() throws Exception
    {
        assertThat(applyChanges(changeset().with(componentBuilder.build())),
                   node("//component/@key", nodeTextEquals("my-key")));
    }

    @Test
    public void componentHasClass() throws Exception
    {
        assertThat(applyChanges(changeset().with(componentBuilder.build())),
                   node("//component/@class", nodeTextEquals(CLASS)));
    }

    @Test
    public void componentHasNoNameByDefault() throws Exception
    {
        assertThat(applyChanges(changeset().with(componentBuilder.build())),
                   node("//component/@name", nullValue()));
    }

    @Test
    public void componentHasSpecifiedName() throws Exception
    {
        componentBuilder.name(some("my-name"));
        
        assertThat(applyChanges(changeset().with(componentBuilder.build())),
                   node("//component/@name", nodeTextEquals("my-name")));
    }

    @Test
    public void componentHasNoNameI18nKeyByDefault() throws Exception
    {
        assertThat(applyChanges(changeset().with(componentBuilder.build())),
                   node("//component/@i18n-name-key", nullValue()));
    }

    @Test
    public void componentHasSpecifiedNameI18nKey() throws Exception
    {
        componentBuilder.nameI18nKey(some("name-key"));
        
        assertThat(applyChanges(changeset().with(componentBuilder.build())),
                   node("//component/@i18n-name-key", nodeTextEquals("name-key")));
    }

    @Test
    public void componentIsPrivateByDefault() throws Exception
    {
        assertThat(applyChanges(changeset().with(componentBuilder.build())),
                   node("//component/@public", nullValue()));
    }

    @Test
    public void componentIsPublicIfSpecified() throws Exception
    {
        componentBuilder.visibility(Visibility.PUBLIC);
        
        assertThat(applyChanges(changeset().with(componentBuilder.build())),
                   node("//component/@public", nodeTextEquals("true")));
    }

    @Test
    public void componentHasNoAliasByDefault() throws Exception
    {
        assertThat(applyChanges(changeset().with(componentBuilder.build())),
                   node("//component/@alias", nullValue()));
    }

    @Test
    public void componentHasSpecifiedAlias() throws Exception
    {
        componentBuilder.alias(some("my-alias"));
        
        assertThat(applyChanges(changeset().with(componentBuilder.build())),
                   node("//component/@alias", nodeTextEquals("my-alias")));
    }

    @Test
    public void componentHasNoDescriptionByDefault() throws Exception
    {
        assertThat(applyChanges(changeset().with(componentBuilder.build())),
                   node("//component/description", nullValue()));
    }

    @Test
    public void componentHasSpecifiedDescription() throws Exception
    {
        componentBuilder.description(some("desc"));
        
        assertThat(applyChanges(changeset().with(componentBuilder.build())),
                   node("//component/description", nodeTextEquals("desc")));
    }

    @Test
    public void componentHasNoDescriptionI18nKeyByDefault() throws Exception
    {
        componentBuilder.description(some("desc"));
        
        assertThat(applyChanges(changeset().with(componentBuilder.build())),
                   node("//component/description/@key", nullValue()));
    }

    @Test
    public void componentHasSpecifiedDescriptionI18nKey() throws Exception
    {
        componentBuilder.description(some("desc")).descriptionI18nKey(some("desc-key"));
        
        assertThat(applyChanges(changeset().with(componentBuilder.build())),
                   node("//component/description/@key", nodeTextEquals("desc-key")));
    }

    @Test
    public void componentHasNoInterfaceByDefault() throws Exception
    {
        assertThat(applyChanges(changeset().with(componentBuilder.build())),
                   node("//component/interface", nullValue()));
    }

    @Test
    public void componentHasSpecifiedInterface() throws Exception
    {
        componentBuilder.interfaceId(some(fullyQualified(INTERFACE)));
        
        assertThat(applyChanges(changeset().with(componentBuilder.build())),
                   node("//component/interface", nodeTextEquals(INTERFACE)));
    }

    @Test
    public void componentHasNoServicePropertiesByDefault() throws Exception
    {
        assertThat(applyChanges(changeset().with(componentBuilder.build())),
                   node("//component/service-properties", nullValue()));
    }

    @Test
    public void componentHasSpecifiedServiceProperties() throws Exception
    {
        componentBuilder.serviceProperties(ImmutableMap.of("foo", "bar"));
        
        assertThat(applyChanges(changeset().with(componentBuilder.build())),
                   node("//component/service-properties/entry[@key='foo']/@value", nodeTextEquals("bar")));
    }

    @Test
    public void duplicateComponentKeyCannotBeAdded() throws Exception
    {
        ComponentDeclaration firstComponent = componentBuilder.name(some("first")).build();
        ComponentDeclaration secondComponentWithSameKey = componentBuilder.name(some("different")).build();
        applyChanges(changeset().with(firstComponent));
        
        assertThat(applyChanges(changeset().with(secondComponentWithSameKey)),
                   nodes("//component", nodeCount(1)));
    }

    @Test
    public void componentWithSameKeyIsNotOverwritten() throws Exception
    {
        ComponentDeclaration firstComponent = componentBuilder.name(some("first")).build();
        ComponentDeclaration secondComponentWithSameKey = componentBuilder.name(some("different")).build();
        applyChanges(changeset().with(firstComponent));
        
        assertThat(applyChanges(changeset().with(secondComponentWithSameKey)),
                   node("//component/@name", nodeTextEquals("first")));
    }
    
    @Test
    public void moduleDescriptorIsAdded() throws Exception
    {
        String module = "<my-module>has some content</my-module>";
        assertThat(applyChanges(changeset().with(moduleDescriptor(module))),
                   node("//my-module", nodeTextEquals("has some content")));
    }
    
    @Test
    public void moduleDescriptorIsPlacedAfterOtherModuleDescriptorsOfSameElementName() throws Exception
    {
        String module1 = "<foo-module type=\"test\">has some content</foo-module>";
        String module2 = "<bar-module type=\"test\">has some content</bar-module>";
        applyChanges(changeset().with(moduleDescriptor(module1), moduleDescriptor(module2)));
        
        String module3 = "<foo-module type=\"test\">another one</foo-module>";
        assertThat(applyChanges(changeset().with(moduleDescriptor(module3))),
                   node("//*[@type='test'][2]", nodeName(equalTo("bar-module"))));
    }
    
    @Test
    public void moduleDescriptorWithSameTypeAndKeyCannotBeAdded() throws Exception
    {
        String module1 = "<my-module key=\"my-key\">good</my-module>";
        String module2 = "<my-module key=\"my-key\">worse</my-module>";
        applyChanges(changeset().with(moduleDescriptor(module1)));
        
        assertThat(applyChanges(changeset().with(moduleDescriptor(module2))),
                   nodes("//my-module", nodeCount(1)));
    }

    @Test
    public void moduleDescriptorWithSameTypeAndKeyIsNotOverwritten() throws Exception
    {
        String module1 = "<my-module key=\"my-key\">good</my-module>";
        String module2 = "<my-module key=\"my-key\">worse</my-module>";
        applyChanges(changeset().with(moduleDescriptor(module1)));
        
        assertThat(applyChanges(changeset().with(moduleDescriptor(module2))),
                   node("//my-module", nodeTextEquals("good")));
    }
    
    protected PluginProjectChangeset addPluginParam()
    {
        return new PluginProjectChangeset().with(pluginParameter("foo", "bar"));
    }
    
    protected PluginProjectChangeset addI18nProperty()
    {
        return new PluginProjectChangeset().with(i18nString("foo", "bar"));
    }
    
    protected XmlWrapper applyChanges(PluginProjectChangeset changes) throws Exception
    {
        rewriter.applyChanges(changes);
        return XmlMatchers.xml(FileUtils.readFileToString(helper.pluginXml));
    }
}
