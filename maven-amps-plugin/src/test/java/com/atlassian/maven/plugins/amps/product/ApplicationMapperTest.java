package com.atlassian.maven.plugins.amps.product;

import com.atlassian.maven.plugins.amps.Application;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThat;

public class ApplicationMapperTest
{
    private ApplicationMapper applicationMapper = new ApplicationMapper(
            ImmutableMap.<String, Map<String, GroupArtifactPair>>of("product1", ImmutableMap.of(
                            "app1", new GroupArtifactPair("p1app1group", "p1app1artifact"),
                            "app2", new GroupArtifactPair("p1app2group", "p1app2artifact")),
                    "product2", ImmutableMap.of(
                            "app1", new GroupArtifactPair("p2app1group", "p2app1artifact"))
            ));

    @Test
    public void returnsEmptyListForOtherProducts() {
        Product product = new Product();
        product.setId("other");

        List<ProductArtifact> result = applicationMapper.provideApplications(product);

        assertThat(result, Matchers.<ProductArtifact>empty());
    }

    @Test
    public void returnsEmptyListIfNoMatchIsFound() {
        Product product = new Product();
        product.setId("product1");
        Application other = new Application();
        other.setApplicationKey("other");
        product.setApplications(ImmutableList.of(other));

        List<ProductArtifact> result = applicationMapper.provideApplications(product);

        assertThat(result, Matchers.<ProductArtifact>empty());
    }

    @Test
    public void returnsCorrectArtifactsForProduct1() {
        Product product = prepareProduct("product1");

        List<ProductArtifact> result = applicationMapper.provideApplications(product);

        @SuppressWarnings ("unchecked")
        Matcher<Iterable<? extends ProductArtifact>> matcher = Matchers.containsInAnyOrder(
                productArtifact("p1app1group", "p1app1artifact", "version1"),
                productArtifact("p1app2group", "p1app2artifact", "version2"));
        assertThat(result, matcher);
    }

    @Test
    public void returnsCorrectArtifactsForProduct2() {
        Product product = prepareProduct("product2");

        List<ProductArtifact> result = applicationMapper.provideApplications(product);

        Matcher<Iterable<? extends ProductArtifact>> matcher = Matchers.contains(
                productArtifact("p2app1group", "p2app1artifact", "version1"));
        assertThat(result, matcher);
    }

    private Matcher<ProductArtifact> productArtifact(final String groupId, final String artifactId, final String version)
    {
        return new TypeSafeMatcher<ProductArtifact>()
        {
            @Override
            public void describeTo(final Description description)
            {
                description.appendValue(new ProductArtifact(groupId, artifactId, version));
            }

            @Override
            protected boolean matchesSafely(final ProductArtifact productArtifact)
            {
                return groupId.equals(productArtifact.getGroupId()) &&
                        artifactId.equals(productArtifact.getArtifactId()) &&
                        version.equals(productArtifact.getVersion());
            }
        };
    }

    private Product prepareProduct(final String product1)
    {
        Product product = new Product();
        product.setId(product1);
        Application app1 = new Application();
        app1.setApplicationKey("app1");
        app1.setVersion("version1");
        Application app2 = new Application();
        app2.setApplicationKey("app2");
        app2.setVersion("version2");
        product.setApplications(ImmutableList.of(app1, app2));
        return product;
    }

}