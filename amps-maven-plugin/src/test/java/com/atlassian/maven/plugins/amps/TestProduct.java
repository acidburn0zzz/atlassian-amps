package com.atlassian.maven.plugins.amps;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TestProduct
{
    private static final String OTHER_SHARED_HOME = "otherSharedHome";
    private static final String THIS_SHARED_HOME = "thisSharedHome";
    private static final int OTHER_PORT = 667;
    private static final int THIS_PORT = 666;

    @Mock private Product otherProduct;
    private Product thisProduct;

    @Before
    public void setUp()
    {
        thisProduct = new Product();
    }

    @Test
    public void mergingShouldPreserveSharedHomeWhenItExists()
    {
        when(otherProduct.getSharedHome()).thenReturn(OTHER_SHARED_HOME);
        thisProduct.setSharedHome(THIS_SHARED_HOME);
        assertEquals(THIS_SHARED_HOME, thisProduct.merge(otherProduct).getSharedHome());
    }

    @Test
    public void mergingShouldSetOtherSharedHomeWhenItDoesNotExist()
    {
        when(otherProduct.getSharedHome()).thenReturn(OTHER_SHARED_HOME);
        assertNull(thisProduct.getSharedHome());
        assertEquals(OTHER_SHARED_HOME, thisProduct.merge(otherProduct).getSharedHome());
    }

    @Test
    public void mergingShouldPreserveAjpPortWhenItExists()
    {
        when(otherProduct.getAjpPort()).thenReturn(OTHER_PORT);
        thisProduct.setAjpPort(THIS_PORT);
        assertEquals(THIS_PORT, thisProduct.merge(otherProduct).getAjpPort());
    }

    @Test
    public void mergingShouldSetOtherAjpPortWhenItDoesNotExist()
    {
        when(otherProduct.getAjpPort()).thenReturn(OTHER_PORT);
        assertEquals(0, thisProduct.getAjpPort());
        assertEquals(OTHER_PORT, thisProduct.merge(otherProduct).getAjpPort());
    }

    @Test
    public void mergingShouldPreserveRmiPortWhenItExists()
    {
        when(otherProduct.getRmiPort()).thenReturn(OTHER_PORT);
        thisProduct.setRmiPort(THIS_PORT);
        assertEquals(THIS_PORT, thisProduct.merge(otherProduct).getRmiPort());
    }

    @Test
    public void mergingShouldSetOtherRmiPortWhenItDoesNotExist()
    {
        when(otherProduct.getRmiPort()).thenReturn(OTHER_PORT);
        assertEquals(0, thisProduct.getRmiPort());
        assertEquals(OTHER_PORT, thisProduct.merge(otherProduct).getRmiPort());
    }
}
