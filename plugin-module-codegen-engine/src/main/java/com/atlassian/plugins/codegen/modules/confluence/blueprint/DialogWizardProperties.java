package com.atlassian.plugins.codegen.modules.confluence.blueprint;

import com.atlassian.plugins.codegen.modules.AbstractNameBasedModuleProperties;
import com.google.common.collect.ImmutableMap;

import java.util.List;

/**
 * Properties for Confluence Blueprint wizards.
 */
public class DialogWizardProperties extends AbstractNameBasedModuleProperties
{
    public static final String DIALOG_PAGES = "DIALOG_PAGES";

    @SuppressWarnings("unchecked")
    public List<DialogPageProperties> getDialogPages()
    {
        return (List<DialogPageProperties>) get(DIALOG_PAGES);
    }

    public void setDialogPages(List<DialogPageProperties> dialogPages)
    {
        put(DIALOG_PAGES, dialogPages);
    }

    @Override
    public ImmutableMap<String, String> getI18nProperties()
    {
        ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
        mapBuilder.putAll(super.getI18nProperties());
        if (getDialogPages() != null)
        {
            for (DialogPageProperties dialogPage : getDialogPages())
            {
                mapBuilder.putAll(dialogPage.getI18nProperties());
            }
        }
        return mapBuilder.build();
    }
}
