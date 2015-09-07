package com.atlassian.maven.plugins.amps.util.minifier;

import com.google.common.collect.ImmutableList;
import com.google.debugging.sourcemap.SourceMapFormat;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceMap;
import org.apache.maven.plugin.logging.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @since version
 */
public class GoogleClosureJSMinifier
{
    /**
     * Needed as a tuple to return two things from the method - compiled code and source map.
     */
    public static class CompiledSourceWithSourceMap
    {
        private final String compiled;
        private final String sourceMap;

        public CompiledSourceWithSourceMap(final String compiled, final String sourceMap)
        {
            this.compiled = compiled;
            this.sourceMap = sourceMap;
        }

        public String getCompiled()
        {
            return compiled;
        }

        public String getSourceMap()
        {
            return sourceMap;
        }
    }

    private static CompilerOptions options;

    public static void setOptions(Map<String,String> closureOptions, Log log)
    {
        GoogleClosureOptionsHandler googleClosureOptionsHandler = new GoogleClosureOptionsHandler(log);
        if(closureOptions != null && !closureOptions.isEmpty()) {
            for(String optionName : closureOptions.keySet())
            {
                googleClosureOptionsHandler.setOption(optionName, closureOptions.get(optionName));
            }
        }

        options = googleClosureOptionsHandler.getCompilerOptions();
    }

    public static CompiledSourceWithSourceMap compile(String code, String sourcePath, Log log) {
        Compiler compiler = new Compiler();
        CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);

        // Dummy file paths used for source map, all those paths will be replaced at runtime.
        options.setSourceMapFormat(SourceMap.Format.V3);
        options.setSourceMapOutputPath("/dummy-file-path");

        JSSourceFile extern = JSSourceFile.fromCode("externs.js","function alert(x) {}");

        // The dummy input name "input.js" is used here so that any warnings or
        // errors will cite line numbers in terms of input.js.
        JSSourceFile input = JSSourceFile.fromCode("input.js", code);

        // compile() returns a Result, but it is not needed here.
        Result result = compiler.compile(extern, input, options);
        String min = compiler.toSource();

        // Getting the source map.
        // Note that it should be called after the `compiler.toSource` otherwise it would be empty.
        StringBuilder sourceMapStream = new StringBuilder();
        try
        {
            result.sourceMap.appendTo(sourceMapStream, "/dummy-file-path");
        }
        catch (IOException e)
        {
            log.warn("can't create source map for " + sourcePath);
        }

        // The compiler is responsible for generating the compiled code; it is not
        // accessible via the Result.
        return new CompiledSourceWithSourceMap(min, sourceMapStream.toString());
    }
}
