package com.atlassian.plugins.codegen.modules.confluence.blueprint;

/**
 * Encapsulates the prompt text, default, and storage key for a single prompt in the Blueprint creator.
 *
 * @since 4.1.8
 */
public enum BlueprintPromptEntry
{
    WEB_ITEM_NAME_PROMPT("Enter Blueprint name (e.g. File List, Meeting Note)", "My Blueprint"),
    INDEX_KEY_PROMPT("Enter Index Key (e.g. file-list, meeting-note)"),
    WEB_ITEM_DESC_PROMPT("Enter Blueprint description"),
    CONTENT_TEMPLATE_KEYS_PROMPT("Enter Content Template key"),
    ANOTHER_CONTENT_TEMPLATE_KEY_PROMPT("Add another Content Template key?", "N"),
    ADVANCED_BLUEPRINT_PROMPT ("Add advanced Blueprint features?", "N"),
    HOW_TO_USE_PROMPT("Add a How-to-Use page to your Blueprint?", "N"),
    DIALOG_WIZARD_PROMPT("Add a Create dialog wizard to your Blueprint?", "N"),
    CONTEXT_PROVIDER_PROMPT("Add a Context Provider to your Blueprint?", "N"),
    SKIP_PAGE_EDITOR_PROMPT("Skip the Editor?", "N"),
    EVENT_LISTENER_PROMPT("Add an Event Listener to your Blueprint?", "N"),
    INDEX_PAGE_TEMPLATE_PROMPT("Add a custom Index page to your Blueprint?", "N")
    ;

    private final String message;
    private final String defaultValue;

    BlueprintPromptEntry(String message, String defaultValue)
    {
        this.message = message;
        this.defaultValue = defaultValue;
    }

    BlueprintPromptEntry(String message)
    {
        this(message, null);   // prompt defaults are generated from previously-entered values
    }

    public String message()
    {
        return message;
    }

    public String defaultValue()
    {
        return defaultValue;
    }
}
