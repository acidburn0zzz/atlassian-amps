package com.atlassian.maven.plugins.amps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.atlassian.maven.plugins.amps.product.ProductHandler;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import static org.apache.commons.lang3.StringUtils.isBlank;

public abstract class AbstractTestGroupsHandlerMojo extends AbstractProductHandlerMojo
{
     /**
     * Test group to run. If provided, used to determine the products to run.
     */
    @Parameter(property = "testGroup")
    protected String testGroup;

    /**
     * The list of configured test groups
     */
    @Parameter
    private List<TestGroup> testGroups = new ArrayList<TestGroup>();

    protected final List<TestGroup> getTestGroups()
    {
        return testGroups;
    }

    protected final List<ProductExecution> getTestGroupProductExecutions(String testGroupId) throws MojoExecutionException
    {
        // Create a container object to hold product-related stuff
        List<ProductExecution> products = new ArrayList<ProductExecution>();
        int dupCounter = 0;
        Set<String> uniqueProductIds = new HashSet<String>();
        Map<String, Product> productContexts = getProductContexts();
        for (String instanceId : getTestGroupInstanceIds(testGroupId))
        {
            Product ctx = productContexts.get(instanceId);
            if (ctx == null)
            {
                throw new MojoExecutionException("The test group '" + testGroupId + "' refers to a product '" + instanceId
                    + "' that doesn't have an associated <product> configuration.");
            }
            ProductHandler productHandler = createProductHandler(ctx.getId());

            // Give unique ids to duplicate product instances
            if (uniqueProductIds.contains(instanceId))
            {
                ctx.setInstanceId(instanceId + "-" + dupCounter++);
            }
            else
            {
                uniqueProductIds.add(instanceId);
            }
            products.add(new ProductExecution(ctx, productHandler));
        }

        return products;
    }

    /**
     * Returns the products in the test group:
     * <ul>
     * <li>If a {@literal <testGroup>} is defined, all the products of this test group</li>
     * <li>If testGroupId is __no_test_group__, adds it</li>
     * <li>If testGroupId is a product instanceId, adds it</li>
     * </ul>
     */
    private List<String> getTestGroupInstanceIds(String testGroupId) throws MojoExecutionException
    {
        List<String> instanceIds = new ArrayList<String>();
        if (NO_TEST_GROUP.equals(testGroupId))
        {
            instanceIds.add(getProductId());
        }

        for (TestGroup group : testGroups)
        {
            if (group.getId().equals(testGroupId))
            {
                instanceIds.addAll(group.getInstanceIds());
            }
        }
        if (ProductHandlerFactory.getIds().contains(testGroupId) && !instanceIds.contains(testGroupId))
        {
            instanceIds.add(testGroupId);
        }

        if (instanceIds.isEmpty())
        {
            List<String> validTestGroups = new ArrayList<String>();
            for (TestGroup group: testGroups)
            {
                validTestGroups.add(group.getId());
            }
            getLog().warn("Unknown test group ID: " + testGroupId + " Detected IDs: " + Arrays.toString(validTestGroups.toArray()));
        }

        return instanceIds;
    }

    protected List<ProductExecution> getProductExecutions() throws MojoExecutionException
    {
        final List<ProductExecution> productExecutions;
        if (!isBlank(testGroup))
        {
            if (products.size() > 1)
            {
                validatePortConfiguration(products);
            }
            productExecutions = getTestGroupProductExecutions(testGroup);
        }
        else if (!isBlank(instanceId))
        {
            Product ctx = getProductContexts().get(instanceId);
            if (ctx == null)
            {
                throw new MojoExecutionException("No product with instance ID '" + instanceId + "'");
            }
            ProductHandler product = createProductHandler(ctx.getId());
            productExecutions = Collections.singletonList(new ProductExecution(ctx, product));
        }
        else
        {
            Product ctx = getProductContexts().get(getProductId());
            ProductHandler product = createProductHandler(ctx.getId());
            productExecutions = Collections.singletonList(new ProductExecution(ctx, product));
        }
        return productExecutions;
    }

