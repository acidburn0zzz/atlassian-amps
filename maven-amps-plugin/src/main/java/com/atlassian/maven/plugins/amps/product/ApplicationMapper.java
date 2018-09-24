package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.Application;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.atlassian.fugue.Option.option;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

class ApplicationMapper
{
    private Map<String, Map<String, GroupArtifactPair>> applicationKeys;

    ApplicationMapper(final Map<String, Map<String, GroupArtifactPair>> applicationKeys)
    {
        this.applicationKeys = applicationKeys;
    }

    List<ProductArtifact> provideApplications(final Product product)
    {
        return option(applicationKeys.get(product.getId()))
                .map(new Function<Map<String, GroupArtifactPair>, List<ProductArtifact>>()
                {
                    @Override
                    public List<ProductArtifact> apply(final Map<String, GroupArtifactPair> applicationKeysForProduct)
                    {
                        final Predicate<Application> isApplicationSupportedByProduct = new Predicate<Application>()
                        {
                            @Override
                            public boolean apply(final Application application)
                            {
                                final String applicationKey = application.getApplicationKey();
                                return applicationKeysForProduct.containsKey(applicationKey);
                            }
                        };
                        final Function<Application, ProductArtifact> toProductArtifact = new Function<Application, ProductArtifact>()
                        {
                            @Override
                            public ProductArtifact apply(final Application application)
                            {
                                final String applicationKey = application.getApplicationKey();
                                final GroupArtifactPair groupArtifactPair = applicationKeysForProduct.get(applicationKey);
                                return groupArtifactPair.createProductArtifactWithVersion(application.getVersion());
                            }
                        };

                        return ImmutableList.copyOf(
                                transform(
                                        filter(product.getApplications(), isApplicationSupportedByProduct),
                                        toProductArtifact));
                    }
                }).getOrElse(Collections.<ProductArtifact>emptyList());
    }
}
