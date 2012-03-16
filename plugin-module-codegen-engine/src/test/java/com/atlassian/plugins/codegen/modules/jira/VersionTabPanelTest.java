package com.atlassian.plugins.codegen.modules.jira;


/**
 * @since 3.6
 */
public class VersionTabPanelTest extends AbstractTabPanelTest
{
    public VersionTabPanelTest()
    {
        super("version-tabpanel", new VersionTabPanelModuleCreator());
    }
}
