package com.atlassian.maven.plugins.amps.minifier.strategies;

import com.atlassian.maven.plugins.amps.minifier.MinifierParameters;
import com.atlassian.maven.plugins.amps.minifier.Minifier;
import com.googlecode.htmlcompressor.compressor.XmlCompressor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Minifies XML files using the compressor from Google's htmlcompressor library.
 */
public class XmlMinifierStrategy implements Minifier {
    @Override
    public void minify(File sourceFile, File destFile, MinifierParameters params) throws IOException {
        Charset cs = params.getCs();
        String source = FileUtils.readFileToString(sourceFile, cs);
        XmlCompressor compressor = new XmlCompressor();
        String min = compressor.compress(source);
        FileUtils.writeStringToFile(destFile, min, cs);
    }
}
