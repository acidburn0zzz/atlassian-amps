package com.atlassian.maven.plugins.amps.util.minifier;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.util.List;

/**
 * @deprecated This will be removed in AMPS 9. Use {@link com.atlassian.maven.plugins.amps.minifier.ResourcesMinifier} instead.
 * @since 4.1
 */
@Deprecated
public class ResourcesMinifier {
    /**
     * @deprecated use {@link com.atlassian.maven.plugins.amps.minifier.ResourcesMinifier#minify(List, String)} instead.
     */
    public static void minify(List<Resource> resources, String outputDir, MinifierParameters minifierParameters) throws MojoExecutionException {
        getImpl(minifierParameters).minify(resources, outputDir);
    }

    /**
     * @deprecated use {@link com.atlassian.maven.plugins.amps.minifier.ResourcesMinifier#processFiletypeInDirectory} instead.
     */
    public void processXml(File resourceDir, File destDir, List<String> includes, List<String> excludes, MinifierParameters minifierParameters) throws MojoExecutionException {
        getImpl(minifierParameters).processFiletypeInDirectory("xml", resourceDir, destDir, includes, excludes);
    }

    /**
     * @deprecated use {@link com.atlassian.maven.plugins.amps.minifier.ResourcesMinifier#processFiletypeInDirectory} instead.
     */
    public void processJs(File resourceDir, File destDir, List<String> includes, List<String> excludes, MinifierParameters minifierParameters) throws MojoExecutionException {
        getImpl(minifierParameters).processFiletypeInDirectory("js", resourceDir, destDir, includes, excludes);
    }

    /**
     * @deprecated use {@link com.atlassian.maven.plugins.amps.minifier.ResourcesMinifier#processFiletypeInDirectory} instead.
     */
    public void processCss(File resourceDir, File destDir, List<String> includes, List<String> excludes, MinifierParameters minifierParameters) throws MojoExecutionException {
        getImpl(minifierParameters).processFiletypeInDirectory("css", resourceDir, destDir, includes, excludes);
    }

    private static com.atlassian.maven.plugins.amps.minifier.ResourcesMinifier getImpl(MinifierParameters minifierParameters) {
        final com.atlassian.maven.plugins.amps.minifier.MinifierParameters params = new com.atlassian.maven.plugins.amps.minifier.MinifierParameters(minifierParameters.isCompressJs(), minifierParameters.isCompressCss(), minifierParameters.isUseClosureForJs(), minifierParameters.getCs(), minifierParameters.getLog(), minifierParameters.getClosureOptions());
        return new com.atlassian.maven.plugins.amps.minifier.ResourcesMinifier(params);
    }
}
