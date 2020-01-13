package com.atlassian.maven.plugins.amps.util.minifier;

import com.google.javascript.jscomp.CompilerOptions;
import org.apache.maven.plugin.logging.Log;

import javax.annotation.Nonnull;

/**
 * @deprecated This will be removed in AMPS 9. Use {@link com.atlassian.maven.plugins.amps.minifier.strategies.googleclosure.GoogleClosureOptionsHandler} instead.
 */
public class GoogleClosureOptionsHandler {
    private final com.atlassian.maven.plugins.amps.minifier.strategies.googleclosure.GoogleClosureOptionsHandler impl;

    public GoogleClosureOptionsHandler(Log log) {
        impl = new com.atlassian.maven.plugins.amps.minifier.strategies.googleclosure.GoogleClosureOptionsHandler(log);
    }

    public void setOption(@Nonnull final String optionName, final String value) {
        impl.setOption(optionName, value);
    }

    public CompilerOptions getCompilerOptions() {
        return impl.getCompilerOptions();
    }
}
