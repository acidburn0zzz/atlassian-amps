package com.atlassian.plugins.codegen.modules.confluence.blueprint;

import com.atlassian.plugins.codegen.modules.AbstractNameBasedModuleProperties;
import com.atlassian.plugins.codegen.modules.common.Resource;
import com.atlassian.plugins.codegen.modules.common.web.WebItemProperties;
import com.atlassian.plugins.codegen.modules.common.web.WebResourceProperties;
import com.atlassian.plugins.codegen.modules.common.web.WebResourceTransformation;
import com.atlassian.plugins.codegen.modules.common.web.WebResourceTransformer;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintPromptEntry.*;
import static com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintProperties.*;
import static com.atlassian.plugins.codegen.modules.confluence.blueprint.ContentTemplateProperties.CONTENT_I18N_DEFAULT_VALUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

/**
 * Tests that the builder converts a simple Properties object returned by the Prompter into a complete
 * BlueprintProperties object.
 *
 * @since 4.1.8
 */
public class BlueprintBuilderTest
{
    public static final String PLUGIN_KEY = "com.atlassian.confluence.plugins.fooprint";

    private String webItemName = "FooPrint";
    private String webItemDesc = "There's no Blueprint like my FooPrint.";
    private String templateModuleKey = "foo-plate";
    private String blueprintIndexKey = "foo-print";
    private String blueprintModuleKey = blueprintIndexKey + "-blueprint";

    private BlueprintBuilder builder;
    private BlueprintPromptEntries promptProps;

    private BlueprintStringer stringer = new BlueprintStringer(blueprintIndexKey, PLUGIN_KEY);

    @Before
    public void setup()
    {
        promptProps = new BlueprintPromptEntries(PLUGIN_KEY);

        promptProps.put(INDEX_KEY_PROMPT, blueprintIndexKey);
        promptProps.put(WEB_ITEM_NAME_PROMPT, webItemName);
        promptProps.put(WEB_ITEM_DESC_PROMPT, webItemDesc);

        List<String> contentTemplateKeys = Lists.newArrayList();
        contentTemplateKeys.add(templateModuleKey);
        promptProps.put(CONTENT_TEMPLATE_KEYS_PROMPT, contentTemplateKeys);

        builder = new BlueprintBuilder(promptProps);
    }

    @Test
    public void basicPropertiesAreValid()
    {
        BlueprintProperties props = builder.build();

        assertThat(props.getPluginKey(), is(PLUGIN_KEY));

        // Create expected Properties objects that the prompter should return.
        BlueprintProperties expectedProps = new BlueprintProperties();
        expectedProps.setModuleKey(blueprintModuleKey);
        expectedProps.setModuleName("FooPrint Blueprint");
        expectedProps.setIndexKey(blueprintIndexKey);
        expectedProps.setWebItem(makeExpectedWebItemProperties());
        expectedProps.addContentTemplate(makeExpectedContentTemplateProperties());
        expectedProps.setIndexTitleI18nKey("com.atlassian.confluence.plugins.fooprint.index.page.title");

        // Assert that all of the things are good things.
        assertBlueprintProperties(expectedProps, props);
        assertWebItemProperties(expectedProps.getWebItem(), props.getWebItem());
        assertContentTemplatePropertiesEqual(expectedProps.getContentTemplates(), props.getContentTemplates());
    }

    @Test
    public void howToUseTemplateAdded()
    {
        promptProps.put(HOW_TO_USE_PROMPT, true);
        BlueprintProperties props = builder.build();

        WebResourceProperties expectedWebResource = newExpectedWebResourceProperties(blueprintIndexKey);
        addSoyTemplateToExpectedWebResource(expectedWebResource);
        addHowToUseToExpectedSoyResource(expectedWebResource);

        assertEquals("Confluence.Blueprints.Plugin.FooPrint.howToUse", props.getHowToUseTemplate());
        assertWebResourceProperties(expectedWebResource, props.getWebResource());
    }

