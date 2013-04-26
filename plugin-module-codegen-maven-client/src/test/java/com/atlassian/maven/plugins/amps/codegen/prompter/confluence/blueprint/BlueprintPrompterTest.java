package com.atlassian.maven.plugins.amps.codegen.prompter.confluence.blueprint;

import com.atlassian.maven.plugins.amps.codegen.prompter.AbstractPrompterTest;
import com.atlassian.maven.plugins.amps.codegen.prompter.PluginModulePrompter;
import com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintBuilder;
import com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintPromptEntries;
import com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintProperties;
import com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintStringer;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.atlassian.maven.plugins.amps.codegen.prompter.confluence.blueprint.BlueprintPrompter.ADVANCED_BLUEPRINT_PROMPT;
import static com.atlassian.maven.plugins.amps.codegen.prompter.confluence.blueprint.BlueprintPrompter.ANOTHER_CONTENT_TEMPLATE_KEY_PROMPT;
import static com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintPromptEntry.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * The logic for creating the {@link BlueprintProperties} object is in the {@link BlueprintBuilder} class, so this
 * test class only checks that the correct prompts exists and that the simple Properties object is filled correctly.
 *
 * @since 4.1.7
 */
public class BlueprintPrompterTest extends AbstractPrompterTest
{
    private String webItemName = "FooPrint";
    private String webItemDesc = "There's no Blueprint like my FooPrint.";
    private String templateModuleKey = "foo-plate";
    private String blueprintIndexKey = "foo-print";

    private BlueprintPrompter modulePrompter;

    @Before
    public void setup() throws PrompterException
    {
        modulePrompter = new BlueprintPrompter(prompter);
        modulePrompter.setUseAnsiColor(false);

        BlueprintStringer stringer = new BlueprintStringer(blueprintIndexKey);

        when(prompter.prompt(INDEX_KEY_PROMPT.message(), INDEX_KEY_PROMPT.defaultValue())).thenReturn(blueprintIndexKey);
        when(prompter.prompt(WEB_ITEM_NAME_PROMPT.message(), WEB_ITEM_NAME_PROMPT.defaultValue())).thenReturn(webItemName);
        when(prompter.prompt(WEB_ITEM_DESC_PROMPT.message(), WEB_ITEM_DESC_PROMPT.defaultValue())).thenReturn(webItemDesc);
        when(prompter.prompt(CONTENT_TEMPLATE_KEYS_PROMPT.message(), stringer.makeContentTemplateKey(0))).thenReturn( templateModuleKey);
        when(prompter.prompt(ANOTHER_CONTENT_TEMPLATE_KEY_PROMPT, PluginModulePrompter.YN_ANSWERS, "N")).thenReturn("N");
        when(prompter.prompt(ADVANCED_BLUEPRINT_PROMPT, PluginModulePrompter.YN_ANSWERS, "N")).thenReturn("N");
        when(prompter.prompt(HOW_TO_USE_PROMPT.message(), PluginModulePrompter.YN_ANSWERS, "N")).thenReturn("N");
        when(prompter.prompt(DIALOG_WIZARD_PROMPT.message(), PluginModulePrompter.YN_ANSWERS, "N")).thenReturn("N");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void basicPropertiesAreValid() throws PrompterException
    {
        BlueprintPromptEntries props = modulePrompter.promptForProps();

        // Assert that all of the things are good things.
        assertEquals(blueprintIndexKey, props.get(INDEX_KEY_PROMPT));
        assertEquals(webItemName, props.get(WEB_ITEM_NAME_PROMPT));
        assertEquals(webItemDesc, props.get(WEB_ITEM_DESC_PROMPT));
        assertFalse((Boolean) props.get(HOW_TO_USE_PROMPT));

        List<String> templateKeys = (List<String>) props.get(CONTENT_TEMPLATE_KEYS_PROMPT);
        assertEquals(1, templateKeys.size());
        assertEquals(templateModuleKey, templateKeys.get(0));

        assertEquals("example", props.getPluginKey());
        assertEquals("com.example", props.getDefaultBasePackage());
    }

    @Test
    public void howToUseTemplateAdded() throws PrompterException
    {
        when(prompter.prompt(ADVANCED_BLUEPRINT_PROMPT, PluginModulePrompter.YN_ANSWERS, "N")).thenReturn("Y");
        when(prompter.prompt(HOW_TO_USE_PROMPT.message(), PluginModulePrompter.YN_ANSWERS, "N")).thenReturn("Y");

        BlueprintPromptEntries props = modulePrompter.promptForProps();

        assertTrue((Boolean) props.get(HOW_TO_USE_PROMPT));
    }

    @Test
    public void dialogWizardAdded() throws PrompterException
    {
        when(prompter.prompt(ADVANCED_BLUEPRINT_PROMPT, PluginModulePrompter.YN_ANSWERS, "N")).thenReturn("Y");
        when(prompter.prompt(DIALOG_WIZARD_PROMPT.message(), PluginModulePrompter.YN_ANSWERS, "N")).thenReturn("Y");

        BlueprintPromptEntries props = modulePrompter.promptForProps();

        assertTrue((Boolean) props.get(DIALOG_WIZARD_PROMPT));
    }
}
