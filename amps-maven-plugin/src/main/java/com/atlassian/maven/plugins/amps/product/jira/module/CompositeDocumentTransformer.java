package com.atlassian.maven.plugins.amps.product.jira.module;

import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;

import java.util.Arrays;
import java.util.Collection;

public class CompositeDocumentTransformer<T> implements DocumentTransformer {
    Collection<DocumentTransformer> documentTransformers;

    public CompositeDocumentTransformer(DocumentTransformer... documentTransformers) {
        this.documentTransformers = Arrays.asList(documentTransformers);
    }

    @Override
    public boolean transform(Document entity) throws MojoExecutionException {
        boolean accumulator = false;
        for (DocumentTransformer documentTransformer : documentTransformers) {
            accumulator |= documentTransformer.transform(entity);
        }
        return accumulator;
    }
}
