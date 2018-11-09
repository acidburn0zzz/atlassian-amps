package com.atlassian.plugins.codegen.modules.common;

import com.atlassian.plugins.codegen.ClassId;
import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.BambooPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.BitbucketPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.ConfluencePluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.CrowdPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.FeCruPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.JiraPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.RefAppPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import static com.atlassian.plugins.codegen.ArtifactDependency.Scope.PROVIDED;
import static com.atlassian.plugins.codegen.ArtifactDependency.Scope.TEST;
import static com.atlassian.plugins.codegen.ArtifactDependency.dependency;
import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;
import static com.atlassian.plugins.codegen.modules.Dependencies.SERVLET_API;

/**
 * @since 3.6
 */
@RefAppPluginModuleCreator
@JiraPluginModuleCreator
@ConfluencePluginModuleCreator
@BambooPluginModuleCreator
@BitbucketPluginModuleCreator
@FeCruPluginModuleCreator
@CrowdPluginModuleCreator
public class RESTModuleCreator extends AbstractPluginModuleCreator<RESTProperties>
{
    public static final String MODULE_NAME = "REST Plugin Module";
    private static final String TEMPLATE_PREFIX = "templates/common/rest/";

    //stub
    private static final String CLASS_TEMPLATE = TEMPLATE_PREFIX + "REST.java.vtl";
    private static final String MODEL_TEMPLATE = TEMPLATE_PREFIX + "RESTModelObject.java.vtl";
    private static final String UNIT_TEST_TEMPLATE = TEMPLATE_PREFIX + "RESTTest.java.vtl";
    private static final String FUNC_TEST_TEMPLATE = TEMPLATE_PREFIX + "RESTFuncTest.java.vtl";

    //examples
    private static final String EXAMPLE_CLASS_TEMPLATE = TEMPLATE_PREFIX + "Example" + CLASS_TEMPLATE;

    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "rest-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(RESTProperties props) throws Exception
    {
        PluginProjectChangeset ret = new PluginProjectChangeset()
            .with(SERVLET_API,
                              dependency("javax.ws.rs", "jsr311-api", "1.1.1", PROVIDED),
                              dependency("javax.xml.bind", "jaxb-api", "2.3.1", PROVIDED),
                              dependency("com.atlassian.plugins.rest", "atlassian-rest-common", "1.0.2", PROVIDED),
                              dependency("com.atlassian.sal", "sal-api", "2.6.0", PROVIDED),
                              dependency("org.apache.wink", "wink-client", "1.4", TEST),
                              MOCKITO_TEST)
            .with(createModule(props, PLUGIN_MODULE_TEMPLATE));
        
        ClassId mainClass = props.getClassId();

        if (props.includeExamples())
        {
            return ret.with(createClass(props, EXAMPLE_CLASS_TEMPLATE));
        }
        else
        {
            ClassId modelClass = mainClass.classNameSuffix("Model");
            return ret.with(createClassAndTests(props, CLASS_TEMPLATE, UNIT_TEST_TEMPLATE, FUNC_TEST_TEMPLATE))
                .with(createClass(props, modelClass, MODEL_TEMPLATE));
        }
    }

    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
