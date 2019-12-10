package com.atlassian.maven.plugins.amps.minifier;

import com.atlassian.maven.plugins.amps.code.Sources;

import java.io.IOException;

/**
 * Manages conversion of original source to a minified equivalent.
 *
 * @since 8.1.0
 */
public interface Minifier {
    /**
     * Take a source file and output a minified equivalent for use in production.
     *
     * @param source the contents to minify, along with any metadata about the contents
     *               that would be relevant to the transformation.
     * @param params any options passed in to the build
     * @throws IOException
     * <p>This will be thrown if there are any problems reading or writing to disk,
     * or if any problems occur during minification of the content.
     * <p>
     * If the exception is thrown, two valid courses of action would be to either:
     * <ul>
     *  <li>continue silently: copy the unminified source to the destination, or</li>
     *  <li>fail loudly: exit the whole build with an error.</li>
     * </ul>
     */
    Sources minify(Sources source, MinifierParameters params) throws IOException;
}
