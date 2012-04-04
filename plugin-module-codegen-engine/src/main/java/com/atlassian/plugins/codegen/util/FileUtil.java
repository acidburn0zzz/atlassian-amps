package com.atlassian.plugins.codegen.util;

import java.io.File;

public class FileUtil
{
    /**
     * Constructs an absolute file reference using a base directory and a dot-delimited identifier,
     * where all but the last component of the identifier are used as subdirectory names.  For instance,
     * "com.foo.bar" with a file extension of ".properties" will become "parentDir/com/foo/bar.properties".
     */
    public static File dotDelimitedFilePath(File parentDir, String identifier, String extension)
    {
        return new File(parentDir, identifier.replace(".", File.separator) + extension);
    }
}
