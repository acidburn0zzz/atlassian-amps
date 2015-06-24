package com.atlassian.maven.plugins.amps;

/**
 * Represents an application to be retrieved
 */
public class Application
{
    private String applicationKey, version;

    public Application() {
    }

    public Application(final String applicationKey) {
        this.applicationKey = applicationKey;
    }

    public Application(final String applicationKey, final String version) {
        this.applicationKey = applicationKey;
        this.version = version;
    }

    public String getApplicationKey()
    {
        return applicationKey;
    }

    public void setApplicationKey(final String applicationKey)
    {
        this.applicationKey = applicationKey;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    @Override
    public String toString()
    {
        return applicationKey + ":" + version;
    }
}
