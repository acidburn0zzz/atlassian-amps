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
import com.google.common.collect.Maps;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

import java.util.List;
import java.util.Map;

import static com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintPromptEntry.*;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Prompts for creation of Confluence Blueprint-related modules.
 *
 * @since 4.1.7
 */
@ModuleCreatorClass(BlueprintModuleCreator.class)
public class BlueprintPrompter extends AbstractModulePrompter<BlueprintProperties>
{
    public static final String ANOTHER_CONTENT_TEMPLATE_KEY_PROMPT = "Add another Content Template key?";
    public static final String ADVANCED_BLUEPRINT_PROMPT = "Add advanced Blueprint features?";

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
        props.put(HOW_TO_USE_PROMPT, false);
        props.put(DIALOG_WIZARD_PROMPT, false);

        // Index key
        showMessage(
            "Blueprints are grouped by an Index Key that will determine the keys of the Blueprint plugin modules,\n" +
                "and appear as a Label on each created Blueprint page."
        );
        // TODO - make sure the key is a valid *label* and also a valid prefix for module keys.
        String indexKey = promptNotBlank(INDEX_KEY_PROMPT, props);
        BlueprintStringer stringer = new BlueprintStringer(indexKey);

        // Name and Description
        showMessage(
            "The Blueprints will be displayed in the Create dialog via a web-item with a name, description and icon.\n" +
                "The i18n keys for the name and description will be automatically generated for your Blueprint but can be\n" +
                "changed later."
        );
        promptNotBlank(WEB_ITEM_NAME_PROMPT, props);
        promptNotBlank(WEB_ITEM_DESC_PROMPT, props);

        // Template(s)
        showMessage(
            "Blueprints Templates are in Confluence XHTML Storage format. A single Blueprint may create " +
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
        while(promptForBoolean(ANOTHER_CONTENT_TEMPLATE_KEY_PROMPT, "N"));
        if (templateIndex > 1)
        {
            // TODO - have a separate prompter to just add one content-template later????
            showMessage(
                "If you added more than one Template, you'll need to provide a way for users to choose between them.\n" +
                    "See the documentation on developer.atlassian.com for details on how to do this.");
        }
        props.put(CONTENT_TEMPLATE_KEYS_PROMPT, templateKeys);

        // Advanced
        showMessage(
            "That's all you need to enter to create a working Blueprint! However, this SDK can create richer Blueprints\n" +
                "with How-to-Use pages, JavaScript wizards and custom Index page."
        );
        if (promptForBoolean(ADVANCED_BLUEPRINT_PROMPT, "N"))
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
        }
        return props;
    }

    private String promptNotBlank(BlueprintPromptEntry promptEntry, BlueprintPromptEntries props) throws PrompterException
    {
        String input = promptNotBlank(promptEntry.message(), promptEntry.defaultValue());
        props.put(promptEntry, input);
        return input;
    }

    private boolean promptForBoolean(BlueprintPromptEntry promptEntry, BlueprintPromptEntries props) throws PrompterException
    {
        boolean input = promptForBoolean(promptEntry.message(), promptEntry.defaultValue());
        props.put(promptEntry, input);
        return input;
    }

    // Shows a message, padded at top and bottom.
    private void showMessage(String message) throws PrompterException
    {
        prompter.showMessage("\n" + message + "\n");
    }
}
