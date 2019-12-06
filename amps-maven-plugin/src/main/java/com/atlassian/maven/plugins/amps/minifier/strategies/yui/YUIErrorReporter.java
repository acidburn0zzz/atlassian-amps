package com.atlassian.maven.plugins.amps.minifier.strategies.yui;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.apache.maven.plugin.logging.Log;

/**
 * @since version
 */
public class YUIErrorReporter implements ErrorReporter
{
    private final Log log;

    public YUIErrorReporter(Log log)
    {
        this.log = log;
    }

    @Override
    public void warning(String message, String sourceName, int line, String lineSource, int lineOffset)
    {
        log.warn(getMessage(message,sourceName,line,lineSource,lineOffset));
    }

    @Override
    public void error(String message, String sourceName, int line, String lineSource, int lineOffset)
    {
        log.error(getMessage(message,sourceName,line,lineSource,lineOffset));
    }

    @Override
    public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset)
    {
        error(message,sourceName,line,lineSource,lineOffset);
        throw new EvaluatorException(message,sourceName,line,lineSource,lineOffset);
    }

    private String getMessage(String message, String sourceName, int line, String lineSource, int lineOffset) {
        StringBuilder builder = new StringBuilder();

        if (sourceName != null) {
            builder.append(sourceName)
                .append(":line ")
                .append(line)
                .append(":column ")
                .append(lineOffset)
                .append(':')
            ;
        }
        if ((message != null) && (message.length() != 0)) {
            builder.append(message);
        } else {
            builder.append("unknown error");
        }
        if ((lineSource != null) && (lineSource.length() != 0)) {
            builder.append("\n\t")
                .append(lineSource)
            ;
        }
        return builder.toString();
    }

}
