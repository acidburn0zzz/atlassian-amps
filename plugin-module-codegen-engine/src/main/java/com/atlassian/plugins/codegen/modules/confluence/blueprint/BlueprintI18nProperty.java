package com.atlassian.plugins.codegen.modules.confluence.blueprint;

/**
 * Holds the Velocity template placeholder, the i18n key suffix and the i18n value for Blueprints i18n properties.
 *
 * @since 1.6
 */
public enum BlueprintI18nProperty
{
    WIZARD_FORM_TITLE_FIELD_LABEL("wizard.page0.title.label", "Page title"),
    WIZARD_FORM_TITLE_FIELD_PLACEHOLDER("wizard.page0.title.placeholder", "Add a title for your new page"),
    WIZARD_FORM_TITLE_FIELD_ERROR("wizard.page0.title.error", "You must enter a title"),
    WIZARD_FORM_JSVAR_FIELD_LABEL("wizard.page0.jsvar.label", "Wizard input"),
    WIZARD_FORM_JSVAR_FIELD_PLACEHOLDER("wizard.page0.jsvar.placeholder", "Type some text here for the page"),
    WIZARD_FORM_PRE_RENDER_TEXT("wizard.page0.pre-render", "This text comes from the pre-render hook in the Wizard JavaScript"),
    WIZARD_FORM_POST_RENDER_TEXT("wizard.page0.post-render", "This text comes from the post-render hook in the Wizard JavaScript"),
    WIZARD_FORM_FIELD_REQUIRED("wizard.required", "required"),
    HOW_TO_USE_HEADING("wizard.how-to-use.heading", "Welcome to my Blueprint"),
    HOW_TO_USE_CONTENT("wizard.how-to-use.content", "This blueprint can be used to create a special page."),
    CONTENT_TEMPLATE_PLACEHOLDER("template.placeholder", "Type here to replace this text"),
    CONTENT_TEMPLATE_MENTION_PLACEHOLDER("template.mention.placeholder", "Type here to mention a user")
    ;

    private final String placeholder;
    private final String i18nSuffix;
    private final String i18nValue;

    BlueprintI18nProperty(String i18nSuffix, String i18nValue)
    {
        this.placeholder = this.name();
        this.i18nSuffix = i18nSuffix;
        this.i18nValue = i18nValue;
    }

    public String getPropertyKey()
    {
        return placeholder;
    }

    public String getI18nKey(BlueprintStringer stringer)
    {
        return stringer.makeI18nKey(i18nSuffix);
    }

    public String getI18nValue()
    {
        return i18nValue;
    }
}
