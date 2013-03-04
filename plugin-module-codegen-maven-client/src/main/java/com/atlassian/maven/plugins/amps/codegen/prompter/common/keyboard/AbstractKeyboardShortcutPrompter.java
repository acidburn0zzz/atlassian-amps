package com.atlassian.maven.plugins.amps.codegen.prompter.common.keyboard;

import com.atlassian.maven.plugins.amps.codegen.prompter.AbstractModulePrompter;
import com.atlassian.plugins.codegen.modules.PluginModuleLocation;
import com.atlassian.plugins.codegen.modules.common.keyboard.AbstractKeyboardShortcutProperties;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 3.6
 */
public abstract class AbstractKeyboardShortcutPrompter<T extends AbstractKeyboardShortcutProperties> extends AbstractModulePrompter<T>
{

    public AbstractKeyboardShortcutPrompter(Prompter prompter)
    {
        super(prompter);
    }

    @Override
    public T promptForBasicProperties(PluginModuleLocation moduleLocation) throws PrompterException
    {
        suppressAdvancedNamePrompt();

        String moduleName = promptNotBlank("Enter Keyboard Shortcut Name", "My Keyboard Shortcut");

        T props = createProperties(moduleName);
        props.setShortcut(promptNotBlank("Enter Shortcut Character"));
        props.setContext(promptForContext(props));
        props.setOperationType(promptForOperation());
        props.setOperationValue(promptNotBlank("Enter Operation Value"));
        return props;
    }

    @Override
    public void promptForAdvancedProperties(AbstractKeyboardShortcutProperties props, PluginModuleLocation moduleLocation) throws PrompterException
    {
        props.setHidden(promptForBoolean("Hidden?", "N"));
        props.setOrder(promptForInt("Order", 10));
    }

    private String promptForOperation() throws PrompterException
    {
        String operation = "";
        List<String> operations = AbstractKeyboardShortcutProperties.OPERATIONS;
        StringBuilder operationQuery = new StringBuilder("Choose An Operation\n");
        List<String> indexChoices = new ArrayList<String>(operations.size());
        Map<String, String> indexedValues = new HashMap<String, String>();
        int index = 1;
        String strIndex;
        for (String entry : operations)
        {
            strIndex = Integer.toString(index);
            operationQuery.append(strIndex + ": " + entry + "\n");
            indexChoices.add(strIndex);
            indexedValues.put(strIndex, entry);
            index++;
        }

        operationQuery.append("Choose a number: ");
        String operationAnswer = prompt(operationQuery.toString(), indexChoices, "");
        operation = indexedValues.get(operationAnswer);

        return operation;
    }

    private String promptForContext(T props) throws PrompterException
    {
        String context = "";
        List<String> contexts = props.getAllowedContexts();
        StringBuilder contextQuery = new StringBuilder("Choose A Context\n");
        List<String> indexChoices = new ArrayList<String>(contexts.size());
        Map<String, String> indexedValues = new HashMap<String, String>();
        int index = 1;
        String strIndex;
        for (String entry : contexts)
        {
            strIndex = Integer.toString(index);
            contextQuery.append(strIndex + ": " + entry + "\n");
            indexChoices.add(strIndex);
            indexedValues.put(strIndex, entry);
            index++;
        }

        contextQuery.append("Choose a number: ");
        String contextAnswer = prompt(contextQuery.toString(), indexChoices, "");
        context = indexedValues.get(contextAnswer);

        return context;
    }

    protected abstract T createProperties(String moduleName);

}