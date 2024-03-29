package com.atlassian.maven.plugins.amps.codegen.prompter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.atlassian.plugins.codegen.modules.PluginModuleLocation;
import com.atlassian.plugins.codegen.modules.PluginModuleProperties;

import org.codehaus.plexus.components.interactivity.PrompterException;

/**
 *
 */
public interface PluginModulePrompter<T extends PluginModuleProperties>
{

    public static final List<String> YN_ANSWERS = new ArrayList<String>(Arrays.asList("Y", "y", "N", "n"));
    public static final List<String> ANDOR_ANSWERS = new ArrayList<String>(Arrays.asList("AND", "and", "OR", "or"));

    T getModulePropertiesFromInput(PluginModuleLocation moduleLocation) throws PrompterException;

    T promptForBasicProperties(PluginModuleLocation moduleLocation) throws PrompterException;

    void promptForAdvancedProperties(T props, PluginModuleLocation moduleLocation) throws PrompterException;

    void setDefaultBasePackage(String basePackage);

    String getDefaultBasePackage();

    void setPluginKey(String pluginKey);
    
    String getPluginKey();
}