    /**
     * Ensures that there are no port conflicts between products and raises an exception if there
     * are conflicts
     * @param products
     * @throws MojoExecutionException
     */
    protected void validatePortConfiguration(List<Product> products) throws MojoExecutionException
    {
        HashSet<String> compared = new HashSet<>();
        HashSet<String> errorSet = new HashSet<>();
        for (Product product : products)
        {
            for (Product product1 : products)
            {
                if (!product.getInstanceId().equals(product1.getInstanceId()) && !compared.contains(product1.getInstanceId()))
                {
                    if (product.isHttps() && product.getHttpsPort() != 0)
                    {
                        checkPortConflicts(product.getHttpsPort(), "HTTPS", product, product1, errorSet);
                    }
                    else
                    {
                        checkPortConflicts(product.getHttpPort(), "HTTP", product, product1, errorSet);
                    }
                    checkPortConflicts(product.getAjpPort(), "AJP", product, product1, errorSet);
                    checkPortConflicts(product.getRmiPort(), "RMI", product, product1, errorSet);
                }
            }
            compared.add(product.getInstanceId());
        }
        if (errorSet.size() > 0)
        {
            for (String error : errorSet)
            {
                getLog().error(error);
            }
            throw new MojoExecutionException("Port conflicts detected. Please see error log for details");
        }
    }

    /**
     * Does the actual comparisons between two provided products and adds error messages to provided errorSet. This
     * method also ensures that duplicates are not reported.
     * @param port The port number being compared to the other portTypes
     * @param portType The port type of the provided port. E.g. HTTP or AJP.
     * @param product The first product
     * @param product1 The second product
     * @param errorSet The set to add error messages to.
     */
    private void checkPortConflicts(int port, String portType, Product product, Product product1, Set<String> errorSet)
    {
        portType = portType.toUpperCase();
        if (port != 0)
        {
            if (port == product1.getHttpsPort())
            {
                errorSet.add(String.format("Conflict between %s port of %s and HTTPS port of %s on %d", portType, product.getInstanceId(), product1.getInstanceId(), port));
            }
            if (port == product1.getHttpPort())
            {
                errorSet.add(String.format("Conflict between %s port of %s and HTTP port of %s on %d", portType, product.getInstanceId(), product1.getInstanceId(), port));
            }
            if (port == product1.getAjpPort())
            {
                errorSet.add(String.format("Conflict between %s port of %s and AJP port of %s on %d", portType, product.getInstanceId(), product1.getInstanceId(), port));
            }
            if (port == product1.getRmiPort())
            {
                errorSet.add(String.format("Conflict between %s port of %s and RMI port of %s on %d", portType, product.getInstanceId(), product1.getInstanceId(), port));
            }
            if (!portType.equalsIgnoreCase("HTTP") && !portType.equalsIgnoreCase("HTTPS"))
            {
                if (port == product.getHttpsPort()) {
                    if (!errorSet.contains(String.format("Conflict between HTTPS port of %s and %s port of %s on %d", product.getInstanceId(), portType, product.getInstanceId(), port))) {
                        errorSet.add(String.format("Conflict between %s port of %s and HTTPS port of %s on %d", portType, product.getInstanceId(), product.getInstanceId(), port));
                    }
                }
                if (port == product.getHttpPort())
                {
                    if (!errorSet.contains(String.format("Conflict between HTTP port of %s and %s port of %s on %d", product.getInstanceId(), portType, product.getInstanceId(), port)))
                    {
                        errorSet.add(String.format("Conflict between %s port of %s and HTTP port of %s on %d", portType, product.getInstanceId(), product.getInstanceId(), port));
                    }
                }
            }
            if (!portType.equalsIgnoreCase("AJP") && port == product.getAjpPort())
            {
                if (!errorSet.contains(String.format("Conflict between AJP port of %s and %s port of %s on %d", product.getInstanceId(), portType, product.getInstanceId(), port)))
                {
                    errorSet.add(String.format("Conflict between %s port of %s and AJP port of %s on %d", portType, product.getInstanceId(), product.getInstanceId(), port));
                }
            }
            if (!portType.equalsIgnoreCase("RMI") && port == product.getRmiPort())
            {
                if (!errorSet.contains(String.format("Conflict between RMI port of %s and %s port of %s on %d", product.getInstanceId(), portType, product.getInstanceId(), port)))
                {
                    errorSet.add(String.format("Conflict between %s port of %s and RMI port of %s on %d", portType, product.getInstanceId(), product.getInstanceId(), port));
                }
            }
        }
    }
}
