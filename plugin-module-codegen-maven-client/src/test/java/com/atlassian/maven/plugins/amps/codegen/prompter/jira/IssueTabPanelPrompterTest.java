package com.atlassian.maven.plugins.amps.codegen.prompter.jira;

import com.atlassian.maven.plugins.amps.codegen.prompter.AbstractModulePrompter;
import com.atlassian.maven.plugins.amps.codegen.prompter.AbstractPrompterTest;
import com.atlassian.maven.plugins.amps.codegen.prompter.PluginModulePrompter;
import com.atlassian.plugins.codegen.modules.jira.TabPanelProperties;

import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.junit.Before;
import org.junit.Test;

import static com.atlassian.maven.plugins.amps.codegen.prompter.AbstractModulePrompter.MODULE_DESCRIP_PROMPT;
import static com.atlassian.maven.plugins.amps.codegen.prompter.AbstractModulePrompter.MODULE_KEY_PROMPT;
import static com.atlassian.maven.plugins.amps.codegen.prompter.AbstractModulePrompter.MODULE_NAME_PROMPT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @since 3.6
 */
public class IssueTabPanelPrompterTest extends AbstractPrompterTest
{
    public static final String PACKAGE = "com.atlassian.plugins.jira.tabpanels";
    public static final String CLASSNAME = "MyIssueTabPanel";
    public static final String MODULE_NAME = "My Issue Tab Panel";
    public static final String MODULE_KEY = "my-issue-tab-panel";
    public static final String DESCRIPTION = "The My Issue Tab Panel Plugin";
    public static final String I18N_NAME_KEY = "my-issue-tab-panel.name";
    public static final String I18N_DESCRIPTION_KEY = "my-issue-tab-panel.description";

    public static final String ADV_MODULE_NAME = "My Awesome Plugin";
    public static final String ADV_MODULE_KEY = "awesome-module";
    public static final String ADV_DESCRIPTION = "The Awesomest Plugin Ever";
    public static final String ADV_I18N_NAME_KEY = "awesome-plugin.name";
    public static final String ADV_I18N_DESCRIPTION_KEY = "pluginus-awesomeous.description";

    public static final String ORDER = "22";
    public static final String LABEL_KEY = "item.label";
    public static final String LABEL_VALUE = "this is my label";

    private Prompter prompter;

    @Before
    public void setup()
    {
        prompter = mock(Prompter.class);
    }

    @Test
    public void basicPropertiesAreValid() throws PrompterException
    {
        when(prompter.prompt("Enter New Classname", "MyIssueTabPanel")).thenReturn(CLASSNAME);
        when(prompter.prompt("Enter Package Name", AbstractModulePrompter.DEFAULT_BASE_PACKAGE + ".jira.tabpanels")).thenReturn(PACKAGE);
        when(prompter.prompt("Show Advanced Setup?", PluginModulePrompter.YN_ANSWERS, "N")).thenReturn("N");
        when(prompter.prompt("Include Example Code?", PluginModulePrompter.YN_ANSWERS, "N")).thenReturn("N");

        IssueTabPanelPrompter modulePrompter = new IssueTabPanelPrompter(prompter);
        TabPanelProperties props = modulePrompter.getModulePropertiesFromInput(moduleLocation);

        assertEquals("wrong class", CLASSNAME, props.getClassId().getName());
        assertEquals("wrong class package", PACKAGE, props.getClassId().getPackage());
        assertEquals("wrong module name", MODULE_NAME, props.getModuleName());
        assertEquals("wrong module key", MODULE_KEY, props.getModuleKey());
        assertEquals("wrong description", DESCRIPTION, props.getDescription());
        assertEquals("wrong i18n name key", I18N_NAME_KEY, props.getNameI18nKey());
        assertEquals("wrong i18n desc key", I18N_DESCRIPTION_KEY, props.getDescriptionI18nKey());
        assertEquals("wrong order", "10", props.getOrder());
        assertEquals("wrong label key", MODULE_KEY + ".label", props.getLabel()
                .getKey());
        assertEquals("wrong label value", MODULE_NAME, props.getLabel()
                .getValue());
    }

    @Test
    public void advancedPropertiesAreValid() throws PrompterException
    {
        when(prompter.prompt("Enter New Classname", "MyIssueTabPanel")).thenReturn(CLASSNAME);
        when(prompter.prompt("Enter Package Name", AbstractModulePrompter.DEFAULT_BASE_PACKAGE + ".jira.tabpanels")).thenReturn(PACKAGE);
        when(prompter.prompt("Show Advanced Setup?", PluginModulePrompter.YN_ANSWERS, "N")).thenReturn("Y");

        when(prompter.prompt(MODULE_NAME_PROMPT, MODULE_NAME)).thenReturn(ADV_MODULE_NAME);
        when(prompter.prompt(MODULE_KEY_PROMPT, MODULE_KEY)).thenReturn(ADV_MODULE_KEY);
        when(prompter.prompt(MODULE_DESCRIP_PROMPT, DESCRIPTION)).thenReturn(ADV_DESCRIPTION);
        when(prompter.prompt("i18n Name Key", I18N_NAME_KEY)).thenReturn(ADV_I18N_NAME_KEY);
        when(prompter.prompt("i18n Description Key", I18N_DESCRIPTION_KEY)).thenReturn(ADV_I18N_DESCRIPTION_KEY);

        when(prompter.prompt("Order", "10")).thenReturn(ORDER);
        when(prompter.prompt("Enter Label Key", MODULE_KEY + ".label")).thenReturn(LABEL_KEY);
        when(prompter.prompt("Enter Label Value", MODULE_NAME)).thenReturn(LABEL_VALUE);

        when(prompter.prompt("Include Example Code?", PluginModulePrompter.YN_ANSWERS, "N")).thenReturn("N");

        IssueTabPanelPrompter modulePrompter = new IssueTabPanelPrompter(prompter);
        TabPanelProperties props = modulePrompter.getModulePropertiesFromInput(moduleLocation);

        assertEquals("wrong adv class", CLASSNAME, props.getClassId().getName());
        assertEquals("wrong adv package", PACKAGE, props.getClassId().getPackage());
        assertEquals("wrong adv module name", ADV_MODULE_NAME, props.getModuleName());
        assertEquals("wrong adv module key", ADV_MODULE_KEY, props.getModuleKey());
        assertEquals("wrong adv description", ADV_DESCRIPTION, props.getDescription());
        assertEquals("wrong adv i18n name key", ADV_I18N_NAME_KEY, props.getNameI18nKey());
        assertEquals("wrong adv i18n desc key", ADV_I18N_DESCRIPTION_KEY, props.getDescriptionI18nKey());
        assertEquals("wrong order", ORDER, props.getOrder());
        assertEquals("wrong label key", LABEL_KEY, props.getLabel()
                .getKey());
        assertEquals("wrong label value", LABEL_VALUE, props.getLabel()
                .getValue());
    }
}
