package com.atlassian.maven.plugins.amps.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;

public class ClassUtils
{
    public static String getClassnameFromFile(File classFile, String removePrefix)
    {
        String regex = "/";

        if(OSUtils.OS.equals(OSUtils.OS.WINDOWS))
        {
            regex = "\\\\";
        }
        return StringUtils.removeEnd(
                StringUtils.removeStart(
                        StringUtils.removeStart(
                                classFile.getAbsolutePath(), removePrefix)
                                   .replaceAll(regex, ".")
                        , ".")
                , ".class");
    }
    
    public static WiredTestInfo getWiredTestInfo(File classFile)
    {
        FileInputStream fis = null;
        boolean isWiredClass = false;
        String applicationFilter = "";
        
        try
        {
            TestClassVisitor visitor = new TestClassVisitor();
            fis = new FileInputStream(classFile);
            ClassReader reader = new ClassReader(fis);
            
            reader.accept(visitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            isWiredClass = visitor.isWiredTest();
            applicationFilter = visitor.getApplicationFilter();
        }
        catch (FileNotFoundException e)
        {
            isWiredClass = false;
            applicationFilter = "";
        }
        catch (IOException e)
        {
            isWiredClass = false;
            applicationFilter = "";
        }
        finally
        {
            IOUtils.closeQuietly(fis);
        }
        
        return new WiredTestInfo(isWiredClass,applicationFilter);
    }
}