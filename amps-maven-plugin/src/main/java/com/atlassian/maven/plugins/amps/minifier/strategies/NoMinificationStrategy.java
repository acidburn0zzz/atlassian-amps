package com.atlassian.maven.plugins.amps.minifier.strategies;

import com.atlassian.maven.plugins.amps.minifier.Minifier;
import com.atlassian.maven.plugins.amps.minifier.MinifierParameters;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Takes a source file and copies it to the destination with no processing at all.
 */
public class NoMinificationStrategy implements Minifier {
    @Override
    public void minify(File source, File dest, MinifierParameters params) throws IOException {
        FileUtils.copyFile(source, dest);
    }
}
