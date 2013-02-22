package com.atlassian.maven.plugins.amps.codegen.prompter.stash.scm;

import com.atlassian.maven.plugins.amps.codegen.annotations.ModuleCreatorClass;
import com.atlassian.maven.plugins.amps.codegen.prompter.AbstractModulePrompter;
import com.atlassian.plugins.codegen.modules.PluginModuleLocation;
import com.atlassian.plugins.codegen.modules.stash.scm.ScmRequestCheckModuleCreator;
import com.atlassian.plugins.codegen.modules.stash.scm.ScmRequestCheckProperties;
import com.atlassian.plugins.codegen.util.ClassnameUtil;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

@ModuleCreatorClass(ScmRequestCheckModuleCreator.class)
public class ScmRequestCheckPrompter extends AbstractModulePrompter<ScmRequestCheckProperties> {

    public ScmRequestCheckPrompter(Prompter prompter) {
        super(prompter);
    }

    @Override
    public ScmRequestCheckProperties promptForBasicProperties(PluginModuleLocation moduleLocation) throws PrompterException {
        String className = promptJavaClassname("Enter New Classname", "MyScmRequestCheck");
        String packageName = promptJavaPackagename("Enter Package Name", getDefaultBasePackage() + ".scm");

        String fqClass = ClassnameUtil.fullyQualifiedName(packageName, className);

        return new ScmRequestCheckProperties(fqClass);
    }

    @Override
    public void promptForAdvancedProperties(ScmRequestCheckProperties props, PluginModuleLocation moduleLocation) throws PrompterException {
        // 150 puts it in the default request check pipeline after authz but before throttling
        props.setWeight(promptForInt("Weight", 150));
    }

}
