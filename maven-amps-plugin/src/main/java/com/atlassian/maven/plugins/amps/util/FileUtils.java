package com.atlassian.maven.plugins.amps.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

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

}
