package com.atlassian.maven.plugins.amps.minifier.strategies.googleclosure;

import com.atlassian.maven.plugins.amps.minifier.MinifierParameters;
import com.atlassian.maven.plugins.amps.minifier.Minifier;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Minifies JavaScript files via the Google Closure Compiler.
 */
public class GoogleClosureJsMinifierStrategy implements Minifier {
    @Override
    public void minify(File sourceFile, File destFile, MinifierParameters minifierParameters) throws IOException {
        Log log = minifierParameters.getLog();
        Charset cs = minifierParameters.getCs();
        Map<String, String> closureOptions = minifierParameters.getClosureOptions();

        String source = FileUtils.readFileToString(sourceFile, cs);
        GoogleClosureJsMinifier.CompiledSourceWithSourceMap result = GoogleClosureJsMinifier.compile(source, sourceFile.getAbsolutePath(), closureOptions, log);
        FileUtils.writeStringToFile(destFile, result.getCompiled(), cs);

        File sourceMapFile = new File(destFile.getAbsolutePath() + ".map");
        FileUtils.writeStringToFile(sourceMapFile, result.getSourceMap(), cs);
    }
}
