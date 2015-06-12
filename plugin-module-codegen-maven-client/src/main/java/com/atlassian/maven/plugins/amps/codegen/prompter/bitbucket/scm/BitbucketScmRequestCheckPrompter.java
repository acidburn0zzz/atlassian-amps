package com.atlassian.maven.plugins.amps.codegen.prompter.bitbucket.scm;

import com.atlassian.maven.plugins.amps.codegen.annotations.ModuleCreatorClass;
import com.atlassian.maven.plugins.amps.codegen.prompter.AbstractModulePrompter;
import com.atlassian.plugins.codegen.modules.PluginModuleLocation;
import com.atlassian.plugins.codegen.modules.bitbucket.scm.BitbucketScmRequestCheckModuleCreator;
import com.atlassian.plugins.codegen.modules.bitbucket.scm.BitbucketScmRequestCheckProperties;
import com.atlassian.plugins.codegen.util.ClassnameUtil;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

/**
 * @since 6.1.0
 */
@ModuleCreatorClass(BitbucketScmRequestCheckModuleCreator.class)
public class BitbucketScmRequestCheckPrompter extends AbstractModulePrompter<BitbucketScmRequestCheckProperties>
{

    public BitbucketScmRequestCheckPrompter(Prompter prompter)
    {
        super(prompter);
    }

    @Override
    public BitbucketScmRequestCheckProperties promptForBasicProperties(PluginModuleLocation moduleLocation)
            throws PrompterException
    {
        String className = promptJavaClassname("Enter New Classname", "MyScmRequestCheck");
        String packageName = promptJavaPackagename("Enter Package Name", getDefaultBasePackage() + ".scm");

        String fqClass = ClassnameUtil.fullyQualifiedName(packageName, className);

        return new BitbucketScmRequestCheckProperties(fqClass);
    }

    @Override
    public void promptForAdvancedProperties(BitbucketScmRequestCheckProperties props,
                                            PluginModuleLocation moduleLocation) throws PrompterException
    {
        // 150 puts it in the default request check pipeline after authz but before throttling
        props.setWeight(promptForInt("Weight", 150));
    }

}
