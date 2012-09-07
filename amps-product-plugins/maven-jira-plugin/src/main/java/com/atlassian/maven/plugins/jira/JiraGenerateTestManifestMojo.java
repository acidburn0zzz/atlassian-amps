package com.atlassian.maven.plugins.jira;

import com.atlassian.maven.plugins.amps.osgi.GenerateManifestMojo;
import com.atlassian.maven.plugins.amps.osgi.GenerateTestManifestMojo;

import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "generate-test-manifest")
public class JiraGenerateTestManifestMojo extends GenerateTestManifestMojo
{
}
