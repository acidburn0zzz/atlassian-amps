package com.atlassian.maven.plugins.amps.minifier.strategies.yui;

import com.atlassian.maven.plugins.amps.code.Sources;
import com.atlassian.maven.plugins.amps.minifier.Minifier;
import com.atlassian.maven.plugins.amps.minifier.MinifierParameters;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Minifies JavaScript using the YUI compressor library.
 * @deprecated YUI will be removed in AMPS 9. Use {@link com.atlassian.maven.plugins.amps.minifier.strategies.googleclosure.GoogleClosureJsMinifierStrategy} instead.
 */
public class YUICompressorJsMinifierStrategy implements Minifier {
    @Override
    public Sources minify(Sources source, MinifierParameters params) throws IOException {
        Log log = params.getLog();
        try (StringReader in = new StringReader(source.getContent());
             StringWriter out = new StringWriter();
        ) {
            JavaScriptCompressor yui = new JavaScriptCompressor(in, new YUIErrorReporter(log));
            yui.compress(out, -1, true, false, false, false);
            return new Sources(out.toString(), null);
        }
    }
}
