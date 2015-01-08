package com.atlassian.maven.plugins.amps.util.minifier;

import com.google.javascript.jscomp.CompilerOptions;
import org.apache.maven.plugin.logging.Log;

public class GoogleClosureOptionsHandler
{

    private CompilerOptions compilerOptions;
    private Log log;

    public GoogleClosureOptionsHandler(Log log)
    {
        this.compilerOptions = new CompilerOptions();
        this.log = log;
    }

    public void setOption(String optionName, String value) {
        if (optionName.equals("languageIn")) {
            setLanguageIn(value);
        } else {
            log.warn(optionName + " is not configurable.  Ignoring this option.");
        }
    }

    public CompilerOptions getCompilerOptions()
    {
        return this.compilerOptions;
    }


    private void setLanguageIn(String value)
    {
        compilerOptions.setLanguageIn(CompilerOptions.LanguageMode.valueOf(value));
    }
}
