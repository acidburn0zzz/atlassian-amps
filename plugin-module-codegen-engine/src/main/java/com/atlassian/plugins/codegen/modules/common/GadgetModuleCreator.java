package com.atlassian.plugins.codegen.modules.common;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.ConfluencePluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.FeCruPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.JiraPluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.RefAppPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.AbstractPluginModuleCreator;

import org.apache.commons.io.FilenameUtils;

import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;

/**
 * @since 3.6
 */
@RefAppPluginModuleCreator
@JiraPluginModuleCreator
@ConfluencePluginModuleCreator
@FeCruPluginModuleCreator
public class GadgetModuleCreator extends AbstractPluginModuleCreator<GadgetProperties>
{

    public static final String MODULE_NAME = "Gadget Plugin Module";
    private static final String TEMPLATE_PREFIX = "templates/common/gadget/";

    //stub
    private static final String GADGET_TEMPLATE = TEMPLATE_PREFIX + "gadget.xml.vtl";

    private static final String PLUGIN_MODULE_TEMPLATE = TEMPLATE_PREFIX + "gadget-plugin.xml.vtl";

    @Override
    public PluginProjectChangeset createModule(GadgetProperties props) throws Exception
    {
        String gadgetLocation = props.getLocation();
        String gadgetFilename = FilenameUtils.getName(gadgetLocation);
        String gadgetPath = FilenameUtils.getPath(gadgetLocation);

        return new PluginProjectChangeset()
            .with(MOCKITO_TEST)
            .with(createModule(props, PLUGIN_MODULE_TEMPLATE))
            .with(createResource(props, gadgetPath, gadgetFilename, GADGET_TEMPLATE));
    }

    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }
}
