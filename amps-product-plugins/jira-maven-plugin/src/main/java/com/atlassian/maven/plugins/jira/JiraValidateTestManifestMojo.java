package com.atlassian.maven.plugins.jira;

import com.atlassian.maven.plugins.amps.osgi.ValidateManifestMojo;
import com.atlassian.maven.plugins.amps.osgi.ValidateTestManifestMojo;

import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "validate-test-manifest")
public class JiraValidateTestManifestMojo extends ValidateTestManifestMojo
{
}
