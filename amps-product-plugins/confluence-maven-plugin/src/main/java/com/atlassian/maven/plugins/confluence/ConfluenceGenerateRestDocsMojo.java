package com.atlassian.maven.plugins.confluence;

import com.atlassian.maven.plugins.amps.GenerateRestDocsMojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * @since 3.6.1
 */
@Mojo(name = "generate-rest-docs", requiresDependencyResolution = ResolutionScope.TEST)
public class ConfluenceGenerateRestDocsMojo extends GenerateRestDocsMojo
{
}
