package com.atlassian.maven.plugins.amps;

import com.atlassian.maven.plugins.amps.product.ProductHandler;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.mock;

public class TestAbstractTestGroupsHandlerMojo
{
    @Rule
    public final ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testValidatePortConfigurationPass() throws MojoExecutionException
    {
        Product product = new Product();
        product.setInstanceId("testProduct");
        product.setUseHttps(false);
        product.setHttpPort(7100);
        product.setAjpPort(7101);
        product.setRmiPort(7102);

        Product product1 = new Product();
        product1.setInstanceId("testProduct1");
        product1.setUseHttps(false);
        product1.setHttpPort(7200);
        product1.setAjpPort(7201);
        product1.setRmiPort(7202);

        Product product2 = new Product();
        product2.setInstanceId("testProduct2");
        product2.setUseHttps(false);
        product2.setHttpPort(7300);
        product2.setAjpPort(7301);
        product2.setRmiPort(7302);

        //No conflicts should be detected
        runValidationTest(product, product1, product2);
    }

    @Test
    public void testValidatePortConfigurationCatchesConflictsWithinSameProduct() throws MojoExecutionException
    {
        Product product = new Product();
        product.setInstanceId("testProduct");
        product.setUseHttps(false);
        product.setHttpPort(7100);
        product.setAjpPort(7100);
        product.setRmiPort(7100);

        expectedEx.expect(MojoExecutionException.class);
        expectedEx.expectMessage("2 port conflicts were detected between the 1 products");

        //The AJP and RMI ports conflict with the HTTP port
        runValidationTest(product);
    }

    @Test
    public void testValidatePortConfigurationIgnoresHttpAndHttpsConflictWhenNotUsed() throws MojoExecutionException
    {
        Product product = new Product();
        product.setInstanceId("testProduct");
        product.setUseHttps(false);
        product.setHttpPort(7100);

        Product product1 = new Product();
        product1.setInstanceId("testProduct1");
        product1.setUseHttps(true);
        product1.setHttpsPort(7000);
        product1.setHttpPort(7100);

        //Should not detect a conflict as http port of testProduct1 is not used
        runValidationTest(product, product1);
    }


    @Test
    public void testValidatePortConfigurationDetectConflicts() throws MojoExecutionException
    {
        Product product = new Product();
        product.setInstanceId("testProduct");
        product.setUseHttps(false);
        product.setHttpPort(7100);
        product.setAjpPort(7300);
        product.setRmiPort(7302);

        Product product1 = new Product();
        product1.setInstanceId("testProduct1");
        product1.setUseHttps(false);
        product1.setHttpPort(7100);
        product1.setAjpPort(7301);
        product1.setRmiPort(7302);

        Product product2 = new Product();
        product2.setInstanceId("testProduct2");
        product2.setUseHttps(true);
        product2.setHttpsPort(7300);
        product2.setAjpPort(7301);
        product2.setRmiPort(7302);

        expectedEx.expect(MojoExecutionException.class);
        expectedEx.expectMessage("5 port conflicts were detected between the 3 products");

        //Should detect and print errors for all conflicts before throwing an exception
        runValidationTest(product, product1, product2);
    }

    private static List<ProductExecution> executionsFor(Product... products)
    {
        ProductHandler productHandler = mock(ProductHandler.class);

        return Stream.of(products)
                .map(product -> new ProductExecution(product, productHandler))
                .collect(toList());
    }

    private void runValidationTest(Product... products) throws MojoExecutionException
    {
        AbstractTestGroupsHandlerMojo testGroupsHandlerMojo = Mockito.spy(AbstractTestGroupsHandlerMojo.class);
        testGroupsHandlerMojo.validatePortConfiguration(executionsFor(products));
    }
}