package com.atlassian.plugins.codegen.modules.common.web;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.*;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;

/**
 * @since 3.6
 */
@RefAppPluginModuleCreator
@JiraPluginModuleCreator
@ConfluencePluginModuleCreator
@BambooPluginModuleCreator
@StashPluginModuleCreator
public class WebPanelRendererModuleCreator extends AbstractPluginModuleCreator<WebPanelRendererProperties>
{
    public static final String MODULE_NAME = "Web Panel Renderer";
    private static final String TEMPLATE_PREFIX = "templates/common/web/webpanelrenderer/";

    //stub
    private static final String CLASS_TEMPLATE = TEMPLATE_PREFIX + "WebPanelRenderer.java.vtl";
    private static final String UNIT_TEST_TEMPLATE = TEMPLATE_PREFIX + "WebPanelRendererTest.java.vtl";

    //examples
    private static final String EXAMPLE_CLASS_TEMPLATE = TEMPLATE_PREFIX + "Example" + CLASS_TEMPLATE;

    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "web-panel-renderer-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(WebPanelRendererProperties props) throws Exception
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
            return ret.with(createClassAndTests(props, CLASS_TEMPLATE, UNIT_TEST_TEMPLATE));
        }
    }
    
    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
