package com.atlassian.maven.plugins.amps.minifier.strategies;

import com.atlassian.maven.plugins.amps.code.Sources;
import com.atlassian.maven.plugins.amps.minifier.Minifier;
import com.atlassian.maven.plugins.amps.minifier.MinifierParameters;
import com.googlecode.htmlcompressor.compressor.XmlCompressor;

/**
 * Minifies XML files using the compressor from Google's htmlcompressor library.
 */
public class XmlMinifierStrategy implements Minifier {
    @Override
    public Sources minify(Sources source, MinifierParameters params) {
        XmlCompressor compressor = new XmlCompressor();
        String min = compressor.compress(source.getContent());
        return new Sources(min, null);
    }
}
