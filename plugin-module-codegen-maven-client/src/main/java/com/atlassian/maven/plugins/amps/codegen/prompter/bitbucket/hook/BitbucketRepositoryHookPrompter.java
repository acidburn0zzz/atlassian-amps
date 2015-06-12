package com.atlassian.maven.plugins.amps.codegen.prompter.bitbucket.hook;

import com.atlassian.maven.plugins.amps.codegen.annotations.ModuleCreatorClass;
import com.atlassian.maven.plugins.amps.codegen.prompter.AbstractModulePrompter;
import com.atlassian.plugins.codegen.modules.PluginModuleLocation;
import com.atlassian.plugins.codegen.modules.bitbucket.hook.BitbucketRepositoryHookModuleCreator;
import com.atlassian.plugins.codegen.modules.bitbucket.hook.BitbucketRepositoryHookProperties;
import com.atlassian.plugins.codegen.util.ClassnameUtil;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

/**
 * @since 6.1.0
 */
@ModuleCreatorClass(BitbucketRepositoryHookModuleCreator.class)
public class BitbucketRepositoryHookPrompter extends AbstractModulePrompter<BitbucketRepositoryHookProperties>
{

    public BitbucketRepositoryHookPrompter(Prompter prompter)
    {
        super(prompter);
    }

    @Override
    public BitbucketRepositoryHookProperties promptForBasicProperties(PluginModuleLocation moduleLocation)
            throws PrompterException
    {
        String type = prompt("Enter Hook Type", BitbucketRepositoryHookModuleCreator.TYPES,
                BitbucketRepositoryHookModuleCreator.TYPE_DEFAULT);
        String className = promptJavaClassname("Enter New Classname",
                BitbucketRepositoryHookModuleCreator.DEFAULT_CLASS_NAME_BY_TYPE.get(type));
        String packageName = promptJavaPackagename("Enter Package Name", getDefaultBasePackage() + ".hook");

        String fqClass = ClassnameUtil.fullyQualifiedName(packageName, className);

        return new BitbucketRepositoryHookProperties(fqClass, type);
    }

    @Override
    public void promptForAdvancedProperties(BitbucketRepositoryHookProperties props,
                                            PluginModuleLocation moduleLocation) throws PrompterException
    {
        props.setIcon(promptForBoolean("Custom Icon", "Y"));
        props.setConfigured(false); // Clear the 'fields'
        props.setConfigured(promptForBoolean("Custom Configuration", "Y"));
    }
}
