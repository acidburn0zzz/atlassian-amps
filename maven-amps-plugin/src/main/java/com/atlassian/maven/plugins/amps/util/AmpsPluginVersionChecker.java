package com.atlassian.maven.plugins.amps.util;

import java.io.File;

import org.apache.maven.project.MavenProject;

/**
 * @since version
 */
public interface AmpsPluginVersionChecker
{
    void checkAmpsVersionInPom(String currentVersion, MavenProject project);
    void skipPomCheck(boolean skip);
}
