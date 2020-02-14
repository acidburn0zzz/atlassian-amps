package com.atlassian.maven.plugins.amps.minifier.strategies;

import com.atlassian.maven.plugins.amps.code.Sources;
import com.atlassian.maven.plugins.amps.minifier.Minifier;
import com.atlassian.maven.plugins.amps.minifier.MinifierParameters;

/**
 * Takes a source file and copies it to the destination with no processing at all.
 */
public class NoMinificationStrategy implements Minifier {
    @Override
    public Sources minify(Sources source, MinifierParameters params) {
        return new Sources(source.getContent(), source.getSourceMapContent());
    }
}
