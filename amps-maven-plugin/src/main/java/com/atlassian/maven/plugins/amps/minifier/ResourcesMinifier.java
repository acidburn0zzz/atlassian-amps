package com.atlassian.maven.plugins.amps.minifier;

import com.atlassian.maven.plugins.amps.code.Sources;
import com.atlassian.maven.plugins.amps.minifier.strategies.NoMinificationStrategy;
import com.atlassian.maven.plugins.amps.minifier.strategies.XmlMinifierStrategy;
import com.atlassian.maven.plugins.amps.minifier.strategies.googleclosure.GoogleClosureJsMinifierStrategy;
import com.atlassian.maven.plugins.amps.minifier.strategies.yui.YUICompressorCssMinifierStrategy;
import com.atlassian.maven.plugins.amps.minifier.strategies.yui.YUICompressorJsMinifierStrategy;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import static java.util.Collections.singletonList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.io.FileUtils.copyFile;
import static org.apache.commons.io.FileUtils.forceMkdir;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getExtension;

/**
 * Iterates through Maven {@link Resource} declarations, applying a {@link Minifier} to files inside them as
 * appropriate.
 *
 * @since 8.1
 */
public class ResourcesMinifier {
    private static final List<String> MINIFIED_FILENAME_SUFFIXES = Arrays.asList("-min", ".min");
    private final MinifierParameters minifierParameters;

    public ResourcesMinifier(MinifierParameters minifierParameters) {
        this.minifierParameters = Objects.requireNonNull(minifierParameters);
    }

    public void minify(List<Resource> resources, String outputPath) throws MojoExecutionException {
        for (Resource resource : resources) {
            minify(resource, outputPath);
        }
    }

    public void minify(Resource resource, String outputPath) throws MojoExecutionException {
        final File resourceDir = new File(resource.getDirectory());
        if (!resourceDir.exists()) {
            return;
        }

        final File outputDir = new File(outputPath);

        final String targetPath = resource.getTargetPath();
        final File destDir = StringUtils.isNotBlank(targetPath) ? new File(outputDir, targetPath) : outputDir;

        // Process all relevant filetypes in the resource dir
        for (String type : getFiletypesToProcess()) {
            processFiletypeInDirectory(type, resourceDir, destDir, resource.getIncludes(), resource.getExcludes());
        }
    }

    /**
     * Discovers all files in a directory with the given filetype, then runs their contents through an appropriate
     * minifier. The minified content of each input file will be written to a file with a `-min.[extname]` suffix
     * in the provided destination directory.
     *
     * @param filetype the filetype to find in the resource dir, such as "js", "css", "xml", etc.
     * @param resourceDir the folder in which to search for input files.
     * @param destDir the folder where minified files will be written.
     * @param includes a list of filepaths that should be processed, regardless of their filetype.
     * @param excludes any filepath in this list will not be processed, even if it matches the given filetype.
     * @throws MojoExecutionException
     */
    public void processFiletypeInDirectory(
        @Nonnull final String filetype,
        final File resourceDir,
        final File destDir,
        final List<String> includes,
        final List<String> excludes) throws MojoExecutionException {

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(resourceDir);

        // Add included files to the scanner from the build, if they are configured.
        // Otherwise, fall back to finding all files of type extname.
        if (isNotEmpty(includes)) {
            scanner.setIncludes(includes.toArray(new String[0]));
        } else {
            scanner.setIncludes(singletonList("**/*." + filetype).toArray(new String[0]));
        }

        // Add excluded files to the scanner from the build, if they are configured.
        if (isNotEmpty(excludes)) {
            scanner.setExcludes(excludes.toArray(new String[0]));
        }
        scanner.addDefaultExcludes();

        // Collect all files to be processed.
        scanner.scan();
        processFileList(filetype, destDir, Arrays.stream(scanner.getIncludedFiles())
            .filter(s -> getExtension(s).endsWith(filetype))
            .map(s -> new File(resourceDir, s))
            .collect(Collectors.toList()));
    }

