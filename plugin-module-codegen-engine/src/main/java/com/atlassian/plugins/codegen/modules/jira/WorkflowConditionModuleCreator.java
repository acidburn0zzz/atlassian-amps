package com.atlassian.plugins.codegen.modules.jira;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.JiraPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;
import static com.atlassian.plugins.codegen.modules.Dependencies.SLF4J;

/**
 * @since 3.6
 */
@JiraPluginModuleCreator
public class WorkflowConditionModuleCreator extends AbstractPluginModuleCreator<WorkflowElementProperties>
{

    public static final String MODULE_NAME = "Workflow Condition";
    private static final String TEMPLATE_PREFIX = "templates/jira/workflow/condition/";

    //stub
    private static final String CLASS_TEMPLATE = TEMPLATE_PREFIX + "WorkflowCondition.java.vtl";
    private static final String FACTORY_TEMPLATE = TEMPLATE_PREFIX + "WorkflowConditionFactory.java.vtl";
    private static final String UNIT_TEST_TEMPLATE = TEMPLATE_PREFIX + "WorkflowConditionTest.java.vtl";
    private static final String VIEW_TEMPLATE = TEMPLATE_PREFIX + "workflow-condition.vm.vtl";
    private static final String INPUT_TEMPLATE = TEMPLATE_PREFIX + "workflow-condition-input.vm.vtl";
    private static final String TEMPLATE_PATH = "conditions";

    //examples
    private static final String EXAMPLE_CLASS_TEMPLATE = TEMPLATE_PREFIX + "Example" + CLASS_TEMPLATE;

    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "workflow-condition-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(WorkflowElementProperties props) throws Exception
    {
        PluginProjectChangeset ret = new PluginProjectChangeset()
            .with(SLF4J,
                  MOCKITO_TEST)
            .with(createModule(props, PLUGIN_MODULE_TEMPLATE));

        if (props.includeExamples())
        {
            return ret.with(createClass(props, EXAMPLE_CLASS_TEMPLATE));
        }
        else
        {
            String moduleKey = props.getModuleKey();
            String viewFileName = moduleKey + ".vm";
            String inputFileName = moduleKey + "-input.vm";
            return ret.with(createClassAndTests(props, CLASS_TEMPLATE, UNIT_TEST_TEMPLATE))
                .with(createClass(props, props.getFactoryClassId(), FACTORY_TEMPLATE))
                .with(createTemplateResource(props, TEMPLATE_PATH, viewFileName, VIEW_TEMPLATE))
                .with(createTemplateResource(props, TEMPLATE_PATH, inputFileName, INPUT_TEMPLATE));
        }
    }

    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
