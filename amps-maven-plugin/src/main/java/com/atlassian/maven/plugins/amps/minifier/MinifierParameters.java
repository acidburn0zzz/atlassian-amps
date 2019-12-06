package com.atlassian.maven.plugins.amps.minifier;

import org.apache.maven.plugin.logging.Log;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * Stores configuration values from the build that are relevant for any minification
 * operations to be performed on resources in the current build.
 */
public class MinifierParameters {
    private boolean compressJs;
    private boolean compressCss;
    private boolean useClosureForJs;
    private Charset cs;
    private Log log;
    private Map<String, String> closureOptions;

    public MinifierParameters(boolean compressJs,
                              boolean compressCss,
                              boolean useClosureForJs,
                              Charset cs, Log log,
                              Map<String, String> closureOptions) {
        this.compressJs = compressJs;
        this.compressCss = compressCss;
        this.useClosureForJs = useClosureForJs;
        this.cs = cs;
        this.log = log;
        this.closureOptions = closureOptions;
    }

    public boolean isCompressJs() {
        return compressJs;
    }

    public boolean isCompressCss() {
        return compressCss;
    }

    public boolean isUseClosureForJs() {
        return useClosureForJs;
    }

    public Charset getCs() {
        return cs;
    }

    public Log getLog() {
        return log;
    }

    public Map<String, String> getClosureOptions() {
        return closureOptions;
    }

}
