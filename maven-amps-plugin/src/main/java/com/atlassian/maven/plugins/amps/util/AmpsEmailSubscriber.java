package com.atlassian.maven.plugins.amps.util;

import java.util.Arrays;
import java.util.List;

import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;

/**
 * @since version
 */
public interface AmpsEmailSubscriber
{
    public static final List<String> ALLOWED_PRODUCTS = Arrays.asList(
            ProductHandlerFactory.CONFLUENCE
            , ProductHandlerFactory.JIRA
            , ProductHandlerFactory.BAMBOO
            , ProductHandlerFactory.FECRU
            , ProductHandlerFactory.CROWD
            , ProductHandlerFactory.STASH);
    void promptForSubscription(String product);
}
