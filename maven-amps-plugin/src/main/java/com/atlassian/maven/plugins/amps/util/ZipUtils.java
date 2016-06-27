package com.atlassian.maven.plugins.amps.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

public class ZipUtils
{

    /**
     * Ungzips and extracts the specified tar.gz file into the specified directory.
     * @param targz the tar.gz file to use
     * @param destDir the directory to contain the extracted contents
     */
    public static void untargz(final File targz, final String destDir) throws IOException
    {
        untargz(targz, destDir, 0);
    }

    /**
     * Ungzips and extracts the specified tar.gz file into the specified directory, trimming
     * the specified number of leading path segments from the extraction path.
     * @param targz the tar.gz file to use
     * @param destDir the directory to contain the extracted contents
     * @param leadingPathSegmentsToTrim the number of leading path segments to remove from the
     *                                  extracted path
     */
    public static void untargz(final File targz, final String destDir, int leadingPathSegmentsToTrim) throws IOException
    {
        FileInputStream fin = new FileInputStream(targz);
        GzipCompressorInputStream gzIn = new GzipCompressorInputStream(fin);
        TarArchiveInputStream tarIn = new TarArchiveInputStream(gzIn);

        try
        {
            while (true)
            {
                TarArchiveEntry entry = tarIn.getNextTarEntry();
                if (entry == null) {
                    // tar file exhausted
                    break;
                }

                File entryFile = new File(destDir + File.separator +
                        trimPathSegments(entry.getName(), leadingPathSegmentsToTrim));

                if (entry.isDirectory())
                {
                    entryFile.mkdirs();
                    continue;
                }

                if (!entryFile.getParentFile().exists())
                {
                    entryFile.getParentFile().mkdirs();
                }

                FileOutputStream fos = null;
                try
                {
                    fos = new FileOutputStream(entryFile);
                    IOUtils.copy(tarIn, fos);

                    // check for user-executable bit on entry and apply to file
                    if ((entry.getMode() & 0100) != 0) {
                        entryFile.setExecutable(true);
                    }
                }
                finally
                {
                    IOUtils.closeQuietly(fos);
                }
            }
        }
        finally
        {
            IOUtils.closeQuietly(fin);
            IOUtils.closeQuietly(tarIn);
            IOUtils.closeQuietly(gzIn);
        }
    }

    public static void unzip(final File zipFile, final String destDir) throws IOException
    {
        unzip(zipFile, destDir, 0);
    }

    /**
     * Unzips a file
     *
     * @param zipFile
     *            the Zip file
     * @param destDir
     *            the destination folder
     * @param leadingPathSegmentsToTrim
     *            number of root folders to skip. Example: If all files are in generated-resources/home/*,
     *            then you may want to skip 2 folders.
     */
    public static void unzip(final File zipFile, final String destDir, int leadingPathSegmentsToTrim) throws IOException
    {
        unzip(zipFile, destDir, leadingPathSegmentsToTrim, false, null);
    }

    /**
     * Unzips a file
     *
     * @param zipFile
     *            the Zip file
     * @param destDir
     *            the destination folder
     * @param leadingPathSegmentsToTrim
     *            number of root folders to skip. Example: If all files are in generated-resources/home/*,
     *            then you may want to skip 2 folders.
     * @param flatten
     *            if true all files from zip are extracted directly to destDir without keeping the subdirectories
     *            structure from the zip file
     * @param pattern
     *            pattern that must be meet by zip entry to be extracted
     */
    public static void unzip(final File zipFile, final String destDir, final int leadingPathSegmentsToTrim,
                             final boolean flatten, final Pattern pattern) throws IOException
    {
        final ZipFile zip = new ZipFile(zipFile);
        try
        {
            final Enumeration<? extends ZipArchiveEntry> entries = zip.getEntries();
            while (entries.hasMoreElements())
            {
                final ZipArchiveEntry zipEntry = entries.nextElement();
                final String name = zipEntry.getName();
                if(pattern == null || pattern.matcher(name).matches())
                {
                    String zipPath = trimPathSegments(name, leadingPathSegmentsToTrim);
                    if (flatten)
                    {
                        zipPath = flattenPath(zipPath);
                    }
                    final File file = new File(destDir + "/" + zipPath);
                    if (zipEntry.isDirectory())
                    {
                        file.mkdirs();
                        continue;
                    }
                    // make sure our parent exists in case zipentries are out of order
                    if (!file.getParentFile().exists())
                    {
                        file.getParentFile().mkdirs();
                    }

                    InputStream is = null;
                    OutputStream fos = null;
                    try
                    {
                        is = zip.getInputStream(zipEntry);
                        fos = new FileOutputStream(file);
                        IOUtils.copy(is, fos);

                        // check for user-executable bit on entry and apply to file
                        if ((zipEntry.getUnixMode() & 0100) != 0)
                        {
                            file.setExecutable(true);
                        }
                    } finally
                    {
                        IOUtils.closeQuietly(is);
                        IOUtils.closeQuietly(fos);
                    }
                    file.setLastModified(zipEntry.getTime());
                }
            }
        }
        finally
        {
            try
            {
                zip.close();
            }
            catch (IOException e)
            {
                // ignore
            }
        }
    }

    /**
     * Count the number of nested root folders. A root folder is a folder which contains 0 or 1 file or folder.
     *
     * Example: A zip with only "generated-resources/home/database.log" has 2 root folders.
     *
     * @param zip the zip file
     * @return the number of root folders.
     */
    public static int countNestingLevel(File zip) throws ZipException, IOException
    {
        ZipFile zipFile = null;
        try
        {
            zipFile = new ZipFile(zip);
            List<String> filenames = toList(zipFile.getEntries());
            return countNestingLevel(filenames);
        }
        finally
        {
            if (zipFile != null)
            {
                try
                {
                    zipFile.close();
                }
                catch (IOException e)
                {
                    // ignore
                }
            }
        }
    }

