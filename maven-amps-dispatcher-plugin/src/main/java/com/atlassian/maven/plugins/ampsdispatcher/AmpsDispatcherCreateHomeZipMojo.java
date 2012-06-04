package com.atlassian.maven.plugins.ampsdispatcher;

import org.apache.maven.plugins.annotations.Mojo;

/**
 * Creates a test-resources compatible zip from the previous run's home dir
 *
 * @since 3.1-m3
 */
@Mojo(name = "create-home-zip")
public class AmpsDispatcherCreateHomeZipMojo extends AbstractAmpsDispatcherMojo {
}
