package com.atlassian.maven.plugins.amps.minifier;

import com.atlassian.maven.plugins.amps.minifier.strategies.NoMinificationStrategy;
import com.atlassian.maven.plugins.amps.minifier.strategies.XmlMinifierStrategy;
import com.atlassian.maven.plugins.amps.minifier.strategies.googleclosure.GoogleClosureJsMinifierStrategy;
import com.atlassian.maven.plugins.amps.minifier.strategies.yui.YUICompressorCssMinifierStrategy;
import com.atlassian.maven.plugins.amps.minifier.strategies.yui.YUICompressorJsMinifierStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.DirectoryScanner;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * Iterates through Maven {@link Resource} declarations, applying a {@link Minifier}
 * to files inside them as appropriate.
 *
 * @since 4.1
 */
public class ResourcesMinifier {
    private static final List<String> SUFFIXES = Arrays.asList("-min", ".min");
    private final MinifierParameters minifierParameters;

    public ResourcesMinifier(MinifierParameters minifierParameters) {
        this.minifierParameters = minifierParameters;
    }

    public void minify(List<Resource> resources, String outputDir) throws MojoExecutionException {
        for (Resource resource : resources) {
            processResource(resource, new File(outputDir));
        }
    }

    public void processResource(Resource resource, File outputDir) throws MojoExecutionException {
        File destDir = outputDir;
        if (StringUtils.isNotBlank(resource.getTargetPath())) {
            destDir = new File(outputDir, resource.getTargetPath());
        }

        File resourceDir = new File(resource.getDirectory());
        if (!resourceDir.exists()) {
            return;
        }

        // Process all relevant filetypes in the resource dir
        for (String type : getFiletypesToProcess()) {
            processFiletype(type, resourceDir, destDir, resource.getIncludes(), resource.getExcludes());
        }
    }

    private void processFiletype(
            @Nonnull final String extname,
            final File resourceDir,
            final File destDir,
            final List<String> includes,
            final List<String> excludes) throws MojoExecutionException {
        Log log = minifierParameters.getLog();
        Minifier strategy = getMinifierStrategy(extname, minifierParameters);

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(resourceDir);

        // Add included files to the scanner from the build, if they are configured.
        // Otherwise, fall back to finding all files of type extname.
        if (includes != null && !includes.isEmpty()) {
            scanner.setIncludes(includes.toArray(new String[0]));
        } else {
            scanner.setIncludes(singletonList("**/*." + extname).toArray(new String[0]));
        }

        // Add excluded files to the scanner from the build, if they are configured.
        if (excludes != null && !excludes.isEmpty()) {
            scanner.setExcludes(excludes.toArray(new String[0]));
        }
        scanner.addDefaultExcludes();

        // Collect all files to be processed.
        scanner.scan();

        int minified = 0;
        int copied = 0;

        for (String name : scanner.getIncludedFiles()) {
            if (!FilenameUtils.getExtension(name).equals(extname)) {
                continue;
            }

            File sourceFile = new File(resourceDir, name);
            if (sourceFile.exists() && sourceFile.canRead()) {
                final String minifiedExtension = "-min." + extname;
                if (maybeCopyPreminifiedFileToDest(sourceFile, destDir, minifiedExtension)) {
                    copied++;
                    continue;
                }

                File destFile = new File(destDir, FilenameUtils.getBaseName(name) + minifiedExtension);

                if (destFile.exists() && destFile.lastModified() > sourceFile.lastModified()) {
                    log.debug("Nothing to do, " + destFile.getAbsolutePath() + " is younger than the original");
                    continue;
                }

                try {
                    log.debug("minifying to " + destFile.getAbsolutePath());
                    FileUtils.forceMkdir(destFile.getParentFile());
                    strategy.minify(sourceFile, destFile, minifierParameters);
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
     * @throws MojoExecutionException If an IOException is encountered reading or writing the source
     *                                or destination file.
     */
    private boolean maybeCopyPreminifiedFileToDest(final File sourceFile,
                                                   final File destDir,
                                                   final String minifiedExtension) throws MojoExecutionException {
        final Log log = minifierParameters.getLog();
        try {
            final String name = sourceFile.getName();
            final String baseName = FilenameUtils.getBaseName(name);
            for (String s : SUFFIXES) {
                if (baseName.endsWith(s)) {
                    String newName = baseName.substring(0, baseName.length() - s.length());
                    File destFile = new File(destDir, newName + minifiedExtension);
                    log.debug(String.format("Copying pre-minified file '%s' to destination '%s' file ends in '%s'", name, destFile.getName(), s));
                    FileUtils.copyFile(sourceFile, destFile);
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

    private Minifier getMinifierStrategy(String extname, MinifierParameters parameters) {
        switch (extname) {
            case "js":
                return parameters.isUseClosureForJs() ? new GoogleClosureJsMinifierStrategy() : new YUICompressorJsMinifierStrategy();
            case "css":
                return new YUICompressorCssMinifierStrategy();
            case "xml":
                return new XmlMinifierStrategy();
            default:
                return new NoMinificationStrategy();
        }
    }
}
