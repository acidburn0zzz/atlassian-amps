package com.atlassian.plugins.codegen.modules.confluence.blueprint;

import com.atlassian.plugins.codegen.modules.AbstractNameBasedModuleProperties;

import java.util.List;

/**
 * @since 4.1.7
 */
public class DialogWizardProperties extends AbstractNameBasedModuleProperties
{
    public static final String DIALOG_PAGES = "DIALOG_PAGES";

    public List<DialogPageProperties> getDialogPages()
    {
        return (List<DialogPageProperties>) get(DIALOG_PAGES);
    }
}
