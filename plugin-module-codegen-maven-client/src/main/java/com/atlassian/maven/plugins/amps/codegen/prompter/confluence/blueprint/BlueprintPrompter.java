package com.atlassian.maven.plugins.amps.codegen.prompter.confluence.blueprint;

import com.atlassian.maven.plugins.amps.codegen.annotations.ModuleCreatorClass;
import com.atlassian.maven.plugins.amps.codegen.prompter.AbstractModulePrompter;
import com.atlassian.plugins.codegen.modules.PluginModuleLocation;
import com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintBuilder;
import com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintModuleCreator;
import com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintPromptEntries;
import com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintPromptEntry;
import com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintProperties;
import com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintStringer;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

import java.util.List;

import static com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintPromptEntry.*;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Prompts for creation of Confluence Blueprint-related modules.
 *
 * @since 4.1.8
 */
@ModuleCreatorClass(BlueprintModuleCreator.class)
public class BlueprintPrompter extends AbstractModulePrompter<BlueprintProperties>
{
    public BlueprintPrompter(Prompter prompter)
    {
        super(prompter);
        suppressAdvancedPrompt();  // our defaults are THE BEST.
    }

    @Override
    public BlueprintProperties promptForBasicProperties(PluginModuleLocation moduleLocation) throws PrompterException
    {
        showMessage(
            "Confluence Blueprints help users create, organise and share Confluence content.\n" +
            "A basic Blueprint consists of 3 plugin modules:\n" +
            "\t<blueprint> - for general Blueprint configuration\n" +
            "\t<content-template> - (one or more) provides content XML for the Blueprint\n" +
            "\t<web-item> - displays the Blueprint in the Create dialog\n" +
            "For a Blueprint tutorial, see https://developer.atlassian.com/display/CONFDEV/Writing+a+Blueprint\n" +
            "For a Blueprint example, see https://bitbucket.org/atlassian/hello-blueprint"
            );

        BlueprintPromptEntries props = promptForProps();

        return new BlueprintBuilder(props).build();
    }

