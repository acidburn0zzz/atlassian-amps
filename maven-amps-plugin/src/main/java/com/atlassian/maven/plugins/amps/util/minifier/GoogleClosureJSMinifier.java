package com.atlassian.maven.plugins.amps.util.minifier;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.Compiler;
import org.apache.maven.plugin.logging.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * @since version
 */
public class GoogleClosureJSMinifier
{
    public static String compile(String code, HashMap<String,String> closureOptions, Log log) {
        Compiler compiler = new Compiler();

        GoogleClosureOptionsHandler googleClosureOptionsHandler = new GoogleClosureOptionsHandler(log);
        if(closureOptions != null && !closureOptions.isEmpty()) {
            for(String optionName : closureOptions.keySet())
            {
                googleClosureOptionsHandler.setOption(optionName, closureOptions.get(optionName));
            }
        }

        CompilerOptions options = googleClosureOptionsHandler.getCompilerOptions();

        CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);

        JSSourceFile extern = JSSourceFile.fromCode("externs.js","function alert(x) {}");

        // The dummy input name "input.js" is used here so that any warnings or
        // errors will cite line numbers in terms of input.js.
        JSSourceFile input = JSSourceFile.fromCode("input.js", code);

        // compile() returns a Result, but it is not needed here.
        compiler.compile(extern, input, options);

        // The compiler is responsible for generating the compiled code; it is not
        // accessible via the Result.
        return compiler.toSource();
    }

}
