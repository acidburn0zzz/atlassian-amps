package com.atlassian.plugins.codegen.modules.jira;


/**
 * @since 3.6
 */
public class IssueTabPanelTest extends AbstractTabPanelTest
{
    public IssueTabPanelTest()
    {
        super("issue-tabpanel", new IssueTabPanelModuleCreator());
    }
}
