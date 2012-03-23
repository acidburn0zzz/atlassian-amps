package com.atlassian.plugins.codegen.modules.common.licensing;

import com.atlassian.plugins.codegen.modules.BasicClassModuleProperties;

/**
 * @since 3.7
 */
public class LicensingProperties extends BasicClassModuleProperties
{
    private String licenseServletPath = "";
    private String helloWorldServletPath = "";
    
    public LicensingProperties()
    {
        super();
    }
    
    public LicensingProperties(String fqClass)
    {
        super(fqClass);
    }

    public String getLicenseServletPath()
    {
        return licenseServletPath;
    }

    public void setLicenseServletPath(String licenseServletPath)
    {
        this.licenseServletPath = licenseServletPath;
        setProperty("LICENSE_SERVLET_PATH", licenseServletPath);
    }

    public String getHelloWorldServletPath()
    {
        return helloWorldServletPath;
    }

    public void setHelloWorldServletPath(String helloWorldServletPath)
    {
        this.helloWorldServletPath = helloWorldServletPath;
        setProperty("HELLO_WORLD_SERVLET_PATH", helloWorldServletPath);
    }
}
