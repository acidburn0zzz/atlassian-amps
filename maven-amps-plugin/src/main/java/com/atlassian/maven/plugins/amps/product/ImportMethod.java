package com.atlassian.maven.plugins.amps.product;

public enum ImportMethod
{
    SQL("sql"), PSQL("psql"), IMPDP("impdp"), SQLCMD("sqlcmd");

    public static ImportMethod getValueOf(final String importMethod)
    {
        for (ImportMethod m : ImportMethod.values())
        {
            // support clients specific import method case-insensitively
            if (m.getMethod().equalsIgnoreCase(importMethod))
            {
                return m;
            }
        }
        return null;
    }

    private final String method;

    ImportMethod(final String method)
    {
        this.method = method;
    }

    public String getMethod()
    {
        return this.method;
    }

}