    private void processFileList(
        @Nonnull final String extname,
        final File destDir,
        final List<File> filenames) throws MojoExecutionException {
        final Log log = minifierParameters.getLog();
        final Minifier strategy = getMinifierStrategy(extname);
        int minified = 0;
        int copied = 0;

        for (File sourceFile : filenames) {
            if (sourceFile.canRead()) {
                final String minifiedExtension = "-min." + extname;
                if (maybeCopyPreminifiedFileToDest(sourceFile, destDir, minifiedExtension)) {
                    copied++;
                    continue;
                }

                final String name = sourceFile.getName();
                final File destFile = new File(destDir, getBaseName(name) + minifiedExtension);

                if (destFile.exists() && destFile.lastModified() > sourceFile.lastModified()) {
                    log.debug("Nothing to do, " + destFile.getAbsolutePath() + " is younger than the original");
                    continue;
                }

                try {
                    log.debug("minifying to " + destFile.getAbsolutePath());
                    final Charset cs = minifierParameters.getCs();
                    final Sources input = new Sources(readFileToString(sourceFile, cs));
                    final Sources output = strategy.minify(input, minifierParameters);
                    forceMkdir(destFile.getParentFile());
                    writeStringToFile(destFile, output.getContent(), cs);

                    if (output.hasSourceMap()) {
                        final File sourceMapFile = new File(destFile.getAbsolutePath() + ".map");
                        writeStringToFile(sourceMapFile, output.getSourceMapContent(), cs);
                    }
                    minified++;
                } catch (IOException e) {
                    throw new MojoExecutionException("IOException when minifying '" + name + "'", e);
                }
            }
        }
        log.info(String.format("%d %s file(s) were output to target directory %s", minified + copied, extname, destDir.getAbsolutePath()));
    }

    /**
     * If the file is determined to be already minified (by checking the file basename for a ".min" or "-min" suffix),
     * then it will copy the file to the destination.
     *
     * @param sourceFile        Source file
     * @param destDir           The output directory where the minified code should end up
     * @param minifiedExtension The correct suffix to apply to the file if the source is considered minified
     * @return true if and only if the file name ends with .min.js or -min.js
     * @throws MojoExecutionException If an IOException is encountered reading or writing the source or destination
     *                                file.
     */
    private boolean maybeCopyPreminifiedFileToDest(final File sourceFile,
        final File destDir,
        final String minifiedExtension) throws MojoExecutionException {
        final Log log = minifierParameters.getLog();
        try {
            final String name = sourceFile.getName();
            final String baseName = getBaseName(name);
            for (String s : MINIFIED_FILENAME_SUFFIXES) {
                if (baseName.endsWith(s)) {
                    String newName = baseName.substring(0, baseName.length() - s.length());
                    File destFile = new File(destDir, newName + minifiedExtension);
                    log.debug(String.format("Copying pre-minified file '%s' to destination '%s' file ends in '%s'", name, destFile.getName(), s));
                    copyFile(sourceFile, destFile);
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            throw new MojoExecutionException("IOException when trying to copy pre-minified file to target", e);
        }
    }

    // Determine which filetypes to process based on build parameters
    private List<String> getFiletypesToProcess() {
        List<String> types = new ArrayList<>();
        if (minifierParameters.isCompressJs()) {
            types.add("js");
        }
        if (minifierParameters.isCompressCss()) {
            types.add("css");
        }
        types.add("xml");
        return types;
    }

    private Minifier getMinifierStrategy(String extname) {
        final boolean withClosure = minifierParameters.isUseClosureForJs();
        switch (extname) {
            case "js":
                return withClosure ? new GoogleClosureJsMinifierStrategy() : new YUICompressorJsMinifierStrategy();
            case "css":
                return new YUICompressorCssMinifierStrategy();
            case "xml":
                return new XmlMinifierStrategy();
            default:
                return new NoMinificationStrategy();
        }
    }
}
