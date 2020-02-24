package com.atlassian.maven.plugins.amps;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

import static org.twdata.maven.mojoexecutor.MojoExecutor.Element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;

/**
 * Utility Class containing dependencies managed by platform
 *
 * @since 8.1
 */
final class BannedDependencies {

    private BannedDependencies() {
    }

    /**
     * @param banningExcludes - dependencies that should be excluded from banning
     * @return set of excluded elements without banning excludes
     */
    static Set<Element> getBannedElements(Set<String> banningExcludes) {
        return PLATFORM_DEPENDENCIES.stream()
                .filter(d -> !banningExcludes.contains(d))
                .map(BannedDependencies::map)
                .map(BannedDependencies::exclude)
                .collect(Collectors.toSet());
    }

    private static String map(String dependency) {
        int colonCount = StringUtils.countMatches(dependency, ":");
        if (colonCount == 1) {
            return dependency.concat(":*:*:compile");
        } else if (colonCount == 2) {
            return dependency.concat(":*:compile");
        }
        return dependency.concat(":compile");
    }

    private static Element exclude(String elementValue) {
        return element(name("exclude"), elementValue);
    }

    /**
     * Alphabetically ordered set of managed dependencies from platform and third-party modules
     * of platform-pom: https://bitbucket.org/atlassian/platform-poms/src/master/
     * Test dependencies such as junit, hamcrest and mockito are not included.
     * List is kept up-to-date manually after changes in platform-pom.
     */
    private static final Set<String> PLATFORM_DEPENDENCIES = ImmutableSet.of(
            "biz.aQute.bnd:biz.aQute.bndlib",
            "com.atlassian:atlassian-failure-cache-plugin",
            "com.atlassian.activeobjects:activeobjects-bamboo-spi",
            "com.atlassian.activeobjects:activeobjects-confluence-spi",
            "com.atlassian.activeobjects:activeobjects-jira-spi",
            "com.atlassian.activeobjects:activeobjects-plugin",
            "com.atlassian.activeobjects:activeobjects-refapp-spi",
            "com.atlassian.activeobjects:activeobjects-spi",
            "com.atlassian.activeobjects:activeobjects-test",
            "com.atlassian.annotations:atlassian-annotations",
            "com.atlassian.applinks:applinks-api",
            "com.atlassian.applinks:applinks-basicauth-plugin",
            "com.atlassian.applinks:applinks-cors-plugin",
            "com.atlassian.applinks:applinks-host",
            "com.atlassian.applinks:applinks-oauth-plugin",
            "com.atlassian.applinks:applinks-pageobjects",
            "com.atlassian.applinks:applinks-plugin",
            "com.atlassian.applinks:applinks-spi",
            "com.atlassian.applinks:applinks-trustedapps-plugin",
            "com.atlassian.beehive:beehive-api", "com.atlassian.beehive:beehive-core",
            "com.atlassian.beehive:beehive-core-tck", "com.atlassian.beehive:beehive-db",
            "com.atlassian.beehive:beehive-hazelcast", "com.atlassian.beehive:beehive-single-node",
            "com.atlassian.cache:atlassian-cache-api",
            "com.atlassian.cache:atlassian-cache-ehcache",
            "com.atlassian.cache:atlassian-cache-memory",
            "com.atlassian.event:atlassian-event",
            "com.atlassian.gadgets:atlassian-gadgets",
            "com.atlassian.healthcheck:atlassian-healthcheck-in-product-test-support",
            "com.atlassian.healthcheck:atlassian-healthcheck-spi",
            "com.atlassian.healthcheck:atlassian-healthcheck",
            "com.atlassian.http:atlassian-http",
            "com.atlassian.httpclient:atlassian-httpclient-api",
            "com.atlassian.httpclient:atlassian-httpclient-plugin",
            "com.atlassian.marshalling:atlassian-marshalling-api",
            "com.atlassian.oauth:atlassian-oauth-admin-plugin",
            "com.atlassian.oauth:atlassian-oauth-api",
            "com.atlassian.oauth:atlassian-oauth-bridge",
            "com.atlassian.oauth:atlassian-oauth-consumer-core",
            "com.atlassian.oauth:atlassian-oauth-consumer-plugin",
            "com.atlassian.oauth:atlassian-oauth-consumer-sal-plugin",
            "com.atlassian.oauth:atlassian-oauth-consumer-spi",
            "com.atlassian.oauth:atlassian-oauth-service-provider-plugin",
            "com.atlassian.oauth:atlassian-oauth-service-provider-sal-plugin",
            "com.atlassian.oauth:atlassian-oauth-service-provider-spi",
            "com.atlassian.oauth:atlassian-oauth-signature-generator-plugin",
            "com.atlassian.plugin:atlassian-spring-scanner-annotation:2",
            "com.atlassian.plugin:atlassian-spring-scanner-runtime",
            "com.atlassian.plugins:atlassian-landlord-plugin",
            "com.atlassian.plugins:atlassian-landlord-spi",
            "com.atlassian.plugins:atlassian-plugins-api",
            "com.atlassian.plugins:atlassian-plugins-core",
            "com.atlassian.plugins:atlassian-plugins-eventlistener",
            "com.atlassian.plugins:atlassian-plugins-framework-bundles",
            "com.atlassian.plugins:atlassian-plugins-main",
            "com.atlassian.plugins:atlassian-plugins-osgi-bridge",
            "com.atlassian.plugins:atlassian-plugins-osgi",
            "com.atlassian.plugins:atlassian-plugins-osgi-events",
            "com.atlassian.plugins:atlassian-plugins-schema",
            "com.atlassian.plugins:atlassian-plugins-servlet",
            "com.atlassian.plugins:atlassian-plugins-spring",
            "com.atlassian.plugins:atlassian-plugins-webfragment-api",
            "com.atlassian.plugins:atlassian-plugins-webfragment",
            "com.atlassian.plugins:atlassian-plugins-webresource-api",
            "com.atlassian.plugins:atlassian-plugins-webresource-common",
            "com.atlassian.plugins:atlassian-plugins-webresource-plugin",
            "com.atlassian.plugins:atlassian-plugins-webresource-rest",
            "com.atlassian.plugins:atlassian-plugins-webresource",
            "com.atlassian.plugins:jquery",
            "com.atlassian.plugins.rest:atlassian-rest-common",
            "com.atlassian.plugins.rest:atlassian-rest-doclet",
            "com.atlassian.plugins.rest:atlassian-rest-module",
            "com.atlassian.plugins.rest:com.atlassian.jersey-library",
            "com.atlassian.plugins.test:atlassian-plugins-test",
            "com.atlassian.prettyurls:atlassian-pretty-urls-plugin",
            "com.atlassian.sal:auiplugin-integration-sal",
            "com.atlassian.sal:sal-api",
            "com.atlassian.sal:sal-core",
            "com.atlassian.sal:sal-spi",
            "com.atlassian.sal:sal-spring",
            "com.atlassian.sal:sal-test-resources",
            "com.atlassian.sal:sal-trust-api",
            "com.atlassian.sal:sal-trusted-apps-plugin-support",
            "com.atlassian.scheduler:atlassian-scheduler-api",
            "com.atlassian.scheduler:atlassian-scheduler-core-test",
            "com.atlassian.scheduler:atlassian-scheduler-core",
            "com.atlassian.scheduler:atlassian-scheduler-quartz1",
            "com.atlassian.scheduler:atlassian-scheduler-quartz2",
            "com.atlassian.scheduler.caesium:atlassian-scheduler-caesium",
            "com.atlassian.security.auth.trustedapps:atlassian-trusted-apps-core",
            "com.atlassian.security.auth.trustedapps:atlassian-trusted-apps-seraph-integration",
            "com.atlassian.soy:atlassian-soy-cli-support",
            "com.atlassian.soy:atlassian-soy-core",
            "com.atlassian.soy:atlassian-soy-spring-boot-support",
            "com.atlassian.soy:atlassian-soy-spring-mvc-support",
            "com.atlassian.soy:atlassian-soy-spring-support",
            "com.atlassian.soy:soy-template-plugin",
            "com.atlassian.soy:soy-template-renderer-api",
            "com.atlassian.soy:soy-template-renderer-plugin-api",
            "com.atlassian.streams:streams-aggregator-plugin",
            "com.atlassian.streams:streams-api",
            "com.atlassian.streams:streams-bamboo-plugin",
            "com.atlassian.streams:streams-core-plugin",
            "com.atlassian.streams:streams-crucible-plugin",
            "com.atlassian.streams:streams-fisheye-plugin",
            "com.atlassian.streams:streams-inline-actions-plugin",
            "com.atlassian.streams:streams-jira-inline-actions-plugin",
            "com.atlassian.streams:streams-jira-plugin",
            "com.atlassian.streams:streams-spi",
            "com.atlassian.streams:streams-thirdparty-plugin",
            "com.atlassian.templaterenderer:atlassian-template-renderer-api",
            "com.atlassian.templaterenderer:atlassian-template-renderer-velocity16-plugin",
            "com.atlassian.tenancy:atlassian-tenancy-api",
            "com.atlassian.tenancy:atlassian-tenancy-compatibility-plugin",
            "com.atlassian.vcache:atlassian-vcache-api",
            "com.atlassian.vcache:atlassian-vcache-internal-api",
            "com.atlassian.vcache:atlassian-vcache-internal-core",
            "com.atlassian.vcache:atlassian-vcache-internal-guava",
            "com.atlassian.vcache:atlassian-vcache-internal-harness",
            "com.atlassian.vcache:atlassian-vcache-internal-legacy",
            "com.atlassian.vcache:atlassian-vcache-internal-memcached",
            "com.atlassian.vcache:atlassian-vcache-internal-redis",
            "com.atlassian.vcache:atlassian-vcache-internal-test-utils",
            "com.atlassian.vcache:atlassian-vcache-internal-test",
            "com.atlassian.velocity.htmlsafe:velocity-htmlsafe",
            "com.atlassian.webhooks:atlassian-webhooks-api",
            "com.atlassian.webhooks:atlassian-webhooks-plugin",
            "com.atlassian.webhooks:atlassian-webhooks-spi",
            "com.google.code.findbugs:jsr305",
            "com.google.guava:guava",
            "com.sun.activation:javax.activation",
            "commons-fileupload:commons-fileupload",
            "commons-io:commons-io",
            "dom4j:dom4j",
            "io.atlassian.fugue:fugue",
            "io.atlassian.fugue:fugue-guava",
            "io.atlassian.fugue:fugue-optics",
            "io.atlassian.fugue:fugue-retry",
            "io.atlassian.fugue:fugue-scala",
            "io.atlassian.util.concurrent:atlassian-util-concurrent",
            "javax.annotation:javax.annotation-api",
            "javax.servlet:javax.servlet-api",
            "javax.validation:validation-api",
            "javax.ws.rs:javax.ws.rs-api",
            "org.apache.commons:commons-lang3",
            "org.apache.httpcomponents:httpclient-cache",
            "org.apache.httpcomponents:httpclient",
            "org.apache.httpcomponents:httpcore",
            "org.apache.httpcomponents:httpmime",
            "org.slf4j:jcl-over-slf4j",
            "org.slf4j:jul-to-slf4j",
            "org.slf4j:slf4j-api",
            "org.slf4j:slf4j-log4j12",
            "org.slf4j:slf4j-nop",
            "org.slf4j:slf4j-simple",
            "org.springframework:spring-aop",
            "org.springframework:spring-beans",
            "org.springframework:spring-context-support",
            "org.springframework:spring-context",
            "org.springframework:spring-core",
            "org.springframework:spring-expression",
            "org.springframework:spring-test",
            "org.springframework:spring-tx",
            "org.springframework:spring-web",
            "org.springframework:spring-webmvc");
}
