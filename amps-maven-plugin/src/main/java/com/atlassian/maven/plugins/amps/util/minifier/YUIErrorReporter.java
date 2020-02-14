package com.atlassian.maven.plugins.amps.util.minifier;

import org.apache.maven.plugin.logging.Log;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

/**
 * @deprecated This will be removed in AMPS 9. There is no replacement.
 */
@Deprecated
public class YUIErrorReporter implements ErrorReporter {
    private final ErrorReporter impl;

    public YUIErrorReporter(Log log) {
        this.impl = new com.atlassian.maven.plugins.amps.minifier.strategies.yui.YUIErrorReporter(log);
    }

    @Override
    public void warning(final String s, final String s1, final int i, final String s2, final int i1) {
        impl.warning(s, s1, i, s2, i1);
    }

    @Override
    public void error(final String s, final String s1, final int i, final String s2, final int i1) {
        impl.error(s, s1, i, s2, i1);
    }

    @Override
    public EvaluatorException runtimeError(final String s, final String s1, final int i, final String s2, final int i1) {
        return impl.runtimeError(s, s1, i, s2, i1);
    }
}
