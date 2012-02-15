package com.atlassian.plugins.codegen.modules.jira;


/**
 * @since 3.6
 */
public class ProjectTabPanelTest extends AbstractTabPanelTest
{
    public ProjectTabPanelTest()
    {
        super("project-tabpanel", new ProjectTabPanelModuleCreator());
    }
}
