package com.atlassian.maven.plugins.amps.util;

/**
 * @since version
 */
public interface UpdateChecker
{

    void check();

    void setSkipCheck(boolean skip);

    void setCurrentVersion(String currentVersion);

    void setForceCheck(boolean forceCheck);
}
