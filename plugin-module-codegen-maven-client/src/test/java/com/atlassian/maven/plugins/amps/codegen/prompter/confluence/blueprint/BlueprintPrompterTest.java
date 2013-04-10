package com.atlassian.maven.plugins.amps.codegen.prompter.confluence.blueprint;

import com.atlassian.maven.plugins.amps.codegen.prompter.AbstractPrompterTest;
import com.atlassian.maven.plugins.amps.codegen.prompter.PluginModulePrompter;
import com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintBuilder;
import com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintPromptEntry;
import com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintProperties;
import com.atlassian.plugins.codegen.modules.confluence.blueprint.BlueprintStringer;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

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

        when(prompter.prompt(INDEX_KEY.message(), INDEX_KEY.defaultValue())).thenReturn(blueprintIndexKey);
        when(prompter.prompt(WEB_ITEM_NAME.message(), WEB_ITEM_NAME.defaultValue())).thenReturn(webItemName);
        when(prompter.prompt(WEB_ITEM_DESC.message(), WEB_ITEM_DESC.defaultValue())).thenReturn(webItemDesc);
        when(prompter.prompt(CONTENT_TEMPLATE_KEYS.message(), stringer.makeContentTemplateKey(0))).thenReturn( templateModuleKey);
        when(prompter.prompt(ANOTHER_CONTENT_TEMPLATE_KEY_PROMPT, PluginModulePrompter.YN_ANSWERS, "N")).thenReturn("N");
        when(prompter.prompt(ADVANCED_BLUEPRINT_PROMPT, PluginModulePrompter.YN_ANSWERS, "N")).thenReturn("N");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void basicPropertiesAreValid() throws PrompterException
    {
        Map<BlueprintPromptEntry, Object> props = modulePrompter.promptForProps();

        // Assert that all of the things are good things.
        assertEquals(blueprintIndexKey, props.get(INDEX_KEY));
        assertEquals(webItemName, props.get(WEB_ITEM_NAME));
        assertEquals(webItemDesc, props.get(WEB_ITEM_DESC));
        assertFalse((Boolean) props.get(HOW_TO_USE));

        List<String> templateKeys = (List<String>) props.get(CONTENT_TEMPLATE_KEYS);
        assertEquals(1, templateKeys.size());
        assertEquals(templateModuleKey, templateKeys.get(0));
    }

    @Test
    public void howToUseTemplateAdded() throws PrompterException
    {
        when(prompter.prompt(ADVANCED_BLUEPRINT_PROMPT, PluginModulePrompter.YN_ANSWERS, "N")).thenReturn("Y");
        when(prompter.prompt(HOW_TO_USE.message(), PluginModulePrompter.YN_ANSWERS, "N")).thenReturn("Y");

        Map<BlueprintPromptEntry, Object> props = modulePrompter.promptForProps();

        assertTrue((Boolean) props.get(HOW_TO_USE));
    }

}
