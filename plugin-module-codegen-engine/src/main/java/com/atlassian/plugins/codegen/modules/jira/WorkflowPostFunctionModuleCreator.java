package com.atlassian.plugins.codegen.modules.jira;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.JiraPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;

/**
 *
 */
@JiraPluginModuleCreator
public class WorkflowPostFunctionModuleCreator extends AbstractPluginModuleCreator<WorkflowPostFunctionProperties>
{
    public static final String MODULE_NAME = "Workflow Post Function";
    private static final String TEMPLATE_PREFIX = "templates/jira/workflow/function/";

    private static final String CLASS_TEMPLATE = TEMPLATE_PREFIX + "PostFunction.java.vtl";
    private static final String FACTORY_TEMPLATE = TEMPLATE_PREFIX + "PostFunctionFactory.java.vtl";
    private static final String UNIT_TEST_TEMPLATE = TEMPLATE_PREFIX + "PostFunctionTest.java.vtl";
    private static final String VIEW_TEMPLATE = TEMPLATE_PREFIX + "post-function.vm.vtl";
    private static final String INPUT_TEMPLATE = TEMPLATE_PREFIX + "post-function-input.vm.vtl";
    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "post-function-plugin.xml.vtl";
    private static final String TEMPLATE_PATH = "postfunctions";
    
    @Override
    public PluginProjectChangeset createModule(WorkflowPostFunctionProperties props) throws Exception
    {
        PluginProjectChangeset ret = new PluginProjectChangeset()
            .with(MOCKITO_TEST)
            .with(createModule(props, PLUGIN_MODULE_TEMPLATE));

        String moduleKey = props.getModuleKey();
        String viewFileName = moduleKey + ".vm";
        String inputFileName = moduleKey + "-input.vm";
        return ret.with(createClassAndTests(props, CLASS_TEMPLATE, UNIT_TEST_TEMPLATE))
            .with(createClass(props, props.getFactoryClassId(), FACTORY_TEMPLATE))
            .with(createTemplateResource(props, TEMPLATE_PATH, viewFileName, VIEW_TEMPLATE))
            .with(createTemplateResource(props, TEMPLATE_PATH, inputFileName, INPUT_TEMPLATE));
    }

    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
