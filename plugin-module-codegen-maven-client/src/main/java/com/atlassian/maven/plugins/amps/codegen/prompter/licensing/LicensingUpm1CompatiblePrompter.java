package com.atlassian.maven.plugins.amps.codegen.prompter.licensing;

import com.atlassian.maven.plugins.amps.codegen.annotations.ModuleCreatorClass;
import com.atlassian.maven.plugins.amps.codegen.prompter.AbstractModulePrompter;
import com.atlassian.plugins.codegen.modules.PluginModuleLocation;
import com.atlassian.plugins.codegen.modules.common.licensing.LicensingProperties;
import com.atlassian.plugins.codegen.modules.common.licensing.LicensingUpm2CompatibleModuleCreator;
import com.atlassian.plugins.codegen.util.ClassnameUtil;

import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

/**
 * @since 3.7
 */
@ModuleCreatorClass(LicensingUpm2CompatibleModuleCreator.class)
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
        showExamplesPrompt = true;

        String className = promptJavaClassname("Enter License Servlet Classname", "LicenseServlet");
        String packageName = promptJavaPackagename("Enter Package Name", getDefaultBasePackage() + ".servlet");
        String licenseServletPath = prompt("Enter License Servlet URL Path (not including /plugins/servlet/)",
                                           getPluginKey() + "/license");
        
        LicensingProperties ret = new LicensingProperties(ClassnameUtil.fullyQualifiedName(packageName, className));
        ret.setLicenseServletPath(licenseServletPath);
        return ret;
    }
    
    @Override
    public LicensingProperties getModulePropertiesFromInput(PluginModuleLocation moduleLocation) throws PrompterException
    {
        LicensingProperties ret = super.getModulePropertiesFromInput(moduleLocation);
        if (ret.includeExamples())
        {
            String helloWorldServletPath = prompt("Enter Hello World Servlet URL Path (not including /plugins/servlet/)",
                                                  getPluginKey() + "/licensehelloworld");
            ret.setHelloWorldServletPath(helloWorldServletPath);
        }
        return ret;
    }
}