package com.atlassian.maven.plugins.amps.codegen.prompter.confluence.blueprint;

import com.atlassian.maven.plugins.amps.codegen.prompter.AbstractPrompterTest;
import com.atlassian.maven.plugins.amps.codegen.prompter.PluginModulePrompter;
import com.atlassian.plugins.codegen.modules.AbstractNameBasedModuleProperties;
import com.atlassian.plugins.codegen.modules.common.Resource;
import com.atlassian.plugins.codegen.modules.common.web.WebItemProperties;
import com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintProperties;
import com.atlassian.plugins.codegen.modules.confluence.blueprint.ContentTemplateProperties;
import com.google.common.collect.Lists;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.junit.Test;

import java.util.List;

import static com.atlassian.maven.plugins.amps.codegen.prompter.confluence.blueprint.BlueprintPrompter.*;
import static com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintProperties.WEB_ITEM_BLUEPRINT_KEY;
import static com.atlassian.plugins.codegen.modules.confluence.blueprint.ContentTemplateProperties.CONTENT_I18N_DEFAULT_VALUE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @since 4.1.7
 */
public class BlueprintPrompterTest extends AbstractPrompterTest
{
    private String webItemName = "FooPrint";
    private String webItemDesc = "There's no Blueprint like my FooPrint.";
    private String templateModuleKey = "foo-plate";
    private String blueprintIndexKey = "foo-print";
    private String blueprintModuleKey = blueprintIndexKey + "-blueprint";

    @Test
    public void basicPropertiesAreValid() throws PrompterException
    {
        BlueprintPrompter modulePrompter = new BlueprintPrompter(prompter);
        modulePrompter.setUseAnsiColor(false);

        // Create expected Properties objects that the prompter should return.
        BlueprintProperties expectedProps = new BlueprintProperties();
        expectedProps.setModuleKey(blueprintModuleKey);
        expectedProps.setModuleName("FooPrint Blueprint");
        expectedProps.setIndexKey(blueprintIndexKey);
        expectedProps.setWebItem(makeExpectedWebItemProperties());
        expectedProps.addContentTemplate(makeExpectedContentTemplateProperties());

        // Fake the User input at the prompt.
        mockPromptResponses();

        // The magic happens here!
        BlueprintProperties props = modulePrompter.getModulePropertiesFromInput(moduleLocation);

        // Assert that all of the things are good things.
        assertBlueprintProperties(expectedProps, props);
        assertWebItemProperties(expectedProps.getWebItem(), props.getWebItem());
        assertContentTemplatePropertiesEqual(expectedProps.getContentTemplates(), props.getContentTemplates());
    }

    @Test
    public void howToUseTemplateAdded() throws PrompterException
    {
        BlueprintPrompter modulePrompter = new BlueprintPrompter(prompter);
        modulePrompter.setUseAnsiColor(false);

        // Fake the User input at the prompt.
        mockPromptResponses();
        when(prompter.prompt(ADVANCED_BLUEPRINT_PROMPT, PluginModulePrompter.YN_ANSWERS, "N")).thenReturn("Y");
        when(prompter.prompt(HOW_TO_USE_PROMPT, PluginModulePrompter.YN_ANSWERS, "N")).thenReturn("Y");

        // The magic happens here!
        BlueprintProperties props = modulePrompter.getModulePropertiesFromInput(moduleLocation);

        String expectedHowToUse = "Confluence.Blueprints.Plugin.FooPrint.howToUse";
        assertEquals(expectedHowToUse, props.getHowToUseTemplate());
    }

    private void mockPromptResponses() throws PrompterException
    {
        when(prompter.prompt(INDEX_KEY_PROMPT, INDEX_KEY_DEFAULT)).thenReturn(blueprintIndexKey);
        when(prompter.prompt(WEB_ITEM_NAME_PROMPT, WEB_ITEM_NAME_DEFAULT)).thenReturn(webItemName);
        when(prompter.prompt(WEB_ITEM_DESC_PROMPT, WEB_ITEM_DESC_DEFAULT)).thenReturn(webItemDesc);
        when(prompter.prompt(CONTENT_TEMPLATE_KEY_PROMPT, blueprintIndexKey + "-template")).thenReturn(templateModuleKey);
        when(prompter.prompt(ANOTHER_CONTENT_TEMPLATE_KEY_PROMPT, PluginModulePrompter.YN_ANSWERS, "N")).thenReturn("N");
        when(prompter.prompt(ADVANCED_BLUEPRINT_PROMPT, PluginModulePrompter.YN_ANSWERS, "N")).thenReturn("N");
    }

