package com.atlassian.maven.plugins.amps.codegen.prompter;

// This is a MODIFIED VERSION of org.codehaus.plexus.components.interactivity.DefaultPrompter which is under the MIT License
/*
 * The MIT License
 *
 * Copyright (c) 2005, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.shared.utils.logging.MessageBuilder;
import org.apache.maven.shared.utils.logging.MessageUtils;
import org.codehaus.plexus.components.interactivity.InputHandler;
import org.codehaus.plexus.components.interactivity.OutputHandler;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.codehaus.plexus.util.StringUtils;

/**
 * @since 3.6
 */
public class PrettyPrompter implements Prompter
{
    /**
     * @requirement
     */
    private OutputHandler outputHandler;

    /**
     * @requirement
     */
    private InputHandler inputHandler;

    public PrettyPrompter()
    {
    }

    public String prompt(String message)
            throws PrompterException
    {
        try
        {
            writePrompt(message);
        } catch (IOException e)
        {
            throw new PrompterException("Failed to present prompt", e);
        }

        try
        {
            return inputHandler.readLine();
        } catch (IOException e)
        {
            throw new PrompterException("Failed to read user response", e);
        }
    }

    public String prompt(String message, String defaultReply)
            throws PrompterException
    {
        try
        {
            writePrompt(formatMessage(message, null, defaultReply));
        } catch (IOException e)
        {
            throw new PrompterException("Failed to present prompt", e);
        }

        try
        {
            String line = inputHandler.readLine();

            if (StringUtils.isEmpty(line))
            {
                line = defaultReply;
            }

            return line;
        } catch (IOException e)
        {
            throw new PrompterException("Failed to read user response", e);
        }
    }

    public String prompt(String message, List possibleValues, String defaultReply)
            throws PrompterException
    {
        String formattedMessage = formatMessage(message, possibleValues, defaultReply);

        String line;

        do
        {
            try
            {
                writePrompt(formattedMessage);
            } catch (IOException e)
            {
                throw new PrompterException("Failed to present prompt", e);
            }

            try
            {
                line = inputHandler.readLine();
            } catch (IOException e)
            {
                throw new PrompterException("Failed to read user response", e);
            }

            if (StringUtils.isEmpty(line))
            {
                line = defaultReply;
            }

            if (line != null && !possibleValues.contains(line))
            {
                try
                {
                    String invalid = MessageUtils.buffer()
                            .failure("Invalid selection.")
                            .toString();
                    outputHandler.writeLine(invalid);
                } catch (IOException e)
                {
                    throw new PrompterException("Failed to present feedback", e);
                }
            }
        }
        while (line == null || !possibleValues.contains(line));

        return line;
    }

    public String prompt(String message, List possibleValues)
            throws PrompterException
    {
        return prompt(message, possibleValues, null);
    }

    public String promptForPassword(String message)
            throws PrompterException
    {
        try
        {
            writePrompt(message);
        } catch (IOException e)
        {
            throw new PrompterException("Failed to present prompt", e);
        }

        try
        {
            return inputHandler.readPassword();
        } catch (IOException e)
        {
            throw new PrompterException("Failed to read user response", e);
        }
    }

    protected String formatMessage(String message, List possibleValues, String defaultReply)
    {
        MessageBuilder builder = MessageUtils.buffer().a(message);

        if (possibleValues != null && !possibleValues.isEmpty())
        {
            builder.a(" (");

            for (Iterator it = possibleValues.iterator(); it.hasNext(); )
            {
                String possibleValue = (String) it.next();

                builder.strong(possibleValue);
                if (it.hasNext())
                {
                    builder.a("/");
                }
            }

            builder.a(")");
        }

        if (defaultReply != null)
        {
            builder.success(" [")
                    .success(defaultReply)
                    .success("]");
        }

        return builder.toString();
    }

    private void writePrompt(String message)
            throws IOException
    {
        outputHandler.write(message + ": ");
    }

    public void showMessage(String message)
            throws PrompterException
    {
        try
        {
            writePrompt(message);
        } catch (IOException e)
        {
            throw new PrompterException("Failed to present prompt", e);
        }

    }
}
