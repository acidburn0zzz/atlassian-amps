package com.atlassian.maven.plugins.amps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.atlassian.maven.plugins.amps.product.ProductHandler;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import static java.util.Objects.requireNonNull;
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
    private List<TestGroup> testGroups = new ArrayList<>();

    protected final List<TestGroup> getTestGroups()
    {
        return testGroups;
    }

    protected final List<ProductExecution> getTestGroupProductExecutions(String testGroupId) throws MojoExecutionException
    {
        // Create a container object to hold product-related stuff
        List<ProductExecution> products = new ArrayList<>();
        int dupCounter = 0;
        Set<String> uniqueProductIds = new HashSet<>();
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

        if (products.size() > 1)
        {
            validatePortConfiguration(products);
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
        List<String> instanceIds = new ArrayList<>();
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
            List<String> validTestGroups = new ArrayList<>();
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
     *
     * @param executions two or more product executions, for which the configured ports should be validated
     * @throws MojoExecutionException if any of the configured ports collide between products
     * @since 8.0
     */
    void validatePortConfiguration(List<ProductExecution> executions) throws MojoExecutionException
    {
        Map<Integer, ConfiguredPort> portsById = new HashMap<>();

        MutableInt collisions = new MutableInt();
        executions.stream()
                .map(ProductExecution::getProduct)
                .flatMap(AbstractTestGroupsHandlerMojo::streamConfiguredPorts)
                .filter(ConfiguredPort::isStatic) // Only verify statically-configured ports
                .forEach(configured -> {
                    ConfiguredPort conflict = portsById.get(configured.port);
                    if (conflict == null)
                    {
                        portsById.put(configured.port, configured);
                    }
                    else
                    {
                        getLog().error(configured.instanceId + ": The configured " + configured.type +
                                " port, " + configured.port + ", is in use by the " + conflict.type +
                                " port for " + conflict.instanceId);
                        collisions.increment();
                    }
                });

        int collisionCount = collisions.intValue();
        if (collisionCount != 0)
        {
            throw new MojoExecutionException(collisionCount + " port conflict" +
                    ((collisionCount == 1) ? " was" : "s were") + " detected between the " +
                    executions.size() + " products in the '" + testGroup + "' test group");
        }
    }

    /**
     * Generates a {@code Stream} of {@link ConfiguredPort configured ports} for the specified {@link Product product}.
     *
     * @param product the product to stream configured ports for
     * @return the configured ports for the specified product
     * @since 8.0
     */
    private static Stream<ConfiguredPort> streamConfiguredPorts(Product product)
    {
        String instanceId = product.getInstanceId();

        ConfiguredPort primary;
        if (product.isHttps())
        {
            primary = new ConfiguredPort(instanceId, product.getHttpsPort(), "HTTPS");
        }
        else
        {
            primary = new ConfiguredPort(instanceId, product.getHttpPort(), "HTTP");
        }

        return Stream.of(primary,
                new ConfiguredPort(instanceId, product.getAjpPort(), "AJP"),
                new ConfiguredPort(instanceId, product.getRmiPort(), "RMI"));
    }

    /**
     * Describes a configured port for a {@link Product}, detailing the instance ID as well as the port and its type.
     * <p>
     * This data class simplifies maintaining an instance+port+type association, which facilitates using descriptive
     * error messages when the configured ports for two instances collide.
     *
     * @since 8.0
     */
    private static class ConfiguredPort
    {
        private final String instanceId;
        private final int port;
        private final String type;

        ConfiguredPort(String instanceId, int port, String type)
        {
            this.instanceId = requireNonNull(instanceId, "instanceId");
            this.port = port;
            this.type = requireNonNull(type, "type");
        }

        boolean isStatic() {
            return port != 0;
        }
    }
}
