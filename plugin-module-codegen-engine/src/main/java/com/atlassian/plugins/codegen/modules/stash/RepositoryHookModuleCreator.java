package com.atlassian.plugins.codegen.modules.stash;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.StashPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static com.atlassian.plugins.codegen.PluginProjectChangeset.changeset;
import static com.atlassian.plugins.codegen.ResourceFile.resourceFile;
import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;

@StashPluginModuleCreator
public class RepositoryHookModuleCreator extends AbstractPluginModuleCreator<RepositoryHookProperties>
{

    public static final String MODULE_NAME = "Repository Hook";

    private static final String TEMPLATE_PREFIX = "templates/stash/repository-hook/";

    private static final String PRE_RECEIVE_TEMPLATE = TEMPLATE_PREFIX + "PreReceiveRepositoryHook.java.vtl";
    private static final String ASYNC_POST_RECEIVE_TEMPLATE = TEMPLATE_PREFIX + "AsyncPostReceiveRepositoryHook.java.vtl";
    private static final String MERGE_CHECK_TEMPLATE = TEMPLATE_PREFIX + "MergeCheckRepositoryHook.java.vtl";
    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "repository-hook-plugin.xml.vtl";
    private static final String SOY_TEMPLATE = TEMPLATE_PREFIX + "repository-hook.soy.vtl";

    public static final String TYPE_PRE = "pre";
    public static final String TYPE_POST = "post";
    public static final String TYPE_MERGE_CHECK = "merge";
    public static final String TYPE_DEFAULT = TYPE_POST;
    public static final List<String> TYPES = Arrays.asList(TYPE_PRE, TYPE_POST, TYPE_MERGE_CHECK);

    @Override
    public PluginProjectChangeset createModule(RepositoryHookProperties props) throws Exception
    {
        PluginProjectChangeset hookClass = createClass(props, props.getClassId(), getTemplate(props.getType()));
        PluginProjectChangeset changeset = new PluginProjectChangeset()
                .with(MOCKITO_TEST)
                .with(createModule(props, PLUGIN_MODULE_TEMPLATE))
                .with(hookClass);
        if (props.getIcon() != null)
        {
            InputStream in = getClass().getClassLoader().getResourceAsStream(TEMPLATE_PREFIX + props.getIcon());
            changeset = changeset.with(changeset().with(resourceFile("", props.getIcon(), IOUtils.toByteArray(in))));
        }
        if (props.ifConfigured())
        {
            // Name of the file isn't referenced by atlassian-plugin.xml
            changeset = changeset.with(createResource(props, "static", "repository-hook.soy", SOY_TEMPLATE));
        }
        return changeset;
    }

    private String getTemplate(String type)
    {
        String template;
        if (TYPE_PRE.equals(type))
        {
            template = PRE_RECEIVE_TEMPLATE;
        }
        else if (TYPE_POST.equals(type))
        {
            template = ASYNC_POST_RECEIVE_TEMPLATE;
        }
        else if (TYPE_MERGE_CHECK.equals(type))
        {
            template = MERGE_CHECK_TEMPLATE;
        }
        else
        {
            throw new RuntimeException("Unsupported type: " + type);
        }
        return template;
    }

    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }

}
