package com.atlassian.maven.plugins.amps.codegen;

import java.util.*;

import com.atlassian.plugins.codegen.modules.PluginModuleCreator;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

/**
 *
 */
public class DefaultPluginModuleSelectionQueryer extends AbstractLogEnabled implements PluginModuleSelectionQueryer
{
    public static final List<String> YN_ANSWERS = new ArrayList<>(Arrays.asList("Y", "y", "N", "n"));
    private Prompter prompter;
    private boolean useAnsiColor;

    public DefaultPluginModuleSelectionQueryer()
    {
        String mavencolor = System.getenv("MAVEN_COLOR");
        if (mavencolor != null && !mavencolor.equals(""))
        {
            useAnsiColor = Boolean.parseBoolean(mavencolor);
        }
        else
        {
            useAnsiColor = false;
        }
    }

    @Override
    public PluginModuleCreator selectModule(Map<Class, PluginModuleCreator> map) throws PrompterException
    {
        if (useAnsiColor)
        {
            return getAnsiModule(map);
        }
        else
        {
            return getPlainModule(map);
        }
    }

    private PluginModuleCreator getAnsiModule(Map<Class, PluginModuleCreator> map) throws PrompterException
    {
        AttributedStringBuilder builder = new AttributedStringBuilder()
                .styled(AttributedStyle.BOLD, "Choose Plugin Module:\n");

        List<String> answers = new ArrayList<>();
        Map<String, PluginModuleCreator> moduleAnswerMap = new HashMap<>();

        int counter = 1;

        for (Map.Entry<Class, PluginModuleCreator> entry : map.entrySet())
        {
            PluginModuleCreator moduleCreator = entry.getValue();

            String answer = String.valueOf(counter);
            builder.styled(AttributedStyle.BOLD, answer);
            if (counter < 10)
            {
                builder.append(":  ");
            }
            else
            {
                builder.append(": ");
            }
            builder.append(entry.getValue().getModuleName())
                    .append("\n");

            answers.add(answer);

            moduleAnswerMap.put(answer, moduleCreator);

            counter++;
        }

        builder.styled(AttributedStyle.BOLD, "Choose a number");

        String answer = prompter.prompt(builder.toAnsi(), answers);

        return moduleAnswerMap.get(answer);
    }

    private PluginModuleCreator getPlainModule(Map<Class, PluginModuleCreator> map) throws PrompterException
    {
        StringBuilder query = new StringBuilder("Choose Plugin Module:\n");

        List<String> answers = new ArrayList<>();
        Map<String, PluginModuleCreator> moduleAnswerMap = new HashMap<>();

        int counter = 1;

        for (Map.Entry<Class, PluginModuleCreator> entry : map.entrySet())
        {
            PluginModuleCreator moduleCreator = entry.getValue();

            String answer = String.valueOf(counter);
            if (counter < 10)
            {
                query.append(answer).append(":  ");
            } else
            {
                query.append(answer).append(": ");
            }
            query.append(entry.getValue().getModuleName()).append("\n");

            answers.add(answer);

            moduleAnswerMap.put(answer, moduleCreator);

            counter++;
        }

        query.append("Choose a number");

        String answer = prompter.prompt(query.toString(), answers);

        return moduleAnswerMap.get(answer);
    }

    @Override
    public boolean addAnotherModule() throws PrompterException
    {
        return promptForBoolean("Add Another Plugin Module?", "N");
    }

    public void setPrompter(Prompter prompter)
    {
        this.prompter = prompter;
    }

    protected boolean promptForBoolean(String message, String defaultValue) throws PrompterException
    {
        String answer;
        if (StringUtils.isBlank(defaultValue))
        {
            answer = prompter.prompt(message, YN_ANSWERS);
        }
        else
        {
            answer = prompter.prompt(message, YN_ANSWERS, defaultValue);
        }

        return "y".equalsIgnoreCase(answer);
    }
}
