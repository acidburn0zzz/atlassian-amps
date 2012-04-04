package com.atlassian.plugins.codegen.modules.jira;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.JiraPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.plugins.codegen.modules.Dependencies.HTTPCLIENT_TEST;
import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;

/**
 * @since 3.6
 */
@JiraPluginModuleCreator
public class WorkflowValidatorModuleCreator extends AbstractPluginModuleCreator<WorkflowElementProperties>
{
    public static final String MODULE_NAME = "Workflow Validator";
    private static final String TEMPLATE_PREFIX = "templates/jira/workflow/validator/";

    //stub
    private static final String FACTORY_TEMPLATE = TEMPLATE_PREFIX + "WorkflowValidatorFactory.java.vtl";
    private static final String CLASS_TEMPLATE = TEMPLATE_PREFIX + "WorkflowValidator.java.vtl";
    private static final String UNIT_TEST_TEMPLATE = TEMPLATE_PREFIX + "WorkflowValidatorTest.java.vtl";
    private static final String VIEW_TEMPLATE = TEMPLATE_PREFIX + "workflow-validator.vm.vtl";
    private static final String INPUT_TEMPLATE = TEMPLATE_PREFIX + "workflow-validator-input.vm.vtl";
    private static final String TEMPLATE_PATH = "validators";
    
    //examples
    private static final String EXAMPLE_CLASS_TEMPLATE = TEMPLATE_PREFIX + "Example" + CLASS_TEMPLATE;

    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "workflow-validator-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(WorkflowElementProperties props) throws Exception
    {
        PluginProjectChangeset ret = new PluginProjectChangeset()
            .with(HTTPCLIENT_TEST,
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
