package com.atlassian.plugins.codegen.annotations.asm;

import java.util.Map;

import com.atlassian.plugins.codegen.PluginModuleCreatorRegistryImpl;
import com.atlassian.plugins.codegen.modules.PluginModuleCreator;
import com.atlassian.plugins.codegen.modules.PluginModuleCreatorRegistry;

import org.junit.Before;
import org.junit.Test;

import fake.jar.annotation.parser.modules.JARInheritedValidJira;
import fake.jar.annotation.parser.modules.JARJiraAndConfluenceCreator;
import fake.jar.annotation.parser.modules.JARJiraAnnotatedWithoutInterface;
import fake.jar.annotation.parser.modules.JARValidJiraModuleCreator;
import fake.jar.annotation.parser.modules.nested.JARNestedValidJira;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @since 3.6
 */
public class JARAnnotationParserTest
{
    private static final String MODULES_PACKAGE = "fake.jar.annotation.parser.modules";

    private PluginModuleCreatorRegistry registry;
    private ModuleCreatorAnnotationParser parser;

    @Before
    public void setup()
    {
        registry = new PluginModuleCreatorRegistryImpl();
        parser = new ModuleCreatorAnnotationParser(registry);
    }

    @Test
    public void hasJiraModule() throws Exception
    {

        parser.parse(MODULES_PACKAGE);

        Map<Class, PluginModuleCreator> modules = registry.getModuleCreatorsForProduct(PluginModuleCreatorRegistry.JIRA);
        assertNotNull("module map is null", modules);
        assertTrue("no testmodules registered", modules.size() > 0);
        assertTrue("jira module not found", modules.containsKey(JARValidJiraModuleCreator.class));
    }

    @Test
    public void annotatedWithoutInterfaceIsNotRegistered() throws Exception
    {
        parser.parse(MODULES_PACKAGE);

        Map<Class, PluginModuleCreator> modules = registry.getModuleCreatorsForProduct(PluginModuleCreatorRegistry.JIRA);
        assertNotNull("module map is null", modules);
        assertTrue("no testmodules registered", modules.size() > 0);
        assertTrue("non-module found", !modules.containsKey(JARJiraAnnotatedWithoutInterface.class));
    }

    @Test
    public void nestedCreatorsAreRegistered() throws Exception
    {
        parser.parse(MODULES_PACKAGE);

        Map<Class, PluginModuleCreator> modules = registry.getModuleCreatorsForProduct(PluginModuleCreatorRegistry.JIRA);
        assertNotNull("module map is null", modules);
        assertTrue("no testmodules registered", modules.size() > 0);
        assertTrue("nested jira module not found", modules.containsKey(JARNestedValidJira.class));
    }

    @Test
    public void inheritedCreatorsAreRegistered() throws Exception
    {
        parser.parse(MODULES_PACKAGE);

        Map<Class, PluginModuleCreator> modules = registry.getModuleCreatorsForProduct(PluginModuleCreatorRegistry.JIRA);
        assertNotNull("module map is null", modules);
        assertTrue("no testmodules registered", modules.size() > 0);
        assertTrue("inherited jira module not found", modules.containsKey(JARInheritedValidJira.class));
    }

    @Test
    public void muiltipleProductsHaveSameCreator() throws Exception
    {
        parser.parse(MODULES_PACKAGE);

        Map<Class, PluginModuleCreator> jiraModules = registry.getModuleCreatorsForProduct(PluginModuleCreatorRegistry.JIRA);
        Map<Class, PluginModuleCreator> confluenceModules = registry.getModuleCreatorsForProduct(PluginModuleCreatorRegistry.CONFLUENCE);

        assertTrue("jiraAndConfluence not found for jira", jiraModules.containsKey(JARJiraAndConfluenceCreator.class));
        assertTrue("jiraAndConfluence not found for confluence", confluenceModules.containsKey(JARJiraAndConfluenceCreator.class));
    }
}
