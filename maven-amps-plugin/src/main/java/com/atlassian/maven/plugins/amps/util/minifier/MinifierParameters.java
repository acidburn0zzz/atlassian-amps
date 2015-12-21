package com.atlassian.maven.plugins.amps.util.minifier;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by christian on 12/21/15.
 */
public class MinifierParameters
{
    private boolean compressJs;
    private boolean compressCss;
    private boolean useClosureForJs;
    private Charset cs;
    private Log log;
    private Map<String,String> closureOptions;

    public MinifierParameters(boolean compressJs,
                              boolean compressCss,
                              boolean useClosureForJs,
                              Charset cs, Log log,
                              Map<String, String> closureOptions)
    {
        this.compressJs = compressJs;
        this.compressCss = compressCss;
        this.useClosureForJs = useClosureForJs;
        this.cs = cs;
        this.log = log;
        this.closureOptions = closureOptions;
    }

    public boolean isCompressJs()
    {
        return compressJs;
    }

    public boolean isCompressCss()
    {
        return compressCss;
    }

    public boolean isUseClosureForJs()
    {
        return useClosureForJs;
    }

    public Charset getCs()
    {
        return cs;
    }

    public Log getLog()
    {
        return log;
    }

    public Map<String, String> getClosureOptions()
    {
        return closureOptions;
    }

}
