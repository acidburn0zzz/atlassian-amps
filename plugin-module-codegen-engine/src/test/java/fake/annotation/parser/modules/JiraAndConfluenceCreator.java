package fake.annotation.parser.modules;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.ConfluencePluginModuleCreator;
import com.atlassian.plugins.codegen.annotations.JiraPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.PluginModuleCreator;
import com.atlassian.plugins.codegen.modules.PluginModuleProperties;

/**
 * @since 3.6
 */
@JiraPluginModuleCreator
@ConfluencePluginModuleCreator
public class JiraAndConfluenceCreator implements PluginModuleCreator
{
    public static final String MODULE_NAME = "Jira And Confluence";

    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }

    @Override
    public PluginProjectChangeset createModule(PluginModuleProperties props) throws Exception
    {
        return new PluginProjectChangeset();
    }
}
