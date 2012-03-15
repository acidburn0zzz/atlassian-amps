package fake.jar.annotation.parser.modules.nested;

import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.annotations.JiraPluginModuleCreator;
import com.atlassian.plugins.codegen.modules.PluginModuleCreator;
import com.atlassian.plugins.codegen.modules.PluginModuleProperties;

/**
 * @since 3.5
 */
@JiraPluginModuleCreator
public class JARNestedValidJira implements PluginModuleCreator<PluginModuleProperties> {
    public static final String MODULE_NAME = "Nested Valid Jira Module";

    @Override
    public String getModuleName() {
        return MODULE_NAME;
    }

    @Override
    public PluginProjectChangeset createModule(PluginModuleProperties props) throws Exception {
        return new PluginProjectChangeset();
    }
}
