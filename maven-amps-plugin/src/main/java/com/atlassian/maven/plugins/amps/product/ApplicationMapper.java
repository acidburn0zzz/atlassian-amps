package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.Application;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.atlassian.fugue.Option.option;
import static java.util.stream.Collectors.toList;

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
                .map(applicationKeysForProduct -> {
                    final Predicate<Application> isApplicationSupportedByProduct = application -> {
                        final String applicationKey = application.getApplicationKey();
                        return applicationKeysForProduct.containsKey(applicationKey);
                    };
                    final Function<Application, ProductArtifact> toProductArtifact = application -> {
                        final String applicationKey = application.getApplicationKey();
                        final GroupArtifactPair groupArtifactPair = applicationKeysForProduct.get(applicationKey);
                        return groupArtifactPair.createProductArtifactWithVersion(application.getVersion());
                    };

                    return product.getApplications().stream()
                            .filter(isApplicationSupportedByProduct)
                            .map(toProductArtifact)
                            .collect(Collectors.collectingAndThen(toList(), Collections::unmodifiableList));
                }).getOrElse(Collections.emptyList());
    }
}
