package com.atlassian.maven.plugins.amps.minifier.strategies.googleclosure;

import com.atlassian.maven.plugins.amps.code.Sources;
import com.atlassian.maven.plugins.amps.minifier.Minifier;
import com.atlassian.maven.plugins.amps.minifier.MinifierParameters;
import org.apache.maven.plugin.logging.Log;

import java.util.Map;

/**
 * Minifies JavaScript files via the Google Closure Compiler.
 */
public class GoogleClosureJsMinifierStrategy implements Minifier {
    @Override
    public Sources minify(Sources source, MinifierParameters minifierParameters) {
        Log log = minifierParameters.getLog();
        Map<String, String> closureOptions = minifierParameters.getClosureOptions();

        return GoogleClosureJsMinifier.compile(source.getContent(), closureOptions, log);
    }
}
