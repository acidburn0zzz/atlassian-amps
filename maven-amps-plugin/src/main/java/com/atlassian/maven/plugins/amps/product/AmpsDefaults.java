package com.atlassian.maven.plugins.amps.product;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AmpsDefaults
{
    public static final String DEFAULT_CONTAINER = "tomcat6x";
    public static final String DEFAULT_SERVER;
    public static final String DEFAULT_PDK_VERSION = "0.6";
    public static final String DEFAULT_WEB_CONSOLE_VERSION = "1.2.8";
    // Please update maven-3-tests/pom.xml when bumping these versions!
    public static final String DEFAULT_FASTDEV_VERSION = "2.2";
    public static final String DEFAULT_DEV_TOOLBOX_VERSION = "2.0.12";
    public static final String DEFAULT_REST_API_BROWSER_VERSION = "3.0.6";
    public static final String DEFAULT_PDE_VERSION = "1.2";

    /**
     * Default product startup timeout: three minutes
     */
    public static final int DEFAULT_PRODUCT_STARTUP_TIMEOUT = 1000 * 60 * 10;

    /**
     * Default product shutdown timeout: three minutes
     */
    public static final int DEFAULT_PRODUCT_SHUTDOWN_TIMEOUT = 1000 * 60 * 10;

    static
    {
        String localHostName = null;
        try
        {
            localHostName = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e)
        {
            localHostName = "localhost";
        }
        DEFAULT_SERVER = localHostName;
    }

}
