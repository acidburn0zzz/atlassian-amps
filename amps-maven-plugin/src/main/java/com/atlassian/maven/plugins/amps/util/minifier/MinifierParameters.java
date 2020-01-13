package com.atlassian.maven.plugins.amps.util.minifier;

import org.apache.maven.plugin.logging.Log;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * @deprecated This will be removed in AMPS 9. Use {@link com.atlassian.maven.plugins.amps.minifier.MinifierParameters} instead.
 */
@Deprecated
public class MinifierParameters {
    private com.atlassian.maven.plugins.amps.minifier.MinifierParameters impl;

    @Deprecated
    public MinifierParameters(boolean compressJs,
        boolean compressCss,
        boolean useClosureForJs,
        Charset cs, Log log,
        Map<String, String> closureOptions) {
        impl = new com.atlassian.maven.plugins.amps.minifier.MinifierParameters(compressJs, compressCss, useClosureForJs, cs, log, closureOptions);
    }

    public boolean isCompressJs() {
        return impl.isCompressJs();
    }

    public boolean isCompressCss() {
        return impl.isCompressCss();
    }

    @Deprecated
    public boolean isUseClosureForJs() {
        return impl.isUseClosureForJs();
    }

    public Charset getCs() {
        return impl.getCs();
    }

    public Log getLog() {
        return impl.getLog();
    }

    public Map<String, String> getClosureOptions() {
        return impl.getClosureOptions();
    }

}
