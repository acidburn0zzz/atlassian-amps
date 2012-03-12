package com.atlassian.plugins.codegen.modules;

import com.atlassian.plugins.codegen.ArtifactDependency;

import static com.atlassian.plugins.codegen.ArtifactDependency.Scope.PROVIDED;

import static com.atlassian.plugins.codegen.ArtifactDependency.dependency;
import static com.atlassian.plugins.codegen.ArtifactDependency.Scope.TEST;

/**
 * Commonly used {@link ArtifactDependency} instances.
 */
public final class Dependencies
{
    public static final ArtifactDependency HTTPCLIENT_TEST = dependency("org.apache.httpcomponents", "httpclient", "4.1.1", TEST);

    public static final ArtifactDependency MOCKITO_TEST = dependency("org.mockito", "mockito-all", "1.8.5", TEST);
    
    public static final ArtifactDependency SERVLET_API = dependency("javax.servlet", "servlet-api", "2.4", PROVIDED);
}
