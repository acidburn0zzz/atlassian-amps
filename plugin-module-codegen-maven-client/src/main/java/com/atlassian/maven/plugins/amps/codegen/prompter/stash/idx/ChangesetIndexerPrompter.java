package com.atlassian.maven.plugins.amps.codegen.prompter.stash.idx;

import com.atlassian.maven.plugins.amps.codegen.annotations.ModuleCreatorClass;
import com.atlassian.maven.plugins.amps.codegen.prompter.AbstractModulePrompter;
import com.atlassian.plugins.codegen.modules.PluginModuleLocation;
import com.atlassian.plugins.codegen.modules.stash.idx.ChangesetIndexerModuleCreator;
import com.atlassian.plugins.codegen.modules.stash.idx.ChangesetIndexerProperties;
import com.atlassian.plugins.codegen.util.ClassnameUtil;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

@ModuleCreatorClass(ChangesetIndexerModuleCreator.class)
public class ChangesetIndexerPrompter extends AbstractModulePrompter<ChangesetIndexerProperties>
{

    public ChangesetIndexerPrompter(Prompter prompter)
    {
        super(prompter);
    }

    @Override
    public ChangesetIndexerProperties promptForBasicProperties(PluginModuleLocation moduleLocation) throws PrompterException
    {
        String className = promptJavaClassname("Enter New Classname", "MyChangesetIndexer");
        String packageName = promptJavaPackagename("Enter Package Name", getDefaultBasePackage() + ".idx");

        String fqClass = ClassnameUtil.fullyQualifiedName(packageName, className);

        return new ChangesetIndexerProperties(fqClass);
    }

}
