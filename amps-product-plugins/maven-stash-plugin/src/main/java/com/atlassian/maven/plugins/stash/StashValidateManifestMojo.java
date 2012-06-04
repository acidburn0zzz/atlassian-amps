package com.atlassian.maven.plugins.stash;

import com.atlassian.maven.plugins.amps.osgi.ValidateManifestMojo;

import org.apache.maven.plugins.annotations.Mojo;

/**
 * @since 3.10
 */
@Mojo(name = "validate-manifest")
public class StashValidateManifestMojo extends ValidateManifestMojo
{
}
