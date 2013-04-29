package com.atlassian.plugins.codegen.modules.confluence.blueprint;

/**
 * Creates strings for the {@link BlueprintBuilder} by convention. This class is separate to the generator class to
 * provide a single point of change if we alter the naming conventions.
 *
 * @since 4.1.7
 */
public class BlueprintStringer
{
    private final String indexKey;
    private final String pluginKey;

    public BlueprintStringer(String indexKey, String pluginKey)
    {
        this.indexKey = indexKey;
        this.pluginKey = pluginKey;
    }

    public String makeBlueprintModuleKey()
    {
        return indexKey + "-blueprint";
    }

    public String makeContentTemplateKey(int templateIndex)
    {
        String key = indexKey + "-template";
        if (templateIndex > 0)
        {
            key += "-" + templateIndex;
        }
        return key;
    }

    public String makeBlueprintModuleName(String blueprintName)
    {
        return blueprintName + " Blueprint";
    }

    public String makeSoyTemplatePackage(String blueprintName)
    {
        // TODO - better util for cleaning up user-entered data. dT
        return "Confluence.Blueprints.Plugin." + blueprintName.replaceAll("\\W", "");
    }

    public String makeContentTemplateName(String webItemName, int counter)
    {
        return webItemName + " Content Template " + counter;
    }

    public String makeI18nKey(String suffix)
    {
        return pluginKey + "." + suffix;
    }
}
