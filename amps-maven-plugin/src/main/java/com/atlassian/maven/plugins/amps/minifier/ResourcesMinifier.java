package com.atlassian.maven.plugins.amps.minifier;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.atlassian.maven.plugins.amps.util.minifier.GoogleClosureJSMinifier;
import com.atlassian.maven.plugins.amps.util.minifier.YUIErrorReporter;
import com.googlecode.htmlcompressor.compressor.XmlCompressor;
import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * @since version
 */
public class ResourcesMinifier
{
    private static ResourcesMinifier INSTANCE;

    private ResourcesMinifier()
    {
    }

    public static void minify(List<Resource> resources, String outputDir, MinifierParameters minifierParameters) throws MojoExecutionException
    {
        if (null == INSTANCE)
        {
            INSTANCE = new ResourcesMinifier();
        }

        for (Resource resource : resources)
        {
            INSTANCE.processResource(resource, new File(outputDir), minifierParameters);
        }
    }

    public void processResource(Resource resource, File outputDir, MinifierParameters minifierParameters) throws MojoExecutionException
    {
        File destDir = outputDir;
        if (StringUtils.isNotBlank(resource.getTargetPath()))
        {
            destDir = new File(outputDir, resource.getTargetPath());
        }

        File resourceDir = new File(resource.getDirectory());
        if (!resourceDir.exists())
        {
            return;
        }

        if (minifierParameters.isCompressJs())
        {
            processJs(resourceDir, destDir, resource.getIncludes(), resource.getExcludes(), minifierParameters);
        }

        if (minifierParameters.isCompressCss())
        {
            processCss(resourceDir, destDir, resource.getIncludes(), resource.getExcludes(), minifierParameters);
        }

        processXml(resourceDir, destDir, resource.getIncludes(), resource.getExcludes(), minifierParameters);
    }

    public void processXml(final File resourceDir, final File destDir, List<String> includes, final List<String> excludes, MinifierParameters minifierParameters) throws MojoExecutionException
    {
        Log log = minifierParameters.getLog();
        Charset cs = minifierParameters.getCs();

        log.info("Compressing XML files");

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(resourceDir);
        if (null == includes || includes.isEmpty())
        {
            includes = Collections.singletonList("**/*.xml");
        }
        scanner.setIncludes(includes.toArray(new String[0]));

        if (null != excludes && !excludes.isEmpty())
        {
            scanner.setExcludes(excludes.toArray(new String[0]));
        }

        scanner.addDefaultExcludes();
        scanner.scan();

        XmlCompressor compressor = new XmlCompressor();
        int numberOfMinifiedFile = 0;
        for (String name : scanner.getIncludedFiles())
        {
            File sourceFile = new File(resourceDir, name);
            // double check xml file
            if (!sourceFile.getName().endsWith(".xml"))
            {
                continue;
            }
            File destFile = new File(destDir, name);

            if (sourceFile.exists() && sourceFile.canRead())
            {
                if (destFile.exists() && destFile.lastModified() > sourceFile.lastModified())
                {
                    log.debug("Nothing to do, " + destFile.getAbsolutePath() + " is younger than the original");
                    continue;
                }

                log.debug("compressing to " + destFile.getAbsolutePath());

                try
                {
                    FileUtils.forceMkdir(destFile.getParentFile());
                    String source = FileUtils.readFileToString(sourceFile, cs);
                    String min = compressor.compress(source);
                    FileUtils.writeStringToFile(destFile, min, cs);
                    numberOfMinifiedFile++;
                }
                catch (IOException e)
                {
                    throw new MojoExecutionException("IOException when compiling JS", e);
                }
            }
        }
        log.info(numberOfMinifiedFile + " XML file(s) were minified into target directory " + destDir.getAbsolutePath());
    }

    public void processJs(File resourceDir, File destDir, List<String> includes, List<String> excludes, MinifierParameters minifierParameters) throws MojoExecutionException
    {
        Log log = minifierParameters.getLog();
        boolean useClosure = minifierParameters.isUseClosureForJs();
        Charset cs = minifierParameters.getCs();
        if (useClosure)
        {
            log.info("Compiling javascript using Closure");
        }
        else
        {
            log.info("Compiling javascript using YUI");
        }

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(resourceDir);
        if (null == includes || includes.isEmpty())
        {
            includes = Collections.singletonList("**/*.js");
        }
        scanner.setIncludes(includes.toArray(new String[0]));

        if (null != excludes && !excludes.isEmpty())
        {
            scanner.setExcludes(excludes.toArray(new String[0]));
        }

        scanner.addDefaultExcludes();
        scanner.scan();
        int numberOfMinifiedFile = 0;
        for (String name : scanner.getIncludedFiles())
        {
            File sourceFile = new File(resourceDir, name);
            // double check javascript file
            if (!sourceFile.getName().endsWith(".js"))
            {
                continue;
            }
            String baseName = FilenameUtils.removeExtension(name);
            File destFile = new File(destDir, baseName + "-min.js");
            File sourceMapFile = new File(destDir, baseName + "-min.js.map");
            if (sourceFile.exists() && sourceFile.canRead())
            {
                if ((destFile.exists() && destFile.lastModified() > sourceFile.lastModified()) &&
                        (!useClosure || (sourceMapFile.exists() && sourceMapFile.lastModified() > sourceFile.lastModified())))
                {
                    log.debug("Nothing to do, " + destFile.getAbsolutePath() + " is younger than the original");
                    continue;
                }

                if (maybeCopyPreminifiedFileToDest(sourceFile, destFile, minifierParameters))
                {
                    continue;
                }

                log.debug("compressing to " + destFile.getAbsolutePath());
                if (useClosure)
                {
                    log.debug("generating source map to " + sourceMapFile.getAbsolutePath());
                    closureJsCompile(sourceFile, destFile, sourceMapFile, minifierParameters);
                }
                else
                {
                    yuiJsCompile(sourceFile, destFile, log, cs);
                }
                numberOfMinifiedFile++;
            }
        }
        log.info(numberOfMinifiedFile + " Javascript file(s) were minified into target directory " + destDir.getAbsolutePath());
    }

