package com.atlassian.maven.plugins.amps.minifier.strategies.yui;

import com.atlassian.maven.plugins.amps.minifier.Minifier;
import com.atlassian.maven.plugins.amps.minifier.MinifierParameters;
import com.yahoo.platform.yui.compressor.CssCompressor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * Minifies CSS files using the YUI compressor library.
 */
public class YUICompressorCssMinifierStrategy implements Minifier {
    @Override
    public void minify(File sourceFile, File destFile, MinifierParameters params) throws IOException {
        Charset cs = params.getCs();
        try (InputStreamReader in = new InputStreamReader(new FileInputStream(sourceFile), cs);
             OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(destFile), cs);
        ) {
            CssCompressor yui = new CssCompressor(in);
            yui.compress(out, -1);
        }
    }
}
