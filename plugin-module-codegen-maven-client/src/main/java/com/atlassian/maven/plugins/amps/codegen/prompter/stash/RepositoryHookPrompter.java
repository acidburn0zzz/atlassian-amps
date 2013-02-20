package com.atlassian.maven.plugins.amps.codegen.prompter.stash;

import com.atlassian.maven.plugins.amps.codegen.annotations.ModuleCreatorClass;
import com.atlassian.maven.plugins.amps.codegen.prompter.AbstractModulePrompter;
import com.atlassian.plugins.codegen.modules.PluginModuleLocation;
import com.atlassian.plugins.codegen.modules.stash.RepositoryHookModuleCreator;
import com.atlassian.plugins.codegen.modules.stash.RepositoryHookProperties;
import com.atlassian.plugins.codegen.util.ClassnameUtil;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

@ModuleCreatorClass(RepositoryHookModuleCreator.class)
public class RepositoryHookPrompter extends AbstractModulePrompter<RepositoryHookProperties>
{
    public RepositoryHookPrompter(Prompter prompter)
    {
        super(prompter);
    }

    @Override
    public RepositoryHookProperties promptForBasicProperties(PluginModuleLocation moduleLocation) throws PrompterException
    {
        String className = promptJavaClassname("Enter New Classname", "MyRepositoryHook");
        String packageName = promptJavaPackagename("Enter Package Name", getDefaultBasePackage() + ".hook");
        String type = prompt("Enter Hook Type", RepositoryHookModuleCreator.TYPES, RepositoryHookModuleCreator.TYPE_DEFAULT);

        String fqClass = ClassnameUtil.fullyQualifiedName(packageName, className);

        return new RepositoryHookProperties(fqClass, type);
    }

    @Override
    public void promptForAdvancedProperties(RepositoryHookProperties props, PluginModuleLocation moduleLocation) throws PrompterException
    {
        props.setIcon(promptForBoolean("Custom Icon", "Y"));
        props.setConfigured(false); // Clear the 'fields'
        props.setConfigured(promptForBoolean("Custom Configuration", "Y"));
    }
}
