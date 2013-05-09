package com.atlassian.plugins.codegen.modules.confluence.blueprint;

/**
 * Holds the Velocity template placeholder, the i18n key suffix and the i18n value for Blueprints i18n properties.
 *
 * @since 1.6
 */
public enum BlueprintI18nProperty
{
    WIZARD_FORM_TITLE_FIELD_LABEL("WIZARD_FORM_FIELD_LABEL_I18N_KEY", "wizard.page0.title.label", "Page title"),
    WIZARD_FORM_TITLE_FIELD_PLACEHOLDER("WIZARD_FORM_FIELD_PLACEHOLDER_I18N_KEY", "wizard.page0.title.placeholder", "Add a title for your new page"),
    WIZARD_FORM_TITLE_FIELD_ERROR("WIZARD_FORM_FIELD_VALIDATION_ERROR_I18N_KEY", "wizard.page0.title.error", "You must enter a title"),
    WIZARD_FORM_JSVAR_FIELD_LABEL("WIZARD_FORM_FIELD_JSVAR_LABEL", "wizard.page0.jsvar.label", "Wizard input"),
    WIZARD_FORM_JSVAR_FIELD_PLACEHOLDER("WIZARD_FORM_FIELD_JSVAR_PLACEHOLDER", "wizard.page0.jsvar.placeholder", "Type some text here for the page"),
    WIZARD_FORM_PRE_RENDER_TEXT("WIZARD_FORM_FIELD_PRE_RENDER_TEXT_I18N_KEY", "wizard.page0.pre-render", "This text comes from the pre-render hook in the Wizard JavaScript"),
    WIZARD_FORM_POST_RENDER_TEXT("WIZARD_FORM_FIELD_POST_RENDER_TEXT_I18N_KEY", "wizard.page0.post-render", "This text comes from the post-render hook in the Wizard JavaScript"),
    WIZARD_FORM_FIELD_REQUIRED("WIZARD_FORM_FIELD_REQUIRED", "wizard.required", "required"),
    HOW_TO_USE_HEADING("HOW_TO_USE_HEADING_I18N_KEY", "wizard.how-to-use.heading", "Welcome to my Blueprint"),
    HOW_TO_USE_CONTENT("HOW_TO_USE_CONTENT_I18N_KEY", "wizard.how-to-use.content", "This blueprint can be used to create a special page.")
    ;

    private String placeholder;
    private String i18nSuffix;
    private String i18nValue;

    BlueprintI18nProperty(String placeholder, String i18nSuffix, String i18nValue)
    {
        this.placeholder = placeholder;
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
