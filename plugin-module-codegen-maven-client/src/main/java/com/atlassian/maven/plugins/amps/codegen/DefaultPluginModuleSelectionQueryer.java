package com.atlassian.maven.plugins.amps.codegen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.plugins.codegen.modules.PluginModuleCreator;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.shared.utils.logging.MessageBuilder;
import org.apache.maven.shared.utils.logging.MessageUtils;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 *
 */
public class DefaultPluginModuleSelectionQueryer extends AbstractLogEnabled implements PluginModuleSelectionQueryer
{
    public static final List<String> YN_ANSWERS = new ArrayList<>(Arrays.asList("Y", "y", "N", "n"));

    private Prompter prompter;

    public DefaultPluginModuleSelectionQueryer()
    {
    }

    @Override
    public PluginModuleCreator selectModule(Map<Class, PluginModuleCreator> map) throws PrompterException
    {
        MessageBuilder builder = MessageUtils.buffer()
                .strong("Choose Plugin Module:")
                .newline();

        List<String> answers = new ArrayList<>();
        Map<String, PluginModuleCreator> moduleAnswerMap = new HashMap<>();

        int counter = 1;

        for (Map.Entry<Class, PluginModuleCreator> entry : map.entrySet())
        {
            PluginModuleCreator moduleCreator = entry.getValue();

            String answer = String.valueOf(counter);
            builder.strong(answer);
            if (counter < 10)
            {
                builder.a(":  ");
            }
            else
            {
                builder.a(": ");
            }
            builder.a(entry.getValue().getModuleName())
                    .newline();

            answers.add(answer);

            moduleAnswerMap.put(answer, moduleCreator);

            counter++;
        }

        builder.strong("Choose a number");

        String answer = prompter.prompt(builder.toString(), answers);

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
