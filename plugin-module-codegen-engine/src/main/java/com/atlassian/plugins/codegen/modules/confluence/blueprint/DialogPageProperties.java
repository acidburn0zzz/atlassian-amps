package com.atlassian.plugins.codegen.modules.confluence.blueprint;

import java.util.Properties;

/**
 * Properties for Dialog Wizard Pages in Confluence Blueprints.
 *
 * @since 4.1.7
 */
public class DialogPageProperties extends Properties
{
    public DialogPageProperties(String indexKey, int pageIndex, String soyPackage)
    {
        String pageId = "page" + pageIndex;
        setProperty("ID", pageId);
        setProperty("TEMPLATE_KEY", soyPackage + ".wizardPage" + pageIndex);
        setProperty("TITLE_I18N_KEY", indexKey + "." + pageId + ".title");
        setProperty("DESC_HEADER_I18N_KEY", indexKey + "." + pageId + ".desc.header");
        setProperty("DESC_CONTENT_I18N_KEY", indexKey + "." + pageId + ".desc.content");
        put("LAST", false);     // just to make it obvious
    }
}