    @Test
    public void editorIsSkipped() throws Exception
    {
        promptProps.put(SKIP_PAGE_EDITOR_PROMPT, true);
        BlueprintProperties props = builder.build();

        assertThat(props.getCreateResult(), is(BlueprintProperties.CREATE_RESULT_VIEW));
    }

    @Test
    public void dialogWizardAdded()
    {
        promptProps.put(DIALOG_WIZARD_PROMPT, true);
        BlueprintProperties props = builder.build();

        DialogWizardProperties expectedWizard = new DialogWizardProperties();
        expectedWizard.setModuleKey("foo-print-wizard");
        List<DialogPageProperties> pages = Lists.newArrayList();
        DialogPageProperties page = new DialogPageProperties(0, "Confluence.Blueprints.Plugin.FooPrint", stringer);
        pages.add(page);
        expectedWizard.setDialogPages(pages);

        assertDialogWizard(expectedWizard, props.getDialogWizard());

        WebResourceProperties expectedWebResource = newExpectedWebResourceProperties(blueprintIndexKey);
        addJsToExpectedWebResource(expectedWebResource);
        addSoyTemplateToExpectedWebResource(expectedWebResource); // order of addition is important to assertions

        assertWebResourceProperties(expectedWebResource, props.getWebResource());

        assertThat(props.getWebResource().getProperty(BlueprintProperties.PLUGIN_KEY), is(PLUGIN_KEY));
        assertThat(props.getWebResource().getProperty(BlueprintProperties.WEB_ITEM_KEY), is(
            "foo-print-blueprint-web-item"));
    }

    @Test
    public void indexPageTemplateAdded() throws Exception
    {
        promptProps.put(INDEX_PAGE_TEMPLATE_PROMPT, true);
        BlueprintProperties props = builder.build();

        ContentTemplateProperties expected = makeExpectedIndexPageContentTemplateProperties();
        assertContentTemplatePropertiesEqual(expected, props.getIndexPageContentTemplate());
    }

    private void addJsToExpectedWebResource(WebResourceProperties properties)
    {
        WebResourceTransformation transformation = new WebResourceTransformation("js");
        WebResourceTransformer transformer = new WebResourceTransformer();
        transformer.setModuleKey("jsI18n");
        transformation.addTransformer(transformer);
        properties.addTransformation(transformation);

        Resource soyResource = new Resource();
        soyResource.setType("download");
        soyResource.setName("dialog-wizard.js");
        soyResource.setLocation("js/dialog-wizard.js");
        properties.addResource(soyResource);

        String titleLabel = PLUGIN_KEY + ".wizard.page0.title.label";
        String titlePlace = PLUGIN_KEY + ".wizard.page0.title.placeholder";
        String titleError = PLUGIN_KEY + ".wizard.page0.title.error";
        String preRender  = PLUGIN_KEY + ".wizard.page0.pre-render";
        String postRender = PLUGIN_KEY + ".wizard.page0.post-render";

        properties.setProperty(WIZARD_FORM_FIELD_LABEL_I18N_KEY, titleLabel);
        properties.addI18nProperty(titleLabel, WIZARD_FORM_FIELD_LABEL_VALUE);

        properties.setProperty(WIZARD_FORM_FIELD_PLACEHOLDER_I18N_KEY, titlePlace);
        properties.addI18nProperty(titlePlace, WIZARD_FORM_FIELD_PLACEHOLDER_VALUE);

        properties.setProperty(WIZARD_FORM_FIELD_VALIDATION_ERROR_I18N_KEY, titleError);
        properties.addI18nProperty(titleError, WIZARD_FORM_FIELD_VALIDATION_ERROR_VALUE);

        properties.setProperty(WIZARD_FORM_FIELD_PRE_RENDER_TEXT_I18N_KEY, preRender);
        properties.addI18nProperty(preRender, WIZARD_FORM_FIELD_PRE_RENDER_TEXT_VALUE);

        properties.setProperty(WIZARD_FORM_FIELD_POST_RENDER_TEXT_I18N_KEY, postRender);
        properties.addI18nProperty(postRender, WIZARD_FORM_FIELD_POST_RENDER_TEXT_VALUE);
    }

