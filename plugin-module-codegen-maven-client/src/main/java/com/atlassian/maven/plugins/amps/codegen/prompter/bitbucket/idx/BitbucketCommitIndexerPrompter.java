package com.atlassian.maven.plugins.amps.codegen.prompter.bitbucket.idx;

import com.atlassian.maven.plugins.amps.codegen.annotations.ModuleCreatorClass;
import com.atlassian.maven.plugins.amps.codegen.prompter.AbstractModulePrompter;
import com.atlassian.plugins.codegen.modules.PluginModuleLocation;
import com.atlassian.plugins.codegen.modules.bitbucket.idx.BitbucketCommitIndexerModuleCreator;
import com.atlassian.plugins.codegen.modules.bitbucket.idx.BitbucketCommitIndexerProperties;
import com.atlassian.plugins.codegen.util.ClassnameUtil;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

/**
 * @since 6.1.0
 */
@ModuleCreatorClass(BitbucketCommitIndexerModuleCreator.class)
public class BitbucketCommitIndexerPrompter extends AbstractModulePrompter<BitbucketCommitIndexerProperties>
{

    public BitbucketCommitIndexerPrompter(Prompter prompter)
    {
        super(prompter);
    }

    @Override
    public BitbucketCommitIndexerProperties promptForBasicProperties(PluginModuleLocation moduleLocation)
            throws PrompterException
    {
        String className = promptJavaClassname("Enter New Classname", "MyChangesetIndexer");
        String packageName = promptJavaPackagename("Enter Package Name", getDefaultBasePackage() + ".idx");

        String fqClass = ClassnameUtil.fullyQualifiedName(packageName, className);

        return new BitbucketCommitIndexerProperties(fqClass);
    }

}
