package com.atlassian.plugins.codegen.modules.confluence.blueprint;

import com.atlassian.plugins.codegen.modules.AbstractPluginModuleProperties;

/**
 * Properties for Dialog Wizard Pages in Confluence Blueprints. Extends plugin-module properties to pick up i18n
 * abilities.
 *
 * @since 4.1.7
 */
public class DialogPageProperties extends AbstractPluginModuleProperties
{
    public DialogPageProperties(int pageIndex, String soyPackage, BlueprintStringer stringer)
    {
        String pageId = "page" + pageIndex;
        setProperty("ID", pageId);
        setProperty("TEMPLATE_KEY", soyPackage + ".wizardPage" + pageIndex);

        String pageTitleI18nKey = stringer.makeI18nKey("wizard." + pageId + ".title");
        String pageDescHeaderI18nKey = stringer.makeI18nKey("wizard." + pageId + ".desc.header");
        String pageDescContentI18nKey = stringer.makeI18nKey("wizard." + pageId + ".desc.content");

        setProperty("TITLE_I18N_KEY", pageTitleI18nKey);
        setProperty("DESC_HEADER_I18N_KEY", pageDescHeaderI18nKey);
        setProperty("DESC_CONTENT_I18N_KEY", pageDescContentI18nKey);

        addI18nProperty(pageTitleI18nKey, "Wizard Page " + pageIndex + " Title");
        addI18nProperty(pageDescHeaderI18nKey, "Page " + pageIndex + " Description");
        addI18nProperty(pageDescContentI18nKey, "This wizard page does A, B and C");

        put("LAST", false);     // just to make it obvious
    }
}
