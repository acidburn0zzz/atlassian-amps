package com.atlassian.plugins.codegen.modules;

import com.atlassian.plugins.codegen.ArtifactDependency;
import com.atlassian.plugins.codegen.ComponentImport;

import static com.atlassian.plugins.codegen.ArtifactDependency.dependency;
import static com.atlassian.plugins.codegen.ArtifactDependency.Scope.PROVIDED;
import static com.atlassian.plugins.codegen.ArtifactDependency.Scope.TEST;
import static com.atlassian.plugins.codegen.ClassId.fullyQualified;
import static com.atlassian.plugins.codegen.ComponentImport.componentImport;
import static com.atlassian.plugins.codegen.VersionId.versionProperty;

/**
 * Commonly used {@link ArtifactDependency} and {@link ComponentImport} instances.
 */
public final class Dependencies
{
    public static final ArtifactDependency HTTPCLIENT_TEST = dependency("org.apache.httpcomponents", "httpclient", "4.1.1", TEST);

    public static final ArtifactDependency MOCKITO_TEST = dependency("org.mockito", "mockito-all", "1.8.5", TEST);

    public static final ArtifactDependency SLF4J = dependency("org.slf4j", "slf4j-api", "1.6.6", PROVIDED);

    public static final ArtifactDependency SERVLET_API = dependency("javax.servlet", "servlet-api", "2.4", PROVIDED);

    public static final ArtifactDependency SAL_API = dependency("com.atlassian.sal", "sal-api",
        versionProperty("sal.api.version", "2.4.0"), PROVIDED);

    public static final ArtifactDependency TEMPLATE_RENDERER_API = dependency("com.atlassian.templaterenderer", "atlassian-template-renderer-api",
        versionProperty("atlassian.templaterenderer.version", "1.0.5"), PROVIDED);

    public static final ArtifactDependency COMMONS_LANG = dependency("commons-lang", "commons-lang", "2.4", PROVIDED);

    public static final ComponentImport APPLICATION_PROPERTIES_IMPORT = componentImport("com.atlassian.sal.api.ApplicationProperties");

    public static final ComponentImport I18N_RESOLVER_IMPORT = componentImport("com.atlassian.sal.api.message.I18nResolver");

    public static final ComponentImport TEMPLATE_RENDERER_IMPORT = componentImport("com.atlassian.templaterenderer.TemplateRenderer")
        .alternateInterfaces(fullyQualified("com.atlassian.templaterenderer.velocity.one.five.VelocityTemplateRenderer"),
                             fullyQualified("com.atlassian.templaterenderer.velocity.one.six.VelocityTemplateRenderer"));
}
