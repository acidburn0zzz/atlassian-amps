package com.atlassian.plugins.codegen.modules.confluence.blueprint;

/**
 * Encapsulates the prompt text, default, and storage key for a single prompt in the Blueprint creator.
 *
 * @since 4.1.7
 */
public enum BlueprintPromptEntry
{
    INDEX_KEY_PROMPT("Enter Index Key (e.g. file-list, meeting-note)", "my-blueprint"),
    WEB_ITEM_NAME_PROMPT("Enter Blueprint name (e.g. File List, Meeting Note)", "My Blueprint"),
    WEB_ITEM_DESC_PROMPT("Enter Blueprint description", "Creates pages based on my Blueprint."),
    CONTENT_TEMPLATE_KEYS_PROMPT("Enter Content Template key", null),   // defaults are generated
    ANOTHER_CONTENT_TEMPLATE_KEY_PROMPT("Add another Content Template key?", "N"),
    ADVANCED_BLUEPRINT_PROMPT ("Add advanced Blueprint features?", "Y"),
    SKIP_PAGE_EDITOR_PROMPT("Skip the Editor?", "N"),
    HOW_TO_USE_PROMPT("Add a How-to-Use page to your Blueprint?", "Y"),
    DIALOG_WIZARD_PROMPT("Add a Create dialog wizard to your Blueprint?", "Y"),
    CONTEXT_PROVIDER_PROMPT("Add a Context Provider to your Blueprint?", "Y"),
    EVENT_LISTENER_PROMPT("Add an Event Listener to your Blueprint?", "Y")
    ;

    private final String message;
    private final String defaultValue;

    BlueprintPromptEntry(String message, String defaultValue)
    {
        this.message = message;
        this.defaultValue = defaultValue;
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
