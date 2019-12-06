package com.atlassian.maven.plugins.amps.minifier.strategies.yui;

import com.atlassian.maven.plugins.amps.minifier.Minifier;
import com.atlassian.maven.plugins.amps.minifier.MinifierParameters;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * Minifies JavaScript using the YUI compressor library.
 */
public class YUICompressorJsMinifierStrategy implements Minifier {
    @Override
    public void minify(File source, File dest, MinifierParameters params) throws IOException {
        Log log = params.getLog();
        Charset cs = params.getCs();
        try (InputStreamReader in = new InputStreamReader(new FileInputStream(source), cs);
             OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(dest), cs)) {
            JavaScriptCompressor yui = new JavaScriptCompressor(in, new YUIErrorReporter(log));
            yui.compress(out, -1, true, false, false, false);
        }
    }
}
