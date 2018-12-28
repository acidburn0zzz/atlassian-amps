package com.atlassian.maven.plugins.amps;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

public class TestAbstractTestGroupsHandlerMojo
{
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

        AbstractTestGroupsHandlerMojo testGroupsHandlerMojo = Mockito.spy(AbstractTestGroupsHandlerMojo.class);
        List<Product> products = Arrays.asList(product, product1, product2);
        //No conflicts should be detected
        testGroupsHandlerMojo.validatePortConfiguration(products);
    }

    @Test
    public void testValidatePortConfigurationPreventHttpAndHttpsConflictWhenNotUsed() throws MojoExecutionException
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

        AbstractTestGroupsHandlerMojo testGroupsHandlerMojo = Mockito.spy(AbstractTestGroupsHandlerMojo.class);
        List<Product> products = Arrays.asList(product, product1);
        //Should not detect a conflict as http port of testProduct1 is not used
        testGroupsHandlerMojo.validatePortConfiguration(products);
    }


    @Test(expected = MojoExecutionException.class)
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

        AbstractTestGroupsHandlerMojo testGroupsHandlerMojo = Mockito.spy(AbstractTestGroupsHandlerMojo.class);
        List<Product> products = Arrays.asList(product, product1, product2);
        //Should detect and print errors for all conflicts before throwing an exception
        testGroupsHandlerMojo.validatePortConfiguration(products);
    }
}