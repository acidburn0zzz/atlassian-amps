package com.atlassian.amps.it;

import org.junit.Assert;
import org.junit.Test;

/**
 * Verifies that unit tests are not run (or re-run) during the {@code integration-test} phase.
 * <p>
 * When {@code IntegrationTestMojo} runs Failsafe, it sets a test pattern that is intended to ensure unit tests
 * are skipped and only integration tests run. If that pattern is incorrect, this test will run along with the
 * {@code IntegrationTest} and fail.
 */
public class UnitTest
{
    @Test
    public void aUnitTest()
    {
        // When the IntegrationTestMojo runs, it sets the product's HTTP port as a system property for use
        // in tests. The UnitTestMojo does not set that property, so if it's set here this test is running
        // with the integration tests.
        Assert.assertNull("Unit tests should not run with integration tests", Integer.getInteger("http.port"));
    }
}