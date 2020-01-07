package com.atlassian.maven.plugins.amps.util.minifier;

import com.google.javascript.jscomp.CompilerOptions;
import org.apache.maven.plugin.logging.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public class GoogleClosureOptionsHandler {
    private CompilerOptions compilerOptions;
    private Log log;
    private final Map<String, Method> configOptions = new HashMap<>();

    public GoogleClosureOptionsHandler(Log log) {
        this.compilerOptions = new CompilerOptions();
        this.log = log;

        // Set sensible default language levels.
        this.compilerOptions.setLanguageIn(CompilerOptions.LanguageMode.ECMASCRIPT_2018);

        // Disable 'use strict' by default, as in batch files,
        // a top-level scoped statement may cause older non-strict-compatible code to fail.
        this.compilerOptions.setEmitUseStrict(false);

        // Set what we've allowed users to configure.
        // Assume it is whatever we've created a private setter method on this class for.
        for (Method m : getClass().getDeclaredMethods()) {
            if (Modifier.isPrivate(m.getModifiers())
                    && m.getParameterCount() == 1
                    && m.getParameterTypes()[0] == String.class
                    && m.getName().startsWith("set")) {
                String name = m.getName().substring(3).toLowerCase();
                configOptions.put(name, m);
            }
        }
    }

    public void setOption(@Nonnull String optionName, String value) {
        final String key = optionName.toLowerCase();
        if (configOptions.containsKey(key)) {
            try {
                configOptions.get(key).invoke(this, value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                log.warn(optionName + " could not be configured.  Ignoring this option.", e);
            }
        } else {
            log.warn(optionName + " is not configurable. Ignoring this option.");
        }
    }

    public CompilerOptions getCompilerOptions() {
        return this.compilerOptions;
    }

    private void setLanguageIn(String value) {
        compilerOptions.setLanguageIn(CompilerOptions.LanguageMode.valueOf(value));
    }

    private void setLanguageOut(String value) {
        compilerOptions.setLanguageOut(CompilerOptions.LanguageMode.valueOf(value));
    }

    private void setEmitUseStrict(String value) {
        compilerOptions.setEmitUseStrict(Boolean.parseBoolean(value));
    }
}
