package com.atlassian.maven.plugins.amps.codegen.prompter.bitbucket.ssh;

import com.atlassian.maven.plugins.amps.codegen.annotations.ModuleCreatorClass;
import com.atlassian.maven.plugins.amps.codegen.prompter.AbstractModulePrompter;
import com.atlassian.plugins.codegen.modules.PluginModuleLocation;
import com.atlassian.plugins.codegen.modules.bitbucket.ssh.BitbucketSshScmRequestHandlerModuleCreator;
import com.atlassian.plugins.codegen.modules.bitbucket.ssh.BitbucketSshScmRequestHandlerProperties;
import com.atlassian.plugins.codegen.util.ClassnameUtil;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

/**
 * @since 6.1.0
 */
@ModuleCreatorClass(BitbucketSshScmRequestHandlerModuleCreator.class)
public class BitbucketSshScmRequestHandlerPrompter extends AbstractModulePrompter<BitbucketSshScmRequestHandlerProperties>
{

    public BitbucketSshScmRequestHandlerPrompter(Prompter prompter)
    {
        super(prompter);
    }

    @Override
    public BitbucketSshScmRequestHandlerProperties promptForBasicProperties(PluginModuleLocation moduleLocation)
            throws PrompterException
    {
        String className = promptJavaClassname("Enter New Classname", "MySshRequest");
        String packageName = promptJavaPackagename("Enter Package Name", getDefaultBasePackage() + ".ssh");

        String fqClass = ClassnameUtil.fullyQualifiedName(packageName, className);

        return new BitbucketSshScmRequestHandlerProperties(fqClass);
    }

}
