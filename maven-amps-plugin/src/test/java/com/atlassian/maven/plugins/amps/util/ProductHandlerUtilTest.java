package com.atlassian.maven.plugins.amps.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class ProductHandlerUtilTest
{

    @Test
    public void toArtifactsShouldReturnEmptyArrayForEmptyString() throws Exception
    {
        assertThat(ProductHandlerUtil.toArtifacts(null).size(), equalTo(0));
    }
}