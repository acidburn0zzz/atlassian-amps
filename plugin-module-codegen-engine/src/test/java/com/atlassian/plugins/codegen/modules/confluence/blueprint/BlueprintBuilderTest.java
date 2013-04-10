package com.atlassian.plugins.codegen.modules.confluence.blueprint;

import com.atlassian.plugins.codegen.modules.AbstractNameBasedModuleProperties;
import com.atlassian.plugins.codegen.modules.common.Resource;
import com.atlassian.plugins.codegen.modules.common.web.WebItemProperties;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintPromptEntry.*;
import static com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintProperties.WEB_ITEM_BLUEPRINT_KEY;
import static com.atlassian.plugins.codegen.modules.confluence.blueprint.ContentTemplateProperties.CONTENT_I18N_DEFAULT_VALUE;
import static org.junit.Assert.assertEquals;

/**
 * Tests that the builder converts a simple Properties object returned by the Prompter into a complete
 * BlueprintProperties object.
 *
 * @since 4.1.7
 */
public class BlueprintBuilderTest
{
    private String webItemName = "FooPrint";
    private String webItemDesc = "There's no Blueprint like my FooPrint.";
    private String templateModuleKey = "foo-plate";
    private String blueprintIndexKey = "foo-print";
    private String blueprintModuleKey = blueprintIndexKey + "-blueprint";

    private BlueprintBuilder builder;
    private Map<BlueprintPromptEntry, Object> promptProps;
    private List<String> contentTemplateKeys;

    @Before
    public void setup()
    {
        promptProps = Maps.newHashMap();

        promptProps.put(INDEX_KEY, blueprintIndexKey);
        promptProps.put(WEB_ITEM_NAME, webItemName);
        promptProps.put(WEB_ITEM_DESC, webItemDesc);
        promptProps.put(HOW_TO_USE, false);

        contentTemplateKeys = Lists.newArrayList();
        contentTemplateKeys.add(templateModuleKey);
        promptProps.put(CONTENT_TEMPLATE_KEYS, contentTemplateKeys);

        builder = new BlueprintBuilder(promptProps);
    }

    @Test
    public void basicPropertiesAreValid()
    {
        // Create expected Properties objects that the prompter should return.
        BlueprintProperties expectedProps = new BlueprintProperties();
        expectedProps.setModuleKey(blueprintModuleKey);
        expectedProps.setModuleName("FooPrint Blueprint");
        expectedProps.setIndexKey(blueprintIndexKey);
        expectedProps.setWebItem(makeExpectedWebItemProperties());
        expectedProps.addContentTemplate(makeExpectedContentTemplateProperties());

        BlueprintProperties props = builder.build();

        // Assert that all of the things are good things.
        assertBlueprintProperties(expectedProps, props);
        assertWebItemProperties(expectedProps.getWebItem(), props.getWebItem());
        assertContentTemplatePropertiesEqual(expectedProps.getContentTemplates(), props.getContentTemplates());
    }

    @Test
    public void howToUseTemplateAdded()
    {
        promptProps.put(HOW_TO_USE, true);

        BlueprintProperties props = builder.build();

        String expectedHowToUse = "Confluence.Blueprints.Plugin.FooPrint.howToUse";
        assertEquals(expectedHowToUse, props.getHowToUseTemplate());
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
