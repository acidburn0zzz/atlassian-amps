package com.atlassian.maven.plugins.amps.product.common;

import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;

public interface XMLDocumentTransformer {
    boolean transform(final Document document) throws MojoExecutionException;
}
