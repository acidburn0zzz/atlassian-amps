package com.atlassian.maven.plugins.amps.codegen.prompter.licensing;

import com.atlassian.maven.plugins.amps.codegen.annotations.ModuleCreatorClass;
import com.atlassian.maven.plugins.amps.codegen.prompter.AbstractModulePrompter;
import com.atlassian.plugins.codegen.modules.PluginModuleLocation;
import com.atlassian.plugins.codegen.modules.common.licensing.LicensingProperties;
import com.atlassian.plugins.codegen.modules.common.licensing.LicensingUpm1CompatibleModuleCreator;

import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

/**
 * @since 3.7
 */
@ModuleCreatorClass(LicensingUpm1CompatibleModuleCreator.class)
public class LicensingUpm1CompatiblePrompter extends AbstractModulePrompter<LicensingProperties>
{
    public LicensingUpm1CompatiblePrompter(Prompter prompter)
    {
        super(prompter);
        showAdvancedPrompt = false;
    }

    @Override
    public LicensingProperties promptForBasicProperties(PluginModuleLocation moduleLocation) throws PrompterException
    {
        String packageName = promptJavaPackagename("Enter Servlet Package Name", getDefaultBasePackage() + ".servlet");
        return new LicensingProperties(packageName + ".LicenseServlet");
    }
}