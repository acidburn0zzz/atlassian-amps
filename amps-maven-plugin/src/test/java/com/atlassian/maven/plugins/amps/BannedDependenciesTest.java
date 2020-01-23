package com.atlassian.maven.plugins.amps;

import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import static com.atlassian.maven.plugins.amps.BannedDependencies.getBannedElements;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BannedDependenciesTest {

    @Test
    public void shouldReturnListReducedByBanningExcludes() {
        int bannedDependenciesInitialSize = getBannedElements(new HashSet<>()).size();
        Set<String> banningExcludes = ImmutableSet.of(
                "javax.ws.rs:javax.ws.rs-api",
                "org.springframework:spring-test",
                "org.springframework:spring-tx",
                "com.atlassian.marshalling:atlassian-marshalling-api",
                "com.atlassian.plugins:atlassian-plugins-eventlistener",
                "com.atlassian.plugins:atlassian-plugins-webfragment",
                "com.atlassian.plugins:atlassian-plugins-webfragment-api"
        );
        Set<MojoExecutor.Element> bannedDependencies = getBannedElements(banningExcludes);
        assertThat(bannedDependencies.size(), is(bannedDependenciesInitialSize - banningExcludes.size()));
    }
}