    private void assertDialogWizard(DialogWizardProperties expectedWizard, DialogWizardProperties actualWizard)
    {
        assertThat(actualWizard, notNullValue());

        List<DialogPageProperties> actualPages = actualWizard.getDialogPages();
        List<DialogPageProperties> expectedPages = expectedWizard.getDialogPages();
        assertThat(actualPages.size(), is(expectedPages.size()));

        DialogPageProperties actualPage = actualPages.get(0);
        DialogPageProperties expectedPage = expectedPages.get(0);

        assertThat(actualPage, is(expectedPage));
    }

    private void assertWebResourceProperties(WebResourceProperties expected, WebResourceProperties actual)
    {
        assertTransformationsEqual(expected.getTransformations(), actual.getTransformations());
        assertResourcesEqual(expected.getResources(), actual.getResources());
        assertEquals(expected.getContexts(), actual.getContexts());
        assertEquals(expected.getDependencies(), actual.getDependencies());
        assertEquals(expected.getI18nProperties(), actual.getI18nProperties());
    }

    private void assertTransformationsEqual(List<WebResourceTransformation> expectedTransformations,
        List<WebResourceTransformation> actualTransformations)
    {
        assertThat(actualTransformations.size(), is(expectedTransformations.size()));
        for (int i = 0; i < expectedTransformations.size(); i++)
        {
            WebResourceTransformation expected = expectedTransformations.get(i);
            WebResourceTransformation actual = actualTransformations.get(i);
            assertTransformationEqual(expected, actual);
        }
    }

    private void assertTransformationEqual(WebResourceTransformation expected, WebResourceTransformation actual)
    {
        assertThat(actual.getExtension(), is(expected.getExtension()));
        assertThat(actual.getTransformers(), is(expected.getTransformers()));
    }

    private WebResourceProperties newExpectedWebResourceProperties(String indexKey)
    {
        WebResourceProperties properties = new WebResourceProperties();

        properties.addContext("atl.general");
        properties.addContext("atl.admin");

        properties.addDependency("com.atlassian.confluence.plugins.confluence-create-content-plugin:resources");

        Resource soyResource = new Resource();
        soyResource.setType("download");
        soyResource.setName("blueprints.css");
        soyResource.setLocation("css/blueprints.css");
        properties.addResource(soyResource);

        properties.setProperty("INDEX_KEY", indexKey);

        return properties;
    }

    private void addSoyTemplateToExpectedWebResource(WebResourceProperties properties)
    {
        // Soy transformer
        WebResourceTransformation transformation = new WebResourceTransformation("soy");
        WebResourceTransformer transformer = new WebResourceTransformer();
        transformer.setModuleKey("soyTransformer");
        transformer.addFunctions("com.atlassian.confluence.plugins.soy:soy-core-functions");
        transformation.addTransformer(transformer);
        properties.addTransformation(transformation);

        Resource soyResource = new Resource();
        soyResource.setType("download");
        soyResource.setName("templates-soy.js");
        soyResource.setLocation("soy/my-templates.soy");
        properties.addResource(soyResource);

        properties.setProperty(BlueprintProperties.SOY_PACKAGE, "Confluence.Blueprints.Plugin.FooPrint");
    }

    private void addHowToUseToExpectedSoyResource(WebResourceProperties properties)
    {
        String soyHeadingI18nKey = PLUGIN_KEY + ".wizard.how-to-use.heading";
        String soyContentI18nKey = PLUGIN_KEY + ".wizard.how-to-use.content";

        properties.setProperty(HOW_TO_USE_HEADING_I18N_KEY, soyHeadingI18nKey);
        properties.setProperty(HOW_TO_USE_CONTENT_I18N_KEY, soyContentI18nKey);

        properties.addI18nProperty(soyHeadingI18nKey, HOW_TO_USE_HEADING_VALUE);
        properties.addI18nProperty(soyContentI18nKey, HOW_TO_USE_CONTENT_VALUE);
    }

