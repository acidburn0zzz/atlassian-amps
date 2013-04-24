package com.atlassian.plugins.codegen.modules.confluence.blueprint;

/**
 * Encapsulates the prompt text, default, and storage key for a single prompt in the Blueprint creator.
 *
 * @since 4.1.7
 */
public enum BlueprintPromptEntry
{
    INDEX_KEY("Enter Index Key (e.g. file-list, meeting-note)", "my-blueprint"),
    WEB_ITEM_NAME("Enter Blueprint name (e.g. File List, Meeting Note)", "My Blueprint"),
    WEB_ITEM_DESC("Enter Blueprint description", "Creates pages based on my Blueprint."),
    CONTENT_TEMPLATE_KEYS("Enter Content Template key", null),   // defaults are generated
    HOW_TO_USE("Add a How-to-Use page to your Blueprint?", "N"),
    DIALOG_WIZARD("Add a Create dialog wizard to your Blueprint?", "N")
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
