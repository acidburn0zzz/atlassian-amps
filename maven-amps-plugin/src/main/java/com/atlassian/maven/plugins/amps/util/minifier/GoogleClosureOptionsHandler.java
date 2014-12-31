package com.atlassian.maven.plugins.amps.util.minifier;

import com.google.javascript.jscomp.CompilerOptions;
import org.apache.maven.plugin.logging.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GoogleClosureOptionsHandler {

    private CompilerOptions compilerOptions;
    private Log log;

    public GoogleClosureOptionsHandler(Log log)
    {
        this.compilerOptions = new CompilerOptions();
        this.log = log;
    }

    public void setOption(String optionName, String value) {
        try {
            Method optionHandler = this.getClass().getDeclaredMethod(optionName, String.class);
            optionHandler.invoke(this, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            log.warn(optionName + " is not configurable.  Ignoring this option.");
        }
    }

    public CompilerOptions getCompilerOptions()
    {
        return this.compilerOptions;
    }


    private void languageIn(String value)
    {
        compilerOptions.setLanguageIn(CompilerOptions.LanguageMode.valueOf(value));
    }
}
