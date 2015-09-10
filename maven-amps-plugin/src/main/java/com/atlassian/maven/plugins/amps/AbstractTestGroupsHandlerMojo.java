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

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import static org.apache.commons.lang.StringUtils.isBlank;

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
            throw new MojoExecutionException("Unknown test group ID: " + testGroupId
                + " Detected IDs: " + Arrays.toString(validTestGroups.toArray()));
        }

        return instanceIds;
    }

    protected List<ProductExecution> getProductExecutions() throws MojoExecutionException
    {
        final List<ProductExecution> productExecutions;
        if (!isBlank(testGroup))
        {
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
}
