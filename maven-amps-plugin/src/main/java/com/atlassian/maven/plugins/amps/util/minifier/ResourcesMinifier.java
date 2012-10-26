package com.atlassian.maven.plugins.amps.util.minifier;

import java.io.*;
import java.util.List;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Resource;
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

    public static void minify(List<Resource> resources, File outputDir, boolean useClosureForJs, Log log)
    {
        if(null == INSTANCE)
        {
            INSTANCE = new ResourcesMinifier();
        }
        
        for(Resource resource : resources)
        {
            INSTANCE.processResource(resource,outputDir,useClosureForJs,log);
        }
    }
    
    public void processResource(Resource resource, File outputDir, boolean useClosureForJs, Log log)
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

        processJs(resourceDir,destDir,resource.getExcludes(),useClosureForJs,log);
        processCss(resourceDir,destDir,resource.getExcludes());
    }
    
    public void processJs(File resourceDir, File destDir, List<String> excludes, boolean useClosure, Log log)
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
        scanner.setIncludes(new String[]{"**/*.js"});

        if(null != excludes && !excludes.isEmpty())
        {
            scanner.setExcludes(excludes.toArray(new String[0]));
        }

        scanner.addDefaultExcludes();
        scanner.scan();
        
        for(String name : scanner.getIncludedFiles())
        {
            File sourceFile = new File(resourceDir,name);
            String baseName = FilenameUtils.removeExtension(name);
            File destFile = new File(destDir,baseName + "-min.js");
            
            if(sourceFile.exists() && sourceFile.canRead())
            {
                if(useClosure)
                {
                    closureJsCompile(sourceFile,destFile);
                }
                else
                {
                    yuiJsCompile(sourceFile,destFile,log);
                }
            }
        }
    }

    public void processCss(File resourceDir, File destDir, List<String> excludes)
    {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(resourceDir);
        scanner.setIncludes(new String[]{"**/*.css"});

        if(null != excludes && !excludes.isEmpty())
        {
            scanner.setExcludes(excludes.toArray(new String[0]));
        }

        scanner.addDefaultExcludes();
        scanner.scan();

        for(String name : scanner.getIncludedFiles())
        {
            File sourceFile = new File(destDir,name);
            String baseName = FilenameUtils.removeExtension(name);
            File destFile = new File(destDir,baseName + "-min.css");

            if(sourceFile.exists() && sourceFile.canRead())
            {
                yuiCssCompile(sourceFile,destFile);
            }
        }
    }

    private void closureJsCompile(File sourceFile, File destFile)
    {
        try
        {
            FileUtils.forceMkdir(destFile.getParentFile());
            String source = FileUtils.readFileToString(sourceFile);
            String min = GoogleClosureJSMinifier.compile(source);
            FileUtils.writeStringToFile(destFile,min);
        }
        catch (IOException e)
        {
            //ignore
        }
    }
    
    private void yuiJsCompile(File sourceFile, File destFile, Log log)
    {
        InputStreamReader in = null;
        OutputStreamWriter out = null;
        try
        {
            FileUtils.forceMkdir(destFile.getParentFile());
            in = new InputStreamReader(new FileInputStream(sourceFile));
            out = new OutputStreamWriter(new FileOutputStream(destFile));
            
            JavaScriptCompressor yui = new JavaScriptCompressor(in,new YUIErrorReporter(log));
            yui.compress(out,-1,true,false,false,false);
        }
        catch (IOException e)
        {
            //ignore
        }
        finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    private void yuiCssCompile(File sourceFile, File destFile)
    {
        InputStreamReader in = null;
        OutputStreamWriter out = null;
        try
        {
            FileUtils.forceMkdir(destFile.getParentFile());
            in = new InputStreamReader(new FileInputStream(sourceFile));
            out = new OutputStreamWriter(new FileOutputStream(destFile));

            CssCompressor yui = new CssCompressor(in);
            yui.compress(out,-1);
        }
        catch (IOException e)
        {
            //ignore
        }
        finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    
}