    private void assertContentTemplatePropertiesEqual(List<ContentTemplateProperties> expectedTemplates,
        List<ContentTemplateProperties> actualTemplates)
    {
        assertEquals("Wrong number of content templates", expectedTemplates.size(), actualTemplates.size());
        for (int i = 0; i < expectedTemplates.size(); i++)
        {
            ContentTemplateProperties expected = expectedTemplates.get(i);
            ContentTemplateProperties actual = actualTemplates.get(i);

            assertNameBasedModulesEqual(expected, actual);
            assertEquals(expected.getContentText(), actual.getContentText());

            assertResourcesEqual(expected.getResources(), actual.getResources());
        }
    }

    private void assertNameBasedModulesEqual(AbstractNameBasedModuleProperties expected,
        AbstractNameBasedModuleProperties actual)
    {
        assertEquals(expected.getModuleKey(), actual.getModuleKey());
        assertEquals(expected.getNameI18nKey(), actual.getNameI18nKey());
        assertEquals(expected.getModuleName(), actual.getModuleName());
        assertEquals(expected.getDescriptionI18nKey(), actual.getDescriptionI18nKey());
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    private void assertResourcesEqual(List<Resource> expectedResources, List<Resource> actualResources)
    {
        assertEquals("Wrong number of Resources", expectedResources.size(), actualResources.size());
        for (int i = 0; i < expectedResources.size(); i++)
        {
            Resource expected = expectedResources.get(i);
            Resource actual = actualResources.get(i);

            assertEquals(expected.getName(), actual.getName());
            assertEquals(expected.getType(), actual.getType());
            assertEquals(expected.getLocation(), actual.getLocation());
        }
    }

    private void assertWebItemProperties(WebItemProperties expected, WebItemProperties actual)
    {
        assertNameBasedModulesEqual(expected, actual);
        assertEquals(expected.getSection(), actual.getSection());
        assertResourcesEqual(expected.getResources(), actual.getResources());
        assertEquals(expected.getParam(WEB_ITEM_BLUEPRINT_KEY), actual.getParam(WEB_ITEM_BLUEPRINT_KEY));
    }

    private void assertBlueprintProperties(BlueprintProperties expected, BlueprintProperties actual)
    {
        assertEquals("Wrong module key", expected.getModuleKey(), actual.getModuleKey());
        assertEquals("Wrong module name", expected.getModuleName(), actual.getModuleName());
        assertEquals("Wrong index key", expected.getIndexKey(), actual.getIndexKey());
        assertEquals("Wrong how-to-use template", expected.getHowToUseTemplate(), actual.getHowToUseTemplate());

        assertNameBasedModulesEqual(expected, actual);
    }

    private WebItemProperties makeExpectedWebItemProperties()
    {
        WebItemProperties webItem = new WebItemProperties();
        webItem.setModuleKey("foo-print-blueprint-web-item");
        webItem.setNameI18nKey("foo-print-blueprint.display.name");
        webItem.setModuleName(webItemName);
        webItem.setDescriptionI18nKey("foo-print-blueprint.display.desc");
        webItem.setDescription(webItemDesc);
        webItem.setSection("system.create.dialog/content");

        Resource webItemResource = new Resource();
        webItemResource.setName("icon");
        webItemResource.setType("download");
        webItemResource.setLocation("images/foo-print-blueprint-icon.png");
        List<Resource> resources = Lists.newArrayList(webItemResource);
        webItem.setResources(resources);
        webItem.addParam(WEB_ITEM_BLUEPRINT_KEY, blueprintModuleKey);
        return webItem;
    }

    private ContentTemplateProperties makeExpectedContentTemplateProperties()
    {
        // TODO - should the ContentTemplateProperties accept the plugin/package and attempt to generate the
        // i18n keys itself? If so, thta logic would be tested in the Properties object (or a "generator" helper for it).
        // TODO - should probably contact Jonathan Dok for this.
        ContentTemplateProperties template = new ContentTemplateProperties(templateModuleKey);
        template.setNameI18nKey("foo-plate.name");
        template.setModuleName("FooPrint Content Template 0");
        template.setDescriptionI18nKey("foo-plate.desc");
        template.setDescription("Contains Storage-format XML used by the FooPrint Blueprint");
        template.setContentText(templateModuleKey + ".content.text", CONTENT_I18N_DEFAULT_VALUE);

        Resource templateResource = new Resource();
        templateResource.setName("template");
        templateResource.setType("download");
        templateResource.setLocation("xml/" + templateModuleKey + ".xml");
        template.addResource(templateResource);
        return template;
    }
}
