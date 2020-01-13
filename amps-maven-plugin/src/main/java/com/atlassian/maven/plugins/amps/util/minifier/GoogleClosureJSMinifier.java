package com.atlassian.maven.plugins.amps.util.minifier;

import com.atlassian.maven.plugins.amps.code.Sources;
import com.atlassian.maven.plugins.amps.minifier.strategies.googleclosure.GoogleClosureJsMinifier;
import org.apache.maven.plugin.logging.Log;

import java.util.Map;

/**
 * @deprecated This will be removed in AMPS 9. Use {@link com.atlassian.maven.plugins.amps.minifier.strategies.googleclosure.GoogleClosureJsMinifier} instead.
 */
public class GoogleClosureJSMinifier {
    /**
     * @deprecated use {@link com.atlassian.maven.plugins.amps.code.Sources} instead.
     */
    static class CompiledSourceWithSourceMap {
        private final Sources impl;

        CompiledSourceWithSourceMap(final String compiled, final String sourceMap) {
            impl = new Sources(compiled, sourceMap);
        }

        String getCompiled() {
            return impl.getContent();
        }

        String getSourceMap() {
            return impl.getSourceMapContent();
        }
    }

    /**
     * @deprecated use {@link GoogleClosureJsMinifier#compile} instead.
     */
    public static CompiledSourceWithSourceMap compile(String code, String sourcePath, Map<String, String> closureOptions, Log log) {
        Sources result = GoogleClosureJsMinifier.compile(code, closureOptions, log);
        return new CompiledSourceWithSourceMap(result.getContent(), result.getSourceMapContent());
    }
}
