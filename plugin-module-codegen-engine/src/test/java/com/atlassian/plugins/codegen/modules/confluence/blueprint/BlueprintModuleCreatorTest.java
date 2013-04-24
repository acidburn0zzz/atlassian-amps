package com.atlassian.plugins.codegen.modules.confluence.blueprint;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;
import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.ResourceFile;
import com.atlassian.plugins.codegen.modules.AbstractNameBasedModuleProperties;
import com.atlassian.plugins.codegen.modules.common.web.WebResourceProperties;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.dom4j.Element;
import org.dom4j.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintPromptEntry.*;
import static java.lang.String.format;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

/**
 * Tests that the {@link BlueprintModuleCreator}, given a valid set of {@link BlueprintProperties}, creates the correct
 * plugin modules and files.
 *
 * NOTE - this test uses the {@link BlueprintBuilder} to generate the {@link BlueprintProperties} passed to the
 * {@link BlueprintModuleCreator}, which makes this test somewhat of an Integration test. If tests in this class start
 * failing, check for unit-test failures in the BlueprintBuilderTest.
 *
 * @since 4.1.7
 */
public class BlueprintModuleCreatorTest extends AbstractModuleCreatorTestCase<BlueprintProperties>
{
    /**
     *  We use the builder to turn simple prompt properties into complex BlueprintProperties objects and reduce
     *  duplication in the test. This means that we assume the Builder does its job correctly - this is the
     *  responsibility of the BlueprintBuilder unit test.
     */
    private Map<BlueprintPromptEntry, Object> promptProps;

    private BlueprintProperties blueprintProps;

    // The expected values for the Properties and created plugin content
    private String blueprintModuleKey = "foo-print-blueprint";
    private String blueprintIndexKey = "foo-print";
    private String webItemName = "FooPrint";
    private String webItemDesc = "There's no Blueprint like my FooPrint.";
    private String templateModuleKey = "foo-plate";
    private String templateContentValue = "Template Content Here";

    public BlueprintModuleCreatorTest()
    {
        super("blueprint", new BlueprintModuleCreator());
    }

    @Before
    public void setupBaseProps() throws Exception
    {
        createBasePromptProperties();
        buildBlueprintProperties();

    }

    private void buildBlueprintProperties()
    {
        // TODO - this is lame. For the inherited tests we assume that we have BlueprintProperties so the object has
        // to exist before our tests can update the promptProps and build a new one.
        changeset = null;
        blueprintProps = new BlueprintBuilder(promptProps).build();
        setProps(blueprintProps);
    }

    /**
     * Common properties for all tests - creates the most basic combination of blueprint, content-template and
     * web-item elements / resources that will result in a browser-testable Blueprint.
     */
    private void createBasePromptProperties()
    {
        promptProps = Maps.newHashMap();

        promptProps.put(INDEX_KEY, blueprintIndexKey);
        promptProps.put(WEB_ITEM_NAME, webItemName);
        promptProps.put(WEB_ITEM_DESC, webItemDesc);
        promptProps.put(HOW_TO_USE, false);

        List<String> contentTemplateKeys = Lists.newArrayList();
        contentTemplateKeys.add(templateModuleKey);
        promptProps.put(CONTENT_TEMPLATE_KEYS, contentTemplateKeys);
    }

    @After
    public void tearDown()
    {
        changeset = null;
    }

    @Test
    public void blueprintModuleBasicSettings() throws Exception
    {
        Element blueprintModule = getGeneratedModule();
        assertNodeText(blueprintModule, "@key", blueprintModuleKey);
        String blueprintModuleName = "FooPrint Blueprint";
        assertNodeText(blueprintModule, "@name", blueprintModuleName);
        assertNodeText(blueprintModule, "@index-key", blueprintIndexKey);
        assertNodeText(blueprintModule, "content-template/@ref", templateModuleKey);

        // TODO - assert i18n name and desc
    }

    @Test
    public void contentTemplateModuleBasicSettings() throws Exception
    {
        Element templateModule = getGeneratedModule("content-template");
        assertNodeText(templateModule, "@key", templateModuleKey);
        assertNodeText(templateModule, "@i18n-name-key", "foo-plate.name");
        assertNodeText(templateModule, "description/@key", "foo-plate.desc");

        assertNodeText(templateModule, "resource/@name", "template");
        assertNodeText(templateModule, "resource/@type", "download");
        assertNodeText(templateModule, "resource/@location", "xml/" + templateModuleKey + ".xml");

        assertI18nString(templateModuleKey + ".name", "FooPrint Content Template 0");
        String templateDesc = "Contains Storage-format XML used by the FooPrint Blueprint";
        assertI18nString(templateModuleKey + ".desc", templateDesc);
    }

    @Test
    public void contentTemplateFileIsCreated() throws Exception
    {
        ResourceFile file = getResourceFile("xml", templateModuleKey + ".xml");
        String xml = new String(file.getContent());
        String templateContentI18nKey = templateModuleKey + ".content.text";
        assertThat(xml, containsString(templateContentI18nKey));
        assertI18nString(templateContentI18nKey, ContentTemplateProperties.CONTENT_I18N_DEFAULT_VALUE);
    }

