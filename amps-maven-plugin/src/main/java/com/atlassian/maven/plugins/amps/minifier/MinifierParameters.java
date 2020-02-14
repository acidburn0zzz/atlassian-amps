package com.atlassian.maven.plugins.amps.minifier;

import com.google.common.collect.ImmutableMap;
import org.apache.maven.plugin.logging.Log;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Stores configuration values from the build that are relevant for any minification operations to be performed on
 * resources in the current build.
 */
public class MinifierParameters {
    private final boolean compressJs;
    private final boolean compressCss;
    private final Charset cs;
    private final Log log;
    private final Map<String, String> closureOptions;

    private boolean useClosureForJs = true;

    public MinifierParameters(boolean compressJs,
                              boolean compressCss,
                              Charset cs,
                              Log log,
                              @Nullable Map<String, String> closureOptions) {
        this.compressJs = compressJs;
        this.compressCss = compressCss;
        this.cs = cs;
        this.log = log;
        this.closureOptions = closureOptions != null ? ImmutableMap.copyOf(closureOptions) : Collections.emptyMap();
    }

    @Deprecated
    public MinifierParameters(boolean compressJs,
                              boolean compressCss,
                              boolean useClosureForJs,
                              Charset cs, Log log,
                              Map<String, String> closureOptions) {
        this(compressJs, compressCss, cs, log, closureOptions);
        this.useClosureForJs = useClosureForJs;
    }

    public boolean isCompressJs() {
        return compressJs;
    }

    public boolean isCompressCss() {
        return compressCss;
    }

    @Deprecated
    public boolean isUseClosureForJs() {
        return useClosureForJs;
    }

    public Charset getCs() {
        return cs;
    }

    public Log getLog() {
        return log;
    }

    @Nonnull
    public Map<String, String> getClosureOptions() {
        return closureOptions;
    }

}