    /**
     * If the file is determined to be already minified (by .min.js or -min.js extension), then it will
     * just copy the file to the destination.
     *
     * @param sourceFile         Source file
     * @param destFile           Target file
     * @param minifierParameters Minifier parameters are constructed higher in the call-chain
     * @return true if and only if the file name ends with .min.js or -min.js
     * @throws MojoExecutionException If an IOException is encountered reading or writing the source
     *                                or destination file.
     */
    private boolean maybeCopyPreminifiedFileToDest(final File sourceFile,
                                                   final File destFile,
                                                   final MinifierParameters minifierParameters) throws MojoExecutionException
    {

        final Log log = minifierParameters.getLog();
        try
        {
            final String fileName = sourceFile.getName();
            if (fileName.endsWith(".min.js") || fileName.endsWith("-min.js"))
            {
                log.debug(String.format("Copying pre-minified file '%s' to destination since file ends in '.min.js' or '-min.js'.", fileName));
                final Charset cs = minifierParameters.getCs();
                FileUtils.forceMkdir(destFile.getParentFile());
                FileUtils.writeStringToFile(destFile, FileUtils.readFileToString(sourceFile, cs), cs);
                return true;
            }
            return false;
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("IOException when trying to copy pre-minified file to target", e);
        }

    }

    public void processCss(File resourceDir, File destDir, List<String> includes, List<String> excludes, MinifierParameters minifierParameters) throws MojoExecutionException
    {
        Log log = minifierParameters.getLog();
        Charset cs = minifierParameters.getCs();
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(resourceDir);
        if (null == includes || includes.isEmpty())
        {
            includes = Collections.singletonList("**/*.css");
        }
        scanner.setIncludes(includes.toArray(new String[0]));

        if (null != excludes && !excludes.isEmpty())
        {
            scanner.setExcludes(excludes.toArray(new String[0]));
        }

        scanner.addDefaultExcludes();
        scanner.scan();
        int numberOfMinifiedFile = 0;
        for (String name : scanner.getIncludedFiles())
        {
            File sourceFile = new File(resourceDir, name);
            // double check css file
            if (!sourceFile.getName().endsWith(".css"))
            {
                continue;
            }
            String baseName = FilenameUtils.removeExtension(name);
            File destFile = new File(destDir, baseName + "-min.css");

            if (sourceFile.exists() && sourceFile.canRead())
            {
                if (destFile.exists() && destFile.lastModified() > sourceFile.lastModified())
                {
                    log.debug("Nothing to do, " + destFile.getAbsolutePath() + " is younger than the original");
                    continue;
                }
                log.debug("compressing to " + destFile.getAbsolutePath());
                yuiCssCompile(sourceFile, destFile, cs);
                numberOfMinifiedFile++;
            }
        }
        log.info(numberOfMinifiedFile + " CSS file(s) were minified into target directory " + destDir.getAbsolutePath());
    }

    private void closureJsCompile(File sourceFile, File destFile, File sourceMapFile, MinifierParameters minifierParameters) throws MojoExecutionException
    {
        Log log = minifierParameters.getLog();
        Charset cs = minifierParameters.getCs();
        Map<String, String> closureOptions = minifierParameters.getClosureOptions();
        try
        {
            FileUtils.forceMkdir(destFile.getParentFile());
            String source = FileUtils.readFileToString(sourceFile, cs);
            GoogleClosureJSMinifier.CompiledSourceWithSourceMap result = GoogleClosureJSMinifier.compile(source, sourceFile.getAbsolutePath(), closureOptions, log);
            FileUtils.writeStringToFile(destFile, result.getCompiled(), cs);
            FileUtils.writeStringToFile(sourceMapFile, result.getSourceMap(), cs);
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("IOException when compiling JS", e);
        }
    }

    private void yuiJsCompile(File sourceFile, File destFile, Log log, Charset cs) throws MojoExecutionException
    {
        try
        {
            FileUtils.forceMkdir(destFile.getParentFile());
            try (InputStreamReader in = new InputStreamReader(new FileInputStream(sourceFile), cs);
                 OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(destFile), cs))
            {
                JavaScriptCompressor yui = new JavaScriptCompressor(in, new YUIErrorReporter(log));
                yui.compress(out, -1, true, false, false, false);
            }
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("IOException when compiling JS", e);
        }
    }

    private void yuiCssCompile(File sourceFile, File destFile, Charset cs) throws MojoExecutionException
    {
        try
        {
            FileUtils.forceMkdir(destFile.getParentFile());
            try (InputStreamReader in = new InputStreamReader(new FileInputStream(sourceFile), cs);
                 OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(destFile), cs))
            {
                CssCompressor yui = new CssCompressor(in);
                yui.compress(out, -1);
            }
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("IOException when compiling JS", e);
        }
    }
}
