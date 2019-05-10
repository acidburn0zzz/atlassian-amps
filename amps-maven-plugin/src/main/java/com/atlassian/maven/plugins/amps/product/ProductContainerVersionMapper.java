package com.atlassian.maven.plugins.amps.product;

import com.google.common.collect.ImmutableMap;
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
    private static final String TOMCAT85_6 = "tomcat85_6";
    private static final String TOMCAT9X = "tomcat9x";

    static
    {
        //Most apps
        populateVersionMapForProduct(ProductHandlerFactory.BAMBOO, "0", "5.1", "5.10");
        populateVersionMapForProduct(ProductHandlerFactory.BITBUCKET, null, null, "1");
        populateVersionMapForProduct(ProductHandlerFactory.CONFLUENCE, "0", "5.5", "5.8", null, "6.10");
        populateVersionMapForProduct(ProductHandlerFactory.CROWD, "0", "2.7.0", null, "3.1.0");
        populateVersionMapForProduct(ProductHandlerFactory.JIRA, new ImmutableMap.Builder<String, String>()
                .put("0", "tomcat6x")
                .put("5.2", "tomcat7x")
                .put("7.0.0", "tomcat8x")
                .put("7.3.0", "tomcat85_6")
                .put("7.6.0", "tomcat85_29")
                .put("7.12.0", "tomcat85_32")
                .put("7.13.0", "tomcat85_35")
                .put("8.2.1", "tomcat85x")
                .build()
        );
        populateVersionMapForProduct(ProductHandlerFactory.REFAPP, "0", "2.21.0", "3.0.0", "5.0.0");
    }

    private static <K, V> void populateVersionMapForProduct(String productId, ImmutableMap<String, String> productToTomcatVersions) {
        TreeMap<ComparableVersion, String> versions = new TreeMap<>();
        for (String productVersion : productToTomcatVersions.keySet()) {
            versions.put(new ComparableVersion(productVersion), productToTomcatVersions.get(productVersion));
        }

        productMapping.put(productId, versions);
    }

    private static void populateVersionMapForProduct(final String productId, final String tomcat6Version, final String tomcat7Version, final String tomcat8Version, final String tomcat85Version, final String tomcat9Version)
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
        if (tomcat9Version != null) {
            versions.put(new ComparableVersion(tomcat9Version), TOMCAT9X);
        }
        productMapping.put(productId, versions);
    }

    private static void populateVersionMapForProduct(final String productId, final String tomcat6Version, final String tomcat7Version, final String tomcat8Version, final String tomcat85Version)
    {
        populateVersionMapForProduct(productId, tomcat6Version, tomcat7Version, tomcat8Version, tomcat85Version, null);
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