    private void assertContentTemplatePropertiesEqual(List<ContentTemplateProperties> expectedTemplates,
        List<ContentTemplateProperties> actualTemplates)
    {
        assertEquals("Wrong number of content templates", expectedTemplates.size(), actualTemplates.size());
        for (int i = 0; i < expectedTemplates.size(); i++)
        {
            ContentTemplateProperties expected = expectedTemplates.get(i);
            ContentTemplateProperties actual = actualTemplates.get(i);

            assertContentTemplatePropertiesEqual(expected, actual);
        }
    }

    private void assertContentTemplatePropertiesEqual(ContentTemplateProperties expected,
        ContentTemplateProperties actual)
    {
        assertNameBasedModulesEqual(expected, actual);
        assertEquals(expected.getContentText(), actual.getContentText());

        assertResourcesEqual(expected.getResources(), actual.getResources());
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
        assertEquals("Wrong index page title i18n", expected.getIndexTitleI18nKey(), actual.getIndexTitleI18nKey());

        assertNameBasedModulesEqual(expected, actual);
    }

    private WebItemProperties makeExpectedWebItemProperties()
    {
        WebItemProperties webItem = new WebItemProperties();
        webItem.setModuleKey("foo-print-blueprint-web-item");
        webItem.setNameI18nKey(PLUGIN_KEY + ".blueprint.display.name");
        webItem.setModuleName(webItemName);
        webItem.setDescriptionI18nKey(PLUGIN_KEY + ".blueprint.display.desc");
        webItem.setDescription(webItemDesc);
        webItem.setSection("system.create.dialog/content");

        webItem.addParam(WEB_ITEM_BLUEPRINT_KEY, blueprintModuleKey);
        return webItem;
    }

    private ContentTemplateProperties makeExpectedContentTemplateProperties()
    {
        ContentTemplateProperties template = new ContentTemplateProperties(templateModuleKey);
        template.setNameI18nKey(PLUGIN_KEY + ".foo-plate.name");
        template.setModuleName("FooPrint Content Template 0");
        template.setDescriptionI18nKey(PLUGIN_KEY + ".foo-plate.desc");
        template.setDescription("Contains Storage-format XML used by the FooPrint Blueprint");
        template.setContentText(PLUGIN_KEY + "." + templateModuleKey + ".content.text", CONTENT_I18N_DEFAULT_VALUE);

        Resource templateResource = new Resource();
        templateResource.setName("template");
        templateResource.setType("download");
        templateResource.setLocation("xml/" + templateModuleKey + ".xml");
        template.addResource(templateResource);
        return template;
    }

    private ContentTemplateProperties makeExpectedIndexPageContentTemplateProperties()
    {
        String moduleKey = BlueprintProperties.INDEX_TEMPLATE_DEFAULT_KEY;
        ContentTemplateProperties template = new ContentTemplateProperties(moduleKey);
        template.setNameI18nKey(PLUGIN_KEY + ".custom-index-page-template.name");
        template.setDescriptionI18nKey(PLUGIN_KEY + ".custom-index-page-template.desc");
        template.setModuleName("Custom Index Page Content Template");
        template.setDescription("Contains Storage-format XML used by the FooPrint Blueprint's Index page");
        template.setContentText(PLUGIN_KEY + ".custom-index-page-template.content.text", ContentTemplateProperties.INDEX_TEMPLATE_CONTENT_VALUE);

        Resource templateResource = new Resource();
        templateResource.setName("template");
        templateResource.setType("download");
        templateResource.setLocation("xml/" + moduleKey + ".xml");
        template.addResource(templateResource);
        return template;
    }
}
