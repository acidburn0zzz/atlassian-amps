package com.atlassian.maven.plugins.amps.util;

import com.google.common.collect.Lists;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.io.FileUtils.copyFile;

public class FileUtils
{
    public static File file(String parent, String... kids)
    {
        return file(new File(parent), kids);
    }

    public static File file(File parent, String... kids)
    {
        File cur = parent;
        for (String kid : kids)
        {
            cur = new File(cur, kid);
        }
        return cur;
    }

    public static boolean doesFileNameMatchArtifact(String fileName, String artifactId)
    {
        // this is not perfect, but it sure beats fileName.contains(artifactId)        
        String pattern = "^" + artifactId + "-\\d.*$";
        return fileName.matches(pattern);
    }

    public static void deleteDir(File dir)
    {
        if (dir.exists())
        {
            com.atlassian.core.util.FileUtils.deleteDir(dir);
        }
    }

    public static String fixWindowsSlashes(final String path)
    {
        return path.replaceAll("\\\\", "/");
    }

    public static void cleanDirectory(File directory, FileFilter filter) throws IOException
    {
        if (!directory.exists()) {
            String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        File[] files = directory.listFiles(filter);
        if (files == null) {  // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }

        IOException exception = null;
        for (File file : files) {
            try {
                org.apache.commons.io.FileUtils.forceDelete(file);
            } catch (IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    /**
     * Copy all files and directories from one folder to another, preserving structure.
     * This is an unfortunate duplicate of other libraries, which do not preserve the executable status of files,
     * most likely due to Java version restrictions.
     * <p />
     * If you do <i>not</i> have this requirement please use commons-io instead:
     * {@link org.apache.commons.io.FileUtils#copyDirectory(java.io.File, java.io.File)}.
     * @param source source directory from which to copy all files/directories from
     * @param destination destination directory to copy all files/directories to
     * @param preserveFileAttrs indicate whether date and exec should be preserved
     * @throws IOException if the destination files could not be created
     */
    public static void copyDirectory(File source, File destination, boolean preserveFileAttrs) throws IOException
    {
        if (!destination.mkdirs() && !destination.isDirectory())
        {
            throw new IOException("Destination '" + destination + "' directory cannot be created");
        }
        File[] srcFiles = source.listFiles();
        // Who decided that listFiles could return null?!?
        srcFiles = srcFiles != null ? srcFiles : new File[0];
        for (File srcFile : srcFiles)
        {
            File dstFile = new File(destination, srcFile.getName());
            if (srcFile.isDirectory())
            {
                copyDirectory(srcFile, dstFile, preserveFileAttrs);
            }
            else
            {
                copyFile(srcFile, dstFile, preserveFileAttrs);
                if (preserveFileAttrs && srcFile.canExecute())
                {
                    dstFile.setExecutable(true);
                }
            }
        }
    }

    /**
     * Calculates the checksum for the list (order-sensitive) of files at on go. The calculated value then can be used
     * to verifiy that is there any change in any file of this list.
     *
     * @param files must not empty. Order of file is matter
     */
    public static String calculateFileChecksum(List<File> files) throws IOException
    {
        if (files.isEmpty())
        {
            throw new IllegalArgumentException("There must be at least one file given for calculating the checksum");
        }

        List<InputStream> fileInputStreams = Lists.newArrayList();
        try
        {
            for (File file : files)
            {
                fileInputStreams.add(new BufferedInputStream(new FileInputStream(file)));
            }
        }
        catch (IOException e)
        {
            for (InputStream fileInputStream : fileInputStreams)
            {
                IOUtils.closeQuietly(fileInputStream);
            }
            throw e;
        }

        SequenceInputStream sequenceInputStream = new SequenceInputStream(Collections.enumeration(fileInputStreams));
        try
        {
            return DigestUtils.md5Hex(sequenceInputStream);
        }
        finally
        {
            IOUtils.closeQuietly(sequenceInputStream);
        }
    }

    /**
     * Checks that the content of the given file (read as text) is the same (case sensitive) with the given text.
     *
     * @param textFile the text file of which content will be checked. It is read using system's encoding.
     * @param contentToCheck the text to compare against the content of the given text file.
     * @return true if same. If the text file does not exist, returning false.
     * @throws IOException if the file cannot be read.
     */
    public static boolean contentEquals(File textFile, String contentToCheck) throws IOException
    {
        if (!textFile.exists())
        {
            return false;
        }
        String text = org.apache.commons.io.FileUtils.readFileToString(textFile);
        return contentToCheck.equals(text);
    }

}