    /**
     * Count the number of nested root directories in the filenames.
     *
     * A root directory is a directory that has no sibling.
     * @param filenames the list of filenames, using / as a separator. Must be a mutable copy,
     * as it will be modified.
     */
    static int countNestingLevel(List<String> filenames)
    {
        String prefix = StringUtils.getCommonPrefix(filenames.toArray(new String[filenames.size()]));
        if (!prefix.endsWith("/"))
        {
            prefix = prefix.substring(0, prefix.lastIndexOf("/") + 1);
        }

        // The first prefix may be wrong, example:
        // root/ <- to be discarded
        // root/nested/ <- to be discarded
        // root/nested/folder1/file.txt <- the root "root/nested/" will be detected properly
        // root/nested/folder2/file.txt
        if (filenames.remove(prefix))
        {
            return countNestingLevel(filenames);
        }

        // The client can't use these filenames anymore.
        filenames.clear();
        return StringUtils.countMatches(prefix, "/");
    }

    private static List<String> toList(final Enumeration<? extends ZipEntry> entries)
    {
        List<String> filenamesList = Lists.newArrayList();
        while (entries.hasMoreElements())
        {
            final ZipEntry zipEntry = entries.nextElement();
            filenamesList.add(zipEntry.getName());
        }
        return filenamesList;
    }

    /**
     * @param prefix the prefix. If empty, uses the srcDir's name. That means you can't create a zip with no
     * root folder.
     */
    public static void zipDir(final File zipFile, final File srcDir, final String prefix) throws IOException
    {
        ZipArchiveOutputStream out = new ZipArchiveOutputStream(new FileOutputStream(zipFile));
        try
        {
            addZipPrefixes(srcDir, out, prefix);
            addZipDir(srcDir, out, prefix);
        }
        finally
        {
            // Complete the ZIP file
            IOUtils.closeQuietly(out);
        }
    }

    private static void addZipPrefixes(File dirObj, ZipArchiveOutputStream out, String prefix) throws IOException
    {
        // need to manually add the prefix folders
        String entryPrefix = ensurePrefixWithSlash(dirObj, prefix);

        String[] prefixes = entryPrefix.split("/");
        String lastPrefix = "";
        for (int i = 0; i < prefixes.length; i++)
        {
            ZipArchiveEntry entry = new ZipArchiveEntry(lastPrefix + prefixes[i] + "/");
            out.putArchiveEntry(entry);
            out.closeArchiveEntry();

            lastPrefix = prefixes[i] + "/";
        }
    }

    private static void addZipDir(File dirObj, ZipArchiveOutputStream out, String prefix) throws IOException
    {
        File[] files = dirObj.listFiles();
        byte[] tmpBuf = new byte[1024];
        File currentFile;
        String entryPrefix = ensurePrefixWithSlash(dirObj, prefix);
        String entryName = "";

        for (int i = 0; i < files.length; i++)
        {
            currentFile = files[i];
            if (currentFile.isDirectory())
            {
                entryName = entryPrefix + currentFile.getName() + "/";

                // need to manually add folders so entries are in order
                ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
                out.putArchiveEntry(entry);
                out.closeArchiveEntry();

                // add the files in the folder
                addZipDir(currentFile, out, entryName);
            }
            else if (currentFile.isFile())
            {

                entryName = entryPrefix + currentFile.getName();
                FileInputStream in = new FileInputStream(currentFile.getAbsolutePath());
                try
                {
                    ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
                    out.putArchiveEntry(entry);
                    if (currentFile.canExecute())
                    {
                        entry.setUnixMode(0700);
                    }
                    // Transfer from the file to the ZIP file
                    int len;
                    while ((len = in.read(tmpBuf)) > 0)
                    {
                        out.write(tmpBuf, 0, len);
                    }

                    // Complete the entry
                    out.closeArchiveEntry();
                }
                finally
                {
                    IOUtils.closeQuietly(in);
                }
            }
        }
    }

    /**
     * Make sure 'prefix' is in format 'entry/' or, by default, 'rootDir/'
     * (not '', '/', '/entry', or 'entry').
     */
    private static String ensurePrefixWithSlash(File rootDir, String prefix)
    {
        String entryPrefix = prefix;

        if (StringUtils.isNotBlank(entryPrefix) && !entryPrefix.equals("/"))
        {
            // strip leading '/'
            if (entryPrefix.charAt(0) == '/')
            {
                entryPrefix = entryPrefix.substring(1);
            }
            // ensure trailing '/'
            if (entryPrefix.charAt(entryPrefix.length() - 1) != '/')
            {
                entryPrefix = entryPrefix + "/";
            }
        }
        else
        {
            entryPrefix = rootDir.getName() + "/";
        }

        return entryPrefix;
    }

    private static String trimPathSegments(String zipPath, final int trimLeadingPathSegments)
    {
        int startIndex = 0;
        for (int i = 0; i < trimLeadingPathSegments; i++)
        {
            int nextSlash = zipPath.indexOf("/", startIndex);
            if (nextSlash == -1)
            {
                break;
            }
            else
            {
                startIndex = nextSlash + 1;
            }
        }

        return zipPath.substring(startIndex);
    }

    private static String flattenPath(String zipPath)
    {
        return zipPath.substring(Math.max(zipPath.lastIndexOf("/"), 0));
    }

}
