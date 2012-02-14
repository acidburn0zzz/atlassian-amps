package fake.annotation.parser.modules.dependencies;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.JiraPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.PluginModuleCreator;
import com.atlassian.plugins.codegen.modules.PluginModuleProperties;

import static com.atlassian.plugins.codegen.modules.Dependencies.MOCKITO_TEST;
import static com.atlassian.plugins.codegen.modules.Dependencies.SERVLET_API;

/**
 * @since 3.6
 */
@JiraPluginModuleCreator
public class ValidJiraWithDependencies implements PluginModuleCreator
{
    public static final String MODULE_NAME = "Valid Jira Module With Dependencies";

    @Override
    public String getModuleName()
    {
        return MODULE_NAME;
    }

    @Override
    public PluginProjectChangeset createModule(PluginModuleProperties props) throws Exception
    {
        return new PluginProjectChangeset()
            .withDependencies(SERVLET_API, MOCKITO_TEST);
    }
}