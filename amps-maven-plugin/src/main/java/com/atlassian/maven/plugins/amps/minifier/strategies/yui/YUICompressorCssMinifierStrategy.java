package com.atlassian.maven.plugins.amps.minifier.strategies.yui;

import com.atlassian.maven.plugins.amps.code.Sources;
import com.atlassian.maven.plugins.amps.minifier.Minifier;
import com.atlassian.maven.plugins.amps.minifier.MinifierParameters;
import com.yahoo.platform.yui.compressor.CssCompressor;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Minifies CSS files using the YUI compressor library.
 */
public class YUICompressorCssMinifierStrategy implements Minifier {
    @Override
    public Sources minify(Sources source, MinifierParameters params) throws IOException {
        try (StringReader in = new StringReader(source.getContent());
             StringWriter out = new StringWriter();
        ) {
            CssCompressor yui = new CssCompressor(in);
            yui.compress(out, -1);
            return new Sources(out.toString(), null);
        }
    }
}