    /*
        Package-level for unit testing... don't look at me like that. The promptForBasicProperties method includes the
        BlueprintBuilder call, so to test the main logic of this class (i.e. the interaction of the user with the
        prompts) we need to isolate the 'Properties' from the 'BlueprintProperties'.
     */
    BlueprintPromptEntries promptForProps() throws PrompterException
    {
        BlueprintPromptEntries props = new BlueprintPromptEntries(getPluginKey(), getDefaultBasePackage());

        // Name and Description
        showMessage(
            "The Blueprints will be displayed in the Create dialog via a web-item with a name, description and icon.\n" +
                "The i18n keys for the name and description will be automatically generated for your Blueprint but can be\n" +
                "changed later."
        );
        String blueprintName = promptNotBlankAndFill(WEB_ITEM_NAME_PROMPT, props);
        String defaultBlueprintDesc = "Creates pages from the " + blueprintName + " blueprint.";
        promptNotBlankAndFill(WEB_ITEM_DESC_PROMPT, defaultBlueprintDesc, props);

        // Index key
        showMessage(
            "Blueprints are grouped by an Index Key that will determine the keys of the Blueprint plugin modules,\n" +
                "and appear as a Label on each created Blueprint page."
        );
        String defaultIndexKey = blueprintName.toLowerCase().replaceAll("\\s+", "-").replaceAll("[^a-z0-9\\-]", "");;
        String indexKey = promptNotBlankAndFill(INDEX_KEY_PROMPT, defaultIndexKey, props);
        BlueprintStringer stringer = new BlueprintStringer(indexKey, getPluginKey());

        // Template(s)
        showMessage(
            "Blueprints Templates are in Confluence XHTML Storage format. A single Blueprint may create \n" +
                "Confluence Pages based on multiple templates, so you may specify more than one template key."
        );
        int templateIndex = 0;
        List<String> templateKeys = newArrayList();
        do
        {
            String defaultValue = stringer.makeContentTemplateKey(templateIndex);
            templateKeys.add(promptNotBlank(CONTENT_TEMPLATE_KEYS_PROMPT.message(), defaultValue));
            templateIndex++;
        }
        while(promptForBoolean(ANOTHER_CONTENT_TEMPLATE_KEY_PROMPT));
        if (templateIndex > 1)
        {
            showMessage(
                "If you added more than one Template, you'll need to provide a way for users to choose between them.\n" +
                    "See the documentation on developer.atlassian.com for details on how to do this.");
        }
        props.put(CONTENT_TEMPLATE_KEYS_PROMPT, templateKeys);

        // Advanced
        showMessage(
            "That's all you need to enter to create a working Blueprint! However, this SDK can create richer Blueprints\n" +
                "with How-to-Use pages, JavaScript wizards, context providers, event listeners and custom Index pages."
        );
        if (promptForBoolean(ADVANCED_BLUEPRINT_PROMPT))
        {
            // How-to-use
            showMessage(
                "How-to-Use pages are shown in the Create dialog when a user selects your Blueprint, and can be used to\n" +
                    "familiarise the user with what your Blueprint does and how it works. All Blueprints shipped with\n" +
                    "Confluence include a How-to-use page and recommend them for most Blueprints."
            );
            promptForBoolean(HOW_TO_USE_PROMPT, props);

            // Dialog wizard
            showMessage(
                "If your Blueprint will require input from the user before creating the page or going to the Editor\n" +
                    "you'll need a Dialog wizard. The Blueprints Dialog wizard config is simple to use, taking Soy\n" +
                    "templates that you provide and connecting them into a multi-page wizard that you can configure\n" +
                    "simply."
            );
            promptForBoolean(DIALOG_WIZARD_PROMPT, props);

            // Create result
            showMessage(
                "If your Blueprint will add enough content from the Wizard and/or Context Provider that the new page\n" +
                    "can be saved (with a title) without the user needing to use the Editor, you can specify that \n" +
                    "the Editor be skipped and the user taken directly to the page View screen. \n" +
                    "This will add an attribute: create-result='view' to the Blueprint's config XML."
            );
            promptForBoolean(SKIP_PAGE_EDITOR_PROMPT, props);

            // Context provider
            showMessage(
                "If your Blueprint will add data to the page that doesn't come from the user (e.g. it comes from a \n" +
                "service that your plugin provides), you'll need to add a Context Provider to your Blueprint's template.\n" +
                "Context providers allow you to add complex XHTML content to your templates, making pretty-much \n" +
                "anything possible."
            );
            promptForBoolean(CONTEXT_PROVIDER_PROMPT, props);

            // Event Listener
            showMessage(
                "If your Blueprint will perform actions after the Blueprint page is created, such as adding watches,\n" +
                    "sending emails, etc, you'll need to add an event listener."
            );
            promptForBoolean(EVENT_LISTENER_PROMPT, props);

            // Index Page template
            showMessage(
                "A Blueprint Index page displays a listing of all pages in the current space that were created by that Blueprint.\n" +
                "You can override the default index page to list the Blueprint pages in any way you choose."
            );
            promptForBoolean(INDEX_PAGE_TEMPLATE_PROMPT, props);
        }
        return props;
    }

    private String promptNotBlankAndFill(BlueprintPromptEntry promptEntry, BlueprintPromptEntries props) throws PrompterException
    {
        return promptNotBlankAndFill(promptEntry, null, props);
    }

    private String promptNotBlankAndFill(BlueprintPromptEntry promptEntry, String defaultValue, BlueprintPromptEntries props) throws PrompterException
    {
        if (defaultValue == null)
            defaultValue = promptEntry.defaultValue();

        String input = promptNotBlank(promptEntry.message(), defaultValue);
        props.put(promptEntry, input);
        return input;
    }

    private boolean promptForBoolean(BlueprintPromptEntry promptEntry, BlueprintPromptEntries props) throws PrompterException
    {
        boolean input = promptForBoolean(promptEntry);
        props.put(promptEntry, input);
        return input;
    }

    private boolean promptForBoolean(BlueprintPromptEntry promptEntry) throws PrompterException
    {
        return promptForBoolean(promptEntry.message(), promptEntry.defaultValue());
    }

    // Shows a message, padded at top and bottom.
    private void showMessage(String message) throws PrompterException
    {
        prompter.showMessage("\n" + message + "\n");
    }
}
