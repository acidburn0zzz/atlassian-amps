package com.atlassian.maven.plugins.amps.codegen.prompter.stash.ssh;

import com.atlassian.maven.plugins.amps.codegen.annotations.ModuleCreatorClass;
import com.atlassian.maven.plugins.amps.codegen.prompter.AbstractModulePrompter;
import com.atlassian.plugins.codegen.modules.PluginModuleLocation;
import com.atlassian.plugins.codegen.modules.stash.ssh.SshScmRequestHandlerModuleCreator;
import com.atlassian.plugins.codegen.modules.stash.ssh.SshScmRequestHandlerProperties;
import com.atlassian.plugins.codegen.util.ClassnameUtil;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

@ModuleCreatorClass(SshScmRequestHandlerModuleCreator.class)
public class SshScmRequestHandlerPrompter extends AbstractModulePrompter<SshScmRequestHandlerProperties>
{

    public SshScmRequestHandlerPrompter(Prompter prompter)
    {
        super(prompter);
    }

    @Override
    public SshScmRequestHandlerProperties promptForBasicProperties(PluginModuleLocation moduleLocation) throws PrompterException
    {
        String className = promptJavaClassname("Enter New Classname", "MySshRequest");
        String packageName = promptJavaPackagename("Enter Package Name", getDefaultBasePackage() + ".ssh");

        String fqClass = ClassnameUtil.fullyQualifiedName(packageName, className);

        return new SshScmRequestHandlerProperties(fqClass);
    }

}
