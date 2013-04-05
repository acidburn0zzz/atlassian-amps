package com.atlassian.maven.plugins.amps.codegen.prompter.confluence.blueprint;

import com.atlassian.maven.plugins.amps.codegen.annotations.ModuleCreatorClass;
import com.atlassian.maven.plugins.amps.codegen.prompter.AbstractModulePrompter;
import com.atlassian.plugins.codegen.modules.PluginModuleLocation;
import com.atlassian.plugins.codegen.modules.common.Resource;
import com.atlassian.plugins.codegen.modules.common.web.WebItemProperties;
import com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintModuleCreator;
import com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintProperties;
import com.atlassian.plugins.codegen.modules.confluence.blueprint.ContentTemplateProperties;
import com.google.common.collect.Lists;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

import java.util.List;

import static com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintProperties.*;

/**
 * Prompts for creation of Confluence Blueprint-related modules.
 *
 * @since 4.1.7
 */
@ModuleCreatorClass(BlueprintModuleCreator.class)
public class BlueprintPrompter extends AbstractModulePrompter<BlueprintProperties>
{
    // TODO - remove publics
    static final String INDEX_KEY_PROMPT = "Enter Index Key (e.g. file-list, meeting-note)";
    public static final String WEB_ITEM_NAME_PROMPT = "Enter Blueprint name (e.g. File List, Meeting Note)";
    public static final String WEB_ITEM_DESC_PROMPT = "Enter Blueprint description";
    public static final String CONTENT_TEMPLATE_KEY_PROMPT = "Enter Content Template key";
    public static final String ANOTHER_CONTENT_TEMPLATE_KEY_PROMPT = "Add another Content Template key?";
    public static final String INDEX_KEY_DEFAULT = "my-blueprint";
    public static final String WEB_ITEM_NAME_DEFAULT = "My Blueprint";
    public static final String WEB_ITEM_DESC_DEFAULT = "Creates pages based on my Blueprint.";

    public BlueprintPrompter(Prompter prompter)
    {
        super(prompter);
        suppressAdvancedPrompt();  // our defaults are THE BEST.
    }

    @Override
    public BlueprintProperties promptForBasicProperties(PluginModuleLocation moduleLocation) throws PrompterException
    {
        prompter.showMessage(
            "\nConfluence Blueprints help users create, organise and share Confluence content\n" +
            "A basic Blueprint consists of 3 plugin modules:\n" +
            "\t<blueprint> - for general Blueprint configuration\n" +
            "\t<content-template> - (one or more) provides content XML for the Blueprint\n" +
            "\t<web-item> - displays the Blueprint in the Create dialog\n" +
            "For further details on Blueprints, see https://developer.atlassian.com/display/CONFDEV/Writing+a+Blueprint"
            );

        BlueprintProperties props = new BlueprintProperties();


        // Index key
        prompter.showMessage(
            "\nBlueprints are grouped by an Index Key that will determine the keys of the Blueprint plugin modules,\n" +
                "and appear as a Label on each created Blueprint page."
        );
        // TODO - make sure the key is a valid *label* and also prefix for module keys.
        String indexKey = promptNotBlank(INDEX_KEY_PROMPT, INDEX_KEY_DEFAULT);
        props.setProperty(INDEX_KEY, indexKey);

        String blueprintModuleKey = makeBlueprintModuleKey(indexKey);
        props.setModuleKey(blueprintModuleKey);


        // Name and Description
        prompter.showMessage(
            "\nThe Blueprints will be displayed in the Create dialog via a web-item with a name, description and icon.\n" +
                "The i18n keys for the name and description will be automatically generated for your Blueprint but can be\n" +
                "changed later."
        );

        String webItemName = promptNotBlank(WEB_ITEM_NAME_PROMPT, WEB_ITEM_NAME_DEFAULT);
        String webItemDesc = promptNotBlank(WEB_ITEM_DESC_PROMPT, WEB_ITEM_DESC_DEFAULT);
        props.setWebItem(makeWebItem(indexKey, blueprintModuleKey, webItemName, webItemDesc));
        props.setModuleName(webItemName + " Blueprint");    // TODO - stuff like this needs its own class and unit test?


        // Template(s)
        prompter.showMessage(
            "\nBlueprints Templates are in Confluence XHTML Storage format. A single Blueprint may create " +
                "Confluence Pages based on multiple templates, so you may specify more than one template key."
        );
        int templateCounter = 0;
        do
        {
            String defaultValue = indexKey + "-template";
            if (templateCounter > 0)
            {
                defaultValue += "-" + templateCounter;
            }
            String contentTemplateKey = promptNotBlank(CONTENT_TEMPLATE_KEY_PROMPT, defaultValue);

            ContentTemplateProperties contentTemplate = makeContentTemplate(contentTemplateKey, webItemName, templateCounter);
            props.addContentTemplate(contentTemplate);
            templateCounter++;
        }
        while(promptForBoolean(ANOTHER_CONTENT_TEMPLATE_KEY_PROMPT, "N"));
        if (templateCounter > 1)
        {
            // TODO - have a separate prompter to just add one content-template later????
            prompter.showMessage(
                "If you added more than one Template, you'll need to provide a way for users to choose between them.\n" +
                "See the documentation on developer.atlassian.com for details on how to do this.\n");
        }

        return props;
    }

    private ContentTemplateProperties makeContentTemplate(String contentTemplateKey, String webItemName, int counter)
    {
        ContentTemplateProperties template = new ContentTemplateProperties(contentTemplateKey);

        template.setModuleName(webItemName + " Content Template " + counter);
        template.setDescription("Contains Storage-format XML used by the " + webItemName + " Blueprint");
        template.setNameI18nKey(contentTemplateKey + ".name");
        template.setDescriptionI18nKey(contentTemplateKey + ".desc");
        Resource templateResource = new Resource();
        templateResource.setName("template");
        templateResource.setType("download");

        // TODO - should allow base path to be set (so xml dir can be relative)
        templateResource.setLocation("xml/" + contentTemplateKey + ".xml");
        template.addResource(templateResource);

        return template;
    }

    // TODO - this stuff in a separate class with tests?? dT
    private String makeBlueprintModuleKey(String indexKey)
    {
        return indexKey + "-blueprint";
    }

    private WebItemProperties makeWebItem(String indexKey, String blueprintModuleKey, String webItemName, String webItemDesc)
    {
        WebItemProperties webItem = new WebItemProperties();

        // TODO - i18n key should be prefixed with the plugin key for uniqueness.
        webItem.setModuleKey(blueprintModuleKey + "-web-item");
        webItem.setModuleName(webItemName);
        webItem.setDescription(webItemDesc);
        webItem.setNameI18nKey(indexKey + "-blueprint.display.name");
        webItem.setDescriptionI18nKey(indexKey + "-blueprint.display.desc");
        webItem.setSection("system.create.dialog/content");
        webItem.addParam(WEB_ITEM_BLUEPRINT_KEY, blueprintModuleKey);

        // TODO - REAALLY shouldn't be like this. data-uri CSS not resource!
        Resource webItemResource = new Resource();
        webItemResource.setName("icon");
        webItemResource.setType("download");
        webItemResource.setLocation("images/" + blueprintModuleKey + "-icon.png");
        List<Resource> resources = Lists.newArrayList(webItemResource);
        webItem.setResources(resources);

        return webItem;
    }
}
