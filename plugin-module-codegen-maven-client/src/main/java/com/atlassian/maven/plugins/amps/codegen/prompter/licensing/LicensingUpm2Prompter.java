package com.atlassian.maven.plugins.amps.codegen.prompter.licensing;

import com.atlassian.maven.plugins.amps.codegen.annotations.ModuleCreatorClass;
import com.atlassian.maven.plugins.amps.codegen.prompter.AbstractModulePrompter;
import com.atlassian.plugins.codegen.modules.PluginModuleLocation;
import com.atlassian.plugins.codegen.modules.common.licensing.LicensingUpm2ModuleCreator;
import com.atlassian.plugins.codegen.modules.common.licensing.LicensingProperties;
import com.atlassian.plugins.codegen.util.ClassnameUtil;

import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

/**
 * @since 3.7
 */
@ModuleCreatorClass(LicensingUpm2ModuleCreator.class)
public class LicensingUpm2Prompter extends AbstractModulePrompter<LicensingProperties>
{

    public LicensingUpm2Prompter(Prompter prompter)
    {
        super(prompter);
        showAdvancedPrompt = false;
        showExamplesPrompt = false;
    }

    @Override
    public LicensingProperties promptForBasicProperties(PluginModuleLocation moduleLocation) throws PrompterException
    {
        String className = promptJavaClassname("Enter License Checker Class name", "LicenseChecker");
        String packageName = promptJavaPackagename("Enter Package Name", getDefaultBasePackage());

        LicensingProperties props = new LicensingProperties(ClassnameUtil.fullyQualifiedName(packageName, className));

        suppressExamplesPrompt();

        return props;
    }
}