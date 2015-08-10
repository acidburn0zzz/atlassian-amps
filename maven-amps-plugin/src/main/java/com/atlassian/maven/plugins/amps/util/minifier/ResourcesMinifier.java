package com.atlassian.maven.plugins.amps.util.minifier;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.googlecode.htmlcompressor.compressor.XmlCompressor;
import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
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

    public static void minify(List<Resource> resources, File outputDir, boolean compressJs, boolean compressCss, boolean useClosureForJs, Charset cs, Log log, Map<String,String> closureOptions) throws MojoExecutionException
    {
        GoogleClosureJSMinifier.setOptions(closureOptions, log);
        if(null == INSTANCE)
        {
            INSTANCE = new ResourcesMinifier();
        }

        for(Resource resource : resources)
        {
            INSTANCE.processResource(resource,outputDir,compressJs, compressCss, useClosureForJs, cs, log);
        }
    }
    
    public void processResource(Resource resource, File outputDir, boolean compressJs, boolean compressCss, boolean useClosureForJs, Charset cs, Log log) throws MojoExecutionException
    {
        File destDir = outputDir;
        if(StringUtils.isNotBlank(resource.getTargetPath()))
        {
            destDir = new File(outputDir,resource.getTargetPath());
        }
        
        File resourceDir = new File(resource.getDirectory());
        
        if(null == resourceDir || !resourceDir.exists())
        {
            return;
        }

        if(compressJs)
        {
            processJs(resourceDir,destDir,resource.getIncludes(),resource.getExcludes(),useClosureForJs,cs,log);
        }
        
        if(compressCss)
        {
            processCss(resourceDir,destDir,resource.getIncludes(),resource.getExcludes(), cs, log);
        }

        processXml(resourceDir, destDir, resource.getIncludes(), resource.getExcludes(), cs, log);
    }

    public void processXml(final File resourceDir, final File destDir, List<String> includes, final List<String> excludes, Charset cs, final Log log) throws MojoExecutionException
    {
        log.info("Compressing XML files");

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(resourceDir);
        if(null == includes || includes.isEmpty())
        {
            includes = Collections.singletonList("**/*.xml");
        }
        scanner.setIncludes(includes.toArray(new String[includes.size()]));

        if(null != excludes && !excludes.isEmpty())
        {
            scanner.setExcludes(excludes.toArray(new String[0]));
        }

        scanner.addDefaultExcludes();
        scanner.scan();

        XmlCompressor compressor = new XmlCompressor();
        int numberOfMinifiedFile = 0;
        for (String name : scanner.getIncludedFiles())
        {
            File sourceFile = new File(resourceDir,name);
            // double check xml file
            if(!sourceFile.getName().endsWith(".xml"))
            {
                continue;
            }
            File destFile = new File(destDir,name);

            if(sourceFile.exists() && sourceFile.canRead())
            {
                if(destFile.exists() && destFile.lastModified() > sourceFile.lastModified())
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
        log.info(numberOfMinifiedFile +" XML file(s) were minified into target directory " + destDir.getAbsolutePath());
    }

    public void processJs(File resourceDir, File destDir, List<String> includes, List<String> excludes, boolean useClosure, Charset cs, Log log) throws MojoExecutionException
    {
        if(useClosure)
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
        scanner.setIncludes(includes.toArray(new String[includes.size()]));

        if(null != excludes && !excludes.isEmpty())
        {
            scanner.setExcludes(excludes.toArray(new String[0]));
        }

        scanner.addDefaultExcludes();
        scanner.scan();
        int numberOfMinifiedFile = 0;
        for(String name : scanner.getIncludedFiles())
        {
            File sourceFile = new File(resourceDir,name);
            // double check javascript file
            if (!sourceFile.getName().endsWith(".js")) {
                continue;
            }
            String baseName = FilenameUtils.removeExtension(name);
            File destFile = new File(destDir,baseName + "-min.js");
            File sourceMapFile = new File(destDir, baseName + "-min.js.map");
            if(sourceFile.exists() && sourceFile.canRead())
            {
                if(
                    (destFile.exists() && destFile.lastModified() > sourceFile.lastModified()) &&
                    (!useClosure || (sourceMapFile.exists() && sourceMapFile.lastModified() > sourceFile.lastModified()))
                )
                {
                    log.debug("Nothing to do, " + destFile.getAbsolutePath() + " is younger than the original");
                    continue;
                }

                log.debug("compressing to " + destFile.getAbsolutePath());
                if(useClosure)
                {
                    log.debug("generating source map to " + sourceMapFile.getAbsolutePath());
                    closureJsCompile(sourceFile, destFile, sourceMapFile, cs, log);
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

    public void processCss(File resourceDir, File destDir, List<String> includes, List<String> excludes, Charset cs, Log log) throws MojoExecutionException
    {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(resourceDir);
        if(null == includes || includes.isEmpty())
        {
            includes = Collections.singletonList("**/*.css");
        }
        scanner.setIncludes(includes.toArray(new String[includes.size()]));

        if(null != excludes && !excludes.isEmpty())
        {
            scanner.setExcludes(excludes.toArray(new String[0]));
        }

        scanner.addDefaultExcludes();
        scanner.scan();
        int numberOfMinifiedFile = 0;
        for(String name : scanner.getIncludedFiles())
        {
            File sourceFile = new File(resourceDir,name);
            // double check css file
            if(!sourceFile.getName().endsWith(".css"))
            {
                continue;
            }
            String baseName = FilenameUtils.removeExtension(name);
            File destFile = new File(destDir,baseName + "-min.css");

            if(sourceFile.exists() && sourceFile.canRead())
            {
                if(destFile.exists() && destFile.lastModified() > sourceFile.lastModified())
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

    private void closureJsCompile(File sourceFile, File destFile, File sourceMapFile, Charset cs, Log log) throws MojoExecutionException
    {
        try
        {
            FileUtils.forceMkdir(destFile.getParentFile());
            String source = FileUtils.readFileToString(sourceFile, cs);
            GoogleClosureJSMinifier.CompiledSourceWithSourceMap result = GoogleClosureJSMinifier.compile(source, sourceFile.getAbsolutePath(), log);
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
        InputStreamReader in = null;
        OutputStreamWriter out = null;
        try
        {
            FileUtils.forceMkdir(destFile.getParentFile());
            in = new InputStreamReader(new FileInputStream(sourceFile), cs);
            out = new OutputStreamWriter(new FileOutputStream(destFile), cs);
            
            JavaScriptCompressor yui = new JavaScriptCompressor(in,new YUIErrorReporter(log));
            yui.compress(out,-1,true,false,false,false);
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("IOException when compiling JS", e);
        }
        finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    private void yuiCssCompile(File sourceFile, File destFile, Charset cs) throws MojoExecutionException
    {
        InputStreamReader in = null;
        OutputStreamWriter out = null;
        try
        {
            FileUtils.forceMkdir(destFile.getParentFile());
            in = new InputStreamReader(new FileInputStream(sourceFile), cs);
            out = new OutputStreamWriter(new FileOutputStream(destFile), cs);

            CssCompressor yui = new CssCompressor(in);
            yui.compress(out,-1);
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("IOException when compiling JS", e);
        }
        finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    
}
