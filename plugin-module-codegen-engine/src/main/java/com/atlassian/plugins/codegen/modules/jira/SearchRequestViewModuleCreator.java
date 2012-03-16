package com.atlassian.plugins.codegen.modules.jira;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.JiraPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.common.Resource;

import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;

/**
 * @since 3.6
 */
@JiraPluginModuleCreator
public class SearchRequestViewModuleCreator extends AbstractPluginModuleCreator<SearchRequestViewProperties>
{
    public static final String MODULE_NAME = "Search Request View";
    private static final String TEMPLATE_PREFIX = "templates/jira/searchrequestview/";

    //stub
    private static final String CLASS_TEMPLATE = TEMPLATE_PREFIX + "SearchRequestView.java.vtl";
    private static final String UNIT_TEST_TEMPLATE = "templates/generic/GenericTest.java.vtl";
    private static final String VIEW_TEMPLATE = "templates/common/actionview.vm.vtl";

    //examples
    private static final String EXAMPLE_CLASS_TEMPLATE = TEMPLATE_PREFIX + "Example" + CLASS_TEMPLATE;

    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "search-request-view-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(SearchRequestViewProperties props) throws Exception
    {
        PluginProjectChangeset ret = new PluginProjectChangeset()
            .withDependencies(MOCKITO_TEST)
            .with(createModule(props, PLUGIN_MODULE_TEMPLATE));
        
        if (props.includeExamples())
        {
            return ret.with(createClass(props, EXAMPLE_CLASS_TEMPLATE));
        }
        else
        {
            ret = ret.with(createClassAndTests(props, CLASS_TEMPLATE, UNIT_TEST_TEMPLATE));
            
            //since we know resources are velocity templates, let's create them
            for (Resource resource : props.getResources())
            {
                ret = ret.with(createTemplateResource(props, resource, VIEW_TEMPLATE));
            }
            
            return ret;
        }
    }

    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
