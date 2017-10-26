package com.atlassian.maven.plugins.amps.product;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * Convenience class to determine the version of tomcat that should be run, need to modify when a product
 * updates their configuration
 */
public class ProductContainerVersionMapper
{
    private static final Map<String, TreeMap<ComparableVersion, String>> productMapping = new HashMap<>();
    private static final String TOMCAT6X = "tomcat6x";
    private static final String TOMCAT7X = "tomcat7x";
    private static final String TOMCAT8X = "tomcat8x";
    private static final String TOMCAT85X = "tomcat85x";

    static
    {
        //Most apps
        populateVersionMapForProduct(ProductHandlerFactory.BAMBOO, "0", "5.1.0", "5.10.0");
        populateVersionMapForProduct(ProductHandlerFactory.BITBUCKET, null, null, "4.0.0");
        populateVersionMapForProduct(ProductHandlerFactory.CONFLUENCE, "0", "5.5", "5.8");
        populateVersionMapForProduct(ProductHandlerFactory.CROWD, "0", "2.7.0", null, "3.1.0");
        populateVersionMapForProduct(ProductHandlerFactory.JIRA, "0", "5.2", "7.0.0", "7.3.0");
        populateVersionMapForProduct(ProductHandlerFactory.REFAPP, "0", "2.21.0", "3.0.0");
        populateVersionMapForProduct(ProductHandlerFactory.STASH, "0", "2.0.0", "3.3.0");
    }

    private static void populateVersionMapForProduct(final String productId, final String tomcat6Version, final String tomcat7Version, final String tomcat8Version, final String tomcat85Version)
    {
        TreeMap<ComparableVersion, String> versions = new TreeMap<>();
        if (tomcat6Version != null) {
            versions.put(new ComparableVersion(tomcat6Version), TOMCAT6X);
        }
        if (tomcat7Version != null) {
            versions.put(new ComparableVersion(tomcat7Version), TOMCAT7X);
        }
        if (tomcat8Version != null) {
            versions.put(new ComparableVersion(tomcat8Version), TOMCAT8X);
        }
        if (tomcat85Version != null) {
            versions.put(new ComparableVersion(tomcat85Version), TOMCAT85X);
        }
        productMapping.put(productId, versions);
    }

    private static void populateVersionMapForProduct(final String productId, final String tomcat6Version, final String tomcat7Version, final String tomcat8Version)
    {
        populateVersionMapForProduct(productId, tomcat6Version, tomcat7Version, tomcat8Version, null);
    }

    public static String containerForProductVersion(String productId, String version)
    {
        if (version == null)
        {
            version = Artifact.LATEST_VERSION;
        }
        final ComparableVersion productVersion = new ComparableVersion(version);
        TreeMap versions = productMapping.get(productId);
        String containerId = null;
        if (versions != null)
        {
            Map.Entry<ComparableVersion, String> entry = versions.floorEntry(productVersion);
            if (entry != null)
            {
                containerId = entry.getValue();
            }
        }
        if (containerId == null)
        {
            containerId = AmpsDefaults.DEFAULT_CONTAINER;
        }
        return containerId;
    }
}
