package com.atlassian.plugins.codegen.modules.jira;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.JiraPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;

/**
 * @since 3.6
 */
@JiraPluginModuleCreator
public class IssueTabPanelModuleCreator extends AbstractPluginModuleCreator<TabPanelProperties>
{

    public static final String MODULE_NAME = "Issue Tab Panel";
    private static final String TEMPLATE_PREFIX = "templates/jira/tabpanel/issue/";

    //stub
    private static final String CLASS_TEMPLATE = TEMPLATE_PREFIX + "IssueTabPanel.java.vtl";
    private static final String UNIT_TEST_TEMPLATE = "templates/generic/GenericTest.java.vtl";
    private static final String VIEW_TEMPLATE = TEMPLATE_PREFIX + "issue-tab-panel.vm.vtl";

    //examples
    private static final String EXAMPLE_CLASS_TEMPLATE = TEMPLATE_PREFIX + "Example" + CLASS_TEMPLATE;

    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "issue-tab-panel-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(TabPanelProperties props) throws Exception
    {
        PluginProjectChangeset ret = new PluginProjectChangeset()
            .with(MOCKITO_TEST)
            .with(createModule(props, PLUGIN_MODULE_TEMPLATE));

        if (props.includeExamples())
        {
            return ret.with(createClass(props, EXAMPLE_CLASS_TEMPLATE));
        }
        else
        {
            if (props.isUseCustomClass())
            {
                ret = ret.with(createClassAndTests(props, CLASS_TEMPLATE, UNIT_TEST_TEMPLATE));
            }
            return ret.with(createTemplateResource(props, "tabpanels", props.getModuleKey() + ".vm", VIEW_TEMPLATE));
        }
    }

    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
