package com.atlassian.maven.plugins.amps.product.jira.xml.module;

import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;

import java.util.Arrays;
import java.util.Collection;

public class CompositeTransformationModule implements TransformationModule {
    Collection<TransformationModule> transformationModules;

    CompositeTransformationModule(TransformationModule... transformationModules) {
        this.transformationModules = Arrays.asList(transformationModules);
    }

    @Override
    public boolean transform(Document document) throws MojoExecutionException {
        boolean accumulator = false;
        for (TransformationModule transformationModule : transformationModules) {
            accumulator |= transformationModule.transform(document);
        }
        return accumulator;
    }
}