    @Test
    public void webItemModuleBasicSettings() throws Exception
    {
        String webItemNameI18nKey = "foo-print-blueprint.display.name";
        String webItemDescI18nKey = "foo-print-blueprint.display.desc";

        Element module = getGeneratedModule("web-item");
        assertNameBasedModuleProperties(module, blueprintProps.getWebItem());

        assertNodeText(module, "@key", "foo-print-blueprint-web-item");
        assertNodeText(module, "@i18n-name-key", webItemNameI18nKey);
        assertNodeText(module, "@section", "system.create.dialog/content");
        assertNodeText(module, "description/@key", webItemDescI18nKey);
        assertNodeText(module, "param/@name", BlueprintProperties.WEB_ITEM_BLUEPRINT_KEY);
        assertNodeText(module, "param/@value", blueprintModuleKey);
        assertNodeText(module, "resource/@name", "icon");
        assertNodeText(module, "resource/@type", "download");
        assertNodeText(module, "resource/@location", "images/foo-print-blueprint-icon.png");

        assertI18nString(webItemNameI18nKey, webItemName);
        assertI18nString(webItemDescI18nKey, webItemDesc);
    }

    @Test
    public void webItemIconIsCreated() throws Exception
    {
        // Check the content of the generated icon file
        // TODO - BAD! The icon should be specified in a CSS resource file! We should generate a stub for the CSS
        // selector that allows a) the default icon to be used if not specified b) the plugin dev to add a data-url.
    }

    @Test
    public void howToUseTemplateIsAdded() throws Exception
    {
        promptProps.put(HOW_TO_USE, true);
        buildBlueprintProperties();

        // 1. The blueprint element should have a new attribute with the how-to-use template reference
        Element blueprintModule = getGeneratedModule();
        assertNodeText(blueprintModule, "@how-to-use-template", blueprintProps.getHowToUseTemplate());

        // 2. There should be a Soy file containing the referenced template
        WebResourceProperties webResource = blueprintProps.getWebResource();
        String soyHeadingI18nKey = webResource.getProperty(BlueprintProperties.SOY_HEADING_I18N_KEY);
        String soyContentI18nKey = webResource.getProperty(BlueprintProperties.SOY_CONTENT_I18N_KEY);
        String soy = new String(getResourceFile("soy", "my-templates.soy").getContent());
        assertThat(soy, containsString(format("{namespace %s}", "Confluence.Blueprints.Plugin.FooPrint")));
        assertThat(soy, containsString("{template .howToUse}"));
        assertThat(soy, containsString(format("{getText('%s')}", soyHeadingI18nKey)));
        assertThat(soy, containsString(format("{getText('%s')}", soyContentI18nKey)));

        // 3. There should be a web-resource pointing to the new file
        Element webResourceModule = getGeneratedModule("web-resource");
        assertWebResource(webResourceModule, webResource);

        // 4. There should new entries in the i18n file for the template
        assertI18nString(soyHeadingI18nKey, BlueprintProperties.SOY_HEADING_VALUE);
        assertI18nString(soyContentI18nKey, BlueprintProperties.SOY_CONTENT_VALUE);

        // TODO - 5. There should (?) be CSS rules for the template
    }

    private void assertWebResource(Element element, WebResourceProperties resourceProperties)
    {
        assertNameBasedModuleProperties(element, resourceProperties);

        // TODO - not sure how best to test here. There should be a unit test confirming that transformations get
        // rendered to XML correctly, but other than that THIS test just needs to confirm that our BlueprintCreator is
        // in fact adding WebResources that do have the correct transformation. Hmm, that's more of a test for the
        // Prompter? Or the generator?
        // TODO - assert dependencies, contexts added?
        assertNotNull(element.selectSingleNode("transformation"));
    }

    private void assertNameBasedModuleProperties(Element element, AbstractNameBasedModuleProperties props)
    {
        assertNodeText(element, "@key", props.getModuleKey());
        assertNodeText(element, "@name", props.getModuleName());
        assertNodeText(element, "@i18n-name-key", props.getNameI18nKey());
        assertNodeText(element, "description/@key", props.getDescriptionI18nKey());
        assertNodeText(element, "description", props.getDescription());
    }

    // Not sure why the changeset isn't always being cached during the test? Pull request comment please :) dT
    @Override
    protected PluginProjectChangeset getChangesetForModule() throws Exception
    {
        if (changeset == null)
        {
            changeset = super.getChangesetForModule();
        }
        return changeset;
    }

    private void assertNodeText(Element element, String nodePath, String expectedText)
    {
        assertEquals(expectedText, getText(element, nodePath));
    }

    private String getText(Element element, String nodePath)
    {
        Node node = element.selectSingleNode(nodePath);
        assertNotNull("Couldn't find node with path: " + nodePath, node);
        return node.getText();
    }

    // I prefer this method name - getI18nString doesn't indicate that an Assert is being done. dT
    private void assertI18nString(String i18nKey, String value) throws Exception
    {
        getI18nString(i18nKey, value);
    }
}
