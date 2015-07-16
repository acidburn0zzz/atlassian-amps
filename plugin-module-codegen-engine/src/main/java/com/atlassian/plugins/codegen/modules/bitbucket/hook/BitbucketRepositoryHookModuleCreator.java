package com.atlassian.plugins.codegen.modules.bitbucket.hook;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.BitbucketPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static com.atlassian.plugins.codegen.PluginProjectChangeset.changeset;
import static com.atlassian.plugins.codegen.ResourceFile.resourceFile;
import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;

/**
 * @since 6.1.0
 */
@BitbucketPluginModuleCreator
public class BitbucketRepositoryHookModuleCreator extends AbstractPluginModuleCreator<BitbucketRepositoryHookProperties>
{

    public static final String MODULE_NAME = "Repository Hook";
    public static final String TYPE_PRE = "pre";
    public static final String TYPE_POST = "post";
    public static final String TYPE_MERGE_CHECK = "merge";
    public static final String TYPE_DEFAULT = TYPE_POST;
    public static final Map<String, String> DEFAULT_CLASS_NAME_BY_TYPE = ImmutableMap.of(
            TYPE_PRE, "MyPreReceiveRepositoryHook",
            TYPE_POST, "MyPostReceiveRepositoryHook",
            TYPE_MERGE_CHECK, "MyMergeCheckHook");
    public static final List<String> TYPES = ImmutableList.copyOf(DEFAULT_CLASS_NAME_BY_TYPE.keySet());

    private static final String TEMPLATE_PREFIX = "templates/bitbucket/repository-hook/";
    private static final String PRE_RECEIVE_TEMPLATE = TEMPLATE_PREFIX + "PreReceiveRepositoryHook.java.vtl";
    private static final String ASYNC_POST_RECEIVE_TEMPLATE = TEMPLATE_PREFIX + "AsyncPostReceiveRepositoryHook.java.vtl";
    private static final String MERGE_CHECK_TEMPLATE = TEMPLATE_PREFIX + "MergeCheckRepositoryHook.java.vtl";
    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "repository-hook-plugin.xml.vtl";
    private static final String SOY_TEMPLATE = TEMPLATE_PREFIX + "repository-hook.soy.vtl";

    @Override
    public PluginProjectChangeset createModule(BitbucketRepositoryHookProperties props) throws Exception
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
            changeset = changeset.with(createResource(props, "static", props.getSoyFile(), SOY_TEMPLATE));
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
