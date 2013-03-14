package com.atlassian.plugins.codegen.modules.jira;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.JiraPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FilenameUtils;

import java.util.Map;

import static com.atlassian.plugins.codegen.modules.Dependencies.*;

/**
 * @since 3.6
 */
@JiraPluginModuleCreator
public class WebworkModuleCreator extends AbstractPluginModuleCreator<WebworkProperties>
{

    public static final String MODULE_NAME = "Webwork Plugin";
    private static final String TEMPLATE_PREFIX = "templates/jira/webwork/";

    //stub
    private static final String CLASS_TEMPLATE = TEMPLATE_PREFIX + "WebworkAction.java.vtl";
    //private static final String UNIT_TEST_TEMPLATE = TEMPLATE_PREFIX + "WebworkActionTest.java.vtl";
    private static final String UNIT_TEST_TEMPLATE = "templates/generic/GenericTest.java.vtl";
    private static final String VIEW_TEMPLATE = "templates/common/actionview.vm.vtl";

    //examples
    private static final String EXAMPLE_CLASS_TEMPLATE = TEMPLATE_PREFIX + "Example" + CLASS_TEMPLATE;

    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "webwork-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(WebworkProperties props) throws Exception
    {
        PluginProjectChangeset ret = new PluginProjectChangeset()
            .with(HTTPCLIENT_TEST,
                  SLF4J,
                  MOCKITO_TEST)
            .with(createModule(props, PLUGIN_MODULE_TEMPLATE));

        if (props.includeExamples())
        {
        }
        else
        {
            for (ActionProperties action : props.getActions())
            {
                ret = ret.with(createClassAndTests(action, CLASS_TEMPLATE, UNIT_TEST_TEMPLATE));
                
                // write view templates
                for (View view : action.getViews())
                {
                    ret = ret.with(createViewResource(action, view, VIEW_TEMPLATE));
                }
            }
        }
        
        return ret;
    }
    
    protected PluginProjectChangeset createViewResource(Map<Object, Object> props, View view, String templateName) throws Exception
    {
        String resourceFullPath = FilenameUtils.separatorsToSystem(view.getPath());
        String path = FilenameUtils.getPath(resourceFullPath);
        String fileName = FilenameUtils.getName(resourceFullPath);
        Map<Object, Object> tempProps = ImmutableMap.builder().putAll(props).put("CURRENT_VIEW", fileName).build();
        return createTemplateResource(tempProps, path, fileName, templateName);
    }

    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
