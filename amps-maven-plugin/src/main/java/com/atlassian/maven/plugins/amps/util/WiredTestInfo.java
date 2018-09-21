package com.atlassian.maven.plugins.amps.util;

public class WiredTestInfo
{
    private final boolean isWiredTest;
    private final String applicationFilter;

    public WiredTestInfo()
    {
        this(false,"");
    }

    public WiredTestInfo(boolean isWiredTest, String applicationFilter)
    {
        this.isWiredTest = isWiredTest;
        this.applicationFilter = applicationFilter;
    }

    public boolean isWiredTest()
    {
        return isWiredTest;
    }

    public String getApplicationFilter()
    {
        return applicationFilter;
    }
}
