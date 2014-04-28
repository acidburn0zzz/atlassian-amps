package com.atlassian.maven.plugins.amps;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link Product}.
 */
public class TestProduct
{
    // Constants
    private static final String OTHER_SHARED_HOME = "otherSharedHome";
    private static final String THIS_SHARED_HOME = "thisSharedHome";
    private static final int OTHER_AJP_PORT = 667;
    private static final int THIS_AJP_PORT = 666;

    @Test
    public void mergingShouldPreserveSharedHomeWhenItExists()
    {
        // Set up
        final Product otherProduct = mock(Product.class);
        when(otherProduct.getSharedHome()).thenReturn(OTHER_SHARED_HOME);
        final Product thisProduct = new Product();
        thisProduct.setSharedHome(THIS_SHARED_HOME);

        // Invoke
        final Product mergedProduct = thisProduct.merge(otherProduct);

        // Check
        assertEquals(THIS_SHARED_HOME, mergedProduct.getSharedHome());
    }

    @Test
    public void mergingShouldSetOtherSharedHomeWhenItDoesNotExist()
    {
        // Set up
        final Product otherProduct = mock(Product.class);
        when(otherProduct.getSharedHome()).thenReturn(OTHER_SHARED_HOME);
        final Product thisProduct = new Product();
        assertNull(thisProduct.getSharedHome());

        // Invoke
        final Product mergedProduct = thisProduct.merge(otherProduct);

        // Check
        assertEquals(OTHER_SHARED_HOME, mergedProduct.getSharedHome());
    }

    @Test
    public void mergingShouldPreserveAjpPortWhenItExists()
    {
        // Set up
        final Product otherProduct = mock(Product.class);
        when(otherProduct.getAjpPort()).thenReturn(OTHER_AJP_PORT);
        final Product thisProduct = new Product();
        thisProduct.setAjpPort(THIS_AJP_PORT);

        // Invoke
        final Product mergedProduct = thisProduct.merge(otherProduct);

        // Check
        assertEquals(THIS_AJP_PORT, mergedProduct.getAjpPort());
    }

    @Test
    public void mergingShouldSetOtherAjpPortWhenItDoesNotExist()
    {
        // Set up
        final Product otherProduct = mock(Product.class);
        when(otherProduct.getAjpPort()).thenReturn(OTHER_AJP_PORT);
        final Product thisProduct = new Product();
        assertEquals(0, thisProduct.getAjpPort());

        // Invoke
        final Product mergedProduct = thisProduct.merge(otherProduct);

        // Check
        assertEquals(OTHER_AJP_PORT, mergedProduct.getAjpPort());
    }
}
