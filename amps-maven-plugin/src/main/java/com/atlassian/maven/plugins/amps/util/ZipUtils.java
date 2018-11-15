package com.atlassian.maven.plugins.amps.util;

import com.google.common.collect.Lists;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

public class ZipUtils
{
    /**
     * A mask to test against to determine whether the executable bit is set in a file's mode.
     * <p>
     * Note that this value needs to be specified in octal (leading 0), not decimal, to set the correct bits.
     */
    @SuppressWarnings("OctalInteger")
    private static final int MASK_EXECUTABLE = 0100;
    /**
     * The mode to use when marking a file executable when creating a zipfile.
     * <p>
     * Note that this value needs to be specified in octal (leading 0), not decimal, to set the correct bits.
     */
    @SuppressWarnings("OctalInteger")
    private static final int MODE_EXECUTABLE = 0700;

    /**
     * Ungzips and extracts the specified tar.gz file into the specified directory.
     * @param targz the tar.gz file to use
     * @param destDir the directory to contain the extracted contents
     * @throws IOException if the archive cannot be expanded
     */
    public static void untargz(File targz, String destDir) throws IOException
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
     * @throws IOException if the archive cannot be expanded
     */
    public static void untargz(File targz, String destDir, int leadingPathSegmentsToTrim) throws IOException
    {
        try (FileInputStream fin = new FileInputStream(targz);
             GzipCompressorInputStream gzIn = new GzipCompressorInputStream(fin);
             TarArchiveInputStream tarIn = new TarArchiveInputStream(gzIn))
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

                try (FileOutputStream fos = new FileOutputStream(entryFile))
                {
                    IOUtils.copy(tarIn, fos);

                    // check for user-executable bit on entry and apply to file
                    if ((entry.getMode() & MASK_EXECUTABLE) != 0) {
                        entryFile.setExecutable(true);
                    }
                }
            }
        }
    }

    public static void unzip(File zipFile, String destDir) throws IOException
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
     * @throws IOException if the archive cannot be expanded
     */
    public static void unzip(File zipFile, String destDir, int leadingPathSegmentsToTrim) throws IOException
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
     * @throws IOException if the archive cannot be expanded
     */
    public static void unzip(File zipFile, String destDir, int leadingPathSegmentsToTrim,
                             boolean flatten, Pattern pattern) throws IOException
    {
        try (ZipFile zip = new ZipFile(zipFile))
        {
            Enumeration<? extends ZipArchiveEntry> entries = zip.getEntries();
            while (entries.hasMoreElements())
            {
                ZipArchiveEntry zipEntry = entries.nextElement();
                String name = zipEntry.getName();
                if(pattern == null || pattern.matcher(name).matches())
                {
                    String zipPath = trimPathSegments(name, leadingPathSegmentsToTrim);
                    if (flatten)
                    {
                        zipPath = flattenPath(zipPath);
                    }
                    File file = new File(destDir + "/" + zipPath);
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

                    try (InputStream is = zip.getInputStream(zipEntry);
                         OutputStream fos = new FileOutputStream(file))
                    {
                        IOUtils.copy(is, fos);

                        // check for user-executable bit on entry and apply to file
                        if ((zipEntry.getUnixMode() & MASK_EXECUTABLE) != 0)
                        {
                            file.setExecutable(true);
                        }
                    }
                    file.setLastModified(zipEntry.getTime());
                }
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
    public static int countNestingLevel(File zip) throws IOException
    {
        try (ZipFile zipFile = new ZipFile(zip))
        {
            List<String> filenames = toList(zipFile.getEntries());
            return countNestingLevel(filenames);
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
        String prefix = StringUtils.getCommonPrefix(filenames.toArray(new String[0]));
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

    private static List<String> toList(Enumeration<? extends ZipEntry> entries)
    {
        List<String> filenamesList = Lists.newArrayList();
        while (entries.hasMoreElements())
        {
            ZipEntry zipEntry = entries.nextElement();
            filenamesList.add(zipEntry.getName());
        }
        return filenamesList;
    }

    /**
     * Recursively zips the <i>children</i> of the specified root directory.
     * <p>
     * Unlike {@link #zipDir}, this method does not include any prefixes. Files and subdirectories under the specified
     * root directory are included <i>without prefixes</i> in the resulting zip.
     * <p>
     * Consider the following directory structure:
     * <code><pre>
     * /tmp
     * |- /tmp/foo
     *    |- /tmp/foo/file.txt
     *    |- /tmp/foo/bar
     *       |- /tmp/foo/bar/file.txt
     * </pre></code>
     * Given {@code /tmp/foo} as {@code rootDir}, the resulting zip will contain the following entries:
     * <ul>
     *     <li>{@code file.txt}</li>
     *     <li>{@code bar/} (directories get explicit entries in the zip file)</li>
     *     <li>{@code bar/file.txt}</li>
     * </ul>
     * Using {@code zipDir(zipFile, new File("/tmp/foo"), null)} would include the same files and directories, but
     * each entry would start with {@code foo/}. There is no way to disable using <i>some</i> prefix.
     *
     * @param zipFile the zip file to create
     * @param rootDir the root directory, from which all files and subdirectories should be zipped
     * @throws IOException if the zip cannot be created
     * @since 8.0
     */
    public static void zipChildren(File zipFile, File rootDir) throws IOException
    {
        Path root = rootDir.toPath();

        try (ZipArchiveOutputStream out = new ZipArchiveOutputStream(zipFile);
             Stream<Path> children = Files.walk(root).skip(1L)) // Drop the root directory
        {
            byte[] buffer = new byte[8192]; //ZipArchiveOutputStream internally supports blocks up to 8K

            for (Path child : (Iterable<Path>) children::iterator)
            {
                BasicFileAttributes attributes = Files.readAttributes(child, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
                if (attributes.isOther() || attributes.isSymbolicLink())
                {
                    // Symbolic links and "other" file types won't zipped (or extracted) faithfully,
                    // so they are ignored
                    continue;
                }

                String path = root.relativize(child).toString();
                if (SystemUtils.IS_OS_WINDOWS)
                {
                    // Always use forward slashes for the zip's entries, even on Windows
                    path = path.replace('\\', '/');
                }
                if (attributes.isDirectory())
                {
                    path += "/";
                }

                ZipArchiveEntry entry = new ZipArchiveEntry(path);
                out.putArchiveEntry(entry);

                if (attributes.isRegularFile())
                {
                    if (Files.isExecutable(child))
                    {
                        entry.setUnixMode(MODE_EXECUTABLE);
                    }

                    try (InputStream content = Files.newInputStream(child))
                    {
                        copyViaBuffer(content, out, buffer);
                    }
                }

                out.closeArchiveEntry();
            }
        }
    }

    /**
     * @param prefix the prefix. If empty, uses the srcDir's name. That means you can't create a zip with no
     * root folder.
     */
    public static void zipDir(File zipFile, File srcDir, String prefix) throws IOException
    {
        try (ZipArchiveOutputStream out = new ZipArchiveOutputStream(zipFile))
        {
            addZipPrefixes(srcDir, out, prefix);
            addZipDir(srcDir, out, prefix);
        }
    }

    private static void addZipPrefixes(File dirObj, ZipArchiveOutputStream out, String prefix) throws IOException
    {
        // need to manually add the prefix folders
        String entryPrefix = ensurePrefixWithSlash(dirObj, prefix);

        String[] prefixes = entryPrefix.split("/");
        String lastPrefix = "";
        for (String p : prefixes)
        {
            ZipArchiveEntry entry = new ZipArchiveEntry(lastPrefix + p + "/");
            out.putArchiveEntry(entry);
            out.closeArchiveEntry();

            lastPrefix = p + "/";
        }
    }

    private static void addZipDir(File dirObj, ZipArchiveOutputStream out, String prefix) throws IOException
    {
        File[] files = dirObj.listFiles();
        if (files == null || files.length == 0)
        {
            return;
        }

        byte[] tmpBuf = new byte[8192]; //ZipArchiveOutputStream internally supports blocks up to 8K
        String entryPrefix = ensurePrefixWithSlash(dirObj, prefix);

        for (File currentFile : files)
        {
            if (currentFile.isDirectory())
            {
                String entryName = entryPrefix + currentFile.getName() + "/";

                // need to manually add folders so entries are in order
                ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
                out.putArchiveEntry(entry);
                out.closeArchiveEntry();

                // add the files in the folder
                addZipDir(currentFile, out, entryName);
            }
            else if (currentFile.isFile())
            {
                String entryName = entryPrefix + currentFile.getName();
                try (FileInputStream in = new FileInputStream(currentFile.getAbsolutePath()))
                {
                    ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
                    out.putArchiveEntry(entry);
                    if (currentFile.canExecute())
                    {
                        entry.setUnixMode(MODE_EXECUTABLE);
                    }
                    // Transfer from the file to the ZIP file
                    copyViaBuffer(in, out, tmpBuf);

                    // Complete the entry
                    out.closeArchiveEntry();
                }
            }
        }
    }

    private static void copyViaBuffer(InputStream inputStream, OutputStream outputStream, byte[] buffer)
            throws IOException
    {
        int read;
        while ((read = inputStream.read(buffer)) != -1)
        {
            outputStream.write(buffer, 0, read);
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

    private static String trimPathSegments(String zipPath, int trimLeadingPathSegments)
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
